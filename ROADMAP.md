# Hatchery CRM — Roadmap

Reference note: feature ideas below are informed by reviewing a customer's existing
poultry/hatchery ERP (POLOXY by Techence IT Solutions) as a competitive reference point.
POLOXY covers a broad multi-vertical scope (feed mill, pharma, fishery, dairy, etc.) —
we are intentionally scoping down to a focused hatchery CRM first.

## Phase 1 — Core CRM (current focus)
- Customer management (add/edit/list/delete)
- Hatch batch management (species, egg count, start/expected hatch dates, status)
- Order management (link customer + batch, quantity, fulfilled status)
- Local-first storage via SQLDelight (Android/iOS/Desktop)
- Simple in-app navigation across Customers / Batches / Orders / Dashboard

## Phase 2 — Hatchery Operations
- Incubation tracking (temperature/humidity logs per batch)
- Hatch rate reporting (eggs set vs. hatched)
- Breeder & Grand Parent stock management
- Batch status lifecycle (Incubating → Hatching → Completed → Cancelled)

## Phase 3 — Sales & Trading
- Processing management (post-hatch chick/bird processing records)
- Trader management (B2B contacts distinct from retail customers)
- Invoicing & payment tracking per order
- Basic reporting (sales by period, top customers, batch yield)

## Phase 4 — Auth, Multi-user & Sync
- Authentication (email/password, roles: owner/staff)
- Backend integration (Supabase or Firebase) for remote sync
- Offline-first conflict resolution (local SQLDelight cache + remote sync)
- Multi-device support per business account

## Phase 5 — Extras (as needed)
- Feed mill inventory tracking (if customers request it)
- Notifications/reminders (e.g., upcoming hatch dates)
- Dashboards & analytics (charts for batches/orders over time)
- Export (CSV/PDF) for reports

---

## Current Implementation Status (Phase 1)
- [x] Data model: Customer, HatchBatch, Order_ (SQLDelight schema)
- [x] CustomerRepository
- [x] BatchRepository, OrderRepository
- [x] Navigation scaffold (sealed `Screen` state in `App.kt`)
- [x] Dashboard/home screen with counts + navigation
- [x] Customer CRUD screens (list + add form)
- [x] Hatch Batch CRUD screens (list + add form)
- [x] Order CRUD screens (list + add form)
- [x] Web demo build (wasmJs) live at https://vikasgill.github.io/HatcheryCRM/web-demo/
      — uses in-memory data (resets on reload); Android/iOS/Desktop keep SQLite persistence
