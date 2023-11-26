package com.showroom.repository;

import com.showroom.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {

    @Query("from Order o where o.customer.id=:id")
    public List<Order> findAllOrderByCustomerId(@Param("id") int customerId);
}
