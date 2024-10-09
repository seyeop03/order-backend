package com.example.org.service.order;

public interface OrderService {
    void createOrder();
    void cancelOrder(Long orderId);
}
