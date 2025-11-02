package com.theusual.services;

import com.theusual.models.Order;
import com.theusual.models.OrderItem;
import com.theusual.dto.OrderRequest;
import com.theusual.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderType(orderRequest.getOrderType());
        order.setTotal(orderRequest.getTotal());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setDeliveryAddress(orderRequest.getDeliveryAddress());
        order.setUserId(orderRequest.getUserId());
        
        // Map items from DTO to OrderItem entities
        List<OrderItem> orderItems = orderRequest.getItems().stream()
            .map(itemDTO -> {
                OrderItem item = new OrderItem();
                item.setItemId(itemDTO.getId());
                item.setName(itemDTO.getName());
                item.setImage(itemDTO.getImage());
                item.setDescription(itemDTO.getDescription());
                item.setQuantity(itemDTO.getQuantity());
                item.setPrice(itemDTO.getPrice());
                item.setCustomizations(itemDTO.getCustomizations());
                return item;
            })
            .collect(Collectors.toList());
        
        order.setItems(orderItems);
        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order cancelOrder(Long id) {
        Order order = getOrderById(id);
        
        if (!"pending".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalStateException("Only pending orders can be cancelled");
        }
        
        order.setStatus("cancelled");
        return orderRepository.save(order);
    }
}
