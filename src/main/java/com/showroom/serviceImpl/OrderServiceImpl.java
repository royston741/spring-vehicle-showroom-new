package com.showroom.serviceImpl;

import com.showroom.constants.EmailConstants;
import com.showroom.constants.ItemType;
import com.showroom.dto.EmailDto;
import com.showroom.dto.FileMetaData;
import com.showroom.entity.*;
import com.showroom.repository.CustomerRepository;
import com.showroom.repository.OrderRepository;
import com.showroom.repository.ShowroomRepository;
import com.showroom.service.EmailService;
import com.showroom.service.OrderService;
import com.showroom.service.PdfGeneratorService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    ShowroomRepository showroomRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    PdfGeneratorService pdfGeneratorService;

    @Transactional
    @Override
    public Response placeOrder(Order order, int customerId) {
        Response response = new Response();
        try {
            // check if exist
            Optional<Customer> existingCustomer = customerRepository.findById(customerId);

            // if exist
            if (existingCustomer.isPresent()) {
                List<OrderItem> processedOrderItemList = processOrder(order);

                order.setOrderItem(processedOrderItemList);
                order.setOrderDate(Date.valueOf(LocalDate.now()));
                order.setCustomer(existingCustomer.get());

                Double orderTotal = order.getOrderItem().stream()
                        .map(OrderItem::getFinalPrice)
                        .reduce(0.0, Double::sum);
                order.setTotal(orderTotal);
                Order savedOrder = orderRepository.save(order);
                response.setResponseData(savedOrder);
                response.setSuccess(true);

                Customer customerDetails = existingCustomer.get();
                ShowRoomDetails showRoomDetails = showroomRepository.findById(1).get();
                byte[] document = pdfGeneratorService.generatePdf(customerDetails, order, showRoomDetails);
//                byte[] document =new byte[0];
                EmailDto newEmail = new EmailDto();
                newEmail.setEmailTo(customerDetails.getEmail());
                newEmail.setSubject(EmailConstants.ORDER_INVOICE_SUBJECT);
                newEmail.setHtmlContentLink("D:\\New folder\\demo\\src\\main\\resources\\templates\\index.html");
                newEmail.getAttachments().add(new FileMetaData(1, "order-invoice.pdf", document));
                emailService.sendMail(newEmail);
            } else {
                response.getErrMssg().add("Customer not found: " + customerId);
            }
        } catch (Exception e) {
            response.getErrMssg().add("Order not saved");
            log.error("Error in placeOrder {}", e);
        }
        return response;
    }

    @Override
    public Response getProcessOrderDetails(Order order) {
        Response response = new Response();
        try {
            // if exist
            if (order != null) {
                List<OrderItem> processedOrderItemList = processOrder(order);
                order.setOrderItem(processedOrderItemList);
                Double orderTotal = order.getOrderItem().stream()
                        .map(OrderItem::getFinalPrice)
                        .reduce(0.0, Double::sum);
                order.setTotal(orderTotal);

                response.setResponseData(order);
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("No order to process ");
            }
        } catch (Exception e) {
            response.getErrMssg().add("Order not processed");
            log.error("Error in getProcessOrderDetails {}", e);
        }
        return response;
    }

    @Override
    public Response getOrdersOfCustomerByCustomerId(int customerId) {
        Response response = new Response();
        try {
            // check if exist
            Optional<Customer> existingCustomer = customerRepository.findById(customerId);

            // if exist
            if (existingCustomer.isPresent()) {
                List<Order> order = orderRepository.findAllOrderByCustomerId(customerId);
                response.setResponseData(order);
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("Customer not found: " + customerId);
            }
        } catch (Exception e) {
            response.getErrMssg().add("Orders not found");
            log.error("Error in placeOrder {}", e);
        }
        return response;
    }

    public List<OrderItem> processOrder(Order order) {
        return order.getOrderItem().stream().map(item -> {
            item.setItemType(ItemType.ORDER);
            item.setInitialPrice(item.getVehicle().getPrice());

            double initialPrice = item.getInitialPrice();
            double additionalChargesOfColor = item.getColor().getAdditionalCharges(initialPrice);
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

            item.setOrder(order);
            return item;
        }).collect(Collectors.toList());
    }
}
