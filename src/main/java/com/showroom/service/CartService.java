package com.showroom.service;

import com.showroom.entity.OrderItem;
import com.showroom.entity.Response;

public interface CartService {

    public Response addToCart(OrderItem newCartItem,int customerId);

    public Response removeItemFromCart(OrderItem removeItem, int customerId) ;

    public Response clearCart(int customerId) ;

    public Response getCartByCustomerId(int customerId);

}
