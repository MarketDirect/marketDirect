package com.marketDirect.controllers;

import com.marketDirect.entities.*;
import com.marketDirect.services.*;
import com.marketDirect.utilities.PasswordStorage;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    CommentRepository comments;

    @Autowired
    MessageRepository messages;

    @PostConstruct
    public void init() throws SQLException, PasswordStorage.CannotPerformOperationException, FileNotFoundException {

        User testUser = new User("FarmerJohn", PasswordStorage.createHash("password1"), true);
        if (users.findByUsername(testUser.getUsername()) == null) {
            users.save(testUser);
        }
//
//        User testUser2 = new User("FarmerClyde", PasswordStorage.createHash("password1"), true);
//        if (users.findByUsername(testUser2.getUsername()) == null) {
//            users.save(testUser2);
//        }
//
        Vendor testVendor = new Vendor("John's Store", "", "555-5555", "John@email.com", "www.johnsstore.com", "Charleston", "7/25/16", testUser);
        if (vendors.findByName(testVendor.getName()) == null) {
            vendors.save(testVendor);
        }
//
//        Vendor testVendor2 = new Vendor("Clyde's Store", "", "555-5555", "Clyde@email.com", "www.clydesstore.com", "Charleston", "8/2/16", testUser2);
//        if (vendors.findByName(testVendor2.getName()) == null) {
//            vendors.save(testVendor2);
//        }
//
//        Item testItem1 = items.findByName("Apples");
//        Item testItem2 = items.findByName("Bananas");
//        Item testItem3 = items.findByName("Strawberries");
//        Item testItem4 = items.findByName("Tomatoes");
//        if (testItem1 == null) {
//            testItem1 = new Item("Apples", "Red Delicious", "Produce", "", "$1.00 / lb", 100, testVendor);
//            items.save(testItem1);
//        }
//        if (testItem2 == null) {
//            testItem2 = new Item("Bananas", "Yellow", "Produce", "", "$5.00 / lb", 20, testVendor);
//            items.save(testItem2);
//        }
//        if (testItem3 == null) {
//            testItem3 = new Item("Strawberries", "Red", "Produce", "", "$2.50 / Bag", 50, testVendor);
//            items.save(testItem3);
//        }
//        if (testItem4 == null) {
//            testItem4 = new Item("Tomatoes", "Creepy", "Produce", "", "$5.00 / ea", 60, testVendor2);
//            items.save(testItem4);
//        }
//
//        User testUser3 = users.findByUsername("ShopperBill");
//        if (testUser3 == null) {
//            testUser3 = new User("ShopperBill", PasswordStorage.createHash("password1"), false);
//            users.save(testUser3);
//
//            List<Item> shit = testUser3.getShoppingList();
//            shit.add(items.findOne(2));
//            shit.add(items.findOne(4));
//
//            testUser3.setShoppingList(shit);
//            users.save(testUser3);
//        }
        Server.createWebServer("-webPort", "1337").start();
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public void login(HttpSession session, @RequestBody User user) throws Exception {
        if (user.getUsername().equals("")  || user.getPassword().equals("")){
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
        if (user.getUsername().equals("") || user.getPassword().equals("")){
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

    @RequestMapping(path = "add-vendor-photo", method = RequestMethod.POST)
    public void addVendorPhoto(HttpSession session, MultipartFile file, int id) throws Exception {
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

        if (user != vendor.getUser()){
            throw new Exception("vendor creator and logged in user do not match");
        }
        if (file.getOriginalFilename().contains(" ")){
            throw new Exception("File name may not contain spaces");
        }

        File f = new File("public/files/" + vendor.getFileName());
        f.delete();

        File dir = new File("public/files");
        dir.mkdirs();

        File uploadedFile = File.createTempFile("file", file.getOriginalFilename(), dir);
        FileOutputStream fos = new FileOutputStream(uploadedFile);
        fos.write(file.getBytes());

        vendor.setFileName(uploadedFile.getName());
        vendors.save(vendor);
    }

    @RequestMapping(path = "/create-item", method = RequestMethod.POST)
    public void createItem(HttpSession session, HttpServletResponse response, String name, String description, String category,  String price, int quantity) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

         User user = users.findByUsername(username);
         if (user == null) {
             throw new Exception("User not in database!");
         }

        Vendor vendor = vendors.findByUser(user);

        Item item = new Item(name, description, category, null , price, quantity, vendor);
        items.save(item);
        response.sendRedirect("/#/profile");
    }

    @RequestMapping(path = "/add-item-photo", method = RequestMethod.POST)
    public void addPicture(int id, HttpSession session, MultipartFile file, HttpServletResponse response) throws Exception {
        Item item = items.findOne(id);

        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }
        Vendor vendor = vendors.findByUser(user);
        if (vendor == null) {
            throw new Exception("logged in user does not match vendor");
        }
        else if (vendor != item.getVendor()){
            throw new Exception("Logged in user can not edit this!");
        }
        if (file.getOriginalFilename().contains(" ")){
            throw new Exception("File name may not contain spaces");
        }
        File dir = new File("public/files");
        dir.mkdirs();

        File uploadedFile = File.createTempFile("file", file.getOriginalFilename(), dir);
        FileOutputStream fos = new FileOutputStream(uploadedFile);
        fos.write(file.getBytes());

        item.setFilename(uploadedFile.getName());
        items.save(item);
        response.sendRedirect("/#/profile");
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
    public Item getItem(HttpSession session, @RequestBody Item i) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }

        return items.findOne(i.getId());
    }

    @RequestMapping(path = "/get-item-images", method = RequestMethod.GET)
    public ArrayList<String> getItemImages(@RequestBody Vendor v){
        List<Item> itemList = (List<Item>) items.findByVendor(v);
        ArrayList<String> shit = new ArrayList<>();
        for (Item item : itemList){
            String fileName = item.getFilename();
            shit.add(fileName);
        }
        return shit;
    }

    @RequestMapping(path = "/items-by-category", method = RequestMethod.GET)
    public Iterable<Item> itemsByCategory(String category){
        return items.findByCategory(category);
    }

    @RequestMapping(path = "/items-produce", method = RequestMethod.GET)
    public Iterable<Item> itemsProduce (){
        return items.findByCategory("Produce");
    }

    @RequestMapping(path = "/items-art", method = RequestMethod.GET)
    public Iterable<Item> itemsArt (){
        return items.findByCategory("Art");
    }

    @RequestMapping(path = "/items-hand-crafted", method = RequestMethod.GET)
    public Iterable<Item> itemsHandCrafted (){
        return items.findByCategory("Hand-Crafted");
    }

    @RequestMapping(path = "/items-misc", method = RequestMethod.GET)
    public Iterable<Item> itemsMisc (){
        return items.findByCategory("Misc");
    }

//    @RequestMapping(path = "/item/{id}", method = RequestMethod.POST) // create new
//    @RequestMapping(path = "/item/{id}", method = RequestMethod.PUT) //edit
//    @RequestMapping(path = "/item/{id}", method = RequestMethod.GET) //get
    @RequestMapping(path = "/delete-item", method = RequestMethod.POST) //delete
    public void deleteItem(HttpSession session, @RequestBody Item i) throws Exception {
        String username = (String) session.getAttribute("username");
        Item item = items.findOne(i.getId());

        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }
        Vendor vendor = vendors.findByUser(user);
        if (vendor != item.getVendor()){
            throw new Exception("logged in user and item creator do not match");
        }
        File f = new File("public/files/" + item.getFilename());
        f.delete();

        items.delete(item);
    }

    @RequestMapping(path = "/edit-item", method = RequestMethod.POST)
    public void editItem(HttpSession session, @RequestBody Item i) throws Exception {
        Item item = items.findOne(i.getId());

        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }
        Vendor vendor = vendors.findByUser(user);
        if (vendor == null) {
            throw new Exception("logged in user does not match vendor");
        }
        else if (vendor != item.getVendor()){
            throw new Exception("Logged in user can not edit this!");
        }

        if (i.getName() != null) {
            item.setName(i.getName());
        }

        if (i.getDescription() != null) {
            item.setDescription(i.getDescription());
        }

        if (i.getCategory() != null) {
            item.setCategory(i.getCategory());
        }

        if (i.getPrice() != null) {
            item.setPrice(i.getPrice());
        }

        if (i.getQuantity() != null) {
            item.setQuantity(i.getQuantity());
        }

        items.save(item);
    }

    @RequestMapping(path = "/edit-item-photo", method = RequestMethod.POST)
    public void editItemImage(HttpSession session, MultipartFile file, @RequestBody Item i) throws Exception {
        Item item = items.findOne(i.getId());

        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }
        Vendor vendor = vendors.findByUser(user);
        if (vendor == null) {
            throw new Exception("logged in user does not match vendor");
        }
        else if (vendor != item.getVendor()){
            throw new Exception("Logged in user can not edit this!");
        }

        if (!file.getContentType().contains("image")){
                throw new Exception("Only images allowed!");
            }
        if (file.getOriginalFilename().contains(" ")){
            throw new Exception("File name may not contain spaces");
        }

        File f = new File("public/files/" + item.getFilename());
        f.delete();

        File dir = new File("public/files");
        dir.mkdirs();

        File uploadedFile = File.createTempFile("file", file.getOriginalFilename(), dir);
        FileOutputStream fos = new FileOutputStream(uploadedFile);
        fos.write(file.getBytes());

        item.setFilename(uploadedFile.getName());
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
    public void editVendor(HttpSession session, @RequestBody Vendor v) throws Exception {
        String username = (String) session.getAttribute("username");
        Vendor vendor = vendors.findOne(v.getId());
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("Username not in database!");
        }

        if (user != vendor.getUser()){
            throw new Exception("vendor creator and logged in user do not match");
        }

        if (v.getName() != null){
            vendor.setName(v.getName());
        }

        if(v.getPhone() != null){
            vendor.setPhone(v.getPhone());
        }

        if (v.getEmail() != null){
            vendor.setEmail(v.getEmail());
        }

        if (v.getWebsite() != null){
            vendor.setWebsite(v.getWebsite());
        }

        if (v.getLocation() != null){
            vendor.setLocation(v.getLocation());
        }

        if (v.getDate() != null){
            vendor.setDate(v.getDate());
        }

        vendors.save(vendor);
    }

    @RequestMapping(path = "/delete-vendor", method = RequestMethod.POST)
    public void deleteVendor(HttpSession session, @RequestBody Vendor v) throws Exception {
        String username = (String) session.getAttribute("username");
        Vendor vendor = vendors.findOne(v.getId());
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }

        if (user != vendor.getUser()){
            throw new Exception("vendor creator and logged in user do not match");
        }

        Iterable<Item> i = items.findByVendor(vendor);
        for (Item item : i){
            items.delete(item);
        }
        Iterable<Message> m = messages.findByVendor(vendor);
        for (Message message : m) {
            messages.delete(message);
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

    @RequestMapping(path = "/add-shopping-list-item/{id}", method = RequestMethod.POST)
    public void createShoppingList(HttpSession session, @PathVariable ("id") int id) throws Exception {
        Item i = items.findOne(id);
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        if (i == null){
            throw new Exception("Item does not exist or invalid id");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }
        List<Item> sl = user.getShoppingList();
        sl.add(items.findOne(id));
        user.setShoppingList(sl);
        users.save(user);
    }

    @RequestMapping(path = "/remove-shopping-list-item/{id}", method = RequestMethod.POST)
    public void removeShoppingListItem(HttpSession session,HttpServletResponse response, @PathVariable ("id") int id) throws Exception {
        Item i = items.findOne(id);
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        if (i == null){
            throw new Exception("Item does not exist or invalid id");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }
        List<Item> sl = user.getShoppingList();
        sl.remove(i);
        user.setShoppingList(sl);
        users.save(user);
        // /#/
        // /#/
        // /#/
        // /#/
    }

    @RequestMapping(path = "/get-shopping-list", method = RequestMethod.GET)
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

    @RequestMapping(path = "/create-comment", method = RequestMethod.POST)
    public void createComment(HttpSession session, int id, @RequestBody Comment comment) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }
        Vendor vendor = vendors.findOne(id);
        Comment c = new Comment(comment.getText(),comment.getRating(), user, vendor);
        vendors.save(vendor);
        comments.save(c);
    }

    @RequestMapping(path = "/get-comments", method = RequestMethod.GET)
    public Iterable<Comment> getComments(HttpSession session, int id) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }
        return comments.findByVendorId(id);
    }

    @RequestMapping(path = "/edit-comment", method = RequestMethod.POST)
    public void editComment(HttpSession session, int id, String text) throws Exception {
        Comment comment = comments.findOne(id);
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }

        if (user != comment.getUser()){
            throw new Exception("This is not your comment");
        }
        else {
            comment.setText(text);
            comments.save(comment);
        }
    }

    @RequestMapping(path = "/delete-comment/{id}", method = RequestMethod.POST)
    public void deleteComment(HttpSession session, @PathVariable("id") int id) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }
        Comment commentFromDb = comments.findOne(id);
        if (user != commentFromDb.getUser()){
            throw new Exception("This is not your comment");
        }
        else {
            comments.delete(commentFromDb);
        }
    }

    @RequestMapping(path = "/find-by-location", method = RequestMethod.GET)
    public Iterable<Vendor> findByLocation(HttpSession session, String location) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }

        return vendors.findByLocation(location);
    }

    @RequestMapping(path = "/create-message", method = RequestMethod.POST)
    public void createMessage(HttpSession session, Integer id, @RequestBody Message message) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in!");
        }

        User user = users.findByUsername(username);
        if (user == null) {
            throw new Exception("User not in database!");
        }

        Vendor vendor = vendors.findOne(id);
        Message m = new Message(message.getText(), vendor);
        messages.save(m);
    }


}
