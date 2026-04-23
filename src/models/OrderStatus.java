package models;

public enum OrderStatus {
    PENDING,
    ACCEPTED,
    DRIVER_FOUND,
    PREPARING,
    READY_FOR_PICKUP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}