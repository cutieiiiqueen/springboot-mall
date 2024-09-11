package com.chloetsai.springbootmall.service;

import com.chloetsai.springbootmall.dto.ProductRequest;
import com.chloetsai.springbootmall.model.Product;

public interface ProductService {

    Product getProductById(Integer productId);

    Integer createProduct(ProductRequest productRequest);

    void updateProduct(Integer productId, ProductRequest productRequest);
}
