# HatcheryCRM — Architecture & Financial Model

**Prepared as:** Principal Software Architect + B2B SaaS Financial Analyst review
**Exchange rate used:** $1 USD = ₹83.5 INR
**Pricing model:** Hybrid (base + metered overage, capped)

| Tier | Base | Included Volume | Overage | Hard Cap | Eggs at Cap Ceiling* |
|---|---|---|---|---|---|
| Starter | $30/mo (₹2,500) | 150,000 eggs | $1.50/10k eggs | $60/mo (₹5,000) | 350,000 eggs |
| Commercial | $90/mo (₹7,500) | 750,000 eggs | $1.20/10k eggs | $180/mo (₹15,000) | 1,500,000 eggs |
| Enterprise | $250/mo (₹20,800) | 3,000,000 eggs | $1.00/10k eggs | $500/mo (₹41,700) | 5,500,000 eggs |

\* Beyond this volume, price is flat (capped) — every extra egg tracked is **pure margin**, which is a strong retention lever for your largest integrators (no bill shock, but you still capture their growth in engagement/lock-in).

---

## 1. Secure Offline Counter Architecture

**Goal:** Tamper-evident egg-count tracking on a device that may be offline for days/weeks, with no reliance on a trusted system clock or a trusted server round-trip.

### 1.1 Data model — Append-only cryptographic event chain

Never store a mutable "current count" as the source of truth. Store an **immutable, hash-linked ledger of events**; the count is always a *derived, recomputable projection*.

```
EggEvent {
  event_id: UUID              // client-generated, globally unique
  batch_id: UUID
  prev_hash: SHA256           // hash of the previous event in this batch's chain
  seq_no: Long                // monotonically increasing per batch, gap-free
  event_type: SET | CANDLE_LOSS | VACCINATION | ADJUSTMENT
  payload: { count_delta, reason, operator_id, ... }
  device_elapsed_ns: Long     // SystemClock.elapsedRealtimeNanos() (Android) / mach_absolute_time (iOS)
  boot_count: Int             // Settings.Global.BOOT_COUNT (Android) — see 1.3
  wall_clock_claim: Timestamp // untrusted, for display only — NEVER used for ordering/integrity
  event_hash: SHA256(prev_hash + seq_no + payload + device_elapsed_ns + boot_count)
  signature: Ed25519(event_hash, device_private_key)
}
```

- **Why hash-chained, not just signed rows:** a single signed row can still be *deleted* or *reordered* without detection. A hash chain means altering or removing any historical event breaks every subsequent `prev_hash`, which is trivially detectable both on-device (during periodic self-audit) and server-side at sync time.
- **Ordering integrity comes from `seq_no` + `prev_hash`**, not from wall-clock time. This is what defeats clock manipulation (see 1.3).
- The **projected total** (`current_egg_count`) is a materialized view rebuilt by replaying the chain — cheap enough to recompute per batch (event volume is low: settings/candling/vaccination events, not per-egg events).

### 1.2 Storage: SQLCipher + Android Keystore / iOS Secure Enclave

- **SQLCipher** (AES-256) encrypts the local DB at rest. The encryption key is **never stored in app code or SharedPreferences**.
- **Key custody:**
  - **Android:** Generate a key in **Android Keystore** (`KeyGenParameterSpec`, `setIsStrongBoxBacked(true)` where available) that is hardware-backed (TEE, or Secure Element/StrongBox on supported devices) and **non-exportable**. Use this Keystore key to wrap (encrypt) the actual SQLCipher database key, which is stored encrypted in app-private storage. The Keystore key itself never leaves secure hardware — even a rooted device can't exfiltrate it, only use it via the OS API while the app holds a valid session.
  - **iOS:** Equivalent pattern using **Secure Enclave** (`kSecAttrTokenIDSecureEnclave`) to protect a key in the **Keychain** with `kSecAttrAccessibleWhenUnlockedThisDeviceOnly`, wrapping the SQLCipher key the same way.
