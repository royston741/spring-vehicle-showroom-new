package com.showroom.dto;

import com.showroom.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {

    private Vehicle vehicle;
    private MultipartFile vehicleImageFile;
}
