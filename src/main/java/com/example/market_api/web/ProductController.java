package com.example.market_api.web;

import com.example.market_api.common.dto.ApiResponse;
import com.example.market_api.common.dto.ProductDtos;
import com.example.market_api.domain.Product;
import com.example.market_api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody ProductDtos.CreateRequest req, @AuthenticationPrincipal Long userId){
        return ApiResponse.ok(productService.create(userId, req));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDtos.Response> get(@PathVariable("id")  Long id){
        return ApiResponse.ok(productService.get(id));
    }

    @PatchMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable("id")  Long id, @Valid @RequestBody ProductDtos.UpdateRequest req, @AuthenticationPrincipal Long userId){
        productService.update(id, userId, req);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id")  Long id, @AuthenticationPrincipal Long userId) {
        productService.delete(id, userId);
        return ApiResponse.ok(null);
    }

    @GetMapping
    public ApiResponse<Page<ProductDtos.Response>> list(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    ){
        Page<ProductDtos.Response> result = productService.search(query, sort, page, size);
        return ApiResponse.ok(result);

    }

}
