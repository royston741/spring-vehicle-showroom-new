package com.showroom.serviceImpl;

import com.showroom.entity.Customer;
import com.showroom.entity.Order;
import com.showroom.entity.OrderItem;
import com.showroom.entity.Response;
import com.showroom.repository.CustomerRepository;
import com.showroom.repository.OrderItemRepository;
import com.showroom.service.ExcelService;
import com.showroom.service.OrderItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ExcelService excelService;

    @Override
    public Response getAllOrderedItemOfCustomerByCustomerId
            (int customerId, String sortBy, String sortDirection, int pageNo, int pageSize) {
        Response response = new Response();
        try {
            if (sortBy.equals("name")) {

            }
            Pageable page = PageRequest.of(pageNo, pageSize,
                    Sort.by(sortDirection.equals("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));
            Optional<Customer> existingCustomer = customerRepository.findById(customerId);
            if (existingCustomer.isPresent()) {
                Page<OrderItem> orderItemList = orderItemRepository.findAllOrderedItemOfCustomerByCustomerId(customerId, page);
                response.setResponseData(orderItemList);
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("Customer not found by given id : " + customerId);
            }
        } catch (Exception e) {
            response.getErrMssg().add("Orders not found");
            log.error("Error in getAllOrderedItemOfCustomerByCustomerId {} ", e);
        }
        return response;
    }

    @Override
    public Response getOrderItemById(int orderItemId) {
        Response response = new Response();
        try {
            Optional<OrderItem> orderItemOptional = orderItemRepository.findById(orderItemId);
            response.setResponseData(orderItemOptional.get());
            response.setSuccess(true);
        } catch (Exception e) {
            response.getErrMssg().add("OrderItem not found");
            log.error("Error in getOrderItemById {}", e);
        }
        return response;
    }

    @Override
    public byte[] generateExcelOfOrderedVehicle(String filterBy, LocalDate start, LocalDate end, int startQuantity, int endQuantity) {
        byte[] result = {};
        try {
            List<Object[]> orderedVehicles = orderItemRepository.getOrderItemDetails(start, end);
            List<String> rowHeader = List.of("Vehicle Name", "quantity", "Total Revenue");
            result = excelService.generateExcel("ordered vehicles", orderedVehicles, rowHeader, start, end);
        } catch (Exception e) {
            log.error("Error in generateExcelOfOrderedVehicle ", e);
        }
        return result;
    }
}
