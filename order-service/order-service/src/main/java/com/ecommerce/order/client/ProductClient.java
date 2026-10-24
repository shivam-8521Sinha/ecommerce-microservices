package com.ecommerce.order.client;

import com.ecommerce.order.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("product-service")
public interface ProductClient
{
    @GetMapping("/products/{id}")
    ProductResponse getProductById(@PathVariable Long id);
}
