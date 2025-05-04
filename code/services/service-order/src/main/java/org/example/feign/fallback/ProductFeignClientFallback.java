package org.example.feign.fallback;

import org.example.feign.ProductFeignClient;
import org.example.product.bean.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductFeignClientFallback implements ProductFeignClient {
    @Override
    public Product getProductById(Long id) {
        System.out.println("兜底回调....");
        Product product = new Product();

        return product;
    }
}
