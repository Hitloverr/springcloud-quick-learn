package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.order.bean.Order;
import org.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class OrderController {
    @Autowired
    OrderService orderService;

    //创建订单
    @GetMapping("/create")
    public Order createOrder(@RequestParam("userId") Long userId,
                             @RequestParam("productId") Long productId) {
        Order order = orderService.createOrder(productId, userId);
        return order;
    }

}
