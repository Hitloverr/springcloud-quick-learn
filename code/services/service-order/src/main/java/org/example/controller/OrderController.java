package org.example.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.example.feign.ProductFeignClient;
import org.example.order.bean.Order;
import org.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RefreshScope
public class OrderController {
    @Autowired
    OrderService orderService;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Value("${order.timeout:}")
    private String orderTimeout;

    //创建订单
    @GetMapping("/create")
    @SentinelResource(value = "createOrder")
    public Order createOrder(@RequestParam("userId") Long userId,
                             @RequestParam("productId") Long productId) {
        Order order = orderService.createOrder(productId, userId);
        return order;
    }

    @GetMapping("/timeout")
    public String getOrderTimeout() {
        return orderTimeout;
    }

}
