# Foodies

Foodies este o platformă de livrare de mâncare construită în Java. Modelează întregul ciclu de viață al unei comenzi - de la răsfoirea meniurilor și adăugarea produselor în coș de către client, prin atribuirea șoferului și livrare, până la recenziile post-livrare.

## Funcționalități

- **Înregistrare la pornire**: La lansarea aplicației, utilizatorul completează un formular simplu (nume, email, telefon) și primește imediat un cont de client activ.
- **Gestionarea clienților și șoferilor**: Înregistrează clienți și șoferi; interogă șoferii disponibili dintr-un pool comun.
- **Răsfoirea restaurantelor și meniurilor**: Răsfoiește restaurante sortate după rating sau după nume; vizualizează meniuri sortate după preț (cel mai ieftin primul).
- **Recenzii restaurant**: Vizualizează toate recenziile lăsate pentru un restaurant ales.
- **Coș de cumpărături**: Adaugă produse în coș cu respectarea regulii unui singur restaurant per comandă.
- **Ciclu de viață interactiv al comenzii**: `În așteptare` → `În preparare` → `Gata de ridicare` → `În livrare` → `Livrată`, avansat pas cu pas prin apăsarea Enter; cu validarea strictă a tranzițiilor de stare.
- **Anulare de către restaurant**: La pasul de confirmare, restaurantul poate anula comanda (opțiunea `c`).
- **Atribuirea automată a șoferilor**: Șoferii disponibili sunt atribuiți automat când o comandă ajunge la starea `Gata de ridicare`.
- **Potrivire după oraș**: Orasul adresei de livrare este preluat automat din orașul restaurantului ales; utilizatorul introduce doar strada și numărul.
- **Anulare cu taxe**: Anulare gratuită în stările `În așteptare` sau `În preparare`; 30% din subtotal la `Gata de ridicare`; subtotal complet + taxa de livrare la `În livrare`.
- **Recenzii**: Clienții lasă un rating de 1–5 stele și un comentariu după livrare; media rulantă a restaurantului este actualizată imediat.
- **Repetă comandă**: Repetă o comandă livrată anterior cu o adresă nouă (orașul trebuie să corespundă în continuare restaurantului).
- **Detalii comandă**: Vizualizează produsele, subtotalul, taxa de livrare, totalul și recenzia aferentă oricărei comenzi proprii.
- **Jurnal de audit**: Fiecare acțiune semnificativă este înregistrată în `logs/audit.csv` printr-un `AuditService` singleton.
- **Interfață consolă interactivă**: CLI în limba română (`ConsoleApp`) cu meniu principal pe două niveluri (Restaurante / Comenzi).

## Structura Proiectului

```
Foodies/
├── src/
│   ├── main/
│   │   ├── Main.java          # Punct de intrare: inițializează serviciile și pornește ConsoleApp
│   │   ├── ConsoleApp.java    # Aplicație consolă interactivă (UI în română, meniu pe două niveluri)
│   │   └── DataSeeder.java    # Populează date demo (restaurante, meniuri, clienți, șoferi, comenzi)
│   ├── interfaces/
│   │   ├── Displayable.java       # Contract toDisplayString() pentru modele afișabile
│   │   ├── Reviewable.java        # Contract addReview / getAverageRating / getReviewCount
│   │   ├── IUserService.java      # Operații pe utilizatori și șoferi
│   │   ├── IRestaurantService.java# Operații pe restaurante și meniuri
│   │   ├── IMenuService.java      # Interogări meniu sortate
│   │   └── IOrderService.java     # Flux complet al comenzii
│   ├── models/
│   │   ├── User.java          # Entitate de bază abstractă (id, nume, email, telefon)
│   │   ├── Customer.java      # Utilizator cu un Coș
│   │   ├── Driver.java        # Utilizator cu indicator de disponibilitate
│   │   ├── Restaurant.java    # Implementează Reviewable și Displayable; meniu, rating cu stele
│   │   ├── MenuItem.java      # Implementează Displayable; preț, descriere, restaurant asociat
│   │   ├── Address.java       # record(stradă, număr, oraș)
│   │   ├── Cart.java          # Coș de cumpărături pentru un singur restaurant
│   │   ├── Order.java         # Implementează Displayable; articole, stare, logica taxei de anulare, clonare/repetă
│   │   ├── OrderStatus.java   # PENDING → PREPARING → READY_FOR_PICKUP → OUT_FOR_DELIVERY → DELIVERED | CANCELLED
│   │   └── Review.java        # record(client, comandă, rating, comentariu, timestamp)
│   ├── service/
│   │   ├── UserService.java        # Registru clienți/șoferi; findAvailableDriver()
│   │   ├── RestaurantService.java  # Registru restaurante/articole meniu; helper-e de afișare sortată
│   │   ├── MenuService.java        # Interogări meniu delegate către RestaurantService
│   │   ├── OrderService.java       # Flux complet al comenzii (plasare → livrare, anulare, recenzie, repetă)
│   │   └── AuditService.java       # Logger CSV singleton → logs/audit.csv
│   └── exceptions/
│       ├── FoodiesException.java        # Excepție de bază Runtime
│       ├── EntityNotFoundException.java # Restaurant/comandă/etc. lipsă
│       └── InvalidOrderException.java   # Încălcări ale regulilor de business
└── logs/
    └── audit.csv              # Jurnal de acțiuni doar pentru adăugare (creat la runtime)
```

## Stack Tehnologic

| | |
|---|---|
| **Limbaj** | Java 21 |
| **Build** | IntelliJ IDEA (modul Java simplu, fără Maven/Gradle) |
| **Persistență** | Colecții în memorie + `logs/audit.csv` |
| **Dependențe** | Doar biblioteca standard |

## Pornire Rapidă

### Cerințe prealabile

- JDK 21 (ex. Azul Zulu 21)
- IntelliJ IDEA (recomandat)

### Rulare în IntelliJ

1. Deschide folderul `Foodies` ca proiect.
2. Asigură-te că SDK-ul este setat la JDK 21 (**File → Project Structure → SDK**).
3. Rulează `main.Main`.

La pornire, programul populează date demo (restaurante, meniuri, clienți, șoferi și câteva comenzi exemplu), apoi te invită să te înregistrezi și lansează consola interactivă.

### Rulare din linia de comandă

```powershell
# Din rădăcina repo-ului — compilare
javac -d out -sourcepath src (Get-ChildItem -Recurse src -Filter *.java | % { $_.FullName })

# Rulare (directorul de lucru trebuie să fie rădăcina repo-ului pentru ca logs/ să fie creat aici)
java -cp out main.Main
```

## Fluxul de Utilizare

1. **Înregistrare** — la pornire, introdu numele, emailul și telefonul pentru a crea un cont.
2. **Meniu principal** — alege între `Restaurante` și `Comenzi`.
3. **Restaurante** — vizualizează lista sortată, explorează meniul unui restaurant (sortat după preț) sau citește recenziile acestuia.
4. **Plasare comandă** — alege restaurantul, selectează produsele după număr (ex. `1,3`), confirmă strada și numărul de livrare.
5. **Ciclu de viață** — avansează manual fiecare etapă cu Enter (confirmare restaurant → gata → ridicare curier → livrare); la confirmare poți tasta `c` pentru anulare din partea restaurantului.
6. **Recenzie** — după livrare ești invitat să lași un rating și un comentariu; poți reveni oricând din submeniul Comenzi.
7. **Repetă comandă** — alege o comandă livrată anterior și furnizează o nouă adresă de livrare (în același oraș).