- **Signing key for the event chain** (Ed25519 keypair, section 1.1) is generated and held the same way — hardware-backed, non-exportable private key, so a compromised/rooted device *at rest* still cannot forge historical signatures without the physical secure element cooperating live.
- **Threat this defends against:** a field worker (or a competitor with device access) editing the SQLite file directly with a hex/DB editor to inflate or deflate egg counts for commission fraud or under-reporting to evade your usage-based billing.

### 1.3 Defense against system-clock manipulation

Farm workers have direct incentive to roll back the device clock (to stay under a metered tier, or to backdate/antedate a loss event). Defenses, layered:

1. **Never use wall-clock time for ordering or billing.** All chain integrity relies on `seq_no` (strictly incrementing, checked server-side for gaps) and `prev_hash`.
2. **Monotonic elapsed timer:** record `SystemClock.elapsedRealtimeNanos()` (Android) / `CLOCK_MONOTONIC` via `mach_absolute_time()` (iOS). This clock **cannot be changed by the user** (it's time-since-boot, immune to date/timezone changes) and is used to compute *durations between events* (e.g., "was this candling event recorded within a plausible interval of the previous setting event?").
3. **Boot count anchor (Android):** read `Settings.Global.getInt(ContentResolver, Settings.Global.BOOT_COUNT)`. Store `(boot_count, elapsed_ns)` pairs. If the OS is not rebooted, elapsed time is monotonic and reboot-proof. If a reboot occurs, `boot_count` increments — the app detects this, and on the *next successful network sync* reconciles the local elapsed-time baseline against the **server's trusted wall clock** at that sync instant, anchoring a `(boot_count → verified_server_time)` mapping. Between syncs, elapsed time is trusted **relative to the last verified anchor**, never in absolute terms.
   - *iOS equivalent:* no direct boot-count API; use `ProcessInfo.systemUptime` combined with detecting large jumps between consecutive app-foreground `systemUptime` reads as a reboot/manipulation signal, anchored the same way at each sync.
4. **Anomaly flagging, not silent rejection:** if wall-clock claims and the anchored-elapsed-time model diverge beyond a tolerance (e.g., >2 hours drift with no corresponding reboot), flag the event `INTEGRITY_SUSPECT` server-side rather than silently dropping it — surfaces it to your brother's ops dashboard for a human follow-up call rather than creating a support black hole.
5. **Server-side gap/replay detection:** on sync, the backend verifies `seq_no` is contiguous per batch and that every `event_hash` correctly chains to the prior one already persisted server-side. Any break triggers a `CHAIN_INTEGRITY_VIOLATION` on that batch, which pauses billing-relevant aggregation for that batch until manually reviewed — this is your actual enforcement point for the metered pricing model.

---

## 2. Conflict Resolution / Sync Pattern

**Context:** rural field workers on unstable 2G/3G, multiple workers possibly touching the same batch (e.g., one records candling loss while another logs a vaccination), long offline windows.

### 2.1 Recommended approach: CRDT-style op-based merge + state vector sync tokens (hybrid, not pure CRDT)

A pure CRDT is overkill for this domain (you don't need arbitrary concurrent text editing); what you need is **commutative, idempotent event application** — which the append-only ledger from Section 1 already gives you almost for free.

- **Each event is already an immutable, uniquely-IDed operation** (`event_id`, `batch_id`, `seq_no` *per device*, not global). Treat this as an **operation-based CRDT (op-CRDT)**: applying the same set of events in any order, any number of times, converges to the same state, *as long as merge/aggregation functions are commutative and idempotent* (sum of egg counts, set-union of vaccination records — both are).
- **Per-device sequence, not global sequence:** switch `seq_no` to be `(device_id, device_local_seq)` rather than a single global counter — this removes the need for coordination between devices before an event can be created offline. The server maintains, per batch, a **vector of last-seen `device_local_seq` per device** — this vector *is* your state-based sync token.

### 2.2 Sync protocol

```
Client → Server:  SyncRequest {
  batch_id,
  last_known_server_vector: { device_id: last_seq_seen, ... },  // token from previous successful sync
  new_events: [ EggEvent, ... ]   // only events with device_local_seq > what server last acked for THIS device
}

Server → Client:  SyncResponse {
  accepted_event_ids: [...],
  rejected: [ { event_id, reason: CHAIN_INTEGRITY_VIOLATION | DUPLICATE } ],
  missing_events_from_others: [ EggEvent, ... ],  // events from OTHER devices client hasn't seen yet
  new_server_vector: { device_id: last_seq, ... } // new sync token to store locally
}
```

- **Payload minimization (your stated 50KB/day/worker target):** the client never re-sends the full batch history — only the **delta since its own last acked `device_local_seq`**, and only *receives* the delta of *other* devices' events it hasn't seen (identified via the vector diff). This is what keeps the payload near your 50KB/day assumption even with multiple workers touching the same batch.
- **Idempotency for free:** if a sync is interrupted mid-upload (very likely on rural networks) and retried, the server simply re-acknowledges already-accepted `event_id`s as `DUPLICATE` (no-op) rather than double-applying — safe to blindly retry the whole batch on any network failure without dedup logic on the client.
- **Conflict semantics that actually apply here:**
  - **Concurrent counts (two workers adjust the same batch offline):** resolved by **summing deltas** (commutative) rather than last-write-wins — critical, because LWW would silently *lose* one worker's legitimate candling-loss entry.
  - **Same logical field edited twice (e.g., vaccination schedule date corrected twice offline):** resolved via **highest `(device_local_seq, device_id)` tuple wins** for that specific field — a deterministic, order-independent tiebreak (last-writer-wins *only* for genuinely single-valued fields, never for counts).
- **Compression:** batch multiple events per sync call, gzip the JSON payload (typically 60–80% reduction on repetitive event schemas) — cheap win to stay well under the 50KB/day target even on a bad-connectivity day with catch-up syncing.

---

## 3. Financial Model — 3-Year Forecast

### 3.1 Key assumptions (stated explicitly — adjust these and re-run the model as real data comes in)

| Assumption | Value |
|---|---|
| Field data-entry workers per client (avg) | Starter: 2 · Commercial: 6 · Enterprise: 20 |
| Sync data volume | 50 KB / worker / active day, 26 active days/mo |
| Cloud cost (managed Postgres, blended storage+backup) | $0.125/GB-month |
| Cloud egress (dashboard reads, exports, retry overhead) | $0.09/GB, at 1.3× ingress volume |
| Client tier mix (funnel-shaped, field-sales-led) | 50% Starter · 35% Commercial · 15% Enterprise |
| India field-sales opex (brother) | ₹90,000/mo total (₹50k draw + ₹20k travel + ₹15k marketing + ₹5k misc) |
| Fixed infra floor (hosting, domain, monitoring) | ~$50/mo, flat regardless of client count |
| Pre-launch sunk cost (devices, incorporation, initial marketing) | ~$3,000 one-time |

### 3.2 Data cost is **not** your cost driver — critical finding

At 50KB/worker/day, even your largest Enterprise client (20 workers) generates only **~0.30 GB/year**. Modeled storage+egress cost per client, even after 3 years of cumulative storage growth:

| Tier | Infra cost, Year 1 | Year 2 | Year 3 |
|---|---|---|---|
| Starter | $0.002/mo | $0.006/mo | $0.010/mo |
| Commercial | $0.006/mo | $0.018/mo | $0.029/mo |
| Enterprise | $0.022/mo | $0.059/mo | $0.096/mo |

**Takeaway:** infrastructure cost is **<0.1% of revenue at every tier, every year** — this pricing model has effectively **~99% gross margin** on the data/infra line. Your real unit economics battle is entirely in **sales cost of acquisition (CAC)**, support, and churn — not bandwidth or storage. Don't let infra-cost anxiety influence pricing decisions; it's noise. Do watch CAC closely.

### 3.3 Break-even client acquisition target

- Blended ARPU (conservative — base price only, no overage): **$84/mo/client**
- Blended ARPU (with modest overage revenue from ~25% of Commercial/Enterprise clients running partly into their metered band): **~$89/mo/client**
- Total monthly burn (India field sales + fixed infra floor): **$1,128/mo (₹94,175/mo)**

> **Break-even: 14 total clients**, in the assumed mix, e.g. **7 Starter + 5 Commercial + 2 Enterprise** ≈ $1,160/mo revenue vs. $1,128/mo burn.

This is a realistic near-term target for a single field rep in one Indian state/region — your brother needs roughly **one new client every 2–3 weeks** in the first ~7–9 months to hit break-even, assuming typical B2B field-sales cycles (relationship-building + demo + pilot + close) of 4–8 weeks per hatchery.

### 3.4 3-Year cash flow forecast

Illustrative adoption curve (adjust once you have real pipeline data — this is a planning scaffold, not a guarantee):

| Year | Total Clients | Starter | Commercial | Enterprise | MRR | ARR | Infra $/mo | Opex $/mo | Net CF/mo (year-end) | Cumulative Cash Flow (year-end) |
|---|---|---|---|---|---|---|---|---|---|---|
| 1 | 40 | 20 | 14 | 6 | $3,360 | $40,320 | ~$50 | $1,128 | +$2,232 | **+$3,626** (incl. −$3,000 pre-launch sunk cost) |
| 2 | 150 | 75 | 52 | 23 | $12,680 | $152,160 | ~$50 | $1,128 | +$11,552 | **+$86,460** |
| 3 | 400 | 200 | 140 | 60 | $33,600 | $403,200 | ~$51 | $1,128 | +$32,471 | **+$350,126** |

**Reading this table:**
- Break-even is crossed **well within Year 1** (at ~14 clients, likely months 5–8 depending on ramp speed) — cumulative cash flow is already positive by end of Year 1 even after absorbing the $3,000 pre-launch cost.
- The model is **extremely operating-leverage-favorable**: opex is nearly flat (~$1,128/mo, dominated by your brother's fixed field-sales cost) while revenue scales linearly with clients — so gross margin on each *incremental* client past break-even is ~99%+.
- **Sensitivity worth running before you commit to this plan:** (a) actual sales cycle length/CAC in your target region, (b) churn rate (not modeled above — assumed 0% for simplicity; even 5–10% annual churn is easily absorbed given the margin structure, but should be tracked), (c) whether tier mix skews more Starter-heavy early on (likely, since Enterprise integrators take longer sales cycles/trust-building — you may want to re-run with a 65/25/10 Year-1 mix as a more conservative case).

---

## 4. Recommendations

1. **Architecture:** build the append-only event chain from day one — retrofitting integrity guarantees onto a "just store the current count" schema later is a much larger rewrite than doing it now while the CRUD screens are still fresh.
2. **Sync:** the per-device vector-clock sync token is the single highest-leverage change vs. a naive full-batch-resync approach — implement it before onboarding your first multi-worker Commercial/Enterprise client, since that's where sync conflicts will actually surface.
3. **Pricing:** the caps are generous relative to realistic hatchery volumes for Starter/Commercial — don't be afraid to also offer **annual prepay at a 15–20% discount** once you have paying customers; at these margins it's pure win to trade a little revenue for locked-in cash flow and lower churn risk while your brother is still building the sales pipeline.
4. **Immediate action for your brother:** the 14-client break-even target is a concrete, motivating number to put in front of him — frame the first sales push as "14 hatcheries to cash-flow-positive," not an abstract revenue target.
