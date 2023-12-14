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
import jakarta.mail.Multipart;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("vehicle")
public class VehicleController {

    @Autowired
    VehicleService vehicleService;

//    @PostMapping(value = "/createVehicle", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseEntity<Response> createVehicle(@RequestPart Vehicle vehicle, @RequestPart MultipartFile vehicleImageFile) throws IOException {
        @PostMapping(value = "/createVehicle")
        public ResponseEntity<Response> createVehicle(@RequestBody Vehicle vehicle) {

//            System.out.println(vehicle);
//        byte[] array=vehicleImageFile.getBytes();
//        System.out.println(array);
//        ByteArrayInputStream bos=new ByteArrayInputStream(array);
//        BufferedImage newImage = ImageIO.read(bos);
//        // write output image
//      boolean b=  ImageIO.write(newImage, "jpg", new File(vehicle.getName()+".jpg"));
//        System.out.println(b);
//        vehicle.setVehicleImg(vehicleImageFile.getBytes());
        Response response = vehicleService.createVehicle(vehicle);
//        Response response=new Response();
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getVehicleById")
    public ResponseEntity<Response> getVehicleById(@RequestParam(name = "id",defaultValue = "0") int id) {
        Response response = vehicleService.getVehicleById(id);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

//    @GetMapping("/getImg")
//    public ResponseEntity<ByteArrayInputStream> getImg(@RequestParam(name = "id",defaultValue = "0") int id, HttpServletRequest request, HttpServletResponse respons) {
//        ByteArrayInputStream response = vehicleService.getImg(id);
//        return new ResponseEntity<>(response,  HttpStatus.OK );
//    }

    @GetMapping("getAllVehicles")
    public ResponseEntity<Response> getAllVehicles(
            @RequestParam(name = "column", defaultValue = "id") String column,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "vehicleType",defaultValue = "") VehicleType vehicleType,
            @RequestParam(name = "twoWheelerType",defaultValue = "") TwoWheelerType twoWheelerType,
            @RequestParam(name = "startPrice",defaultValue = "0") Double startPrice,
            @RequestParam(name = "endPrice",defaultValue = "0") Double endPrice,
            @RequestParam(name = "pageNumber",defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize",defaultValue = "5") int pageSize
            ) {
            Response response = vehicleService.getAllVehicles(column, direction,vehicleType,twoWheelerType,startPrice,endPrice,pageNumber,pageSize);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deleteVehicleById")
    public ResponseEntity<Response> deleteVehicleById(@RequestParam(name = "id",defaultValue = "0") int id) {
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
