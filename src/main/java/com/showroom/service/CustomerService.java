package com.showroom.service;

import com.showroom.entity.Customer;
import com.showroom.entity.Response;

public interface CustomerService {

	public Response createCustomer(Customer customer);

	public Response updateCustomer(Customer customer);

	public Response getCustomerById(int id);

	public Response getCustomerByFirstNameAndLastNameAndPhoneNo(String firstName, String lastName, String phoneNo);

	public Response deleteCustomerById(int id);

	public Response getAllCustomers(String sortBy, String sortDirection, String filterValue,  int pageNo, int pageSize);

	public Response logIn(String name, String password) ;

	public Response getOtpToResetPassword(String email);

	public Response validateOtpCode(Integer otpCode);

	public Response resetPassword(String email,String password);
}
