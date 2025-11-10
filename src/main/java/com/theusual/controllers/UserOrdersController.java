package com.theusual.controllers;

import com.theusual.models.Order;
import com.theusual.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserOrdersController {

    private final OrderService orderService;

    @PostMapping(value = "/user-orders", produces = "application/json")
    public ResponseEntity<List<Order>> getUserOrders(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        System.out.println("API user-orders for userId: " + userId);
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }
}
