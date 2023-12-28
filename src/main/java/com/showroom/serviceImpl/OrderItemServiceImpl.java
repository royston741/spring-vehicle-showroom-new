package com.showroom.serviceImpl;

import com.showroom.entity.Customer;
import com.showroom.entity.OrderItem;
import com.showroom.entity.Response;
import com.showroom.repository.CustomerRepository;
import com.showroom.repository.OrderItemRepository;
import com.showroom.service.ExcelService;
import com.showroom.service.OrderItemService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Transactional
    @Override
    public Response getAllOrderedItemOfCustomerByCustomerId
            (int customerId, String sortBy, String sortDirection, int pageNo, int pageSize) {
        Response response = new Response();
        try {
//            if (sortBy.equals("name")) {}

            Pageable page = PageRequest.of(pageNo, pageSize,
                    Sort.by(sortDirection.equals("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));

            Optional<Customer> existingCustomer = customerRepository.findById(customerId);

            if (existingCustomer.isPresent()) {
                Page<OrderItem> orderItemList = orderItemRepository.findAllOrderedItemOfCustomerByCustomerId(customerId, page);
                //set the buy date of order items
                orderItemList.getContent().forEach(i -> i.setOrderItemBuyDate(i.getOrder().getOrderDate()));
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
            Optional<OrderItem> orderItem = orderItemRepository.findById(orderItemId);
            //set the buy date of order item
            orderItem.get().setOrderItemBuyDate(orderItem.get().getOrder().getOrderDate());
            response.setResponseData(orderItem.get());
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
            result = excelService.generateExcel("ordered vehicles", orderedVehicles, rowHeader);
        } catch (Exception e) {
            log.error("Error in generateExcelOfOrderedVehicle ", e);
        }
        return result;
    }

    public void processItem(OrderItem item) {
        item.setInitialPrice(item.getVehicle().getPrice());

        double initialPrice = item.getInitialPrice();
        // get additional charge
        double additionalChargesOfColor = item.getColor().getAdditionalCharges(initialPrice);

        // get discount
        double discount = 0;
        switch (item.getVehicle().getVehicleType()) {
            case CAR -> discount = item.getFuelType().getDiscountedPrice(initialPrice);
            case BIKE -> discount = item.getVehicle().getTwoWheelerType().getDiscountedPrice(initialPrice);
        }
        initialPrice += additionalChargesOfColor;
        initialPrice -= discount;
        item.setDiscount(discount);
        item.setAdditionalCharges(additionalChargesOfColor);

        item.setFinalPrice(initialPrice * item.getQuantity());

    }
}
