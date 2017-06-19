package com.acme.extorderapp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.acme.extorderapp.model.Products;
import com.acme.extorderapp.services.ProductService;

@Controller
public class RootController {

	private ProductService productService;
	
	public RootController(ProductService productServiceClient) {
		this.productService = productServiceClient;
	}
	
	@RequestMapping("/")
	public String index(Model model) {
		
		Products products = productService.findByProductGroup("L001");
		
		model.addAttribute("count", products.getResults().size());
		model.addAttribute("products", products.getResults());
		return "index";
	}
}