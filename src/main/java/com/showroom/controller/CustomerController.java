package com.showroom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.showroom.entity.Customer;
import com.showroom.entity.Response;
import com.showroom.service.CustomerService;

@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("customer")
public class CustomerController {

	@Autowired
	CustomerService customerService;

	@PostMapping("/createCustomer")
	public ResponseEntity<Response> createCustomer(@RequestBody Customer customer) {
		Response response = customerService.createCustomer(customer);
		return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@GetMapping("/getCustomerById")
	public ResponseEntity<Response> getCustomerById(@RequestParam(name = "id",defaultValue = "0") int id) {
		Response response = customerService.getCustomerById(id);
		return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@GetMapping("/getAllCustomers")
	public ResponseEntity<Response> getAllCustomers(
			@RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
			@RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection,
			@RequestParam(name = "filterValue", defaultValue = "") String filterValue,
			@RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
			@RequestParam(name = "pageSize", defaultValue = "5") int pageSize

	) {
		Response response = customerService.getAllCustomers(sortBy, sortDirection, filterValue,  pageNo, pageSize);
		return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@GetMapping("/getCustomerByFirstNameAndLastNameAndPhoneNo")
	public ResponseEntity<Response> getCustomerByFirstNameAndLastNameAndPhoneNo(
			@RequestParam(name = "firstName", defaultValue = "") String firstName,
			@RequestParam(name = "lastName", defaultValue = "") String lastName,
			@RequestParam(name = "phoneNo", defaultValue = "") String phoneNo) {
		Response response = customerService.getCustomerByFirstNameAndLastNameAndPhoneNo(firstName, lastName, phoneNo);
		return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@PutMapping("/updateCustomer")
	public ResponseEntity<Response> updateCustomer(@RequestBody Customer customer) {
		Response response = customerService.updateCustomer(customer);
		return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@DeleteMapping("/deleteCustomerById/{id}")
	public ResponseEntity<Response> deleteCustomerById(@PathVariable(name = "id") int id) {
		Response response = customerService.deleteCustomerById(id);
		return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}


	@GetMapping("/logIn")
	public ResponseEntity<Response> logIn(@RequestParam(name="name",defaultValue = "") String name,@RequestParam(name="password",defaultValue = "") String password) {
		Response response = customerService.logIn(name,password);
		return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@GetMapping("/getOtpToResetPassword")
	public ResponseEntity<Response> getOtpToResetPassword(@RequestParam(name="email",defaultValue = "") String email) {
		Response response = customerService.getOtpToResetPassword(email);
		return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@GetMapping("/validateOtpCode")
	public ResponseEntity<Response> validateOtpCode(@RequestParam(name="otp",defaultValue = "0") Integer otpCode,
													@RequestParam(name="email",defaultValue = "") String email) {
		Response response = customerService.validateOtpCode(otpCode,email);
		return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@PostMapping("/resetPassword")
	public ResponseEntity<Response> resetPassword(@RequestParam(name="email",defaultValue = "") String email,
												  @RequestParam(name="password",defaultValue = "") String password) {
		Response response = customerService.resetPassword(email,password);
		return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
}
