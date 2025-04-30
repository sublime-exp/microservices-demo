package com.sub.microservices.order.controller;

import com.sub.microservices.order.dto.OrderRequest;
import com.sub.microservices.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest order) {
        orderService.placeOrder(order);
        return "Order Placed Successfully!";
    }
}
