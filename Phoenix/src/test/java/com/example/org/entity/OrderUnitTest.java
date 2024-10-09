package com.example.org.entity;

import com.example.org.exception.OrderDomainException;
import com.example.org.repository.MemberRepository;
import com.example.org.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class OrderUnitTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderRepository orderRepository;

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
    @DisplayName("회원과 주문 엔티티 연관관계 테스트")
    void testEntity() {
        Member member1 = memberRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("No user"));
        log.info("member1.getId() : {}", member1.getId());

        Order order1 = new Order();
        Order order2 = new Order();
        order1.setMember(member1);
        order2.setMember(member1);
        Order savedOrder1 = orderRepository.save(order1);
        Order savedOrder2 = orderRepository.save(order2);


        assertThat(savedOrder1).isNotNull();
        assertThat(savedOrder2).isNotNull();
        assertThat(savedOrder1.getMember().getId()).isEqualTo(member1.getId());
        assertThat(savedOrder2.getMember().getId()).isEqualTo(member1.getId());
        assertThat(orderRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("주문취소 테스트 (서비스 계층 X)")
    void testOrderCancelWithoutService() {
        Member member = memberRepository.findById(1L).orElse(null);
        Order order = new Order();
        order.setMember(member);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PAID);
        Order savedOrder = orderRepository.save(order);

        log.info("Before canceling order : {}", savedOrder.getStatus());
        savedOrder.setStatus(OrderStatus.CANCEL);
        Order cancelledOrder = orderRepository.save(savedOrder);
        log.info("After canceling order : {}", savedOrder.getStatus());

        assertThat(savedOrder).isEqualTo(cancelledOrder);
        assertThat(cancelledOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
    }

    @Test
    @DisplayName("주문취소 실패 테스트 (서비스 계층 X)")
    void testOrderCancelFailureWithoutService() {
        Member member = memberRepository.findById(1L).orElse(null);
        Order order = new Order();
        order.setMember(member);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.READY);
        Order savedOrder = orderRepository.save(order);

        log.info("Before canceling order : {}", savedOrder.getStatus());
        assertThatThrownBy(savedOrder::cancel)
                .isInstanceOf(OrderDomainException.class)
                .hasMessageContaining("주문을 취소할 수 없습니다.");
        log.info("After canceling order : {}", savedOrder.getStatus());
    }
}