package com.example.org.entity;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class OrderTest {

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
        orderRepository.save(order1);
        orderRepository.save(order2);


        assertThat(order1).isNotNull();
        assertThat(order2).isNotNull();
        assertThat(order1.getMember().getId()).isEqualTo(member1.getId());
        assertThat(order2.getMember().getId()).isEqualTo(member1.getId());
        assertThat(orderRepository.findAll().size()).isEqualTo(2);
    }
}