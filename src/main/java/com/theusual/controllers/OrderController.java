package com.theusual.controllers;

import com.theusual.models.Order;
import com.theusual.models.OrderItem;
import com.theusual.models.Product;
import com.theusual.repositories.OrderRepository;
import com.theusual.repositories.OrderItemRepository;
import com.theusual.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> orderData) {
        try {
            Order order = new Order();
            order.setOrderType((String) orderData.get("orderType"));
            order.setTotal(((Number) orderData.get("total")).doubleValue());
            order.setPaymentMethod((String) orderData.get("paymentMethod"));
            order.setDeliveryAddress((String) orderData.get("deliveryAddress"));
            
            // Check for pending order for this user (assuming userId is provided)
            String userId = (String) orderData.get("userId"); // phone number from frontend
            if (userId != null) {
                List<Order> pendingOrders = orderRepository.findByUserIdAndStatus(userId, "pending");
                if (!pendingOrders.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "You have a pending order. Please cancel or wait for it to be completed."));
                }
            }
            
            Order savedOrder = orderRepository.save(order);
            
            // Create order items
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
            for (Map<String, Object> item : items) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(savedOrder);
                
                Long productId = ((Number) item.get("id")).longValue();
                Product product = productRepository.findById(productId).orElse(null);
                if (product != null) {
                    orderItem.setProduct(product);
                    orderItem.setQuantity(((Number) item.get("quantity")).intValue());
                    orderItem.setPrice(((Number) item.get("price")).doubleValue());
                    orderItem.setCustomizations(item.get("customizations").toString());
                    orderItemRepository.save(orderItem);
                }
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "orderNumber", savedOrder.getOrderNumber(),
                "orderId", savedOrder.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<Order>> getAllOrders() {
        long startTime = System.currentTimeMillis();
        try {
            List<Order> orders = orderRepository.findAllWithItems(); // Use fetch join
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("GET /orders took " + duration + "ms");
            System.out.println("Total orders returned: " + orders.size());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping(params = "userId")
    @Cacheable(value = "userOrders", key = "#userId")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Order>> getOrdersByUserId(@RequestParam String userId) {
        long startTime = System.currentTimeMillis();
        try {
            List<Order> orders = orderRepository.findByUserIdWithItems(userId); // Use fetch join
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("GET /orders?userId=" + userId + " took " + duration + "ms");
            System.out.println("Total orders for user: " + orders.size());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            Order order = orderRepository.findByIdWithItems(id);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();
        if ("delivered".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Delivered orders cannot be cancelled."));
        }
        order.setStatus("cancelled");
        orderRepository.save(order);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/{id}/not-attended")
    public ResponseEntity<?> markNotAttended(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();
        order.setAttended(false);
        orderRepository.save(order);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/{id}/receipt")
    public ResponseEntity<?> markReceiptReceived(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();
        if (!"delivered".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Receipt can only be marked after delivery."));
        }
        order.setReceiptReceived(true);
        orderRepository.save(order);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
