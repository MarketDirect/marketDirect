package com.marketDirect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketDirect.entities.Comment;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileInputStream;

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
	public void btestLogin() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.post("/login")
						.param("username", "Alice@Gmail.com")
						.param("password", "password")
		);
		Assert.assertTrue(users.count() == 1);
	}

	@Test
	public void atestCreateUser() throws Exception {

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
	public void ctestCreateVendor() throws Exception {
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
	public void dtestCreateItem() throws Exception {
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
	public void dtestCreateComment() throws Exception {

	Comment c = new Comment();
		c.setText("Text");
		c.setRating(5);
		c.setVendor(vendors.findByName("Store"));
		c.setUser(users.findByUsername("Alice@Gmail.com"));

		ObjectMapper om = new ObjectMapper();
		String json = om.writeValueAsString(c);


		mockMvc.perform(
				MockMvcRequestBuilders.post("/create-comment")
						.content(json)
						.contentType("application/json")
						.sessionAttr("username", "Alice@Gmail.com")

		);
		Assert.assertTrue(comments.count() == 1);
	}
}
