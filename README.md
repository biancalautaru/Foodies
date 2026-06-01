# Foodies 🍔

Foodies este o aplicație Java de tip food delivery, rulată din consolă. Aplicația folosește PostgreSQL prin JDBC pentru persistența datelor și scrie acțiunile importante într-un fișier de audit.

## Funcționalități 📖

- autentificare și înregistrare pentru clienți
- listarea restaurantelor după rating sau alfabetic
- afișarea meniului unui restaurant
- vizualizarea recenziilor pentru restaurante
- plasarea unei comenzi cu produse din meniul unui restaurant
- simularea fluxului comenzii: confirmare, pregătire, atribuire curier, ridicare și livrare
- anularea unei comenzi de către restaurant
- afișarea istoricului de comenzi al clientului
- afișarea detaliilor unei comenzi: produse, subtotal, taxă de livrare și total
- repetarea unei comenzi livrate anterior
- adăugarea unei recenzii pentru o comandă livrată
- ștergerea comenzilor livrate sau anulate

## Structura proiectului 🗃️

```text
Foodies/
+-- db.example.properties
+-- sql/
|   +-- schema.sql
+-- logs/
|   +-- audit.csv
+-- src/
    +-- config/
    +-- exceptions/
    +-- interfaces/
    +-- main/
    +-- models/
    +-- repository/
    +-- service/
```

Clasele principale sunt grupate astfel:

- `models`: entitățile aplicației, precum `Customer`, `Driver`, `Restaurant`, `MenuItem`, `Order`, `Review`, `Cart` și `Address`
- `repository`: accesul la baza de date pentru clienți, curieri, restaurante, produse, comenzi și recenzii
- `service`: logica aplicației pentru utilizatori, restaurante, comenzi și audit
- `main`: pornirea aplicației, meniul din consolă și popularea inițială a bazei de date

## Baza de date 🗄️

Schema PostgreSQL se află în `sql/schema.sql` și definește tabelele:
`addresses`, `customers`, `drivers`, `restaurants`, `menu_items`, `orders`, `order_items`, `reviews`

Înainte de rulare, creează baza de date și aplică schema:
```bash
createdb foodies
psql -d foodies -f sql/schema.sql
```

Copiază `db.example.properties` în `db.properties` și completează datele de conectare:
```properties
db.url=jdbc:postgresql://localhost:5432/foodies
db.user=user
db.password=parola
```

Daca baza este goala, `DataSeeder` adauga date initiale pentru testare.

## Rulare 💻

Cerintele proiectului:
- JDK 17 sau mai nou
- PostgreSQL
- driverul JDBC PostgreSQL adăugat în classpath

Clasa de pornire este `main.Main`.

### IntelliJ IDEA

1. Deschide folderul proiectului `Foodies` în IntelliJ IDEA.
2. Setează un SDK Java 17 sau mai nou din `File > Project Structure > Project SDK`.
3. Adaugă driverul PostgreSQL JDBC în proiect din `File > Project Structure > Libraries > + > Java`, apoi selectează fișierul `postgresql-42.7.x.jar`.
4. Creează baza de date PostgreSQL și rulează scriptul `sql/schema.sql`.
5. Copiază `db.example.properties` în `db.properties` și completează `db.url`, `db.user` si `db.password`.
6. Rulează clasa `main.Main`.

### Linie de comandă

Exemplu din PowerShell:

```powershell
javac -d out -cp "cale\spre\postgresql-42.7.x.jar" (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp "out;cale\spre\postgresql-42.7.x.jar" main.Main
```

Pe Linux/macOS, separatorul pentru classpath este `:` in loc de `;`.