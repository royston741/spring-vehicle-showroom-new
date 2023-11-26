package com.showroom.entity;

import com.showroom.constants.TwoWheelerType;
import com.showroom.constants.VehicleType;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "vehicle")
@Entity
public class Vehicle {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-generator")
	@SequenceGenerator(name = "sequence-generator", sequenceName = "vehicle_sequence", allocationSize = 1)
	@Column(name = "vehicle_id")
	private Integer id;

	@NotBlank(message = "Vehicle name must not be empty")
	@Size(min = 3, message = "Vehicle name should be of at least of 3 letters")
	@Column(name = "vehicle_name")
	private String name;

	@Column(name = "price")
	private Double price;

	@Column(name = "rating")
	private double rating;

	@Enumerated(EnumType.STRING)
	@Column(name = "vehicle_type")
	private VehicleType vehicleType;

	@Enumerated(EnumType.STRING)
	@Column(name = "two_wheeler_type")
	private TwoWheelerType twoWheelerType;

	@NotBlank(message = "img must not be empty")
	@Column(name = "imgUrl")
	private String imgUrl;

}
