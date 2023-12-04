package com.showroom.controller;

import com.showroom.entity.OrderItem;
import com.showroom.entity.Response;
import com.showroom.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("cart")
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/getCartByCustomerId")
    public ResponseEntity<Response> getCartByCustomerId(@RequestParam(name = "id") int id) {
        Response response = cartService.getCartByCustomerId(id);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PostMapping("/addToCart")
    public ResponseEntity<Response> addToCart(@RequestBody OrderItem addACartItem,@RequestParam(name = "id") int customerId) {
        Response response = cartService.addToCart(addACartItem,customerId);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PutMapping("/removeItemFromCart")
    public ResponseEntity<Response> removeItemFromCart(@RequestBody OrderItem removeCartItem,@RequestParam(name = "id") int customerId) {
        Response response = cartService.removeItemFromCart(removeCartItem,customerId);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/clearCart")
    public ResponseEntity<Response> clearCart(@RequestParam(name = "id") int customerId) {
        Response response = cartService.clearCart(customerId);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}
