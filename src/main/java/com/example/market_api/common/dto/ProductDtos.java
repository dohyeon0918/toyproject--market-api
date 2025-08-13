package com.example.market_api.common.dto;

import com.example.market_api.domain.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ProductDtos {

    public record CreateRequest(
        @NotBlank(message = "제목은 필수") @Size(max=100) String title,
        @NotBlank(message = "설명은 필수") String description,
        @NotNull(message = "가격은 필수") @Positive Integer price
    ){}

    public record UpdateRequest(
            @Size(max=100) String title,
            String description,
            @Positive Integer price
    ){}

    public record Response(
            Long id, String title, String description, Integer price,
            Product.Status status, Long sellerId
    ){
        public static Response from(Product p) {
            return new Response(p.getId(), p.getTitle(), p.getDescription(),
                    p.getPrice(), p.getStatus(), p.getSellerId());
        }
    }
}
