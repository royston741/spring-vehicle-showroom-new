package com.showroom.serviceImpl;

import com.showroom.constants.ItemType;
import com.showroom.entity.Cart;
import com.showroom.entity.OrderItem;
import com.showroom.entity.Response;
import com.showroom.repository.CartRepository;
import com.showroom.repository.OrderItemRepository;
import com.showroom.service.CartService;
import com.showroom.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    OrderItemRepository cartItemRepository;

    @Autowired
    OrderItemServiceImpl orderItemService;

    @Transactional
    @Override
    public Response addToCart(OrderItem newCartItem, int customerId) {
        Response response = new Response();
        try {
            // check if cart exist
            Optional<Cart> existingCart = cartRepository.findCartByCustomerId(customerId);

            // if exist
            if (existingCart.isPresent()) {
                Cart cart = existingCart.get();
                cart.getCartItems().forEach(c->{
                });

                newCartItem.setItemType(ItemType.CART);
                orderItemService.processItem(newCartItem);
//                newCartItem.setInitialPrice(newCartItem.getVehicle().getPrice());
//                newCartItem.setFinalPrice(newCartItem.getInitialPrice() * newCartItem.getQuantity());
                newCartItem.setCart(cart);


                List<OrderItem> existingCartItem= cart.getCartItems().stream().filter(item ->
                        // check if item needed to be added is already present by 1) Id 2) fuel type is equal  3) color is already present
                        item.getVehicle().getId().equals( newCartItem.getVehicle().getId()) && item.getFuelType()==newCartItem.getFuelType() && item.getColor()==newCartItem.getColor()
                ).toList();

                // if item already exist
                if(existingCartItem.size()>0){
                    // update the details of existing item
                    OrderItem item=existingCartItem.get(0);
                    item.setQuantity(item.getQuantity()+newCartItem.getQuantity());
                    item.setFinalPrice(item.getFinalPrice()+newCartItem.getFinalPrice());
                }
                else {
                    cart.addItem(cartItemRepository.save(newCartItem));
                }

                Double cartTotal = cart.getCartItems().stream() // get all cart items
                        .map(OrderItem::getFinalPrice) // create a stream of vehicle final price
                        .reduce(0.0, Double::sum); // sum price

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
            // check if cart exist
            Optional<Cart> existingCart = cartRepository.findCartByCustomerId(customerId);
            // if exist
            if (existingCart.isPresent()) {
                // get cart
                Cart cart = existingCart.get();
                // remove item from cart
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
            response.getErrMssg().add("Cart item not removed");
            log.error("Error in removeItemFromCart {}", e);
        }
        return response;
    }

    @Override
    public Response clearCart(int customerId) {
        Response response = new Response();
        try {
            // check if cart exist
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
            response.getErrMssg().add("Cart not cleared");
            log.error("Error in clearCart {}", e);
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
