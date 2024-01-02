package com.showroom.entity;

import com.showroom.constants.Color;
import jakarta.persistence.*;
import lombok.*;

//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@ToString
//@Table(name = "vehicle")
//@Entity
public class VehicleImg {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-generator-img")
    @SequenceGenerator(name = "sequence-generator-img", sequenceName = "img_sequence", allocationSize = 1)
    @Column(name = "vehicle_img__id")
    private Integer id;

    @Lob
    @Column(name = "vehicle_img")
    private String 	vehicleImg;

    private Color color;
}
