package com.showroom.controller;

import com.showroom.entity.Order;
import com.showroom.entity.Response;
import com.showroom.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/placeOrder")
    public ResponseEntity<Response> placeOrder(@RequestBody Order order, @RequestParam(name = "customerId",defaultValue = "0") int id) {
        Response response = orderService.placeOrder(order,id);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }


    @PostMapping("/getProcessOrderDetails")
    public ResponseEntity<Response> getProcessOrderDetails(@RequestBody Order order) {
        Response response = orderService.getProcessOrderDetails(order);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getOrdersOfCustomerByCustomerId")
    public ResponseEntity<Response> getOrdersOfCustomerByCustomerId(@RequestParam(value = "id",defaultValue = "0") int customerId) {
        Response response = orderService.getOrdersOfCustomerByCustomerId(customerId);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}
