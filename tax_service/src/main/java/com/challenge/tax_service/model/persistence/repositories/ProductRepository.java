package com.challenge.tax_service.model.persistence.repositories;

import java.util.List;

import com.challenge.tax_service.model.persistence.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByName(String name);

}
