import exceptions.*;
import models.*;
import service.*;

public class Main {
    public static void main(String[] args) {
        // --- Inițializare servicii ---
        UserService userService = new UserService();
        RestaurantService restaurantService = new RestaurantService();
        MenuService menuService = new MenuService(restaurantService);
        OrderService orderService = new OrderService(userService, restaurantService);

        System.out.println("=== Înregistrare clienți ===");
        Customer customer1 = new Customer("C1", "Ion Popescu", "ion.popescu@gmail.com", "0721564326");
        Customer customer2 = new Customer("C2", "Maria Ionescu", "maria.ionescu@gmail.com", "0734565371");
        Customer customer3 = new Customer("C6", "Radu Munteanu", "radu.m@yahoo.com", "0756781234");
        Customer customer4 = new Customer("C7", "Ioana Constantin", "ioana.c@gmail.com", "0743219876");
        userService.addCustomer(customer1);
        userService.addCustomer(customer2);
        userService.addCustomer(customer3);
        userService.addCustomer(customer4);

        System.out.println("\n=== Înregistrare curieri ===");
        Driver driver1 = new Driver("D1", "Andrei Dumitrescu", "andrei.d@gmail.com", "0725658576");
        Driver driver2 = new Driver("D2", "Elena Vasile", "elena.v@yahoo.com", "0774524567");
        Driver driver3 = new Driver("D3", "Mihai Popa", "mihai.p@gmail.com", "0762345678");
        userService.addDriver(driver1);
        userService.addDriver(driver2);
        userService.addDriver(driver3);

        System.out.println("\n=== Restaurante și adrese (București) ===");
        Address addrPizza    = new Address("Str. Amzei", "12", "București");
        Address addrGrill    = new Address("Bd. Unirii", "45", "București");
        Address addrSushi    = new Address("Str. Calea Victoriei", "88", "București");
        Address addrVegan    = new Address("Str. Episcopiei", "5", "București");
        Address addrTacos    = new Address("Bd. Magheru", "31", "București");

        Restaurant pizzaPlace = new Restaurant("R1", "Pizza La Mama", addrPizza);
        Restaurant grillPlace = new Restaurant("R2", "Grill & Burger București", addrGrill);
        Restaurant sushiPlace = new Restaurant("R3", "Sakura Sushi", addrSushi);
        Restaurant veganPlace = new Restaurant("R4", "Verde & Sănătos", addrVegan);
        Restaurant tacosPlace = new Restaurant("R5", "El Rancho Tacos", addrTacos);
        restaurantService.addRestaurant(pizzaPlace);
        restaurantService.addRestaurant(grillPlace);
        restaurantService.addRestaurant(sushiPlace);
        restaurantService.addRestaurant(veganPlace);
        restaurantService.addRestaurant(tacosPlace);

        System.out.println("\n=== Articole meniu ===");
        // R1 – Pizza La Mama
        MenuItem pizza1      = new MenuItem("M1", "Pizza Margherita", "Blat pizza, sos de roșii, mozzarella, busuioc", 36.99);
        MenuItem pizza2      = new MenuItem("M2", "Pizza Diavola", "Blat pizza, sos de roșii, mozzarella, salam picant", 42.99);
        MenuItem salad       = new MenuItem("M3", "Salată Caesar", "Salată verde, dressing Caesar", 28.99);
        MenuItem calzone     = new MenuItem("M7", "Calzone Prosciutto", "Blat pizza împăturit, sos de roșii, șuncă, mozzarella, ciuperci", 44.99);
        MenuItem tiramisu    = new MenuItem("M8", "Tiramisu", "Desert italian clasic cu mascarpone", 22.99);

        // R2 – Grill & Burger
        MenuItem burger1     = new MenuItem("M4", "Burger Clasic", "Vită, salată, roșii", 32.99);
        MenuItem burger2     = new MenuItem("M5", "Burger Deluxe", "Dublu cheddar, bacon", 45.99);
        MenuItem fries       = new MenuItem("M6", "Cartofi prăjiți", "Porție mare, crocant", 16.99);
        MenuItem chickenWing = new MenuItem("M9", "Aripioare BBQ", "Aripioare de pui glazurate, sos BBQ afumat", 38.99);
        MenuItem coleslaw    = new MenuItem("M10", "Coleslaw", "Varză albă, morcov, maioneză ușoară", 12.99);

        // R3 – Sakura Sushi
        MenuItem sushiSalmon = new MenuItem("M11", "Salmon Nigiri (6 buc)", "Somon proaspăt pe orez, sos soia", 39.99);
        MenuItem sushiTuna   = new MenuItem("M12", "Tuna Roll (8 buc)", "Ton, avocado, castraveți, nori", 43.99);
        MenuItem miso        = new MenuItem("M13", "Supă Miso", "Tofu, alge wakame, ceapă verde", 18.99);
        MenuItem edamame     = new MenuItem("M14", "Edamame", "Soia fiartă cu sare grunjoasă", 14.99);
        MenuItem tempura     = new MenuItem("M15", "Ebi Tempura (4 buc)", "Creveți în aluat crocant, sos ponzu", 49.99);

        // R4 – Verde & Sănătos
        MenuItem buddhaVegan = new MenuItem("M16", "Buddha Bowl Vegan", "Quinoa, năut, avocado, legume la cuptor", 37.99);
        MenuItem smoothie    = new MenuItem("M17", "Smoothie Verde", "Spanac, banană, ghimbir, lapte de cocos", 19.99);
        MenuItem hummus      = new MenuItem("M18", "Humus cu Pită", "Humus de casă, pită integrală, boia afumată", 24.99);
        MenuItem falafels    = new MenuItem("M19", "Falafel Wrap", "Falafel crocant, tahini, salată, roșii", 31.99);
        MenuItem rawCake     = new MenuItem("M20", "Tort Raw Vegan", "Dată, nuci, cacao crudă, strat mango", 26.99);

        // R5 – El Rancho Tacos
        MenuItem tacoBeef    = new MenuItem("M21", "Taco Carne Asada", "Vită marinată, guacamole, coriandru", 34.99);
        MenuItem tacoChicken = new MenuItem("M22", "Taco Pollo", "Pui la grătar, salsa verde, smântână", 31.99);
        MenuItem nachos      = new MenuItem("M23", "Nachos cu Chili", "Tortilla, fasole neagră, cheddar topit", 27.99);
        MenuItem quesadilla  = new MenuItem("M24", "Quesadilla Mixta", "Pui, ardei, cheddar, smântână", 33.99);
        MenuItem churros     = new MenuItem("M25", "Churros cu Ciocolată", "Churros prăjiți, sos de ciocolată neagră", 21.99);

        restaurantService.addMenuItemToRestaurant("R1", pizza1);
        restaurantService.addMenuItemToRestaurant("R1", pizza2);
        restaurantService.addMenuItemToRestaurant("R1", salad);
        restaurantService.addMenuItemToRestaurant("R1", calzone);
        restaurantService.addMenuItemToRestaurant("R1", tiramisu);

        restaurantService.addMenuItemToRestaurant("R2", burger1);
        restaurantService.addMenuItemToRestaurant("R2", burger2);
        restaurantService.addMenuItemToRestaurant("R2", fries);
        restaurantService.addMenuItemToRestaurant("R2", chickenWing);
        restaurantService.addMenuItemToRestaurant("R2", coleslaw);

        restaurantService.addMenuItemToRestaurant("R3", sushiSalmon);
        restaurantService.addMenuItemToRestaurant("R3", sushiTuna);
        restaurantService.addMenuItemToRestaurant("R3", miso);
        restaurantService.addMenuItemToRestaurant("R3", edamame);
        restaurantService.addMenuItemToRestaurant("R3", tempura);

        restaurantService.addMenuItemToRestaurant("R4", buddhaVegan);
        restaurantService.addMenuItemToRestaurant("R4", smoothie);
        restaurantService.addMenuItemToRestaurant("R4", hummus);
        restaurantService.addMenuItemToRestaurant("R4", falafels);
        restaurantService.addMenuItemToRestaurant("R4", rawCake);

        restaurantService.addMenuItemToRestaurant("R5", tacoBeef);
        restaurantService.addMenuItemToRestaurant("R5", tacoChicken);
        restaurantService.addMenuItemToRestaurant("R5", nachos);
        restaurantService.addMenuItemToRestaurant("R5", quesadilla);
        restaurantService.addMenuItemToRestaurant("R5", churros);

        System.out.println("\n=== Descoperire restaurante (lista completă) ===");
        restaurantService.displayAllRestaurants();

        System.out.println("=== Toate restaurantele sortate alfabetic ===");
        restaurantService.displayRestaurantsSortedByName();

        System.out.println("=== Toate restaurantele sortate după rating ===");
        restaurantService.displayRestaurantsSortedByRating();

        System.out.println("\n=== Meniuri restaurante ===");
        restaurantService.displayRestaurantMenu("R1");
        menuService.displayMenuSortedByName("R1");
        menuService.displayMenuSortedByPrice("R1");

        restaurantService.displayRestaurantMenu("R3");
        menuService.displayMenuSortedByPrice("R3");

        restaurantService.displayRestaurantMenu("R4");
        menuService.displayMenuSortedByPrice("R4");

        restaurantService.displayRestaurantMenu("R5");
        menuService.displayMenuSortedByPrice("R5");

        System.out.println("\n=== Clientul 1 plasează comanda (R1 – Pizza) ===");
        Cart cart1 = customer1.getCart();
        cart1.addItem(pizza1);
        cart1.addItem(pizza2);
        cart1.addItem(salad);

        Address deliveryAddr1 = new Address("Str. Dorobanți", "18", "București");
        orderService.placeOrder(customer1, deliveryAddr1);

        System.out.println("\n=== Clientul 2 plasează comanda (R2 – Grill) ===");
        Cart cart2 = customer2.getCart();
        cart2.addItem(burger1);
        cart2.addItem(burger2);
        cart2.addItem(fries);

        Address deliveryAddr2 = new Address("Str. Fabrica de Glucoză", "9", "București");
        orderService.placeOrder(customer2, deliveryAddr2);

        System.out.println("\n=== Clientul 3 plasează comanda (R3 – Sushi) ===");
        Cart cartSushi = customer3.getCart();
        cartSushi.addItem(sushiSalmon);
        cartSushi.addItem(sushiTuna);
        cartSushi.addItem(miso);
        cartSushi.addItem(edamame);
        orderService.placeOrder(customer3, new Address("Str. Ion Câmpineanu", "10", "București"));

        System.out.println("\n=== Clientul 4 plasează comanda (R4 – Vegan) ===");
        Cart cartVegan = customer4.getCart();
        cartVegan.addItem(buddhaVegan);
        cartVegan.addItem(smoothie);
        cartVegan.addItem(hummus);
        orderService.placeOrder(customer4, new Address("Str. Batiștei", "3", "București"));

        System.out.println("\n=== Clientul 3 plasează o a doua comandă (R5 – Tacos) ===");
        Cart cartTacos = customer3.getCart();
        cartTacos.addItem(tacoBeef);
        cartTacos.addItem(nachos);
        cartTacos.addItem(churros);
        orderService.placeOrder(customer3, new Address("Str. Aviatorilor", "22", "București"));

        System.out.println("\n=== Acceptare toate comenzile (declanșează asignarea curierilor) ===");
        orderService.acceptOrder("#1");
        orderService.acceptOrder("#2");
        orderService.acceptOrder("#3");
        orderService.acceptOrder("#4");
        orderService.acceptOrder("#5");

        System.out.println("\n=== Procesare comandă #1 (Pizza La Mama) ===");
        orderService.startOrderPreparation("#1");
        orderService.markOrderReady("#1");
        orderService.pickupOrder("#1");
        orderService.deliverOrder("#1");

        System.out.println("\n=== Procesare comandă #2 (Grill & Burger) ===");
        orderService.startOrderPreparation("#2");
        orderService.markOrderReady("#2");
        orderService.pickupOrder("#2");
        orderService.deliverOrder("#2");

        System.out.println("\n=== Procesare comandă #3 (Sakura Sushi) ===");
        orderService.startOrderPreparation("#3");
        orderService.markOrderReady("#3");
        orderService.pickupOrder("#3");
        orderService.deliverOrder("#3");

        System.out.println("\n=== Procesare comandă #4 (Verde & Sănătos) ===");
        orderService.startOrderPreparation("#4");
        orderService.markOrderReady("#4");
        orderService.pickupOrder("#4");
        orderService.deliverOrder("#4");

        System.out.println("\n=== Procesare comandă #5 (El Rancho Tacos) ===");
        orderService.startOrderPreparation("#5");
        orderService.markOrderReady("#5");
        orderService.pickupOrder("#5");
        orderService.deliverOrder("#5");

        System.out.println("\n=== Recenzii după livrare ===");
        orderService.submitReview("#1", 5, "Pizza excelentă, livrare rapidă!");
        orderService.submitReview("#2", 4, "Burger bun, dar cartofii erau reci.");
        orderService.submitReview("#3", 5, "Sushi proaspăt și bine prezentat, recomand!");
        orderService.submitReview("#4", 4, "Mâncare sănătoasă și gustoasă, smoothie-ul e delicios.");
        orderService.submitReview("#5", 3, "Tacos buni, dar nachos-ul era cam sărat.");

        System.out.println("\n=== Sortare restaurante după rating (după recenzii) ===");
        restaurantService.displayRestaurantsSortedByRating();

        System.out.println("\n=== Detalii comenzi + recenzii ===");
        orderService.displayOrderDetails("#1");
        orderService.displayOrderDetails("#2");
        orderService.displayOrderDetails("#3");
        orderService.displayOrderDetails("#4");
        orderService.displayOrderDetails("#5");

        System.out.println("\n=== Recenzii pe restaurant ===");
        orderService.displayRestaurantReviews("R1");
        orderService.displayRestaurantReviews("R2");
        orderService.displayRestaurantReviews("R3");
        orderService.displayRestaurantReviews("R4");
        orderService.displayRestaurantReviews("R5");

        System.out.println("\n=== Istoric comenzi clienți ===");
        orderService.getCustomerOrderHistory("C1");
        orderService.getCustomerOrderHistory("C2");
        orderService.getCustomerOrderHistory("C6");
        orderService.getCustomerOrderHistory("C7");

        System.out.println("\n=== Anulare cu taxă (după ce curierul a fost asignat) ===");
        Cart cart6 = customer1.getCart();
        cart6.addItem(pizza1);
        orderService.placeOrder(customer1, new Address("Str. Test", "7", "București"));
        orderService.acceptOrder("#6");
        System.out.println("Anulăm comanda #6 în starea cu curier asignat (taxă 25,00 lei conform Order.cancelOrder).");
        orderService.cancelOrder("#6");

        System.out.println("\n=== Scenarii de eroare ===");

        System.out.println("Încercare recenzie duplicată pentru comanda #1:");
        try {
            orderService.submitReview("#1", 3, "Mă răzgândesc...");
        } catch (InvalidReviewException e) {
            System.out.println("Eroare (recenzie invalidă): " + e.getMessage());
        }

        System.out.println("\nRecenzie înainte de livrare (comandă #7 acceptată, încă nu livrată):");
        Cart cart7 = customer1.getCart();
        cart7.addItem(pizza2);
        orderService.placeOrder(customer1, new Address("Str. Recenzie", "3", "București"));
        orderService.acceptOrder("#7");
        try {
            orderService.submitReview("#7", 5, "Prea devreme pentru recenzie!");
        } catch (InvalidReviewException e) {
            System.out.println("Eroare (recenzie invalidă): " + e.getMessage());
        }

        System.out.println("\nLivrare fără ridicare de la restaurant (tranziție invalidă) — comanda #7:");
        try {
            orderService.deliverOrder("#7");
        } catch (InvalidOrderStateException e) {
            System.out.println("Eroare (stare comandă invalidă): " + e.getMessage());
        }

        System.out.println("\nRestaurant inexistent (afișare meniu sortat):");
        try {
            menuService.displayMenuSortedByName("R999");
        } catch (RestaurantNotFoundException e) {
            System.out.println("Eroare (restaurant negăsit): " + e.getMessage());
        }

        System.out.println("\nComandă cu coș gol:");
        try {
            orderService.placeOrder(
                    new Customer("C8", "Utilizator Coș Gol", "gol@email.ro", "0700-000-000"), deliveryAddr1);
        } catch (EmptyCartException e) {
            System.out.println("Eroare (coș gol): " + e.getMessage());
        }

        System.out.println("\nOraș livrare diferit de orașul restaurantului:");
        Customer testCustomer = new Customer("C9", "Test Oraș", "test@email.ro", "0700-111-111");
        testCustomer.getCart().addItem(pizza1);
        try {
            orderService.placeOrder(testCustomer, new Address("Str. Departe", "1", "Cluj-Napoca"));
        } catch (DeliveryAddressMismatchException e) {
            System.out.println("Eroare (adresă livrare nepotrivită): " + e.getMessage());
        }

        System.out.println("\nArticole din restaurante diferite în același coș:");
        Customer mixedCartCustomer = new Customer("C10", "Coș Mixt", "mixt@email.ro", "0700-222-222");
        mixedCartCustomer.getCart().addItem(pizza1);
        try {
            mixedCartCustomer.getCart().addItem(burger1);
        } catch (MixedRestaurantCartException e) {
            System.out.println("Eroare (coș cu restaurante diferite): " + e.getMessage());
        }

        System.out.println("\nAnulare când comanda e deja în livrare:");
        Cart cart8 = customer2.getCart();
        cart8.addItem(burger1);
        orderService.placeOrder(customer2, new Address("Str. Anulare", "6", "București"));
        orderService.acceptOrder("#8");
        orderService.startOrderPreparation("#8");
        orderService.markOrderReady("#8");
        orderService.pickupOrder("#8");
        try {
            orderService.cancelOrder("#8");
        } catch (OrderCancellationException e) {
            System.out.println("Eroare (anulare comandă): " + e.getMessage());
        }

        System.out.println("\n=== Finalizare comenzi rămase înainte de re-comandă ===");
        orderService.deliverOrder("#8");
        orderService.startOrderPreparation("#7");
        orderService.markOrderReady("#7");
        orderService.pickupOrder("#7");
        orderService.deliverOrder("#7");

        System.out.println("\n=== Re-comandă dintr-o comandă livrată (aceleași articole, adresă nouă) ===");
        System.out.println("Clientul 1 reia comanda #1 la o adresă nouă:");
        orderService.reorder(customer1, "#1", new Address("Str. Nouă", "42", "București"));
        orderService.acceptOrder("#9");
        orderService.startOrderPreparation("#9");
        orderService.markOrderReady("#9");
        orderService.pickupOrder("#9");
        orderService.deliverOrder("#9");

        System.out.println("\n=== Erori la re-comandă ===");

        System.out.println("Alt client încearcă să reia comanda lui Ion (#1):");
        try {
            orderService.reorder(customer2, "#1", new Address("Str. Nouă", "42", "București"));
        } catch (OrderAccessDeniedException e) {
            System.out.println("Eroare (acces refuzat la comandă): " + e.getMessage());
        }

        System.out.println("\nRe-comandă din comandă anulată (#6):");
        try {
            orderService.reorder(customer1, "#6", new Address("Str. Nouă", "42", "București"));
        } catch (InvalidOrderStateException e) {
            System.out.println("Eroare (stare comandă invalidă): " + e.getMessage());
        }
    }
}
