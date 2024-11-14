package com.chloetsai.springbootmall.rowmapper;

import com.chloetsai.springbootmall.model.OrderItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemMapper implements RowMapper<OrderItem> {

    @Override
    public OrderItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderItem orderItem = new OrderItem();

        orderItem.setOrder_item_id(rs.getInt("order_item_id"));
        orderItem.setOrder_id(rs.getInt("order_id"));
        orderItem.setProduct_id(rs.getInt("product_id"));
        orderItem.setQuantity(rs.getInt("quantity"));
        orderItem.setAmount(rs.getInt("amount"));

        // 可選擇擴充 orderItem 或者新增另一個 class，這邊選擇直接擴充
        orderItem.setProductName(rs.getString("product_name"));
        orderItem.setImageUrl(rs.getString("image_url"));

        return orderItem;
    }
}
