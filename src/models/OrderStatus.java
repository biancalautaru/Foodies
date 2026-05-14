package models;

public enum OrderStatus {
    PENDING("În așteptare"),
    PREPARING("În preparare"),
    READY_FOR_PICKUP("Gata de ridicare"),
    OUT_FOR_DELIVERY("În livrare"),
    DELIVERED("Livrată"),
    CANCELLED("Anulată");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
