package com.chloetsai.springbootmall.controller;

import com.chloetsai.springbootmall.dto.CreateOrderRequest;
import com.chloetsai.springbootmall.dto.OrderQueryParams;
import com.chloetsai.springbootmall.model.Order;
import com.chloetsai.springbootmall.service.OrderService;
import com.chloetsai.springbootmall.util.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<?> getOrders(@PathVariable Integer userId,
                                       @RequestParam(defaultValue = "10") @Max(1000) @Min(0) Integer limit,
                                       @RequestParam(defaultValue = "0") @Min(0) Integer offset){

        OrderQueryParams orderQueryParams = new OrderQueryParams();
        orderQueryParams.setUserId(userId);
        orderQueryParams.setLimit(limit);
        orderQueryParams.setOffset(offset);

        // 取得 order List
        List<Order> orderList = orderService.getOrders(orderQueryParams);

        // 取得 order 總數量
        Integer count = orderService.countOrder(orderQueryParams);

        // 分頁
        Page<Order> page = new Page<>();
        page.setLimit(limit);
        page.setOffset(offset);
        page.setTotal(count);
        page.setResults(orderList);

        return ResponseEntity.status(HttpStatus.OK).body(page);

    }


    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<Order> createOrder(@PathVariable Integer userId,
                                         @RequestBody @Valid CreateOrderRequest createOrderRequest){

        // orderService.createOrder這方法會返回一個orderId的值
        Integer orderId = orderService.createOrder(userId, createOrderRequest);

        // 將整筆訂單資訊查出來回傳給前端
        Order order = orderService.getOrderById(orderId);

        // 將此筆建立好的 order 回傳給前端
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }


}
