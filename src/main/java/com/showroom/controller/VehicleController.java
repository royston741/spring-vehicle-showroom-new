package com.showroom.controller;


import com.showroom.constants.Color;
import com.showroom.constants.FuelType;
import com.showroom.constants.TwoWheelerType;
import com.showroom.constants.VehicleType;
import com.showroom.entity.Customer;
import com.showroom.entity.MiscellaneousCost;
import com.showroom.entity.Response;
import com.showroom.entity.Vehicle;
import com.showroom.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("vehicle")
public class VehicleController {

    @Autowired
    VehicleService vehicleService;

    @PostMapping("/createVehicle")
    public ResponseEntity<Response> createVehicle(@RequestBody Vehicle vehicle) {
        Response response = vehicleService.createVehicle(vehicle);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getVehicleById/{id}")
    public ResponseEntity<Response> getVehicleById(@PathVariable(name = "id") int id) {
        Response response = vehicleService.getVehicleById(id);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("getAllVehicles")
    public ResponseEntity<Response> getAllVehicles(
            @RequestParam(name = "column", defaultValue = "id") String column,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "vehicleType",defaultValue = "") VehicleType vehicleType,
            @RequestParam(name = "twoWheelerType",defaultValue = "") TwoWheelerType twoWheelerType,
            @RequestParam(name = "startPrice",defaultValue = "0") Double startPrice,
            @RequestParam(name = "endPrice",defaultValue = "0") Double endPrice) {
            Response response = vehicleService.getAllVehicles(column, direction,vehicleType,twoWheelerType,startPrice,endPrice);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deleteVehicleById/{id}")
    public ResponseEntity<Response> deleteVehicleById(@PathVariable(name = "id") int id) {
        Response response = vehicleService.deleteVehicleById(id);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PutMapping("/updateVehicle")
    public ResponseEntity<Response> updateVehicle(@RequestBody Vehicle vehicle) {
        Response response = vehicleService.updateVehicle(vehicle);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }


    @GetMapping("getMaxAndMinPrice")
    public ResponseEntity<Response> getMaxAndMinPrice() {
        Response response = vehicleService.getMaxAndMinPriceOfVehicles();
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getExtraChargeByColor")
    public ResponseEntity<Response> getExtraChargeByColor(){
        List<MiscellaneousCost> colorCharges = Arrays.stream(Color.values()).map(c -> new MiscellaneousCost(c.name(), c.getCharges())).toList();
        Response response=new Response(true,new ArrayList<>(),colorCharges);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getDiscountByFuelType")
    public ResponseEntity<Response> getDiscountByFuelType(){
        List<MiscellaneousCost> fuelTypeDiscount = Arrays.stream(FuelType.values()).map(c -> new MiscellaneousCost(c.name(), c.getDiscount())).toList();
        Response response = new Response(true,new ArrayList<>(),fuelTypeDiscount);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getDiscountByTwoWheelerType")
    public ResponseEntity<Response> getDiscountByTwoWheelerType(){
        List<MiscellaneousCost> twoWheelerTypeDiscount = Arrays.stream(TwoWheelerType.values()).map(c -> new MiscellaneousCost(c.name(),c.getDiscount())).toList();
        Response response = new Response(true,new ArrayList<>(),twoWheelerTypeDiscount);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }


    @GetMapping("/getVehicleType")
    public ResponseEntity<Response> getVehicleType(){
        List<VehicleType> twoWheelerTypeDiscount = Arrays.stream(VehicleType.values()).toList();
        Response response = new Response(true,new ArrayList<>(),twoWheelerTypeDiscount);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}
