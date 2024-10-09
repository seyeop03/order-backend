package com.example.org.service;

import com.example.org.config.springevent.EventPublisher;
import com.example.org.entity.*;
import com.example.org.event.order.OrderCancelEvent;
import com.example.org.exception.DomainException;
import com.example.org.repository.DeliveryRepository;
import com.example.org.repository.MemberRepository;
import com.example.org.repository.OrderRepository;
import com.example.org.service.order.DeliveryService;
import com.example.org.service.order.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Autowired
    DeliveryService deliveryService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    DeliveryRepository deliveryRepository;

    @BeforeAll
    public void setUp() {
        Member member1 = new Member();
        Member member2 = new Member();

        member1.setName("test1");
        member2.setName("test2");
        memberRepository.save(member1);
        memberRepository.save(member2);
    }

    @Test
    @DisplayName("주문취소 서비스 테스트")
    void testCancelOrder() {
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new DomainException("No User"));

        Order order = new Order();
        order.setMember(member);
        order.setStatus(OrderStatus.PAID);

        Delivery delivery = new Delivery();
        delivery.setStatus(DeliveryStatus.READY);
        Delivery savedDelivery = deliveryRepository.save(delivery);
        order.setDelivery(savedDelivery);
        Order paidOrder = orderRepository.save(order);


        log.info("Before canceling order status : {}", paidOrder.getStatus());
        orderService.cancelOrder(paidOrder.getId());

        Order findOrder = orderRepository.findById(paidOrder.getId())
                .orElse(null);
        log.info("After canceling order status : {}", findOrder.getStatus());

        assertThat(paidOrder).isNotNull();
        assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
    }

    @Test
    @DisplayName("주문취소 시 배송취소 서비스 테스트")
    void testCancelDeliveryAsync() throws InterruptedException {
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new DomainException("No User"));

        Order order = new Order();
        order.setMember(member);
        order.setStatus(OrderStatus.PAID);

        Delivery delivery = new Delivery();
        delivery.setStatus(DeliveryStatus.READY);
        Delivery savedDelivery = deliveryRepository.save(delivery);
        order.setDelivery(savedDelivery);
        Order paidOrder = orderRepository.save(order);


        log.info("Before canceling paidOrder status : {}", paidOrder.getStatus());
//        deliveryService.cancelDelivery(order.getId());

        // Async Test
        EventPublisher.publish(new OrderCancelEvent(order.getId()));
        Thread.sleep(500);

        log.info("After canceling paidOrder status : {}", paidOrder.getStatus());
        Delivery findDelivery = deliveryRepository.findById(paidOrder.getDelivery().getId())
                .orElse(null);

        assertThat(findDelivery).isNotNull();
        assertThat(findDelivery.getStatus()).isEqualTo(DeliveryStatus.CANCELLED);
        assertThat(findDelivery.getIsDeleted()).isTrue();
    }
}
