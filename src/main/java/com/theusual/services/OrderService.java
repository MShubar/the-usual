package com.theusual.services;

import com.theusual.models.Order;
import com.theusual.dto.OrderRequest;
import com.theusual.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        order.setOrderType(request.getOrderType());
        order.setTotal(request.getTotal());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setUserId(request.getUserId());
        
        // Convert items to Map format for JSON storage
        List<Map<String, Object>> items = request.getItems().stream()
            .map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("price", item.getPrice());
                itemMap.put("customizations", item.getCustomizations());
                return itemMap;
            })
            .collect(Collectors.toList());
        
        order.setItems(items);
        
        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
