package com.showroom.serviceImpl;

import java.util.*;
import java.util.stream.Collectors;

import com.showroom.constants.EmailConstants;
import com.showroom.constants.UserType;
import com.showroom.dto.EmailDto;
import com.showroom.dto.ResetPasswordDto;
import com.showroom.entity.Cart;
import com.showroom.exception.EmailDuplicationException;
import com.showroom.service.EmailService;
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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    EmailService emailService;

    Map<String, Integer> otpList = new HashMap<>();

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
            if (existingMail.isPresent() && ((!existingMail.get().getId().equals(customer.getId())  ))) {
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

//    @Transactional
    @Override
    public Response getAllCustomers(String sortBy, String sortDirection, String filterValue, int pageNo,
                                    int pageSize) {
        Response response = new Response();
        try {
            // create a page with page no., page size, sort(direction,column)
            Pageable page = PageRequest.of(pageNo, pageSize,
                    Sort.by(sortDirection.equals("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));

            Page<Customer> customerPage = null;
            if (filterValue.length() == 0) {
                customerPage = customerRepository.findAllByUserType(UserType.CUSTOMER, page);
                // if filter value given than check in the given columns
            } else if (filterValue.length() > 0) {
                customerPage = customerRepository.filterCustomerList(
                        filterValue,filterValue,filterValue,page);
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

//    @Transactional
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
            log.error("Error in logIn {}", e);
        }
        return response;
    }

    @Override
    public Response getOtpToResetPassword(String email) {
        Response response = new Response();
        try {
            // check if customer exist
            Optional<Customer> existingEmail = customerRepository.findByEmail(email);
            // if exist
            if (existingEmail.isPresent()) {
                Random randomNumber = new Random();
                int generatedOtp = randomNumber.nextInt(10000);

                String emailId = existingEmail.get().getEmail();
                otpList.put(emailId, generatedOtp);
//                System.out.println("List is :"+otpList);
                EmailDto newEmail = new EmailDto();
                // send to
                newEmail.setEmailTo(emailId);
                // set subject
                newEmail.setSubject(EmailConstants.RESET_PASSWORD_OTP);
                // text
                newEmail.setTextMessage("Your otp to rest password is : " + generatedOtp);
                // send mail
                emailService.sendMail(newEmail);


                expireOtpAfter5Min(email, generatedOtp);

                response.setResponseData(existingEmail.get().getEmail());
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("Email does not exist.");
            }
        } catch (Exception e) {
            response.getErrMssg().add("Email was not sent.");
            log.error("Error in getOtpToResetPassword {}", e);
        }
        return response;
    }

    public void expireOtpAfter5Min(String email, Integer otp) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               otpList.remove(email);
                               log.info("Expired otp ---->" + otp + "------>" + email);
                           }
                       }
                , 5 * 60 * 1000);
    }

    @Override
    public Response validateOtpCode(Integer otpCode,String email) {
        Response response = new Response();
        try {

            List<ResetPasswordDto> filteredOtp =
                    otpList.entrySet().stream().
                            filter(otp -> otp.getValue().equals(otpCode)&&otp.getKey().equals(email)).
                            map(otpEntry -> {
                                ResetPasswordDto resetPasswordData = new ResetPasswordDto();
                                resetPasswordData.setEmail(otpEntry.getKey());
                                resetPasswordData.setOtp(otpEntry.getValue());
                                return resetPasswordData;
                            }).toList();
//            System.out.println(filteredOtp);

            if (filteredOtp.isEmpty()) {
                response.getErrMssg().add("Wrong OTP code");
            } else {
                String key = filteredOtp.get(0).getEmail();
                otpList.remove(key);
                response.setSuccess(true);
            }
        }catch (Exception e) {
            response.getErrMssg().add("OTP service is down.");
            log.error("Error in validateOtpCode {}", e);
        }
        return response;
    }

    @Override
    public Response resetPassword(String email, String newPassword) {
        Response response = new Response();
        try {
            // check if email is already present
            Optional<Customer> checkIfCustomerExistByEmail = customerRepository.findByEmail(email);

            // if exist
            if (checkIfCustomerExistByEmail.isPresent()) {
                Customer customer = checkIfCustomerExistByEmail.get();
                customer.setPassword(newPassword);
                // update customer password
                Customer updatedCustomerPassword = customerRepository.save(customer);

                EmailDto newEmail = new EmailDto();
                // send to
                newEmail.setEmailTo(checkIfCustomerExistByEmail.get().getEmail());
                // set subject
                newEmail.setSubject(EmailConstants.RESET_OTP_SUCCESS);
                // text
                newEmail.setTextMessage("Your password has been reset successfully for MyDrive account ");
//                 send mail
                emailService.sendMail(newEmail);

                response.setResponseData(updatedCustomerPassword);
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("Customer does not exist by email " + email);
            }
        } catch (Exception e) {
            response.getErrMssg().add("Password not updated");
            log.error("Error in updateCustomer {}", e);
        }
        return response;
    }

}
