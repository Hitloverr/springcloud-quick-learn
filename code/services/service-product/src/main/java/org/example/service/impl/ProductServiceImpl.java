package org.example.service.impl;
import java.math.BigDecimal;
import org.example.product.bean.Product;
import org.example.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public Product getProductById(Long productId) {
        Product product = new Product();
        product.setId(productId);
        product.setPrice(new BigDecimal("99"));
        product.setProductName("苹果-"+productId);
        product.setNum(2);
        return product;
    }
}
