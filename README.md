# 🍔 Foodies

Foodies este o platformă de livrare de mâncare construită în Java. Modelează întregul ciclu de viață al unei comenzi de livrare - de la răsfoirea meniurilor și adăugarea produselor în coș de către client, prin atribuirea șoferului și livrare, până la recenziile post-livrare.

## ✨ Funcționalități

- 👤 **Gestionarea clienților și șoferilor**: Înregistrează clienți și șoferi; interogă șoferii disponibili dintr-un pool comun.
- 🍽️ **Răsfoirea restaurantelor și meniurilor**: Răsfoiește restaurante sortate după rating sau nume; vizualizează meniuri sortate după nume sau preț.
- 🛒 **Coș de cumpărături**: Adaugă produse în coș cu respectarea regulii unui singur restaurant per comandă.
- 📦 **Ciclul de viață al comenzii**: `În așteptare` -> `În preparare` -> `Gata de ridicare` -> `În livrare` -> `Livrată`, cu validarea strictă a tranzițiilor de stare.
- 🚴 **Atribuirea automată a șoferilor**: Șoferii disponibili sunt atribuiți automat când o comandă ajunge la starea `Gata de ridicare`.
- 📍 **Potrivire după oraș**: Comenzile pot fi plasate doar la un restaurant din același oraș cu adresa de livrare.
- ❌ **Anulare cu taxe**: Anulare gratuită în stările `În așteptare` sau `În preparare`; 30% din subtotal la `Gata de ridicare`; subtotal complet + taxa de livrare la `În livrare`.
- ⭐ **Recenzii**: Clienții lasă un rating de 1–5 stele și un comentariu după livrare; media rulantă a restaurantului este actualizată imediat.
- 🔁 **Recomandă**: Repetă o comandă livrată anterior cu o adresă nouă (orașul trebuie să corespundă în continuare restaurantului).
- 📋 **Jurnal de audit**: Fiecare acțiune semnificativă este adăugată în `logs/audit.csv` printr-un `AuditService` singleton.
- 💬 **Interfață consolă interactivă**: CLI în limba română (`ConsoleApp`) pentru explorarea manuală a tuturor funcționalităților.

## 📁 Structura Proiectului

```
Foodies/
├── src/
│   ├── main/
│   │   ├── Main.java          # Punct de intrare: populează date demo, rulează fluxuri de lucru, pornește ConsoleApp
│   │   └── ConsoleApp.java    # Aplicație consolă interactivă (UI în română)
│   ├── models/
│   │   ├── User.java          # Entitate de bază (id, nume, email, telefon)
│   │   ├── Customer.java      # Utilizator cu un Coș
│   │   ├── Driver.java        # Utilizator cu indicator de disponibilitate
│   │   ├── Restaurant.java    # Meniu, rating cu stele, număr de recenzii
│   │   ├── MenuItem.java      # Preț, descriere, restaurant asociat
│   │   ├── Address.java       # record(stradă, număr, oraș)
│   │   ├── Cart.java          # Coș de cumpărături pentru un singur restaurant
│   │   ├── Order.java         # Articole, stare, logica taxei de anulare, clonare/recomandă
│   │   ├── OrderStatus.java   # PENDING -> ... -> DELIVERED | CANCELLED
│   │   └── Review.java        # record(client, comandă, rating, comentariu, timestamp)
│   ├── service/
│   │   ├── UserService.java        # Registru clienți/șoferi; findAvailableDriver()
│   │   ├── RestaurantService.java  # Registru restaurante/articole meniu; helper-e de afișare sortată
│   │   ├── MenuService.java        # Interogări meniu delegate către RestaurantService
│   │   ├── OrderService.java       # Flux complet al comenzii (plasare → livrare, anulare, recenzie, recomandă)
│   │   └── AuditService.java       # Logger CSV singleton → logs/audit.csv
│   └── exceptions/
│       ├── FoodiesException.java        # Excepție de bază Runtime
│       ├── EntityNotFoundException.java # Restaurant/comandă/etc. lipsă
│       └── InvalidOrderException.java   # Încălcări ale regulilor de business
└── logs/
    └── audit.csv              # Jurnal de acțiuni doar pentru adăugare (creat la runtime)
```

## 🛠️ Stack Tehnologic

| | |
|---|---|
| ☕ **Limbaj** | Java 21 |
| 🏗️ **Build** | IntelliJ IDEA (modul Java simplu, fără Maven/Gradle) |
| 💾 **Persistență** | Colecții în memorie + `logs/audit.csv` |
| 📦 **Dependențe** | Doar biblioteca standard |

## 🚀 Pornire Rapidă

### Cerințe prealabile

- ☕ JDK 21 (ex. Azul Zulu 21)
- 💡 IntelliJ IDEA (recomandat)

### ▶️ Rulare în IntelliJ

1. Deschide folderul `Foodies` ca proiect.
2. Asigură-te că SDK-ul este setat la JDK 21 (**File → Project Structure → SDK**).
3. Rulează `main.Main`.

Programul populează restaurante, clienți, șoferi și comenzi demo, execută o prezentare generală scriptată, apoi lansează consola interactivă.

### ⌨️ Rulare din linia de comandă

```powershell
# Din rădăcina repo-ului — compilare
javac -d out -sourcepath src (Get-ChildItem -Recurse src -Filter *.java | % { $_.FullName })

# Rulare (directorul de lucru trebuie să fie rădăcina repo-ului pentru ca logs/ să fie creat aici)
java -cp out main.Main
```