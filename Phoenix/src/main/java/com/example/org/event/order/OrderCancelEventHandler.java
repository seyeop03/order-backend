package com.example.org.event.order;

import com.example.org.service.order.DeliveryService;
import com.example.org.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCancelEventHandler {

    private final OrderService orderService;
    private final DeliveryService deliveryService;

    @Async
    @EventListener(OrderCancelEvent.class)
    public void handle(OrderCancelEvent event) {
        deliveryService.cancelDelivery(event.getOrderId());
    }
}
