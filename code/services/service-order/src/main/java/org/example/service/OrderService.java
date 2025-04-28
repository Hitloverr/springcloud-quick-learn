package org.example.service;


import org.example.order.bean.Order;

public interface OrderService {

    Order createOrder(Long productId, Long userId);
}
