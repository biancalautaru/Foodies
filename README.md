# Foodies 🍔

Aplicație de tip food-delivery în Java, accesibilă din consolă. Clientul își face cont, explorează restaurantele și meniurile, plasează comenzi, urmărește fluxul de livrare și lasă recenzii. Datele sunt persistate într-o bază PostgreSQL prin JDBC, iar acțiunile importante sunt scrise într-un fișier de audit.

## 📋 Cuprins

- [✨ Funcționalități](#-funcționalități)
- [📁 Structura proiectului](#-structura-proiectului)
- [🧩 Clasele](#-clasele)
- [🗄️ Baza de date](#-baza-de-date)
- [▶️ Cum se rulează](#-cum-se-rulează)

## ✨ Funcționalități

1. 👤 Creare cont client la pornire.
2. 🏪 Listarea restaurantelor sortate alfabetic sau după rating.
3. 📖 Vizualizarea meniului unui restaurant sortat după preț sau alfabetic.
4. ⭐ Răsfoirea recenziilor publice ale unui restaurant.
5. 🛒 Plasarea unei comenzi noi cu produse din coș și adresă de livrare.
6. ✅ Confirmarea comenzii de către restaurant.
7. ❌ Anularea comenzii cât timp este în așteptare.
8. 🍳 Marcarea comenzii ca gata + asignarea automată a unui curier disponibil.
9. 🛵 Ridicarea și livrarea comenzii.
10. 📝 Trimiterea unei recenzii (1–5 stele) pentru o comandă livrată.
11. 📜 Vizualizarea istoricului propriu de comenzi.
12. 🔍 Vizualizarea detaliilor unei comenzi (produse, subtotal, taxă, total).
13. 🔁 Re-plasarea unei comenzi livrate anterior la o adresă nouă.

## 📁 Structura proiectului

```
Foodies/
├── db.properties            🔑 Credențialele DB (din db.example.properties)
├── sql/schema.sql           🗄️ Script PostgreSQL
├── logs/audit.csv           📝 Generat la rulare
└── src/
    ├── config/              🔌 DatabaseConfiguration (singleton)
    ├── exceptions/          🚨 Excepții custom
    ├── interfaces/          🧾 Interfețe servicii + Displayable, Reviewable
    ├── models/              🧩 Address, User, Customer, Driver, Restaurant,
    │                           MenuItem, Order, OrderStatus, Review, Cart
    ├── repository/          🧰 GenericRepository + repo-uri JDBC
    ├── service/             🧠 UserService, RestaurantService, MenuService,
    │                           OrderService, AuditService
    └── main/                ▶️ Main, ConsoleApp, DataSeeder
```

## 🧩 Clasele

| Clasă | Rol |
|---|---|
| `User` (abstractă) | 👤 Bază pentru utilizatori (date de contact). |
| `Customer extends User` | 🧑 Client cu coș (`Cart`). |
| `Driver extends User` | 🛵 Curier, marcat disponibil/indisponibil. |
| `Restaurant` | 🏪 Restaurant cu meniu și recenzii. |
| `MenuItem` | 🍕 Produs din meniu (nume, descriere, preț). |
| `Order` | 📦 Comandă cu produse, status, curier, recenzie, total. |
| `OrderStatus` (enum) | 🚦 `PENDING → PREPARING → READY_FOR_PICKUP → OUT_FOR_DELIVERY → DELIVERED` (+ `CANCELLED`). |
| `Review` | ⭐ Recenzie (1–5 stele + comentariu) legată de o comandă. |
| `Cart` | 🛒 Coșul clientului (produse dintr-un singur restaurant). |
| `Address` | 📍 Adresă (stradă, număr, oraș). |

## 🗄️ Baza de date

Schema este în `sql/schema.sql` și conține tabelele: `addresses`, `customers`, `drivers`, `restaurants`, `menu_items`, `orders`, `order_items`, `reviews`.

Înainte de prima rulare:

```bash
createdb foodies
psql -d foodies -f sql/schema.sql
```

Apoi se copiază `db.example.properties` în `db.properties` și se completează credențialele:

```properties
db.url=jdbc:postgresql://localhost:5432/foodies
db.user=postgres
db.password=parola_ta
```

🌱 La prima rulare, dacă baza e goală, `DataSeeder` o populează cu 3 clienți, 3 curieri, 5 restaurante, ~25 de produse și câteva comenzi (inclusiv livrate cu recenzii).

## ▶️ Cum se rulează

### Cerințe prealabile

- ☕ JDK 17 sau mai nou
- 🐘 PostgreSQL 13+
- 📦 Driver `postgresql-42.7.x.jar`

### 🧠 În IntelliJ IDEA

1. `File → Open` și alege folderul `Foodies`.
2. Adaugă `postgresql-42.7.x.jar` la `Project Structure → Libraries` (dacă nu este deja).
3. Copiază `db.example.properties` → `db.properties` și completează credențialele.
4. Rulează `sql/schema.sql` pe baza de date.
5. Rulează clasa `main.Main`.

### 💻 Din linia de comandă

```powershell
javac -d out -cp "cale\spre\postgresql-42.7.11.jar" (Get-ChildItem -Recurse src -Filter *.java).FullName
java  -cp "out;cale\spre\postgresql-42.7.11.jar" main.Main
```

Pe Linux/macOS separatorul de classpath este `:` în loc de `;`.