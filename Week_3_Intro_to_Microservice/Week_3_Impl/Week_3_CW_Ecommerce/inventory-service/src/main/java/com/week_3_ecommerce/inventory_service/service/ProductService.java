package com.week_3_ecommerce.inventory_service.service;

import com.week_3_ecommerce.inventory_service.dto.OrderRequestDto;
import com.week_3_ecommerce.inventory_service.dto.OrderRequestItemDto;
import com.week_3_ecommerce.inventory_service.dto.ProductDto;
import com.week_3_ecommerce.inventory_service.entity.Product;
import com.week_3_ecommerce.inventory_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ModelMapper modelMapper;

	public List<ProductDto> getAllInventory(){
		log.info("Fetching all inventory items");
		List<Product> inventories = productRepository.findAll();
		return inventories.stream()
				.map((element) -> modelMapper.map(element, ProductDto.class))
				.toList();
	}

	public ProductDto getProductById(Long id){
		log.info("Fetching Product with ID: {}",id);
		Optional<Product> inventory = productRepository.findById(id);
		return inventory.map(item -> modelMapper.map(item, ProductDto.class))
				.orElseThrow(() -> new RuntimeException("Inventory not found"));
	}

	@Transactional
	public Double reduceStocks(OrderRequestDto orderRequestDto) {
		log.info("Reducing the stocks");
		Double totalPrice = 0.0;
		for(OrderRequestItemDto orderRequestItemDto : orderRequestDto.getItems()){
			Long productId = orderRequestItemDto.getProductId();
			Integer quantity = orderRequestItemDto.getQuantity();

			Product product = productRepository.findById(productId).orElseThrow(() ->
					new RuntimeException("Product not found with id: "+productId));

			if(product.getStock() < quantity){
				throw new RuntimeException("Product cannot be fulfilled for given quantity");
			}

			product.setStock(product.getStock() - quantity);
			productRepository.save(product);
			totalPrice += quantity * product.getPrice();
		}
		return totalPrice;
	}

	@Transactional
	public boolean restocks(OrderRequestDto orderRequestDto) {
		for(OrderRequestItemDto orderRequestItemDto: orderRequestDto.getItems()){
			Long productId = orderRequestItemDto.getProductId();
			Integer quantity = orderRequestItemDto.getQuantity();

			Product product = productRepository.findById(productId).orElseThrow(() ->
					new RuntimeException("Product not found with id: "+productId));

			product.setStock(product.getStock() + quantity);
			productRepository.save(product);
		}
		return true;
	}
}
