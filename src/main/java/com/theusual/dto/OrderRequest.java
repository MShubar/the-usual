package com.theusual.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class OrderRequest {
    private String orderType;
    private Double total;
    private String paymentMethod;
    private Map<String, Object> deliveryAddress;
    private String userId;
    private List<OrderItemDTO> items;
    
    @Data
    public static class OrderItemDTO {
        private Long id;
        private Integer quantity;
        private Double price;
        private Map<String, String> customizations;
    }
}
