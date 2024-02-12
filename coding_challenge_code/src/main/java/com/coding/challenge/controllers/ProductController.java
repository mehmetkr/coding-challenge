package com.coding.challenge.controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import com.coding.challenge.model.requests.TaxCalculationRequest;
import com.coding.challenge.model.requests.UpsertProductRequest;
import com.coding.challenge.security.JWTAuthenticationVerficationFilter;
import com.coding.challenge.security.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.json.JSONObject;

import com.coding.challenge.model.persistence.Product;
import com.coding.challenge.model.persistence.repositories.ProductRepository;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/product")
public class ProductController {

	@Value("${tax.service.server.url}")
	private String taxServiceServerUrl;

	private static final String RESULT_MESSAGE = "The tax for the given product and tax type is ";

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private JWTAuthenticationVerficationFilter authenticationVerfication;

	@GetMapping
	public ResponseEntity<List<Product>> getProducts() {
		return ResponseEntity.ok(productRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable Long id) {
		return ResponseEntity.of(productRepository.findById(id));
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Product>> getProductsByName(@PathVariable String name) {
		List<Product> products = productRepository.findByName(name);
		return products == null || products.isEmpty() ? ResponseEntity.notFound().build()
				: ResponseEntity.ok(products);
			
	}

	@PostMapping("/upsert")
	public ResponseEntity<Product> upsertProduct(@RequestBody UpsertProductRequest upsertProductRequest, HttpServletRequest request) {
		Product product = new Product();
		String userName = authenticationVerfication.getUserNameFromJWT(request.getHeader(SecurityConstants.HEADER_STRING));

		// If the request body has an id element, potentially perform an update operation, else perform an insert operation
		if(!StringUtils.isBlank(upsertProductRequest.getId())) {
			Long upsertID = Long.valueOf(upsertProductRequest.getId());
			Optional<Product> existingProduct = productRepository.findById(upsertID);
			// If a product exists for the given id and the username of the product is not the same as the current user
			// then give an unauthorized error
			if(existingProduct.isPresent() && !StringUtils.equals(existingProduct.get().getUsername(), userName)) {
				// If the username of the existing product is the same as the current user, give an unauthorized error
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
			}
			// else, proceed with to an update operation
			else {
				product.setId(upsertID);
			}
		}
		product.setUsername(userName);
		product.setName(upsertProductRequest.getName());
		product.setPrice(new BigDecimal(upsertProductRequest.getPrice()));
		product.setDescription(upsertProductRequest.getDescription());
		productRepository.save(product);
		return ResponseEntity.ok(product);
	}

	@PostMapping("calculateTax")
	public ResponseEntity<String> calculateTax(@RequestBody TaxCalculationRequest taxCalculationRequest) {

		try {
			return ResponseEntity.ok(callTaxServer(taxCalculationRequest.getId(),
					taxCalculationRequest.getTaxtype()).toString());

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

		}

	}

	private BigDecimal callTaxServer(String id, String taxtype) throws Exception {

		URL url = new URL(taxServiceServerUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);

		JSONObject data = new JSONObject();
		data.put("id", id);
		data.put("taxtype", taxtype);

		String jsonData = data.toString();
		byte[] bytes = jsonData.getBytes(StandardCharsets.UTF_8);

		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		writer.write(new String(bytes, StandardCharsets.UTF_8));
		writer.flush();
		writer.close();

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder response = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			response.append(line);
		}
		reader.close();

		return new BigDecimal(response.toString());
	}

}
