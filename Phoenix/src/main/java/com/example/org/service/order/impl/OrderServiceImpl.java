package com.example.org.service.order.impl;

import com.example.org.config.springevent.EventPublisher;
import com.example.org.entity.Order;
import com.example.org.entity.OrderStatus;
import com.example.org.event.order.OrderCancelEvent;
import com.example.org.exception.OrderDomainException;
import com.example.org.repository.OrderRepository;
import com.example.org.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Override
    public void createOrder() {

    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderDomainException("해당 주문번호 : " + orderId + "가 존재하지 않습니다."));

        order.cancel();
        orderRepository.save(order);

        EventPublisher.publish(new OrderCancelEvent(order.getId()));
    }
}
