package com.showroom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.showroom.constants.Color;
import com.showroom.constants.FuelType;

import com.showroom.constants.ItemType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "order_item")
@Entity
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-generator")
	@SequenceGenerator(name = "sequence-generator", sequenceName = "order_item_sequence", allocationSize = 1)
	@Column(name = "order_item_id")
	private Integer id;

	@Column(name = "quantity")
	private int quantity;

	@Enumerated(EnumType.STRING)
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

	@Transient
	private Double discount;

	@Transient
	private Double additionalCharges;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private Order order;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private Cart cart;

	@OneToOne
	private Vehicle vehicle;
}
