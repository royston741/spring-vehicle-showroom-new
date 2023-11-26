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

    @GetMapping("/getCartByCustomerId/{id}")
    public ResponseEntity<Response> getCartByCustomerId(@PathVariable(name = "id") int id) {
        Response response = cartService.getCartByCustomerId(id);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PostMapping("/addToCart/{id}")
    public ResponseEntity<Response> addToCart(@RequestBody OrderItem orderItem,@PathVariable(name = "id") int customerId) {
        Response response = cartService.addToCart(orderItem,customerId);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PutMapping("/removeItemFromCart/{id}")
    public ResponseEntity<Response> removeItemFromCart(@RequestBody OrderItem orderItem,@PathVariable(name = "id") int customerId) {
        Response response = cartService.removeItemFromCart(orderItem,customerId);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/clearCart/{id}")
    public ResponseEntity<Response> clearCart(@PathVariable(name = "id") int customerId) {
        Response response = cartService.clearCart(customerId);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}
