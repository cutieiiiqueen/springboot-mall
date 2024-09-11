package com.chloetsai.springbootmall.service;

import com.chloetsai.springbootmall.dto.ProductRequest;
import com.chloetsai.springbootmall.model.Product;

public interface ProductService {

    Product getProductById(int productId);

    Integer createProduct(ProductRequest productRequest);
}
