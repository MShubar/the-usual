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

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        System.out.println("=== HEALTH CHECK ===");
        return ResponseEntity.ok("API is working");
    }

    @PostMapping(value = "/fetch-orders", produces = "application/json")
    public ResponseEntity<List<Order>> fetchOrders(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        System.out.println("=== API /fetch-orders REACHED ===");
        System.out.println("userId: " + userId);
        List<Order> orders = orderService.getOrdersByUserId(userId);
        System.out.println("Found " + orders.size() + " orders");
        return ResponseEntity.ok(orders);
    }
}
