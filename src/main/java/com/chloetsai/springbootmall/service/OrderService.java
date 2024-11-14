package com.chloetsai.springbootmall.service;

import com.chloetsai.springbootmall.dto.CreateOrderRequest;
import com.chloetsai.springbootmall.model.Order;

public interface OrderService {

    Order getOrderById(Integer orderId);

    Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest);

}
