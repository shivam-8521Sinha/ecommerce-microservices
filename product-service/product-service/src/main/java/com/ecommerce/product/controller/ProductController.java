package com.ecommerce.product.controller;

import com.ecommerce.product.entity.Product;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.product.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController
{
    public ProductService productService;
    public ProductController(ProductService productService)
    {
        this.productService = productService;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody Product product)
    {
        return productService.createProduct(product);
    }

    @GetMapping
    public List<Product> getAllProduct()
    {
        return productService.getAllProduct();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id)
    {
        return productService.getProductById(id);
    }

    @DeleteMapping("/id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductById(@RequestParam Long id)
    {
        productService.deleteProductById(id);
    }
}
