package com.showroom.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.showroom.constants.UserType;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "customer")
@Entity
public class Customer {

	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-generator-customer")
	@SequenceGenerator(name = "sequence-generator-customer", sequenceName = "customer_sequence", allocationSize = 1) // allocation size amount to increment
	@Column(name = "customer_id")
	private Integer id;

	@NotBlank(message = "First name must not be empty")
	@Size(min = 3, message = "first name should be of at least of 3 letters")
	@Column(name = "first_name")
	private String firstName;

	@NotBlank(message = "Last name must not be empty")
	@Size(min = 3, message = "last name should be of at least of 3 letters")
	@Column(name = "second_name")
	private String lastName;

	@NotBlank(message = "Gmail must not be empty")
//	@Size(min = 7, message = "gmail name should be of at least of 7 letters")
	@Email(regexp = "[a-z0-9$&+,:;=?@#|'<>.^*()%!-]+@[a-z]+\\.[a-z]{2,3}", message = "Email is not valid")
	@Column(name = "email")
	private String email;

	@NotBlank(message = "Phone no. must not be empty")
	@Size(min = 10, max = 10, message = "Phone no should be of length 10")
	@Column(name = "phone_no")
	private String phoneNo;

	@NotBlank(message = "Address must not be empty")
	@Size(min = 3, message = "address should be of at least of 3 letters")
	@Column(name = "address")
	private String address;

	@NotBlank(message = "Password must not be empty")
	@Size(min = 8, message = "password should be of at least of 8 letters")
	@Column(name = "password")
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_type")
	private UserType userType;

	@JsonIgnore
	@OneToMany(cascade = { CascadeType.REMOVE }, mappedBy = "customer")
	private List<Order> orders = new ArrayList<>();

	@JsonIgnore
	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, mappedBy = "customer")
	private Cart cart = new Cart();

}
