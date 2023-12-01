package com.showroom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.showroom.constants.Color;
import com.showroom.constants.FuelType;

import com.showroom.constants.ItemType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "order_item")
@Entity
public class OrderItem {

	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-generator-item")
	@SequenceGenerator(name = "sequence-generator-item", sequenceName = "order_item_sequence", allocationSize = 1)
	@Column(name = "order_item_id")
	private Integer id;

	@Column(name = "quantity")
	private int quantity;

	@Enumerated(EnumType.STRING) // save the enum as string
	@Column(name = "color")
	private Color color;

	@Enumerated(EnumType.STRING)
	@Column(name = "fuel_type")
	private FuelType fuelType;

	@Enumerated(EnumType.STRING)
	@Column(name = "item_type")
	private ItemType itemType;

	@Column(name = "initial_price")
	private Double initialPrice;

	@Column(name = "final_price")
	private Double finalPrice;

	@Transient // don't save this fields
	private Double discount;

	@Transient
	private Double additionalCharges;

	@Transient
	private Date orderItemBuyDate;

	@JsonIgnore // Ignore the data when serialization
	@ManyToOne(fetch = FetchType.LAZY)
	private Order order;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private Cart cart;

	@ManyToOne
	private Vehicle vehicle;
}
