package com.week_3_ecommerce.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

	private Long id;

	private String name;

	private Double price;

	private Integer stock;

}
