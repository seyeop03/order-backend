package com.example.org.entity;

import com.example.org.repository.ItemRepository;
import com.example.org.repository.MemberRepository;
import com.example.org.repository.OrderItemRepository;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class OrderItemTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OrderItemRepository orderItemRepository;


    @BeforeAll
    public void setUp() {
        Member member1 = new Member();
        Member member2 = new Member();

        member1.setName("test1");
        member2.setName("test2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Item item1 = new Item();
        item1.setName("불닭");
        item1.setPrice(1500);

        Item item2 = new Item();
        item2.setName("공화춘");
        item2.setPrice(1300);

        itemRepository.save(item1);
        itemRepository.save(item2);
    }


    @Test
    @DisplayName("주문상품 엔티티 연관관계 테스트")
    void testEntity() {
        Order order = new Order();
        order.setMember(memberRepository.findById(1L).orElse(null));
        orderRepository.save(order);

        List<Item> items = itemRepository.findAll();
        List<OrderItem> orderItems = new ArrayList<>();
        for (Item item : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setPrice(item.getPrice());
            orderItem.setCreatedAt(LocalDateTime.now());
            orderItem.setQuantity(2);
            orderItems.add(orderItem);
        }
        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);
        log.info("savedOrderItems size : {}", savedOrderItems.size());


        int expectedValue = 0;
         expectedValue += savedOrderItems.stream()
                .mapToInt(orderItem -> {
                    int unitPrice = orderItem.getPrice() * orderItem.getQuantity();
                    log.info("unitPrice : {}", unitPrice);
                    return unitPrice;
                })
                .sum();
//        for (OrderItem orderItem : savedOrderItems)
//            expectedValue += orderItem.getPrice();

        assertThat(expectedValue).isEqualTo(5600);
        assertThat(orderItems.get(0).getItem().getId()).isEqualTo(items.get(0).getId());
        assertThat(orderItems.get(1).getItem().getId()).isEqualTo(items.get(1).getId());
        assertThat(items.get(0).getOrderItems().get(0).getId()).isEqualTo(orderItems.get(0).getId());
        assertThat(items.get(1).getOrderItems().get(1).getId()).isEqualTo(orderItems.get(1).getId());
    }
}