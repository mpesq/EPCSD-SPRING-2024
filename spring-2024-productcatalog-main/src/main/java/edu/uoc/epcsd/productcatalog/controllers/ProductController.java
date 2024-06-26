package edu.uoc.epcsd.productcatalog.controllers;


import java.net.URI;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import edu.uoc.epcsd.productcatalog.controllers.dtos.CreateProductRequest;
import edu.uoc.epcsd.productcatalog.controllers.dtos.GetProductResponse;
import edu.uoc.epcsd.productcatalog.entities.Product;
import edu.uoc.epcsd.productcatalog.services.ProductService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<Product> getAllProducts() {
        log.trace("getAllProducts");

        return productService.findAll();
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetProductResponse> getProductById(@PathVariable @NotNull Long productId) {
        log.trace("getProductById");

        return productService.findById(productId).map(product -> ResponseEntity.ok().body(GetProductResponse.fromDomain(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Long> createProduct(@RequestBody CreateProductRequest createProductRequest) {
        log.trace("createProduct");

        log.trace("Creating product " + createProductRequest);
        Long productId = productService.createProduct(
                createProductRequest.getCategoryId(),
                createProductRequest.getName(),
                createProductRequest.getDescription(),
                createProductRequest.getDailyPrice(),
                createProductRequest.getBrand(),
                createProductRequest.getModel()).getId();
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productId)
                .toUri();

        return ResponseEntity.created(uri).body(productId);
    }

    // TODO: add the code for the missing system operations here:
    // 1. remove product (use DELETE HTTP verb). Must remove the associated items
    
    @DeleteMapping("/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable @NotNull Long productId) {
    	log.trace("deleteProduct");

        log.trace("Delete Product with id: " + productId);
        
    	 if (productService.existsById(productId)) {
    	        productService.deleteById(productId);
    	        return ResponseEntity.ok().body(true);
    	    } else {
    	        return ResponseEntity.ok().body(false);
    	    }
    	
    }
    
    // 2. query products by name
    
    @GetMapping("/name/{productName}")
    public ResponseEntity<List<Product>> getProductsByName(@PathVariable @NotNull String productName) {
    	
    	List<Product> productsList = productService.findProductsByName(productName);
    	if (productsList != null && !productsList.isEmpty()) {
    		return ResponseEntity.ok().body(productsList);
    	}
		return ResponseEntity.notFound().build();
    	
    }
    // 3. query products by category/subcategory
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable @NotNull Long categoryId) {
    	
    	List<Product> productsList = productService.findProductsByCategory(categoryId);
    	if (productsList != null && !productsList.isEmpty()) {
    		return ResponseEntity.ok().body(productsList);
    	}
		return ResponseEntity.notFound().build();
    	
    }
}
