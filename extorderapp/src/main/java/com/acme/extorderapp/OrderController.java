package com.acme.extorderapp;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.acme.extorderapp.model.OneTimeCustomerRecord;
import com.acme.extorderapp.model.Product;
import com.acme.extorderapp.model.SalesOrder;
import com.acme.extorderapp.model.SalesOrderItem;
import com.acme.extorderapp.services.OneTimeCustomerRecordService;
import com.acme.extorderapp.services.ProductService;
import com.acme.extorderapp.services.SalesOrderService;

@Controller
public class OrderController {

	private ProductService productService;
	private SalesOrderService salesOrderService;
	private OneTimeCustomerRecordService oneTimeCustomerRecordService;

	public OrderController(
				ProductService productService,
				SalesOrderService salesOrderService,
				OneTimeCustomerRecordService oneTimeCustomerRecordService) {
		this.productService = productService;
		this.salesOrderService = salesOrderService;
		this.oneTimeCustomerRecordService = oneTimeCustomerRecordService;
	}

	@GetMapping("/order")
	public String orderForm(@RequestParam(value="productId", required=true) String productId, Model model) {
		// On this page we need to retrieve the product data again from S/4HANA since we don't cache data between requests.
		Product product = productService.findById(productId);

		model.addAttribute("otcRecord", new OneTimeCustomerRecord());
		model.addAttribute("product", product);

		return "order";
	}


	@PostMapping("/order")
	public RedirectView orderSubmit(
			@ModelAttribute final OneTimeCustomerRecord otcRecord,
			@RequestParam(value="productId", required=true) String productId,
			RedirectAttributes attributes) {
		boolean successful = true;

		// POST the sales order to S/4HANA
		SalesOrder so = new SalesOrder();
		// Note in this sample we pre-define most of the sales order with static values due to simplicity...
		so.setSalesOrderType(SalesOrder.TYPE_STANDARD_SALES_ORDER);
		so.setDistributionChannel("10");
		so.setOrganizationDivision("00");

		// ... since we are working with anonymous external users. the sales order is created under a one time customer
		so.setSoldToParty("10401010");

		SalesOrderItem soItem = new SalesOrderItem();
		soItem.setMaterial(productId);
		soItem.setRequestedQuantity("1");

		so.addItem(soItem);
		so.setPurchaseOrderByCustomer("Web Order " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
		SalesOrder soResult = salesOrderService.create(so);	// the result contains the newly creates Sales Order number
		otcRecord.setOrderId(soResult.getSalesOrder());				// ... which we also write into the custom buiness object

		// POST the one time customer record data to S/4HANA
		OneTimeCustomerRecord otcResult = oneTimeCustomerRecordService.create(otcRecord);

		if (successful) {
			attributes.addAttribute("createdSalesOrderId", soResult.getSalesOrder());
			attributes.addAttribute("createdOtcRecordId", otcResult.getId());
			return new RedirectView("/order-confirmation", true);
		} else {
			attributes.addAttribute("productId", productId);
			return new RedirectView("/order", true);
		}
	}


	@GetMapping("/order-confirmation")
	public String orderConfirmation() {
		return "order-confirmation";
	}

}