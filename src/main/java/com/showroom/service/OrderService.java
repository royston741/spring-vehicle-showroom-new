package com.showroom.service;

import com.showroom.entity.Order;
import com.showroom.entity.Response;

public interface OrderService {

    public Response placeOrder(Order order,int customerId);

    public Response getProcessOrderDetails(Order order );

    public Response getOrdersOfCustomerByCustomerId(int customerId);
    }
