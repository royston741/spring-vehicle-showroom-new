package com.showroom.serviceImpl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.showroom.constants.TwoWheelerType;
import com.showroom.constants.VehicleType;
import com.showroom.entity.Response;
import com.showroom.entity.Vehicle;
import com.showroom.repository.VehicleRepository;
import com.showroom.service.VehicleService;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;

@Slf4j
@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    VehicleRepository vehicleRepository;

    @Transactional
    @Override
    public Response createVehicle(Vehicle vehicle) {
        Response response = new Response();
        try {
            Optional<Vehicle> existingVehicle = vehicleRepository.findByName(vehicle.getName());
            if (existingVehicle.isPresent()) {
                response.getErrMssg().add("Vehicle already exist by given name");
            } else {
                Vehicle savedVehicle = vehicleRepository.save(vehicle);
                response.setResponseData(savedVehicle);
                response.setSuccess(true);
            }
        } catch (Exception e) {
            response.getErrMssg().add("Vehicle not added");
//			if (e.getCause().getCause() instanceof ConstraintViolationException cv) {
//				response.getErrMssg().addAll(cv.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
//						.toList());
//			} else {
            log.error("Error occurred in createVehicle ", e);
//			}
        }
        return response;
    }

    @Override
    public Response updateVehicle(Vehicle vehicle) {
        Response response = new Response();
        try {
            // check if exist
            Optional<Vehicle> existingVehicle = vehicleRepository.findById(vehicle.getId());
            // if exist
            if (existingVehicle.isPresent()) {
                // update customer
                Vehicle updatedVehicle = vehicleRepository.save(vehicle);
                response.setResponseData(updatedVehicle);
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("Vehicle does not exist");
            }
        } catch (Exception e) {
            response.getErrMssg().add("Vehicle not updated");
//			if (e.getCause().getCause() instanceof ConstraintViolationException cv) {
//				response.getErrMssg().addAll(cv.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
//						.toList());
//			} else {
            log.error("Error in updateVehicle {}", e);
//			}
        }
        return response;
    }

    @Override
    public Response getVehicleById(int id) {
        Response response = new Response();
        try {
            // check if exist
            Optional<Vehicle> existingVehicle = vehicleRepository.findById(id);

            // if exist
            if (existingVehicle.isPresent()) {
                response.setResponseData(existingVehicle.get());
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("Vehicle does not exist by Id : " + id);
            }
        } catch (Exception e) {
            response.getErrMssg().add("Vehicle not found");
            log.error("Error in getVehicleById {}", e);
        }
        return response;
    }

//	@Override
//    public ByteArrayInputStream getImg(int id) {
//        byte[] img = vehicleRepository.findById(id).get().getVehicleImg();
//        ByteArrayInputStream bos = new ByteArrayInputStream(img);
//        return bos;
//    }

    @Override
    public Response deleteVehicleById(int id) {
        Response response = new Response();
        try {
            // check if exist
            boolean doesVehicleExist = vehicleRepository.existsById(id);
            // if exist
            if (doesVehicleExist) {
                vehicleRepository.deleteById(id);
                response.setResponseData("Vehicle deleted");
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("Vehicle does not exist by Id : " + id);
            }
        } catch (Exception e) {
            response.getErrMssg().add("Vehicle not found");
            log.error("Error in deleteVehicleById {}", e);
        }
        return response;
    }

    @Transactional
    @Override
    public Response getAllVehicles(
            String columnToSort, String sortDirection,
            VehicleType vehicleType, TwoWheelerType twoWheelerType,
            Double startPrice, Double endPrice,
            int pageNumber, int pageSize, String filterValue)
    {
        Response response = new Response();
        try {
            // sort ( direction, column)
            Sort sort = Sort.by(sortDirection.equals("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC, columnToSort);
            Pageable page = PageRequest.of(pageNumber, pageSize, sort);

            Page<Vehicle> vehicleList = null;

            if (filterValue.length() > 0) {
                vehicleList = searchForVehicleByVehicleName(filterValue, startPrice, endPrice, vehicleType, twoWheelerType, page);
            } else {
                // if start price given and end price
                if (startPrice >= 0 && endPrice >= 0 && vehicleType == null) {
                    vehicleList = vehicleRepository.findAllByPriceBetween(startPrice, endPrice, page);
                }

                // if vehicle type given
                if (vehicleType != null) {
                    // if two wheeler type given
                    if (twoWheelerType != null) {
                        vehicleList = vehicleRepository.findAllByPriceBetweenAndVehicleTypeAndTwoWheelerType(startPrice, endPrice, vehicleType, twoWheelerType, page);
                    }else {
                        vehicleList = vehicleRepository.findAllByPriceBetweenAndVehicleType(startPrice, endPrice, vehicleType, page);

                    }
                }
            }

            if (vehicleList.getTotalElements() > 0) {
                response.setResponseData(vehicleList);
                response.setSuccess(true);
            } else {
                response.getErrMssg().add("No vehicles to show");
            }
        } catch (Exception e) {
            response.getErrMssg().add("Vehicles not found");
            log.error("Error in getAllVehicles {}", e);
        }
        return response;
    }

    public Page<Vehicle> searchForVehicleByVehicleName(String filterValue, Double startPrice, Double endPrice, VehicleType vehicleType, TwoWheelerType twoWheelerType, Pageable page) {
        Page<Vehicle> resulltPage = null;
        if (vehicleType != null) {
            if (twoWheelerType != null) {
                resulltPage = vehicleRepository.findAllByPriceBetweenAndNameStartsWithIgnoreCaseAndVehicleTypeAndTwoWheelerType(startPrice, endPrice, filterValue, vehicleType, twoWheelerType, page);
            } else {
                resulltPage = vehicleRepository.findAllByPriceBetweenAndNameStartsWithIgnoreCaseAndVehicleType(startPrice, endPrice, filterValue, vehicleType, page);
            }
        } else {
            resulltPage = vehicleRepository.findAByPriceBetweenAndNameStartsWithIgnoreCase(startPrice, endPrice, filterValue, page);
        }
        return resulltPage;
    }

    @Override
    public Response getMaxAndMinPriceOfVehicles() {
        Response response = new Response();
        try {
            // check if exist
            Double maxPrice = vehicleRepository.findHighestPrice();
            Double minPrice = vehicleRepository.findLowestPrice();
            response.setResponseData(List.of(maxPrice, minPrice));
            response.setSuccess(true);
        } catch (Exception e) {
            response.getErrMssg().add("No value");
            log.error("Error in getMaxAndMinPriceOAvailableVehicles {}", e);
        }
        return response;
    }


}