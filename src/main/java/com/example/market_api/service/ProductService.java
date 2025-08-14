package com.example.market_api.service;

import com.example.market_api.common.dto.ProductDtos;
import com.example.market_api.common.exception.BadRequestException;
import com.example.market_api.common.exception.NotFoundException;
import com.example.market_api.domain.Product;
import com.example.market_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    // 허용할 정렬 필드 (화이트리스트)
    private static final Set<String> ALLOW_SORT = Set.of("price","createdAt");


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

    //정렬할 것, 정렬 순서 파싱 + 검증, 기본값
    private Sort parseSortOrDefault(String sortParam){
        if (sortParam == null|| sortParam.isBlank()){
            return Sort.by("createdAt").descending();
        }

        String[] parts = sortParam.split(",",2);
        String key = parts[0].trim();
        String dir = (parts.length>1?parts[1].trim().toLowerCase():"desc");

        if(!ALLOW_SORT.contains(key)){
            throw new BadRequestException("정렬 필드가 허용되지 않습니다: " + key);
        }
        if ("asc".equals(dir)) return Sort.by(key).ascending();
        if ("desc".equals(dir)) return Sort.by(key).descending();

        return Sort.by(key).descending();
    }

    // 검색
    public Page<ProductDtos.Response> search(String query,String sort, int page, int size){
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        Pageable pageable = PageRequest.of(safePage, safeSize, parseSortOrDefault(sort));

        Page<Product> pageResult = (query == null || query.isBlank())
                ? productRepository.findAll(pageable)
                : productRepository.findByTitleContainingIgnoreCase(query, pageable);

        return pageResult.map(ProductDtos.Response::from);
    }
}
