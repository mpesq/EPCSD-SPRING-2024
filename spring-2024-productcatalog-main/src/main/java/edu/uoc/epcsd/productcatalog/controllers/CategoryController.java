package edu.uoc.epcsd.productcatalog.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import edu.uoc.epcsd.productcatalog.controllers.dtos.CreateCategoryRequest;
import edu.uoc.epcsd.productcatalog.entities.Category;
import edu.uoc.epcsd.productcatalog.services.CategoryService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/categories")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/")
	@ResponseStatus(HttpStatus.OK)
	public List<Category> getAllCategories() {
		log.trace("getAllCategories");

		return categoryService.findAll();
	}

	@PostMapping
	public ResponseEntity<Long> createCategory(@RequestBody CreateCategoryRequest createCategoryRequest) {
		log.trace("createCategory");

		log.trace("Creating category " + createCategoryRequest);
		Long categoryId = categoryService.createCategory(createCategoryRequest.getParentId(),
				createCategoryRequest.getName(), createCategoryRequest.getDescription()).getId();
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(categoryId).toUri();

		return ResponseEntity.created(uri).body(categoryId);
	}

	// TODO: add the code for the missing system operations here:
	// 1. query categories by name
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Category>> getCategoriesByName(@PathVariable String name) {
		log.trace("getCategoryByName");

		log.trace("Getting category by name " + name);
		List<Category> categoriesListFound = categoryService.findByName(name);
		if (categoriesListFound != null && !categoriesListFound.isEmpty()) {
			return ResponseEntity.ok().body(categoriesListFound);
		} else {
			return ResponseEntity.notFound().build();
		}

	}
	// 2. query categories by description
	@GetMapping("/description/{description}")
	public ResponseEntity<List<Category>> getCategoriesByDescription(@PathVariable String description) {
		log.trace("getCategoryByDescription");

		log.trace("Getting category by description " + description);
		List<Category> categoriesListFound = categoryService.findByDescription(description);
		if (categoriesListFound != null && !categoriesListFound.isEmpty()) {
			return ResponseEntity.ok().body(categoriesListFound);
		} else {
			return ResponseEntity.notFound().build();
		}

	}	
	// 3. query categories by parent category (must return all categories under the
	// category specified by the id parameter)
	@GetMapping("/parent/{parentId}")
	public ResponseEntity<List<Category>> getCategoriesByParent(@PathVariable Long parentId) {
		log.trace("getCategoryByParent");

		log.trace("Getting category by parent " + parentId);
		List<Category> categoriesListFound = categoryService.findByParent(parentId);
		if (categoriesListFound != null && !categoriesListFound.isEmpty()) {
			return ResponseEntity.ok().body(categoriesListFound);
		} else {
			return ResponseEntity.notFound().build();
		}

	}
}
