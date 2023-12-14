package com.showroom.repository;

import java.util.List;
import java.util.Optional;

import com.showroom.constants.TwoWheelerType;
import com.showroom.constants.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.showroom.entity.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

	public Optional<Vehicle> findByName(String name);

	public Page<Vehicle> findAllByPriceBetween(Double startPrice, Double endPrice, Pageable pageable);

	public Page<Vehicle> findAllByPriceBetweenAndVehicleType(Double startPrice, Double endPrice, VehicleType vehicleType,  Pageable pageable);

	public Page<Vehicle> findAllByPriceBetweenAndVehicleTypeAndTwoWheelerType(Double startPrice, Double endPrice, VehicleType vehicleType, TwoWheelerType twoWheelerType,  Pageable pageable);

	@Query("select max(v.price) from Vehicle v")
	public Double findHighestPrice();

	@Query("select min(v.price) from Vehicle v")
	public Double findLowestPrice();
//	@Query("SELECT v FROM Vehicle v WHERE " + "LOWER(v.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
//			+ "OR v.vehicleType = :vehicleType " + "OR v.twoWheelerType = :twoWheelerType")
//	Page<Vehicle> findAllByNameOrVehicleTypeOrTwoWheelerType(@Param("name") String name,
//			@Param("vehicleType") VehicleType vehicleType, @Param("twoWheelerType") TwoWheelerType twoWheelerType,
//			Pageable pageable);
}
