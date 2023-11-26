package com.showroom.service;

import com.showroom.entity.Customer;
import com.showroom.entity.Order;
import com.showroom.entity.ShowRoomDetails;

public interface PdfGeneratorService {

    public byte[] generatePdf(Customer customer, Order order, ShowRoomDetails showRoomDetails);
}
