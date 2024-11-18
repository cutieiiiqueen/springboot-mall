package com.chloetsai.springbootmall.service;

import com.chloetsai.springbootmall.dto.CreateOrderRequest;
import com.chloetsai.springbootmall.dto.OrderQueryParams;
import com.chloetsai.springbootmall.model.Order;

import java.util.List;

public interface OrderService {

    Integer countOrder (OrderQueryParams orderQueryParams);

    List<Order> getOrders(OrderQueryParams orderQueryParams);

    Order getOrderById(Integer orderId);

    Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest);

}
