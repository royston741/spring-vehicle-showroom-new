package com.showroom.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.showroom.constants.Color;
import com.showroom.constants.FuelType;

import com.showroom.constants.ItemType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart")
@Entity
public class Cart {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-generator")
    @SequenceGenerator(name = "sequence-generator", sequenceName = "cart_sequence", allocationSize = 1)
    @Column(name = "cart_id")
    private Integer id;

    @Column(name = "cart_total")
    private Double cartTotal;

    @OneToMany(cascade = {CascadeType.PERSIST,CascadeType.REMOVE,CascadeType.MERGE}, mappedBy = "cart",orphanRemoval = true)
    private List<OrderItem> cartItems=new ArrayList<>();

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")  //create a column with name
    private Customer customer;

    public void addItem(OrderItem item){
        this.cartItems.add(item);
    }

    public void removeItem(OrderItem item){
        this.cartItems.removeIf(existingItem-> existingItem.getId().equals( item.getId()));
    }

    public void clearCart(){
        this.cartItems.clear();
    }
}
