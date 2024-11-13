package com.chloetsai.springbootmall.service.Impl;

import com.chloetsai.springbootmall.dao.OrderDao;
import com.chloetsai.springbootmall.dao.ProductDao;
import com.chloetsai.springbootmall.dao.impl.ProductDaoImpl;
import com.chloetsai.springbootmall.dto.BuyItem;
import com.chloetsai.springbootmall.dto.CreateOrderRequest;
import com.chloetsai.springbootmall.model.Order;
import com.chloetsai.springbootmall.model.OrderItem;
import com.chloetsai.springbootmall.model.Product;
import com.chloetsai.springbootmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    @Transactional
    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {

        //計算訂單總價 & 創建 OrderItem
        int totalAmount = 0;
        List<OrderItem> orderItemList = new ArrayList<>();

        //for loop 使用者購買的每一個商品
        for(BuyItem buyItem: createOrderRequest.getBuyItemList()){

            //使用 productDao.getProductById 方法，根據前端傳來的 productId 去 資料庫 查詢出這筆商品的資料
            Product product = productDao.getProductById(buyItem.getProductId());

            //將商品數量 * 此筆商品的價格 算出商品總價
            int amount = buyItem.getQuantity() * product.getPrice();
            totalAmount += amount;

            //轉換 BuyItem to OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct_id(buyItem.getProductId());
            orderItem.setQuantity(buyItem.getQuantity());
            orderItem.setAmount(amount);

            orderItemList.add(orderItem);
        }

        //創建訂單
        Integer orderId = orderDao.createOrder(userId, totalAmount);

        orderDao.createOrderItems(orderId, orderItemList);

        return orderId;
    }
}
