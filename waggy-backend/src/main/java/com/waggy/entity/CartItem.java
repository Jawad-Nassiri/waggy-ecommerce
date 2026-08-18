package com.waggy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@Check(constraints = "quantity > 0")
public class CartItem {

    @EmbeddedId
    private CartItemId id;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne
    @MapsId("cartId")
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}