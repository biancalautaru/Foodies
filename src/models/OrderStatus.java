package models;

public enum OrderStatus {
    PENDING,
    ACCEPTED,
    DRIVER_ASSIGNED,
    PREPARING,
    READY_FOR_PICKUP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}