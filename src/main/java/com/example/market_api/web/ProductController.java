package com.example.market_api.web;

import com.example.market_api.common.dto.ApiResponse;
import com.example.market_api.common.dto.ProductDtos;
import com.example.market_api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody ProductDtos.CreateRequest req){
        return ApiResponse.ok(productService.create(req));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDtos.Response> get(@PathVariable Long id){
        return ApiResponse.ok(productService.get(id));
    }

    @PatchMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ProductDtos.UpdateRequest req){
        productService.update(id, req);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.ok(null);
    }
}
