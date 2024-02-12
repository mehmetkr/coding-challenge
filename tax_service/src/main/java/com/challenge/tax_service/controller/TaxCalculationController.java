package com.challenge.tax_service.controller;

import com.challenge.tax_service.model.persistence.repositories.ProductRepository;
import com.challenge.tax_service.model.requests.TaxCalculationRequest;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/tax")
public class TaxCalculationController {

	@Value("${mongodb.collection.collectionname}")
	private String collectionName;

	@Autowired
	MongoTemplate mongoTemplate;

	// TODO: Create methods to get the tax rate of specific product types and tax types
	private static final HashMap<String, Float> taxData = new HashMap<>();



	// Setting a single static value for now
	static {
		taxData.put("Generic-VAT", 0.18F);
	}
	@Autowired
	private ProductRepository productRepository;

	@PostMapping("/calculate")
	public ResponseEntity<String> upsertProduct(@RequestBody TaxCalculationRequest taxCalculationRequest) {

		try {
			String result = taxCalculator(taxCalculationRequest.getId(),
					taxCalculationRequest.getTaxtype()).toString();
			logTaxCalculationRequest(taxCalculationRequest, result);

			Map<String, Object> taxServiceLog = new HashMap<>();

			taxServiceLog.put("date_time", LocalDateTime.now().toString());
			taxServiceLog.put("product_id", taxCalculationRequest.getId());
			taxServiceLog.put("tax_type", taxCalculationRequest.getTaxtype());
			taxServiceLog.put("tax_amount", result);

			Document document = new Document(taxServiceLog);
			mongoTemplate.insert(document, collectionName);
			
			return ResponseEntity.ok(result);

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

		}

	}

	private void logTaxCalculationRequest(TaxCalculationRequest taxCalculationRequest, String result) {
		
	}

	private BigDecimal taxCalculator(String productId, String taxType) throws Exception {
		BigDecimal taxRate = BigDecimal.valueOf(getTaxRate(getProductType(productId), taxType));

		BigDecimal productPrice = productRepository.findById(Long.valueOf(productId)).get().getPrice();

		return productPrice.multiply(taxRate).setScale(2, RoundingMode.HALF_EVEN);

	}

	private Float getTaxRate(String productType, String taxType) throws Exception {

		String compositeKey = productType + "-" + taxType;

		Float taxRate = taxData.get(compositeKey);

		if (taxRate == null)
			throw new Exception("Tax type not found");

		return taxRate;

	}

	private String getProductType(String productId) {
		// TODO: Add a field named "Product Type" to the Product Entity and then in this method, get the product type of the product with the given product id.
		// Returning a constant value for now

		return "Generic";
	}


}
