package org.example.feign;


import org.example.feign.fallback.ProductFeignClientFallback;
import org.example.product.bean.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-product", fallback = ProductFeignClientFallback.class)
public interface ProductFeignClient {
    //mvc注解的两套使用逻辑
    //1、标注在Controller上，是接受这样的请求
    //2、标注在FeignClient上，是发送这样的请求
    @GetMapping("/product/{id}")
    public Product getProductById(@PathVariable("id") Long id);
}
