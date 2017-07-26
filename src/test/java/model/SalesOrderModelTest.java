package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.acme.extorderapp.model.SalesOrder;
import com.acme.extorderapp.model.SalesOrderItem;
import com.acme.extorderapp.services.ODataClient;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SalesOrderModelTest {
	
	@Test
	public void testConversionToJson() {
		
		ObjectMapper mapper = ODataClient.getObjectMapper();		
		
		SalesOrder so = new SalesOrder() {{
			setSalesOrderType(SalesOrder.TYPE_STANDARD_SALES_ORDER);
			setDistributionChannel("01");
			setOrganizationDivision("00");
			setSoldToParty("0101110");
			
			addItem(new SalesOrderItem() {{
				setMaterial("FOO");
				setRequestedQuantity("2");
			}});
		}};
		
		try {
			String json = "{\"d\":" + mapper.writeValueAsString(so) + "}";
			assertTrue(json.indexOf("\"SoldToParty\":\"0101110\"") > -1);
			assertTrue(json.indexOf("\"SalesOrder\":") == -1); // the property "SalesOrder" must not be in the JSON.
			assertTrue(json.indexOf("\"Material\":\"FOO\"") > -1);
			System.out.println("Sales Order JSON: \n" + json);
			
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testConversionFromJson() {
			
		ObjectMapper mapper = ODataClient.getObjectMapper();
		
		try {			
			SalesOrder t = mapper.readValue(new File("src/test/resources/model/A_SalesOrder('69').json"), SalesOrder.class);
			assertEquals("69", t.getSalesOrder());
			assertEquals("TG12", t.getItems().getResults().get(0).getMaterial());
			
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}				
		
	}	
}