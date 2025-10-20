package com.theusual.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String image;
    private List<OptionDTO> sizes;
    private List<OptionDTO> milks;
    private List<OptionDTO> shots;
    private List<OptionDTO> mixers;

    @Data
    public static class OptionDTO {
        private String key;
        private String value;
    }
}