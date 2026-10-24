package com.ecommerce.product.service;

import com.ecommerce.product.entity.Product;
import org.springframework.stereotype.Service;
import com.ecommerce.product.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService
{
    //@Autowired :- want it then remove final and constructor
    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository)
    {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product)
    {
        return productRepository.save(product);
    }

    public List<Product> getAllProduct()
    {
        return productRepository.findAll();
    }

    public Product getProductById(Long id)
    {
        return productRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Product with id: " + id + " not found")
                );
    }

    public  void deleteProductById(Long id)
    {
        productRepository.deleteById(id);
    }
}
