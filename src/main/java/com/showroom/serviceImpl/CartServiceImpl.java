package com.showroom.serviceImpl;

import com.showroom.constants.ItemType;
import com.showroom.entity.Cart;
import com.showroom.entity.Customer;
import com.showroom.entity.OrderItem;
import com.showroom.entity.Response;
import com.showroom.repository.CartRepository;
import com.showroom.repository.OrderItemRepository;
import com.showroom.service.CartService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    OrderItemRepository cartItemRepository;

    @Transactional
    @Override
    public Response addToCart(OrderItem newCartItem, int customerId) {
        Response response = new Response();
        try {
            // check if exist
            Optional<Cart> existingCart = cartRepository.findCartByCustomerId(customerId);

            // if exist
            if (existingCart.isPresent()) {
                Cart cart = existingCart.get();

                newCartItem.setItemType(ItemType.CART);
                newCartItem.setInitialPrice(newCartItem.getVehicle().getPrice());
                newCartItem.setFinalPrice(newCartItem.getInitialPrice() * newCartItem.getQuantity());
                newCartItem.setCart(cart);
                cart.addItem(cartItemRepository.save(newCartItem));


                Double cartTotal = cart.getCartItems().stream()
                        .map(OrderItem::getFinalPrice)
                        .reduce(0.0, Double::sum);

                cart.setCartTotal(cartTotal);

                Cart savedCart = cartRepository.save(cart);
                response.setResponseData(savedCart);
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("Cart does not exist for customer Id : " + customerId);
            }
        } catch (Exception e) {
            response.getErrMssg().add("Cart not found");
            log.error("Error in addToCart {}", e);
        }
        return response;
    }

    @Transactional
    @Override
    public Response removeItemFromCart(OrderItem removeItem, int customerId) {
        Response response = new Response();
        try {
            // check if exist
            Optional<Cart> existingCart = cartRepository.findCartByCustomerId(customerId);

            // if exist
            if (existingCart.isPresent()) {
                Cart cart = existingCart.get();
                cart.removeItem(removeItem);
                Double cartTotal = cart.getCartItems().stream()
                        .map(OrderItem::getFinalPrice)
                        .reduce(0.0, Double::sum);
                cart.setCartTotal(cartTotal);

                Cart savedCart = cartRepository.save(cart);
                response.setResponseData(savedCart);
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("Cart does not exist for customer Id : " + customerId);
            }
        } catch (Exception e) {
            response.getErrMssg().add("Cart not found");
            log.error("Error in addToCart {}", e);
        }
        return response;
    }

    @Override
    public Response clearCart(int customerId) {
        Response response = new Response();
        try {
            // check if exist
            Optional<Cart> existingCart = cartRepository.findCartByCustomerId(customerId);

            // if exist
            if (existingCart.isPresent()) {
                Cart cart = existingCart.get();
                cart.clearCart();
                cart.setCartTotal(0d);
                Cart savedCart = cartRepository.save(cart);
                response.setResponseData(savedCart);
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("Cart does not exist for customer Id : " + customerId);
            }
        } catch (Exception e) {
            response.getErrMssg().add("Cart not found");
            log.error("Error in addToCart {}", e);
        }
        return response;    }

    @Override
    public Response getCartByCustomerId(int customerId) {
        Response response = new Response();
        try {
            // check if exist
            Optional<Cart> existingCart = cartRepository.findCartByCustomerId(customerId);
            // if exist
            if (existingCart.isPresent()) {
                response.setResponseData(existingCart);
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("Cart does not exist for customer Id : " + customerId);
            }
        } catch (Exception e) {
            response.getErrMssg().add("Cart not found");
            log.error("Error in getCartByCustomerId {}", e);
        }
        return response;
    }
}
