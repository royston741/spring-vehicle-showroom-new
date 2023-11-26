package com.showroom.repository;

import java.util.Optional;

import com.showroom.constants.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.showroom.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	public Optional<Customer> findByFirstNameAndLastNameAndPhoneNo(String firstName, String lastname, String phoneNo);

	Page<Customer> findAllByUserType(UserType userType,Pageable page);
	Page<Customer> findAllByFirstNameLikeOrLastNameLikeOrEmailLikeOrPhoneNoOrAddressLikeAndUserType(String firstName,
																									String lastName, String email, String phoneNo, String address, UserType userType, Pageable pageable);

	Optional<Customer> findByFirstNameAndPassword(String name, String password);
}
