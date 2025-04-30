package com.sub.services.product.service;

import com.sub.services.product.dto.ProductRequest;
import com.sub.services.product.dto.ProductResponse;
import com.sub.services.product.model.Product;
import com.sub.services.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product
                .builder()
                .description(productRequest.description())
                .name(productRequest.name())
                .price(productRequest.price())
                .skuCode(productRequest.skuCode())
                .build();
        Product saved = productRepository.save(product);
        log.info("Product created successfully");
        return new ProductResponse(saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getSkuCode(),
                saved.getPrice());
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(x -> new ProductResponse(x.getId(),
                        x.getName(),
                        x.getDescription(),
                        x.getSkuCode(),
                        x.getPrice()))
                .toList();
    }
}
