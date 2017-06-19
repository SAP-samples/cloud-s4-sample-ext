package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.acme.extorderapp.model.Product;
import com.acme.extorderapp.model.Products;
import com.acme.extorderapp.services.ODataClient;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProductModelTest {
	
	@Test
	public void testConversionFromJson() {
			
		ObjectMapper mapper = ODataClient.getObjectMapper();
		
		try {			
			Product t = mapper.readValue(new File("src/test/resources/model/A_Product('11').json"), Product.class);
			assertEquals("TG12", t.getProduct());
			assertEquals("Trad.Good 12,Reorder Point,Reg.Trad.", t.getDescription());
						
			Products l = mapper.readValue(new File("src/test/resources/model/A_Product.json"), Products.class);
			assertEquals(4, l.getResults().size());
			assertEquals("T001", l.getResults().get(0).getProduct());
			assertEquals("Junior Consultant", l.getResults().get(0).getDescription());
			
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}				
		
	}
}