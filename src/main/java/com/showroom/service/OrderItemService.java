package com.showroom.service;

import com.showroom.entity.OrderItem;
import com.showroom.entity.Response;

import java.time.LocalDate;
import java.util.List;

public interface OrderItemService {

    public Response getAllOrderedItemOfCustomerByCustomerId( int customerId,String sortBy,
                                                            String sortDirection,
                                                           int pageNo, int pageSize);

    public Response getOrderItemById(int orderItemId);

    public byte[] generateExcelOfOrderedVehicle(String filterBy, LocalDate start, LocalDate end, int startQuantity, int endQuantity) ;
}
