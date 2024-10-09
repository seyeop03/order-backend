package com.example.org.service.order.impl;

import com.example.org.entity.Delivery;
import com.example.org.entity.DeliveryStatus;
import com.example.org.entity.Order;
import com.example.org.exception.OrderDomainException;
import com.example.org.repository.DeliveryRepository;
import com.example.org.repository.OrderRepository;
import com.example.org.service.order.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;

    @Override
    @Transactional
    public void cancelDelivery(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderDomainException("No Order Entity"));

        Delivery delivery = deliveryRepository.findById(order.getDelivery().getId())
                .orElseThrow(() -> new OrderDomainException("No Delivery Entity"));

        if(delivery.getStatus() != DeliveryStatus.READY) {
            throw new OrderDomainException("배송 준비 중이 아니라 주문을 취소할 수 없습니다");
        }

        delivery.setStatus(DeliveryStatus.CANCELLED);
        delivery.setIsDeleted(true);
        deliveryRepository.save(delivery);
        log.info("주문번호 : {}, 배송취소 완료", orderId);
    }
}
