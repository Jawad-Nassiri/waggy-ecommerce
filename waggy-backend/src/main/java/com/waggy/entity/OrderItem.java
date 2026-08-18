package com.waggy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@Check(constraints = "price > 0")
@Check(constraints = "quantity > 0")
public class OrderItem {

    @EmbeddedId
    private OrderItemId id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @MapsId("orderId")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @MapsId("productId")
    private Product product;
}
