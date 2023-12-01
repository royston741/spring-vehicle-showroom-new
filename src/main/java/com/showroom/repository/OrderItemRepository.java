package com.showroom.repository;

import com.showroom.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Integer> {

    @Query("from OrderItem i where i.order.customer.id=:id")
    public Page<OrderItem> findAllOrderedItemOfCustomerByCustomerId(@Param("id") int customerId, Pageable page);

//    @Query("from OrderItem o where order.customer.name=:name AND order.customer.phoneNo=:phoneNo AND v.vehicleName=:vehicleName")
//    Page<OrderItem> findOrderByVehicleName(@Param("vehicleName") String vehicleName,  Pageable page);

    @Query("Select i.vehicle.name, count(i.vehicle.id), sum(i.finalPrice) from OrderItem i  where i.order.orderDate between :startDate and :endDate group by i.vehicle.name")
    public List<Object[]> getOrderItemDetails(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
