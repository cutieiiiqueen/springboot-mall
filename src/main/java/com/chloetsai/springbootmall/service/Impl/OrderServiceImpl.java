package com.chloetsai.springbootmall.service.Impl;

import com.chloetsai.springbootmall.dao.OrderDao;
import com.chloetsai.springbootmall.dao.ProductDao;
import com.chloetsai.springbootmall.dao.UserDao;
import com.chloetsai.springbootmall.dao.impl.ProductDaoImpl;
import com.chloetsai.springbootmall.dto.BuyItem;
import com.chloetsai.springbootmall.dto.CreateOrderRequest;
import com.chloetsai.springbootmall.dto.OrderQueryParams;
import com.chloetsai.springbootmall.model.Order;
import com.chloetsai.springbootmall.model.OrderItem;
import com.chloetsai.springbootmall.model.Product;
import com.chloetsai.springbootmall.model.User;
import com.chloetsai.springbootmall.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderServiceImpl implements OrderService {

    private final static Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private UserDao userDao;


    @Override
    public Integer countOrder(OrderQueryParams orderQueryParams) {
        return orderDao.countOrder(orderQueryParams);
    }

    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {
        // 找出符合這批條件的訂單
        List<Order> orderList = orderDao.getOrders(orderQueryParams);

        // 取得每個訂單所對應的訂單明細，並設定到該訂單中
        for(Order order : orderList) {

            List<OrderItem> orderItemList = orderDao.getOrderItemsByOrderId(order.getOrderId());

            // 將訂單明細列表放入訂單中
            order.setOrderItemList(orderItemList);
        }
        return orderList;
    }

    @Override
    public Order getOrderById(Integer orderId) {

        Order order = orderDao.getOrderById(orderId);

        List<OrderItem> orderItemList = orderDao.getOrderItemsByOrderId(orderId);

        // 合併 order 以及 orderItemList 一併返回給前端，這邊一樣可以擴充 order 類別或者新增另一個 class
        order.setOrderItemList(orderItemList);

        return order;
    }

    @Transactional
    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {

        //檢查 user 是否存在
        User user = userDao.getUserById(userId);

        if(user == null){
            log.warn("該 userId {} 不存在", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        //計算訂單總價 & 創建 OrderItem
        int totalAmount = 0;
        List<OrderItem> orderItemList = new ArrayList<>();

        //for loop 使用者購買的每一個商品
        for(BuyItem buyItem: createOrderRequest.getBuyItemList()){

            //使用 productDao.getProductById 方法，根據前端傳來的 productId 去 資料庫 查詢出這筆商品的資料
            Product product = productDao.getProductById(buyItem.getProductId());

            //檢查 product 是否存在、庫存是否充足
            if(product == null){
                log.warn("商品Id {} 不存在", buyItem.getProductId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            } else if (product.getStock() < buyItem.getQuantity()){
                log.warn("商品Id {} 庫存數量不足，無法購買。剩餘庫存 {}，欲購買數量 {}",
                        buyItem.getProductId(), product.getStock(), buyItem.getQuantity());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            //扣除商品庫存
            productDao.updateStock(product.getProductId(), product.getStock() - buyItem.getQuantity());

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

        //創建訂單 (Order & OrderItem)
        Integer orderId = orderDao.createOrder(userId, totalAmount);

        orderDao.createOrderItems(orderId, orderItemList);

        return orderId;
    }
}
