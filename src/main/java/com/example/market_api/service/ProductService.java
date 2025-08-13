package com.example.market_api.service;

import com.example.market_api.common.dto.ProductDtos;
import com.example.market_api.common.exception.BadRequestException;
import com.example.market_api.common.exception.NotFoundException;
import com.example.market_api.domain.Product;
import com.example.market_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Long create(Long userId, ProductDtos.CreateRequest req){
        Product p = new Product();
        p.setTitle(req.title());
        p.setDescription(req.description());
        p.setPrice(req.price());
        p.setSellerId(userId);
        return productRepository.save(p).getId();
    }

    public Product getEntity(long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다"));
    }

    public ProductDtos.Response get(Long id){
        return ProductDtos.Response.from(getEntity(id));
    }

    @Transactional
    public void update(Long id, Long userId, ProductDtos.UpdateRequest req){
        Product p = getEntity(id);
        if(!p.getSellerId().equals(userId)){
            throw new BadRequestException("본인 상품만 수정할 수 있습니다.");
        }
        if(req.title() != null) p.setTitle(req.title());
        if (req.description() != null) p.setDescription(req.description());
        if (req.price() != null) p.setPrice(req.price());
    }

    @Transactional
    public void delete(Long id, Long userId){
        Product p = getEntity(id);
        if(!p.getSellerId().equals(userId)){
            throw new BadRequestException("본인 상품만 삭제할 수 있습니다.");
        }
        productRepository.delete(p);
    }
}
