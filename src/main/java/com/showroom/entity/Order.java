package com.showroom.entity;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vehicle_order")
@Entity
public class Order {

	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-generator-order")
	@SequenceGenerator(name = "sequence-generator-order", sequenceName = "order_sequence", allocationSize = 1)
	@Column(name = "order_id")
	private Integer id;

	@Temporal(TemporalType.DATE)  // save only date exclude time stc
	@Column(name = "order_date")
	private Date orderDate;

	@Column(name = "order_total")
	private Double total;

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, mappedBy = "order")
	private List<OrderItem> orderItem;

	@ManyToOne(fetch = FetchType.LAZY)
	private Customer customer;
}
