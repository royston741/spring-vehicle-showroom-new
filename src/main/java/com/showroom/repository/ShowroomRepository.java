package com.showroom.repository;


import com.showroom.entity.ShowRoomDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowroomRepository extends JpaRepository<ShowRoomDetails,Integer> {
}
