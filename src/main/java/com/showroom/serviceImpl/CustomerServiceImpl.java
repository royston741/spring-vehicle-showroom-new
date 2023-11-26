package com.showroom.serviceImpl;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import com.showroom.constants.UserType;
import com.showroom.entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.showroom.entity.Customer;
import com.showroom.entity.Response;
import com.showroom.repository.CustomerRepository;
import com.showroom.service.CustomerService;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Transactional
    @Override
    public Response createCustomer(Customer customer) {
        Response response = new Response();

        try {
            Optional<Customer> existingCustomer = customerRepository.findByFirstNameAndLastNameAndPhoneNo(
                    customer.getFirstName(), customer.getLastName(), customer.getPhoneNo());
            if (existingCustomer.isPresent()) {
                response.getErrMssg().add("User already exist by given details");
            } else {
                Cart cart = new Cart();
                cart.setCustomer(customer);
                customer.setCart(cart);

                Customer savedCustomer = customerRepository.save(customer);
                response.setResponseData(savedCustomer);
                response.setSuccess(true);
            }
        } catch (Exception e) {
            response.getErrMssg().add("User not added");
            if (e.getCause().getCause() instanceof ConstraintViolationException cv) {
                response.getErrMssg().addAll(cv.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
                        .collect(Collectors.toList()));
            } else {
                log.error("Error in createCustomer {}", e);
            }
        }
        return response;
    }

    @Transactional
    @Override
    public Response updateCustomer(Customer customer) {
        Response response = new Response();
        try {
            // check if exist
            Optional<Customer> existingCustomer = customerRepository.findById(customer.getId());
            // if exist
            if (existingCustomer.isPresent()) {
                customer.getCart().setCustomer(customer);
                // update customer
                Customer updatedCustomer = customerRepository.save(customer);
                response.setResponseData(updatedCustomer);
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("Customer does not exist");
            }
        } catch (Exception e) {
            response.getErrMssg().add("Customer not added");
            if (e.getCause().getCause() instanceof ConstraintViolationException cv) {
                response.getErrMssg().addAll(cv.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
                        .collect(Collectors.toList()));
            } else {
                log.error("Error in createCustomer {}", e);
            }
        }
        return response;
    }

    @Override
    public Response getCustomerById(int id) {
        Response response = new Response();
        try {
            // check if exist
            Optional<Customer> existingCustomer = customerRepository.findById(id);
            System.out.println(existingCustomer);
            // if exist
            if (existingCustomer.isPresent()) {
                response.setResponseData(existingCustomer);
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("User does not exist by Id : " + id);
            }
        } catch (Exception e) {
            response.getErrMssg().add("Customer not found");
            log.error("Error in getCustomerById {}", e);
        }
        return response;
    }

    @Override
    public Response getCustomerByFirstNameAndLastNameAndPhoneNo(String firstName, String lastName, String phoneNo) {
        Response response = new Response();
        try {
            // check if exist
            Optional<Customer> existingCustomer = customerRepository.findByFirstNameAndLastNameAndPhoneNo(firstName,
                    lastName, phoneNo);
            // if exist
            if (existingCustomer.isPresent()) {
                response.setResponseData(existingCustomer);
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("Customer does not exist by given details");
            }
        } catch (Exception e) {
            response.getErrMssg().add("Customer not found");
            log.error("Error in getCustomerByFirstNameAndLastNameAndPhoneNo {}", e);
        }
        return response;
    }

    @Override
    public Response deleteCustomerById(int id) {
        Response response = new Response();
        try {
            // check if exist
            boolean doesCustomerExist = customerRepository.existsById(id);
            // if exist
            if (doesCustomerExist) {
                customerRepository.deleteById(id);
                response.setResponseData("Customer deleted");
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("User does not exist by Id : " + id);
            }
        } catch (Exception e) {
            response.getErrMssg().add("User not found");
            log.error("Error in deleteCustomerById {}", e);
        }
        return response;
    }

    @Override
    public Response getAllCustomers(String sortBy, String sortDirection, String filterValue,  int pageNo, int pageSize) {
        Response response = new Response();
        try {
            Pageable page = PageRequest.of(pageNo, pageSize,
                    Sort.by(sortDirection.equals("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));
            Page<Customer> customerPage = customerRepository.findAllByUserType(UserType.CUSTOMER,page);
            if (filterValue.length()>0 ) {
                customerPage = customerRepository.findAllByFirstNameLikeOrLastNameLikeOrEmailLikeOrPhoneNoOrAddressLikeAndUserType(
                        filterValue, filterValue, filterValue, filterValue, filterValue, UserType.CUSTOMER, page);
            }
            if (customerPage.getTotalElements() > 0) {
                response.setResponseData(customerPage);
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("No customers");
            }
        } catch (Exception e) {
            response.getErrMssg().add("Customers not found");
            log.error("Error in getAllCustomers {}", e);
        }
        return response;

    }

    @Override
    public Response logIn(String name, String password) {
        Response response = new Response();
        try {
            // check if exist
            Optional<Customer> customer = customerRepository.findByFirstNameAndPassword(name, password);
            // if exist
            if (customer.isPresent()) {
                response.setResponseData(customer);
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("Invalid credentials");
            }
        } catch (Exception e) {
            response.getErrMssg().add("Customer not found");
            log.error("Error in findCustomerById {}", e);
        }
        return response;
    }

}
