package com.showroom.controller;

import com.showroom.entity.Response;
import com.showroom.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("orderItem")
public class OrderItemController {

    @Autowired
    OrderItemService orderItemService;

    @GetMapping("/getAllOrderedItemOfCustomerByCustomerId")
    public ResponseEntity<Response> getAllOrderedItemOfCustomerByCustomerId(
            @RequestParam(name = "id") int customerId,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") String sortDirection,
            @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(name = "filterValue", defaultValue = "") String filterValue

    ) {
        Response response = orderItemService.getAllOrderedItemOfCustomerByCustomerId(customerId, sortBy, sortDirection, pageNo, pageSize);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getOrderItemById")
    public ResponseEntity<Response> getOrderItemById(@RequestParam(name = "id") int id) {
        Response response = orderItemService.getOrderItemById(id);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }


    @GetMapping("getExelOfOrderedVehicle")
    public ResponseEntity<byte[]> generateExcelOfOrderedVehicle(
            @RequestParam(name = "filterBy", defaultValue = "date") String filterBy,
            @RequestParam(name = "startDate", defaultValue = "") LocalDate startDate,
            @RequestParam(name = "endDate", defaultValue = "") LocalDate endDate,
            @RequestParam(name = "startQuantity",defaultValue = "0") int startQuantity,
            @RequestParam(name = "endQuantity", defaultValue = "0") int endQuantity
    ) {
        byte[] response = orderItemService.generateExcelOfOrderedVehicle(filterBy ,startDate, endDate,startQuantity,endQuantity);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
