package com.restaurant.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private Double price;
}
