package edu.uoc.epcsd.productcatalog.services;

import edu.uoc.epcsd.productcatalog.entities.Category;
import edu.uoc.epcsd.productcatalog.entities.ItemStatus;
import edu.uoc.epcsd.productcatalog.entities.Product;
import edu.uoc.epcsd.productcatalog.repositories.ItemRepository;
import edu.uoc.epcsd.productcatalog.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private CategoryService categoryService;

//    @Autowired
//    private ItemService itemService;

	public List<Product> findAll() {
		return productRepository.findAll();
	}

	public Optional<Product> findById(Long productId) {
		return productRepository.findById(productId);
	}

	public Product createProduct(Long categoryId, String name, String description, Double dailyPrice, String brand,
			String model) {

		Product product = Product.builder().name(name).description(description).dailyPrice(dailyPrice).brand(brand)
				.model(model).build();

		if (categoryId != null) {
			Optional<Category> category = categoryService.findById(categoryId);

			if (category.isPresent()) {
				product.setCategory(category.get());
			}
		}

		return productRepository.save(product);
	}

	public boolean existsById(@NotNull Long productId) {
		return productRepository.existsById(productId);
	}

	public void deleteById(@NotNull Long productId) {

		Optional<Product> opProduct = findById(productId);
		Product product;
		if (opProduct.isPresent()) {
			product = opProduct.get();
			product.getItemList().forEach(item -> {

				item.setStatus(ItemStatus.NON_OPERATIONAL);
				itemRepository.save(item);
			});
		} else {
			throw new IllegalArgumentException("Could not find the product with Id: " + productId);
		}

	}

	public List<Product> findProductsByName(String productName) {

		return productRepository.findAll().stream().filter(product -> product.getName().contains(productName))
				.collect(Collectors.toList());
	}

	public List<Product> findProductsByCategory(Long categoryId) {

		return productRepository.findAll().stream().filter(product -> product.getCategory().getId().equals(categoryId))
				.collect(Collectors.toList());

	}
}
