package com.chloetsai.springbootmall.dao;

import com.chloetsai.springbootmall.model.Product;

public interface ProductDao {

    Product getProductById(int productId);
}
