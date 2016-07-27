package com.marketDirect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketDirect.entities.Comment;
import com.marketDirect.entities.Item;
import com.marketDirect.entities.User;
import com.marketDirect.entities.Vendor;
import com.marketDirect.services.CommentRepository;
import com.marketDirect.services.ItemRepository;
import com.marketDirect.services.UserRepository;
import com.marketDirect.services.VendorRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MarketDirectApplication.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class MarketDirectApplicationTests {

	@Autowired
	WebApplicationContext wac;

	MockMvc mockMvc;

	@Before
	public void before() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Autowired
	UserRepository users;

	@Autowired
	VendorRepository vendors;

	@Autowired
	CommentRepository comments;

	@Autowired
	ItemRepository items;

	@Test
	public void bTestLogin() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.post("/login")
						.param("username", "Alice@Gmail.com")
						.param("password", "password")
		);
		Assert.assertTrue(users.count() == 1);
	}

	@Test
	public void aTestCreateUser() throws Exception {

		User user = new User();
		user.setUsername("Alice@Gmail.com");
		user.setPassword("password");
		user.setShoppingList(null);

		ObjectMapper om = new ObjectMapper();
		String json = om.writeValueAsString(user);


		mockMvc.perform(
				MockMvcRequestBuilders.post("/create-user")
						.content(json)
						.contentType("application/json")
		);
		Assert.assertTrue(users.count() == 1);
	}

	@Test
	public void cTestCreateVendor() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "farmer.jpg", "image/jpeg", new FileInputStream("farmer.jpg"));
		mockMvc.perform(
				MockMvcRequestBuilders.fileUpload("/create-vendor")
						.file(file)
						.param("name", "Store")
						.param("phone", "555-5555")
						.param("email", "Alice@Email.com")
						.param("website", "www.store.com")
						.param("location", "Charleston")
						.param("date", "1/1/1900")
						.sessionAttr("username", "Alice@Gmail.com")
		);

		System.out.println(vendors.count());
		Assert.assertTrue(vendors.count() == 1);

		//Assert.assertTrue(vendors.findOne(1).getName().equals("Store"));
	}

	@Test
	public void dTestCreateItem() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "farmer.jpg", "image/jpeg", new FileInputStream("farmer.jpg"));
		mockMvc.perform(
				MockMvcRequestBuilders.fileUpload("/create-item")
						.file(file)
						.param("name", "Apples")
						.param("description", "Red Delicious")
						.param("category", "Produce")
						.param("price", "$1.00 / lb")
						.param("quantity", "100")
						.sessionAttr("username", "Alice@Gmail.com")
		);
		Assert.assertTrue(items.count() == 1);
	}

	@Test
	public void eTestCreateComment() throws Exception {

		Comment c = new Comment();
		c.setText("Text");
		c.setRating(5);
		c.setVendor(vendors.findByName("Store"));
		c.setUser(users.findByUsername("Alice@Gmail.com"));

		ObjectMapper om = new ObjectMapper();
		String json = om.writeValueAsString(c);


		mockMvc.perform(
				MockMvcRequestBuilders.post("/create-comment")
						.param("id", "1")
						.content(json)
						.contentType("application/json")
						.sessionAttr("username", "Alice@Gmail.com")

		);
		Assert.assertTrue(comments.count() == 1);
	}

	@Test
	public void fTestGetVendor() throws Exception {
		ResultActions ra = mockMvc.perform(
				MockMvcRequestBuilders.get("/get-vendor")
				.param("id", "1")
						.sessionAttr("username", "Alice@Gmail.com")

		);
		MvcResult result = ra.andReturn();
		MockHttpServletResponse response = result.getResponse();
		String json = response.getContentAsString();

		ObjectMapper om = new ObjectMapper();
		Vendor vendor = om.readValue(json, Vendor.class);

		Assert.assertTrue(vendor.getName().equals("Store"));
	}

	@Test
	public void gTestGetItem() throws Exception {
		ResultActions ra = mockMvc.perform(
				MockMvcRequestBuilders.get("/get-item")
						.param("id", "1")
						.sessionAttr("username", "Alice@Gmail.com")

		);
		MvcResult result = ra.andReturn();
		MockHttpServletResponse response = result.getResponse();
		String json = response.getContentAsString();

		ObjectMapper om = new ObjectMapper();
		Item item = om.readValue(json, Item.class);

		Assert.assertTrue(item.getName().equals("Apples"));
	}

	@Test
	public void hTestEditItem() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "banana.jpeg", "image/jpeg", new FileInputStream("banana.jpeg"));
		mockMvc.perform(
				MockMvcRequestBuilders.fileUpload("/edit-item")
						.file(file)
						.param("id", "1")
						.param("name", "Bananas")
						.param("description", "Yellow")
						.param("category", "Produce")
						.param("price", "$1.00 / lb")
						.param("quantity", "200")
						.sessionAttr("username", "Alice@Gmail.com")
		);
		Assert.assertTrue(items.findOne(1).getName().equals("Bananas"));
	}


	@Test
	public void iTestDeleteItem() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.post("/delete-item")
				.param("id", "1")
				.sessionAttr("username", "Alice@Gmail.com")
		);
		Assert.assertTrue(items.count() == 0);
	}
	@Test
	public void jTestEditVendor() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "banana.jpeg", "image/jpeg", new FileInputStream("banana.jpeg"));
		mockMvc.perform(
				MockMvcRequestBuilders.fileUpload("/edit-item")
						.file(file)
						.param("id", "1")
						.param("name", "Better Store")
						.param("phone", "444-4444")
						.param("email", "BetterStore@Hotmail.com")
						.param("website", "www.betterstore.com")
						.param("date", "tomorrow")
						.sessionAttr("username", "Alice@Gmail.com")
		);
		Assert.assertTrue(items.findOne(1).getName().equals("Better Store"));
	}

}
