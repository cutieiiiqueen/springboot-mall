package com.chloetsai.springbootmall.dao.impl;

import com.chloetsai.springbootmall.dao.OrderDao;
import com.chloetsai.springbootmall.dto.OrderQueryParams;
import com.chloetsai.springbootmall.model.Order;
import com.chloetsai.springbootmall.model.OrderItem;
import com.chloetsai.springbootmall.rowmapper.OrderItemMapper;
import com.chloetsai.springbootmall.rowmapper.OrderRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderDaoImpl implements OrderDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    public Integer countOrder(OrderQueryParams orderQueryParams) {

        String sql = "SELECT count(*) FROM `order` WHERE 1=1";

        Map<String, Object> map = new HashMap<>();

        // 查詢條件
        sql = addFilteringSql(sql, map, orderQueryParams);

        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return total;
    }

    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {

        String sql = "SELECT `order`.order_id, `order`.user_id, total_amount, `order`.created_date, `order`.last_modified_date" +
                " FROM `order`" +
                "WHERE 1=1 ";
        Map<String, Object> map = new HashMap<>();

        // 查詢條件
        sql = addFilteringSql(sql, map, orderQueryParams);

        // 排序
        sql = sql + " ORDER BY created_date DESC";

        // 分頁
        sql = sql + " LIMIT :limit OFFSET :offset";
        map.put("limit", orderQueryParams.getLimit());
        map.put("offset", orderQueryParams.getOffset());

        List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderRowMapper());

        return orderList;
    }

    @Override
    public Order getOrderById(Integer orderId) {

        String sql = "SELECT `order`.order_id, `order`.user_id, `order`.total_amount, `order`.created_date, `order`.last_modified_date " +
                "FROM `order` " +
                "WHERE `order`.order_id = :orderId";

        Map<String,Object> map = new HashMap<>();
        map.put("orderId",orderId);

        List<Order> orderList = namedParameterJdbcTemplate.query(sql,map, new OrderRowMapper());

        if(orderList.size()>0){
            return orderList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {

        String sql = "SELECT order_item_id, order_item.order_id, order_item.product_id, quantity, amount, " +
                "product.product_name, product.image_url " +
                "FROM order_item " +
                "LEFT JOIN product ON order_item.product_id = product.product_id " +
                "WHERE order_item.order_id = :orderId";

        Map<String, Object> map = new HashMap<>();
        map.put("orderId",orderId);

        List<OrderItem> orderItemList = namedParameterJdbcTemplate.query(sql, map, new OrderItemMapper());

        return orderItemList;
    }

    @Override
    public Integer createOrder(Integer userId, Integer totalAmount) {
        String sql = "INSERT INTO `order` (user_id, total_amount, created_date, last_modified_date) " +
                "VALUES (:userId, :totalAmount, :createdDate, :lastModifiedDate)";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("totalAmount", totalAmount);

        Date now = new Date();
        map.put("createdDate", now);
        map.put("lastModifiedDate", now);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        int orderId = keyHolder.getKey().intValue();

        return orderId;
    }

    public void createOrderItems(Integer orderId, List<OrderItem> orderItemList){

//        // 方法 1 : 使用 for loop 一條一條 sql 加入數據，效率較低
//        for (OrderItem orderItem : orderItemList) {
//
//            String sql = "INSERT INTO order_item (order_id, product_id, quantity, amount)" +
//                    " VALUES (:order_id, :product_id, :quantity, :amount)" ;
//
//            Map<String, Object> map = new HashMap<>();
//            map.put("order_id", orderId);
//            map.put("product_id", orderItem.getProduct_id());
//            map.put("quantity", orderItem.getQuantity());
//            map.put("amount", orderItem.getAmount());
//
//            namedParameterJdbcTemplate.update(sql, map);
//        }

        // 方法 2 : 使用 batchUpdate 一次性加入數據，效率更高
        String sql = "INSERT INTO order_item (order_id, product_id, quantity, amount)" +
                " VALUES (:order_id, :product_id, :quantity, :amount)" ;

        MapSqlParameterSource[] parameterSources = new MapSqlParameterSource[orderItemList.size()];

        for(int i = 0; i < orderItemList.size(); i++){
            OrderItem orderItem = orderItemList.get(i);

            parameterSources[i] = new MapSqlParameterSource();
            parameterSources[i].addValue("order_id", orderId);
            parameterSources[i].addValue("product_id", orderItem.getProduct_id());
            parameterSources[i].addValue("quantity", orderItem.getQuantity());
            parameterSources[i].addValue("amount", orderItem.getAmount());
        }

        namedParameterJdbcTemplate.batchUpdate(sql, parameterSources);
    }

    // 提煉程式
    private String addFilteringSql(String sql, Map<String, Object> map, OrderQueryParams orderQueryParams) {

        // 如果 userId 不為 null 則將條件拼接上去，並將 userId put 到 map 裡面
        if(orderQueryParams.getUserId() != null){
            sql += " AND `order`.`user_id` = :userId ";
            map.put("userId", orderQueryParams.getUserId());
        }
        return sql;
    }
}
