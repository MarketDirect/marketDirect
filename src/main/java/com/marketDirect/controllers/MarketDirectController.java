package com.marketDirect.controllers;

import com.marketDirect.entities.Item;
import com.marketDirect.entities.User;
import com.marketDirect.entities.Vendor;
import com.marketDirect.services.ItemRepository;
import com.marketDirect.services.UserRepository;
import com.marketDirect.services.VendorRepository;
import com.marketDirect.utilities.PasswordStorage;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by michaeldelli-gatti on 7/19/16.
 */
@RestController
public class MarketDirectController {
    @Autowired
    UserRepository users;

    @Autowired
    VendorRepository vendors;

    @Autowired
    ItemRepository items;

    @PostConstruct
    public void init() throws SQLException, PasswordStorage.CannotPerformOperationException {

        User testUser = new User("FarmerJohn", PasswordStorage.createHash("password1"), true);
        if (users.findByUsername(testUser.getUsername()) == null) {
            users.save(testUser);
        }

        Vendor testVendor = new Vendor("John's Store", "", "555-5555", "John@email.com", "www.johnsstore.com", "Charleston", "7/25/16", testUser);
        if (vendors.findByName(testVendor.getName()) == null) {
            vendors.save(testVendor);
        }

        Item testItem1 = new Item("Apples", "Red Delicious", "Produce", "", "$1.00 / lb", 100, testVendor);
        Item testItem2 = new Item("Bananas", "Yellow", "Produce", "", "$5.00 / lb", 20, testVendor);
        Item testItem3 = new Item("Strawberries", "Red", "Produce", "", "$2.50 / Bag", 50, testVendor);
        if (items.findByName(testItem1.getName()) == null) {
            items.save(testItem1);
        }
        if (items.findByName(testItem2.getName()) == null) {
            items.save(testItem2);
        }
        if (items.findByName(testItem3.getName()) == null) {
            items.save(testItem3);
        }
        Server.createWebServer().start();
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public void login(HttpSession session, @RequestBody User user) throws Exception {
        if (user.getUsername() == "" || user.getPassword() == ""){
            throw new Exception("name and password fields may not be blank");
        }
        User userFromDb = users.findByUsername(user.getUsername());
        if (userFromDb == null) {
            throw new Exception("User does not exist. Please create account.");

        }
        else if (!PasswordStorage.verifyPassword(user.getPassword(), userFromDb.getPassword())) {
            throw new Exception("Incorrect password");
        }
        session.setAttribute("username", user.getUsername());
    }

    @RequestMapping(path = "/create-user", method = RequestMethod.POST)
    public void createUser(HttpSession session, @RequestBody User user) throws Exception {
        if (user.getUsername() == "" || user.getPassword() == ""){
            throw new Exception("name and password fields may not be blank");
        }
        User userFromDb = users.findByUsername(user.getUsername());
        if (userFromDb == null){
            user.setPassword(PasswordStorage.createHash(user.getPassword()));
            users.save(user);
        }
        else {
            throw new Exception("User already exists");
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public void logout(HttpSession session, HttpServletResponse response) throws Exception {
        session.invalidate();
        response.sendRedirect("/");
    }

    @RequestMapping(path = "/create-vendor", method = RequestMethod.POST)
    public void createVendor(HttpSession session, MultipartFile file, String name, String phone, String email, String website, String location, String date) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("Username not in database!");
        }

        if (!file.getContentType().contains("image")){
            throw new Exception("Only images allowed!");
        }

        File dir = new File("public/files");
        dir.mkdirs();

        File uploadedFile = File.createTempFile("file", file.getOriginalFilename(), dir);
        FileOutputStream fos = new FileOutputStream(uploadedFile);
        fos.write(file.getBytes());

        Vendor vendor = new Vendor(name, uploadedFile.getName(), phone, email, website, location, date, user);
        vendors.save(vendor);
    }

    @RequestMapping(path = "/create-item", method = RequestMethod.POST)
    public void createItem(HttpSession session, String category, MultipartFile file, String description, String price, int quantity) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }

        File dir = new File("public/files");
        dir.mkdirs();

        File uploadedFile = File.createTempFile("file", file.getOriginalFilename(), dir);
        FileOutputStream fos = new FileOutputStream(uploadedFile);
        fos.write(file.getBytes());

        Vendor vendor = vendors.findByName(username);

