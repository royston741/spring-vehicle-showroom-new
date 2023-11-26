package com.showroom.service;

import com.showroom.constants.TwoWheelerType;
import com.showroom.constants.VehicleType;
import com.showroom.entity.Response;
import com.showroom.entity.Vehicle;

public interface VehicleService {
	public Response createVehicle(Vehicle Vehicle);

	public Response updateVehicle(Vehicle vehcile);

	public Response getVehicleById(int id);

	public Response deleteVehicleById(int id);

	public Response getAllVehicles(String columnToSort, String sortDirection, VehicleType vehicleType, TwoWheelerType twoWheelerType, Double startPrice, Double endPrice) ;

	public Response getMaxAndMinPriceOfVehicles() ;
}