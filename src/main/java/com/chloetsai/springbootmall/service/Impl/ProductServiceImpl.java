package com.chloetsai.springbootmall.service.Impl;

import com.chloetsai.springbootmall.dao.ProductDao;
import com.chloetsai.springbootmall.model.Product;
import com.chloetsai.springbootmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Override
    public Product getProductById(int productId) {
        return productDao.getProductById(productId);
    }
}
