package com.showroom.serviceImpl;

import java.util.Optional;

import com.showroom.constants.UserType;
import com.showroom.entity.Cart;
import com.showroom.exception.EmailDuplicationException;
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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    //    @Transactional
    @Override
    public Response createCustomer(Customer customer) {
        Response response = new Response();

        try {
            // check if email is already present
            Optional<Customer> existingEmail = customerRepository.findByEmail(customer.getEmail());
            if (existingEmail.isPresent()) {
                throw new EmailDuplicationException("Email already present");
            }

            // check if customer already present
            Optional<Customer> existingCustomer = customerRepository.findByFirstNameAndLastNameAndPhoneNo(
                    customer.getFirstName(), customer.getLastName(), customer.getPhoneNo());
            if (existingCustomer.isPresent()) {
                response.getErrMssg().add("User already exist by given details");
            } else {
                // set cart
                Cart cart = new Cart();
                cart.setCustomer(customer);
                customer.setCart(cart);
                // save customer
                Customer savedCustomer = customerRepository.save(customer);
                response.setResponseData(savedCustomer);
                response.setSuccess(true);
            }
        }
//        catch (ConstraintViolationException e) {  // if constraint voilated
//            response.getErrMssg().addAll(e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toList());
//        }
        catch (EmailDuplicationException e) { // email duplicated
            response.getErrMssg().add(e.getMessage());
        } catch (Exception e) {
            System.out.println("This is Exaception");
            if (e.getCause().getCause() instanceof ConstraintViolationException cv) {
                response.getErrMssg().addAll(cv.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
                        .toList());
            } else {
                response.getErrMssg().add("Customer not created");
                log.error("Error in createCustomer {}", e);
            }
        }
        return response;
    }

//    @Transactional(rollbackFor = ConstraintViolationException.class)
    @Override
    public Response updateCustomer(Customer customer) {
        Response response = new Response();
        try {
            // check if email is already present
            Optional<Customer> existingMail = customerRepository.findByEmail(customer.getEmail());
            // if email is present and phone no. not equal to given customer phone no
            if (existingMail.isPresent() && (!existingMail.get().getPhoneNo().equals(customer.getPhoneNo()))) {
                throw new EmailDuplicationException("Email already present");
            }
            // check if customer exist
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
        } catch (EmailDuplicationException e) { // email duplicated
            response.getErrMssg().add(e.getMessage());
        } catch (Exception e) {
            if (e.getCause().getCause() instanceof ConstraintViolationException cv) {
                response.getErrMssg().addAll(cv.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
                        .toList());
            } else {
                response.getErrMssg().add("Customer not updated");
                log.error("Error in updateCustomer {}", e);
            }
        }
        return response;
    }

    @Override
    public Response getCustomerById(int id) {
        Response response = new Response();
        try {
            // check if customer exist
            Optional<Customer> existingCustomer = customerRepository.findById(id);
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
            // check if customer exist
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
            // check if customer exist
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
            response.getErrMssg().add("User not deleted");
            log.error("Error in deleteCustomerById {}", e);
        }
        return response;
    }

    @Transactional
    @Override
    public Response getAllCustomers(String sortBy, String sortDirection, String filterValue, int pageNo,
                                    int pageSize) {
        Response response = new Response();
        try {
            // create a page with page no., page size, sort(direction,column)
            Pageable page = PageRequest.of(pageNo, pageSize,
                    Sort.by(sortDirection.equals("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));


            Page<Customer> customerPage = customerRepository.findAllByUserType(UserType.CUSTOMER, page);
            // if filter value given than check in the given columns
            if (filterValue.length() > 0) {
                customerPage = customerRepository.findAllByFirstNameLikeOrLastNameLikeOrEmailLikeOrPhoneNoOrAddressLikeAndUserType(
                        filterValue, filterValue, filterValue, filterValue, filterValue, UserType.CUSTOMER, page);
            }

            // if customers are present
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
            // check if customer exist
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
