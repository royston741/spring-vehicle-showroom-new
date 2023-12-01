package com.showroom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Table(name = "showroom_details")
@Entity
public class ShowRoomDetails {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sequence-generator-s")
    @SequenceGenerator(
            name = "sequence-generator-s",
            sequenceName = "showroom_sequence",
            allocationSize = 1
    )
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

}
