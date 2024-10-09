package com.example.org.event.order;

public class OrderCancelEvent {

    private Long orderId;

    public OrderCancelEvent(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }
}
