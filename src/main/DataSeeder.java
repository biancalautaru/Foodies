package main;

import interfaces.IMenuService;
import interfaces.IOrderService;
import interfaces.IRestaurantService;
import interfaces.IUserService;
import models.*;

public class DataSeeder {

    public static void seed(IUserService userService, IRestaurantService restaurantService, IMenuService menuService, IOrderService orderService) {
        // Curieri
        userService.addDriver(new Driver("D1", "Andrei Dumitrescu", "andrei.d@gmail.com", "0725658576"));
        userService.addDriver(new Driver("D2", "Elena Vasile",      "elena.v@yahoo.com",  "0774524567"));
        userService.addDriver(new Driver("D3", "Mihai Popa",        "mihai.p@gmail.com",  "0762345678"));

        // Clienti
        Customer maria = new Customer("C2", "Maria Ionescu",    "maria.ionescu@gmail.com", "0734565371");
        Customer radu  = new Customer("C6", "Radu Munteanu",    "radu.m@yahoo.com",        "0756781234");
        Customer ioana = new Customer("C7", "Ioana Constantin", "ioana.c@gmail.com",       "0743219876");
        userService.addCustomer(maria);
        userService.addCustomer(radu);
        userService.addCustomer(ioana);

        // Restaurante
        Restaurant pizzaPlace = new Restaurant("R1", "Pizza La Mama",   new Address("Str. Amzei",           "12", "București"));
        Restaurant grillPlace = new Restaurant("R2", "Grill & Burger",  new Address("Bd. Unirii",           "45", "București"));
        Restaurant sushiPlace = new Restaurant("R3", "Sakura Sushi",    new Address("Str. Calea Victoriei", "88", "București"));
        Restaurant veganPlace = new Restaurant("R4", "Verde & Sănătos", new Address("Str. Episcopiei",      "5",  "București"));
        Restaurant tacosPlace = new Restaurant("R5", "El Rancho Tacos", new Address("Bd. Magheru",          "31", "București"));
        restaurantService.addRestaurant(pizzaPlace);
        restaurantService.addRestaurant(grillPlace);
        restaurantService.addRestaurant(sushiPlace);
        restaurantService.addRestaurant(veganPlace);
        restaurantService.addRestaurant(tacosPlace);

        // Meniu R1
        restaurantService.addMenuItemToRestaurant("R1", new MenuItem("M1", "Pizza Margherita",   "Blat pizza, sos de roșii, mozzarella, busuioc",                   36.99));
        restaurantService.addMenuItemToRestaurant("R1", new MenuItem("M2", "Pizza Diavola",      "Blat pizza, sos de roșii, mozzarella, salam picant",              42.99));
        restaurantService.addMenuItemToRestaurant("R1", new MenuItem("M3", "Salată Caesar",      "Salată verde, dressing Caesar",                                   28.99));
        restaurantService.addMenuItemToRestaurant("R1", new MenuItem("M7", "Calzone Prosciutto", "Blat pizza împăturit, sos de roșii, șuncă, mozzarella, ciuperci", 44.99));
        restaurantService.addMenuItemToRestaurant("R1", new MenuItem("M8", "Tiramisu",           "Desert italian clasic cu mascarpone",                             22.99));

        // Meniu R2
        MenuItem burger1 = new MenuItem("M4",  "Burger Clasic",   "Vită, salată, roșii",               32.99);
        MenuItem burger2 = new MenuItem("M5",  "Burger Deluxe",   "Dublu cheddar, bacon",               45.99);
        MenuItem fries   = new MenuItem("M6",  "Cartofi prăjiți", "Porție mare, crocant",               16.99);
        restaurantService.addMenuItemToRestaurant("R2", burger1);
        restaurantService.addMenuItemToRestaurant("R2", burger2);
        restaurantService.addMenuItemToRestaurant("R2", fries);
        restaurantService.addMenuItemToRestaurant("R2", new MenuItem("M9",  "Aripioare BBQ", "Aripioare de pui glazurate, sos BBQ afumat", 38.99));
        restaurantService.addMenuItemToRestaurant("R2", new MenuItem("M10", "Coleslaw",      "Varză albă, morcov, maioneză ușoară",        12.99));

        // Meniu R3
        MenuItem sushiSalmon = new MenuItem("M11", "Salmon Nigiri (6 buc)", "Somon proaspăt pe orez, sos soia", 39.99);
        MenuItem sushiTuna   = new MenuItem("M12", "Tuna Roll (8 buc)",     "Ton, avocado, castraveți, nori",   43.99);
        MenuItem miso        = new MenuItem("M13", "Supă Miso",             "Tofu, alge wakame, ceapă verde",   18.99);
        restaurantService.addMenuItemToRestaurant("R3", sushiSalmon);
        restaurantService.addMenuItemToRestaurant("R3", sushiTuna);
        restaurantService.addMenuItemToRestaurant("R3", miso);
        restaurantService.addMenuItemToRestaurant("R3", new MenuItem("M14", "Edamame",             "Soia fiartă cu sare grunjoasă",               14.99));
        restaurantService.addMenuItemToRestaurant("R3", new MenuItem("M15", "Ebi Tempura (4 buc)", "Creveți în aluat crocant, sos ponzu",         49.99));

        // Meniu R4
        MenuItem buddhaVegan = new MenuItem("M16", "Buddha Bowl Vegan", "Quinoa, năut, avocado, legume la cuptor",     37.99);
        MenuItem smoothie    = new MenuItem("M17", "Smoothie Verde",    "Spanac, banană, ghimbir, lapte de cocos",     19.99);
        MenuItem hummus      = new MenuItem("M18", "Humus cu Pită",     "Humus de casă, pită integrală, boia afumată", 24.99);
        restaurantService.addMenuItemToRestaurant("R4", buddhaVegan);
        restaurantService.addMenuItemToRestaurant("R4", smoothie);
        restaurantService.addMenuItemToRestaurant("R4", hummus);
        restaurantService.addMenuItemToRestaurant("R4", new MenuItem("M19", "Falafel Wrap",   "Falafel crocant, tahini, salată, roșii",   31.99));
        restaurantService.addMenuItemToRestaurant("R4", new MenuItem("M20", "Tort Raw Vegan", "Dată, nuci, cacao crudă, strat mango",     26.99));

        // Meniu R5
        restaurantService.addMenuItemToRestaurant("R5", new MenuItem("M21", "Taco Carne Asada",     "Vită marinată, guacamole, coriandru",              34.99));
        restaurantService.addMenuItemToRestaurant("R5", new MenuItem("M22", "Taco Pollo",           "Pui la grătar, salsa verde, smântână",             31.99));
        restaurantService.addMenuItemToRestaurant("R5", new MenuItem("M23", "Nachos cu Chili",      "Tortilla, fasole neagră, cheddar topit",           27.99));
        restaurantService.addMenuItemToRestaurant("R5", new MenuItem("M24", "Quesadilla Mixta",     "Pui, ardei, cheddar, smântână",                    33.99));
        restaurantService.addMenuItemToRestaurant("R5", new MenuItem("M25", "Churros cu Ciocolată", "Churros prăjiți, sos de ciocolată neagră",         21.99));

        // Comenzi
        // #1 Maria — R2: PENDING
        maria.getCart().addItem(burger1);
        maria.getCart().addItem(burger2);
        maria.getCart().addItem(fries);
        orderService.placeOrder(maria, new Address("Str. Floreasca", "7", "București"));

        // #2 Radu — R3: DELIVERED + recenzie
        radu.getCart().addItem(sushiSalmon);
        radu.getCart().addItem(sushiTuna);
        radu.getCart().addItem(miso);
        orderService.placeOrder(radu, new Address("Str. Ion Câmpineanu", "10", "București"));
        orderService.confirmOrder("#2");
        orderService.markOrderReady("#2");
        orderService.pickupOrder("#2");
        orderService.deliverOrder("#2");
        orderService.submitReview("#2", 5, "Sushi proaspăt și bine prezentat, recomand!");

        // #3 Ioana — R4: DELIVERED + recenzie
        ioana.getCart().addItem(buddhaVegan);
        ioana.getCart().addItem(smoothie);
        ioana.getCart().addItem(hummus);
        orderService.placeOrder(ioana, new Address("Str. Batiștei", "3", "București"));
        orderService.confirmOrder("#3");
        orderService.markOrderReady("#3");
        orderService.pickupOrder("#3");
        orderService.deliverOrder("#3");
        orderService.submitReview("#3", 4, "Mâncare sănătoasă și gustoasă, smoothie-ul e delicios.");
    }
}