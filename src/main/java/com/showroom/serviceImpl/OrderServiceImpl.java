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
            // check if customer exist
            Optional<Customer> existingCustomer = customerRepository.findById(customerId);

            // if exist
            if (existingCustomer.isPresent()) {
                // process order
                List<OrderItem> processedOrderItemList = processOrder(order);

                // set order item
                order.setOrderItem(processedOrderItemList);
                // set order date
                order.setOrderDate(Date.valueOf(LocalDate.now()));
                // set customer
                order.setCustomer(existingCustomer.get());

                Double orderTotal = order.getOrderItem().stream()
                        .map(OrderItem::getFinalPrice)
                        .reduce(0.0, Double::sum);
                order.setTotal(orderTotal);

                Order savedOrder = orderRepository.save(order);
                response.setResponseData(savedOrder);
                response.setSuccess(true);

                // SENDING MAIL //
                Customer customerDetails = existingCustomer.get();
                // get show room details
                ShowRoomDetails showRoomDetails = showroomRepository.findById(1).get();
                // generate pdf
                byte[] document = pdfGeneratorService.generatePdf(customerDetails, order, showRoomDetails);
//                byte[] document =new byte[0];

                // Create a emilDTo
                EmailDto newEmail = new EmailDto();
                // send to
                newEmail.setEmailTo(customerDetails.getEmail());
                // set subject
                newEmail.setSubject(EmailConstants.ORDER_INVOICE_SUBJECT);
                // set html content
                newEmail.setHtmlContentLink("D:\\vehicle project\\New folder\\spring-vehicle-showroom-new\\src\\main\\resources\\templates\\index.html");
                // add attachments
                newEmail.getAttachments().add(new FileMetaData(1, "order-invoice.pdf", document));
                // send mail
                emailService.sendMail(newEmail);
            } else {
                response.getErrMssg().add("Customer not found: " + customerId);
            }
        } catch (Exception e) {
            response.getErrMssg().add("Order not placed");
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
        // return a stream of processed item
        return order.getOrderItem().stream().map(item -> {
            // set item type to order
            item.setItemType(ItemType.ORDER);
            // set initial price
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

            item.setOrder(order);
            return item;
        }).collect(Collectors.toList());
    }
}
