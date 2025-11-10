package com.theusual.controllers;

import com.theusual.models.Order;
import com.theusual.dto.OrderRequest;
import com.theusual.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        Order order = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // Simpler POST endpoint path (avoiding nested paths that Azure might rewrite)
    @PostMapping(value = "/by-user", produces = "application/json")
    public ResponseEntity<List<Order>> getOrdersByUserId(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        System.out.println("POST by-user for userId: " + userId);
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @GetMapping(value = "/user/{userId}", produces = "application/json")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable String userId) {
        try {
            // Decode the userId to handle %2B -> +
            String decodedUserId = URLDecoder.decode(userId, StandardCharsets.UTF_8);
            System.out.println("Original userId: " + userId);
            System.out.println("Decoded userId: " + decodedUserId);
            return ResponseEntity.ok(orderService.getOrdersByUserId(decodedUserId));
        } catch (Exception e) {
            System.err.println("Error processing userId: " + e.getMessage());
            return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
        }
    }

    // Add alternative endpoint using query parameter (more reliable for special characters)
    @GetMapping(value = "/user", produces = "application/json")
    public ResponseEntity<List<Order>> getUserOrdersByQuery(@RequestParam String userId) {
        System.out.println("Query param userId: " + userId);
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Order cancelledOrder = orderService.cancelOrder(id);
            response.put("success", true);
            response.put("message", "Order cancelled successfully");
            response.put("order", cancelledOrder);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Order not found or could not be cancelled");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
