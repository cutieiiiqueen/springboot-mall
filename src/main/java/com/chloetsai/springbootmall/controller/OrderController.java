package com.chloetsai.springbootmall.controller;

import com.chloetsai.springbootmall.dto.CreateOrderRequest;
import com.chloetsai.springbootmall.model.Order;
import com.chloetsai.springbootmall.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<?> createOrder(@PathVariable Integer userId,
                                         @RequestBody @Valid CreateOrderRequest createOrderRequest){

        // orderService.createOrder這方法會返回一個orderId的值
        Integer orderId = orderService.createOrder(userId, createOrderRequest);

        // 將整筆訂單資訊查出來回傳給前端
        Order order = orderService.getOrderById(orderId);

        // 將此筆建立好的 order 回傳給前端
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

}
