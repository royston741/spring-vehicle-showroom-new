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
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sequence-generator")
    @SequenceGenerator(
            name = "sequence-generator",
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
