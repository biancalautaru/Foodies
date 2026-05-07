# 🍔 Foodies

Foodies is a food-delivery platform built in Java. It models the full lifecycle of a delivery order — from a customer browsing menus and building a cart, through driver assignment and delivery, to post-delivery reviews — backed by an audit log written to CSV.

## ✨ Features

- 👤 **Customer & driver management** — Register customers and drivers; query available drivers from a shared pool.
- 🍽️ **Restaurant & menu browsing** — Browse restaurants sorted by rating or name; view menus sorted by name or price.
- 🛒 **Cart** — Add items to a cart enforcing a single-restaurant-per-order rule.
- 📦 **Order lifecycle** — `PENDING` → `PREPARING` → `READY_FOR_PICKUP` → `OUT_FOR_DELIVERY` → `DELIVERED`, with strict status-transition validation.
- 🚴 **Automatic driver assignment** — Available drivers are assigned automatically when an order reaches `READY_FOR_PICKUP`.
- 📍 **City matching** — Orders can only be placed to a restaurant in the same city as the delivery address.
- ❌ **Cancellation with fees** — Free cancellation while `PENDING` or `PREPARING`; 30 % of subtotal at `READY_FOR_PICKUP`; full subtotal + delivery fee at `OUT_FOR_DELIVERY`.
- ⭐ **Reviews** — Customers leave a 1–5 star rating and comment after delivery; the restaurant's rolling average is updated immediately.
- 🔁 **Reorder** — Repeat a past delivered order with a new address (city must still match the restaurant).
- 📋 **Audit trail** — Every significant action is appended to `logs/audit.csv` via a singleton `AuditService`.
- 💬 **Interactive console UI** — Romanian-language CLI (`ConsoleApp`) for manual exploration of all features.

## 📁 Project Structure

```
Foodies/
├── src/
│   ├── main/
│   │   ├── Main.java          # Entry point: seeds demo data, runs scripted workflows, starts ConsoleApp
│   │   └── ConsoleApp.java    # Interactive console application (Romanian UI)
│   ├── models/
│   │   ├── User.java          # Base entity (id, name, email, phone)
│   │   ├── Customer.java      # User with a Cart
│   │   ├── Driver.java        # User with availability flag
│   │   ├── Restaurant.java    # Menu, star rating, review count
│   │   ├── MenuItem.java      # Price, description, linked restaurant
│   │   ├── Address.java       # record(street, number, city)
│   │   ├── Cart.java          # Single-restaurant shopping cart
│   │   ├── Order.java         # Line items, status, cancellation fee logic, clone/reorder
│   │   ├── OrderStatus.java   # PENDING → … → DELIVERED | CANCELLED
│   │   └── Review.java        # record(customer, order, rating, comment, timestamp)
│   ├── service/
│   │   ├── UserService.java        # Customer/driver registry; findAvailableDriver()
│   │   ├── RestaurantService.java  # Restaurant/menu-item registry; sorted display helpers
│   │   ├── MenuService.java        # Menu queries delegating to RestaurantService
│   │   ├── OrderService.java       # Full order workflow (place → deliver, cancel, review, reorder)
│   │   └── AuditService.java       # Singleton CSV logger → logs/audit.csv
│   └── exceptions/
│       ├── FoodiesException.java        # Runtime base exception
│       ├── EntityNotFoundException.java # Missing restaurant/order/etc.
│       └── InvalidOrderException.java   # Business-rule violations
└── logs/
    └── audit.csv              # Append-only action log (created at runtime)
```

## 🛠️ Tech Stack

| | |
|---|---|
| ☕ **Language** | Java 21 |
| 🏗️ **Build** | IntelliJ IDEA (plain Java module, no Maven/Gradle) |
| 💾 **Persistence** | In-memory collections + `logs/audit.csv` |
| 📦 **Dependencies** | Standard library only |

## 🚀 Getting Started

### Prerequisites

- ☕ JDK 21 (e.g. Azul Zulu 21)
- 💡 IntelliJ IDEA (recommended)

### ▶️ Run in IntelliJ

1. Open the `Foodies` folder as a project.
2. Ensure the SDK is set to JDK 21 (**File → Project Structure → SDK**).
3. Run `main.Main`.

The program seeds demo restaurants, customers, drivers, and orders, executes a scripted walkthrough, then launches the interactive console.

### ⌨️ Run from the command line

```powershell
# From the repo root — compile
javac -d out -sourcepath src (Get-ChildItem -Recurse src -Filter *.java | % { $_.FullName })

# Run (working directory must be the repo root so logs/ is created here)
java -cp out main.Main
```

## 📋 Audit Log

Every key action (add customer, place order, submit review, cancel, etc.) is recorded in `logs/audit.csv`:

```
action_name,timestamp
addCustomer,2026-05-07T21:00:00.123
placeOrder,2026-05-07T21:00:01.456
...
```

The `logs/` directory is created automatically on first run relative to the working directory.