        Item item = new Item(username, description, category, uploadedFile.getName(), price, quantity, vendor);
        items.save(item);
    }

    @RequestMapping(path = "/get-items", method = RequestMethod.GET)
    public Iterable<Item> getItems(HttpSession session) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }

        return items.findAll();
    }

    @RequestMapping(path = "/get-item", method = RequestMethod.GET)
    public Item getItem(HttpSession session, int id) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }

        return items.findOne(id);
    }

    @RequestMapping(path = "/items-by-category", method = RequestMethod.GET)
    public Iterable<Item> itemsByCategory(String category){
        return items.findByCategory(category);
    }

    @RequestMapping(path = "/delete-item", method = RequestMethod.POST)
    public void deleteItem(HttpSession session, int id) throws Exception {
        String username = (String) session.getAttribute("username");
        Item item = items.findOne(id);
        Vendor vendor = vendors.findByName(username);
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }
        else if (vendor != item.getVendor()){
            throw new Exception("logged in user and item creator do not match");
        }
        File f = new File("public/files/" + item.getFilename());
        f.delete();
        items.delete(item);
    }

    @RequestMapping(path = "/edit-item", method = RequestMethod.POST)
    public void editItem(int id, HttpSession session, MultipartFile file, String name, String description, String category, String price, Integer quantity) throws Exception {
        Item item = items.findOne(id);

        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        Vendor vendor = vendors.findByName(username);
        if (vendor == null) {
            throw new Exception("Vendor not in database!");
        }
        else if (vendor != item.getVendor()){
            throw new Exception("Logged in vendor can not delete this!");
        }

        if (name != null) {
            item.setName(name);
        }

        if (description != null) {
            item.setDescription(description);
        }

        if (category != null) {
            item.setCategory(category);
        }

        if (price != null) {
            item.setPrice(price);
        }

        if (quantity != null) {
            item.setQuantity(quantity);
        }

        if (file != null) {

            if (!file.getContentType().contains("image")){
                throw new Exception("Only images allowed!");
            }

            File f = new File("public/files/" + item.getFilename());
            f.delete();

            File dir = new File("public/files");
            dir.mkdirs();

            File uploadedFile = File.createTempFile("file", file.getOriginalFilename(), dir);
            FileOutputStream fos = new FileOutputStream(uploadedFile);
            fos.write(file.getBytes());

            item.setFilename(uploadedFile.getName());
        }

        items.save(item);
    }

    @RequestMapping(path = "/get-vendors", method = RequestMethod.GET)
    public Iterable<Vendor> getVendors(HttpSession session) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }

        return vendors.findAll();
    }

    @RequestMapping(path = "/get-vendor", method = RequestMethod.GET)
    public Vendor getVendor(HttpSession session, int id) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }

        return vendors.findOne(id);
    }

    @RequestMapping(path = "/edit-vendor", method = RequestMethod.POST)
    public void editVendor(int id,HttpSession session, MultipartFile file, String name, String phone, String email, String website, String location, String date) throws Exception {
        String username = (String) session.getAttribute("username");
        Vendor vendor = vendors.findOne(id);
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("Username not in database!");
        }

        if (!file.getContentType().contains("image")){
            throw new Exception("Only images allowed!");
        }

        if (name != null){
            vendor.setName(name);
        }

        if(phone != null){
            vendor.setPhone(phone);
        }

        if (email != null){
            vendor.setEmail(email);
        }

        if (website != null){
            vendor.setWebsite(website);
        }

        if (location != null){
            vendor.setLocation(location);
        }

        if (date != null){
            vendor.setDate(date);
        }

        if(file != null) {
            File f = new File("public/files/" + vendor.getFileName());
            f.delete();

            File dir = new File("public/files");
            dir.mkdirs();

            File uploadedFile = File.createTempFile("file", file.getOriginalFilename(), dir);
            FileOutputStream fos = new FileOutputStream(uploadedFile);
            fos.write(file.getBytes());

            vendor.setFileName(uploadedFile.getName());
        }
        vendors.save(vendor);
    }

    @RequestMapping(path = "/delete-vendor", method = RequestMethod.POST)
    public void deleteVendor(HttpSession session, int id) throws Exception {
        String username = (String) session.getAttribute("username");
        Vendor vendor = vendors.findOne(id);
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }

        Iterable<Item> i = items.findByVendor(vendor);
        for (Item item : i){
            items.delete(item);
        }
        vendors.delete(vendor);
    }

    @RequestMapping(path = "/search-item", method = RequestMethod.GET)
    public Iterable<Item> searchItem(String search){
        return items.findByNameLike("%" + search + "%");
    }

    @RequestMapping(path = "/search-vendor", method = RequestMethod.GET)
    public Iterable<Vendor> searchVendors(String search){
        return vendors.findByNameLike( "%" + search + "%");
    }

    @RequestMapping(path = "add-shopping-list-item", method = RequestMethod.POST)
    public void createShoppingList(HttpSession session, @RequestBody Item item) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }
        List<Item> sl = user.getShoppingList();
        sl.add(item);
        user.setShoppingList(sl);
        users.save(user);
    }

    @RequestMapping(path = "remove-shopping-list-item", method = RequestMethod.POST)
    public void removeShoppingListItem(HttpSession session, @RequestBody Item item) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }
        List<Item> sl = user.getShoppingList();
        sl.remove(item);
        user.setShoppingList(sl);
        users.save(user);
    }

    @RequestMapping(path = "get-shopping-list", method = RequestMethod.GET)
        public Iterable<Item> getShoppingList(HttpSession session) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }
        return user.getShoppingList();
    }
}
