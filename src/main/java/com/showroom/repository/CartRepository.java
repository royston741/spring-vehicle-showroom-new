package com.showroom.repository;

import com.showroom.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Integer> {

    @Query("from Cart c where c.customer.id=:id")
    public Optional<Cart> findCartByCustomerId(@Param("id") int customerId);
}
