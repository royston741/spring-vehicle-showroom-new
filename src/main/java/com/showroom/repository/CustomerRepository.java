package com.showroom.repository;

import java.util.List;
import java.util.Optional;

import com.showroom.constants.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.showroom.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    public Optional<Customer> findByFirstNameAndLastNameAndPhoneNo(String firstName, String lastname, String phoneNo);

    Page<Customer> findAllByUserType(UserType userType, Pageable page);

    @Query(value = "from Customer c where userType='CUSTOMER' " +
            "and (c.firstName Like :firstName% or c.phoneNo Like :phoneNo% or c.address Like :address%   )")
    Page<Customer> filterCustomerList(@Param("firstName") String firstName,
                                      @Param("phoneNo") String phoneNo,
                                      @Param("address") String address,
                                      Pageable pageable);

    Optional<Customer> findByFirstNameAndPassword(String name, String password);

    Optional<Customer> findByEmail(String email);


}
