# IA#1 — The system you are specifying for: table-order

**CSC13114 · 23KTPM1 · read this before you write your spec**

`table-order` is a small ordering system for one restaurant. It is already in production. You have not built it, and you do not need to — your spec adds one feature to it, **split the bill at a table**. Everything below is how the system works today. Anything not written here, your spec has to decide.

## Who uses it

- **Customers** scan a QR code on their table. The browser gets a guest session (a cookie, no account, no login) tied to that table. Several phones at one table means several guest sessions on the same table.
- **Waiters** use a staff app, logged in with a staff account (JWT, role waiter). They can see every table, add items, apply a discount, and close an order.
- **The kitchen** has a screen that shows order items. It is not affected by payment in any way.

## Data, as it is now (PostgreSQL)

| Table         | Columns that matter                                                                                               |
| :------------ | :---------------------------------------------------------------------------------------------------------------- |
| `tables`      | `id`, `code` (printed in the QR, e.g. T07), `status`: `free` \| `occupied`                                        |
| `orders`      | `id`, `table_id`, `status`: `open` \| `paid` \| `closed`, `discount_percent` (0-50, set by a waiter), `opened_at` |
| `order_items` | `id`, `order_id`, `menu_item_id`, `qty`, `unit_price`, `added_by` (guest session id or staff id), `added_at`      |
| `payments`    | `id`, `order_id`, `amount`, `status`: `pending` \| `succeeded` \| `failed`, `provider_ref`, `created_at`          |

- A table has at most one open order at a time.
- Prices are integers in `đồng`, VAT (8%) already included. There are no fractional `đồng` anywhere in the database.
- The order total is computed, not stored: `sum(qty × unit_price)`, then the discount, rounded to the nearest `đồng`.

## API, as it is now (Express, JSON)

| Method and path                      | Who                        | What it does                                                                 |
| :----------------------------------- | :------------------------- | :--------------------------------------------------------------------------- |
| `GET /api/tables/:code/order`        | guest at that table, staff | the open order with its items and total                                      |
| `POST /api/tables/:code/order/items` | guest at that table, staff | adds items; opens an order if none is open                                   |
| `POST /api/orders/:id/discount`      | staff                      | sets `discount_percent`                                                      |
| `POST /api/orders/:id/pay`           | guest at that table        | creates a pending payment for the whole total and returns a payment-page URL |
| `POST /api/webhooks/payment`         | the payment provider       | reports succeeded or failed for a `provider_ref`                             |
| `POST /api/orders/:id/close`         | staff                      | closes a paid order and frees the table                                      |

Errors today have one shape:

```json
{
  "error": "<code>",
  "message": "<text>"
}
```

## The payment provider

- Payment happens on the provider's own page (cards and e-wallets). Our server never sees card details.
- The result arrives later, through the webhook. The webhook is signed, may arrive more than once for the same payment, may arrive minutes late, and occasionally never arrives — a payment can stay pending.
- Refunds exist in the provider's API but `table-order` does not use them yet.

## Live updates

Every phone at a table and the staff app hold a WebSocket. When the order changes (items added, discount, payment status), the server broadcasts the new order to that table and to staff.

## What you are adding

> The people at a table want to pay separately, each from their own phone, instead of one person paying the whole bill. The kitchen is not affected. The order stays open until the whole amount is paid.

That sentence is all the product owner wrote. Your spec turns it into something another developer — or an agent — could build without asking you anything.
