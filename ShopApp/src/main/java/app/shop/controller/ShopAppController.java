package app.shop.controller;

import app.shop.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.shop.service.ShopAppService;
import app.shop.util.loger.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/shop")
public class ShopAppController {
    private final ShopAppService service;
    private final File mainPage = new File("src/main/resources/form.html/mainPage.html");
    private final File registrationPage = new File("src/main/resources/form.html/registration.html");
    private final File authorizationPage = new File("src/main/resources/form.html/authorization.html");

    public ShopAppController(ShopAppService service) {
        this.service = service;
    }

    @GetMapping("")
    private ResponseEntity mainPage() {
        String html = "";
        try (FileReader reader = new FileReader(mainPage.getPath())) {
            int i;
            while ((i = reader.read()) != -1) html += (char) i;
            return ResponseEntity.ok(html);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/to_registration")
    private ResponseEntity registrationPage() {
        String html = "";
        try (FileReader reader = new FileReader(registrationPage.getPath())) {
            int i;
            while ((i = reader.read()) != -1) html += (char) i;
            Logger.debugLogNews("send registrationPage");
            return ResponseEntity.ok(html);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/to_authorization")
    private ResponseEntity authorizationPage() {
        String html = "";
        try (FileReader reader = new FileReader(authorizationPage.getPath())) {
            int i;
            while ((i = reader.read()) != -1) html += (char) i;
            Logger.debugLogNews("send authorizationPage");
            return ResponseEntity.ok(html);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/registration")
    private ResponseEntity registration(
            @RequestParam("userName") String userName,
            @RequestParam("mail") String mail,
            @RequestParam("password") String password
    ) {
        if (service.getAccountByMail(mail) == null && password.length() > 7) {

            User newUser = new User(userName, mail, password);
            PersonalAccount newAccount = new PersonalAccount(newUser);
            service.addAccount(newAccount);

            return authorizationPage();
        }
        return registrationPage();
    }

    @PostMapping("/authorization")
    private ResponseEntity authorization(@RequestParam("login") String login, @RequestParam("password") String password) {
        if (service.authorization(login, password) != null)

            return mainPage();
        return authorizationPage();
    }

    @GetMapping("/account/user")
    private ResponseEntity deleteAccount(
            @RequestParam("accountId") long accountId,
            @RequestParam("deleteId") long deleteId
    ) {
        PersonalAccount personalAccount = service.getAccountById(accountId);
        PersonalAccount deleteAccount = service.getAccountById(deleteId);
        if (personalAccount == null | deleteAccount == null || personalAccount.getStatus() != AccountStatus.ADMIN) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(service.deleteAccountById(deleteId)){
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/account/organizationRegistration")
    private ResponseEntity organizationRegistration(
            @RequestParam("accountId") long accountId,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("logo") String logo
    ) {
        PersonalAccount personalAccount = service.getAccountById(accountId);
        Organization organization = service.getOrganizationByName(name);
        if (personalAccount == null | organization != null || personalAccount.isFreeze()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Logger.debugLogNews("adding org " + name);
        Organization newOrganization = new Organization(name, description, logo);

        service.addOrganization(newOrganization, accountId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/product/add")
    private ResponseEntity addProduct(
            @RequestParam("accountId") long accountId,
            @RequestParam("organizationName") String organizationName,
            @RequestParam("product") String productJson
    ) {
        Logger.debugLogNews("add product " + accountId + " " + organizationName);
        PersonalAccount personalAccount = service.getAccountById(accountId);
        if (personalAccount == null | productJson.isEmpty() || personalAccount.isFreeze()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Product product = new Gson().fromJson(productJson, Product.class);
        Logger.debugLogNews("account detected " + personalAccount.getStatus());

        if (personalAccount.getStatus() == AccountStatus.ORGANIZATION) {
            Logger.debugLogNews("account detected " + accountId);
            for (Organization e : personalAccount.getOrganizations()) {
                if (e.getName().equals(organizationName)) {
                    service.addProduct(product);
                    return ResponseEntity.status(HttpStatus.OK).build();
                }
            }
        }

        if (personalAccount.getStatus() == AccountStatus.ADMIN) {
            service.addProduct(product);
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/products_list")
    private ResponseEntity<List<Product>> getProducts() {
        List<Product> products = service.getProductAll();
        Logger.debugLogNews("request productAll " + products);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/product/change_info")
    private ResponseEntity changeProduct(
            @RequestParam("accountId") long accountId,
            @RequestParam("productId") long productId,
            @RequestParam("product") String productJson
    ) {
        PersonalAccount personalAccount = service.getAccountById(accountId);
        Product oldProduct = service.getProductById(productId);
        if (personalAccount == null | productJson.isEmpty() || personalAccount.isFreeze()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Product product = new Gson().fromJson(productJson, Product.class);

        if (personalAccount.getStatus() == AccountStatus.ADMIN) {
            service.changeProduct(product, productId, personalAccount.getStatus());
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        if (personalAccount.getStatus() == AccountStatus.ORGANIZATION) {
            for (Organization e : personalAccount.getOrganizations()) {
                if (e.getName().equals(product.getOrganization())) {
                    service.changeProduct(product, productId, personalAccount.getStatus());
                    return ResponseEntity.status(HttpStatus.OK).build();
                }
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @SneakyThrows
    @PostMapping("/product/add_discount")
    private ResponseEntity addProductDiscount(
            @RequestParam("accountId") long accountId,
            @RequestParam("products") String productsJson,
            @RequestParam("discount") String discountJson
    ) {
        PersonalAccount personalAccount = service.getAccountById(accountId);
        if (
                personalAccount == null |
                        personalAccount.getStatus() != AccountStatus.ADMIN ||
                        personalAccount.isFreeze() ||
                        productsJson.isEmpty()
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        Discount discount = objectMapper.readValue(discountJson, Discount.class);
        Product[] products = objectMapper.readValue(productsJson, Product[].class);

        service.addDiscount(discount, List.of(products));

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @SneakyThrows
    @GetMapping("/product/change_discount")
    private ResponseEntity changeProductDiscount(
            @RequestParam("accountId") long accountId,
            @RequestParam("discount") String discountJson
    ) {
        PersonalAccount personalAccount = service.getAccountById(accountId);
        if (personalAccount == null | personalAccount.getStatus() != AccountStatus.ADMIN || personalAccount.isFreeze()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        Discount discount = objectMapper.readValue(discountJson, Discount.class);
        service.changeDiscount(discount, service.getDiscountIdByField(discount));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/basket/buy_products")
    private ResponseEntity buyProducts(
            @RequestParam("products") String productsJson,
            @RequestParam("accountId") long accountId,
            @RequestParam("reviews") String reviewsJson
    ) {
        PersonalAccount personalAccount = service.getAccountById(accountId);
        if (personalAccount == null | personalAccount.isFreeze()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Product[] products = new Gson().fromJson(productsJson, Product[].class);
        String[] reviews = new Gson().fromJson(reviewsJson, String[].class);

        long amount = 0;
        Product product = null;

        for (int i = 0; i < products.length; i++) {
            product = products[i];

            amount += product.getPrice();
            if (personalAccount.getStatus() == AccountStatus.ORGANIZATION) {
                if(personalAccount.getOrganizations().equals(product.getOrganization())){
                    continue;
                }
            }
            product.setReview(reviews[i]);
            long productId = service.getProductIdByName(product.getName());
            service.changeProduct(product, productId, personalAccount.getStatus());
        }

        if (personalAccount.getUser().getBalance() - amount >= 0) {
            List<Product> productList = new ArrayList<>(List.of(products));

            Map<Product, Integer> count = new HashMap<>();
            long quantitiesInStock = 0;
            for (Product e : productList) {
                if(count.containsKey(e)) {
                    quantitiesInStock = e.getQuantitiesInStock();
                    if(count.get(e) > quantitiesInStock){
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                    }
                    count.put(e, count.get(e) + 1);
                }
                else count.put(e, 1);
            }

            if (!service.buyProduct(productList, accountId)) {
                System.out.println("2");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }


        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/history/refund_products")
    private ResponseEntity refundProducts(
            @RequestParam("operationId") long operationId,
            @RequestParam("accountId") long accountId
    ) {
        PersonalAccount personalAccount = service.getAccountById(accountId);
        if (personalAccount == null | personalAccount.isFreeze()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!service.refundProducts(operationId, accountId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/product/review")
    private ResponseEntity addReview(
            @RequestParam("productId") long productId,
            @RequestParam("accountId") long accountId,
            @RequestParam("review") String review
    ) {
        PersonalAccount personalAccount = service.getAccountById(accountId);
        Product product = service.getProductById(productId);
        if (personalAccount == null | product == null || personalAccount.isFreeze()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (personalAccount.getStatus() == AccountStatus.ORGANIZATION) {
            if (personalAccount.getOrganizations().contains(product.getOrganization())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        product.setReview(product.getReview() + "\n" + personalAccount.getUser() + "\n" + review);
        service.changeProduct(product, productId, personalAccount.getStatus());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/replenish_balance")
    private ResponseEntity replenishBalance(
            @RequestParam("accountId") long accountId,
            @RequestParam("recipientId") long recipientId,
            @RequestParam("amount") long amount
    ) {
        PersonalAccount personalAccount = service.getAccountById(accountId);
        PersonalAccount recipient = service.getAccountById(recipientId);

        if (personalAccount == null | recipient == null || personalAccount.isFreeze()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User user = recipient.getUser();
        PersonalAccount changeable = recipient;
        long changeableId = recipientId;

        if (personalAccount.getStatus() != AccountStatus.ADMIN) {
            user = personalAccount.getUser();
            changeable = personalAccount;
            changeableId = accountId;
        }

        user.setBalance(user.getBalance() + amount);
        changeable.setUser(user);

        service.changeAccount(changeable, changeableId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/account/get_notifications")
    private ResponseEntity getNotifications(
            @RequestParam("accountId") long accountId
    ) {
        PersonalAccount personalAccount = service.getAccountById(accountId);
        if (personalAccount == null | personalAccount.isFreeze()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(personalAccount.getNotifications());
    }

    @GetMapping("/user")
    private ResponseEntity userInfo(
            @RequestParam("accountId") long accountId,
            @RequestParam("accountUserId") long accountUserId
    ) {
        PersonalAccount personalAccount = service.getAccountById(accountId);
        PersonalAccount accountUser = service.getAccountById(accountUserId);
        if (personalAccount == null | accountUser == null || personalAccount.isFreeze()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (personalAccount.getStatus() != AccountStatus.ADMIN) {
            return ResponseEntity.ok(personalAccount.getUser());
        }
        return ResponseEntity.ok(accountUser.getUser());
    }

    @GetMapping("/user/user_freeze")
    private ResponseEntity userFreeze(
            @RequestParam("accountId") long accountId,
            @RequestParam("freeze") boolean freeze,
            @RequestParam("accountUserId") long accountUserId
    ) {
        PersonalAccount personalAccount = service.getAccountById(accountId);
        PersonalAccount accountUser = service.getAccountById(accountUserId);
        if (personalAccount == null | accountUser == null || personalAccount.isFreeze()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (personalAccount.getStatus() == AccountStatus.ADMIN) {
            service.freezeAccount(accountUserId, freeze);
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @SneakyThrows
    @PostMapping("/user/send_notification")
    private ResponseEntity sendNotification(
            @RequestParam("accountId") long accountId,
            @RequestParam("accountUserId") long accountUserId,
            @RequestParam("notification") String notificationJson
    ) {
        PersonalAccount personalAccount = service.getAccountById(accountId);
        PersonalAccount accountUser = service.getAccountById(accountUserId);
        if (personalAccount == null | accountUser == null || personalAccount.isFreeze()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        Notification notification = objectMapper.readValue(notificationJson, Notification.class);

        if (personalAccount.getStatus() == AccountStatus.ADMIN) {
            service.addNotification(notification, accountUserId);
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/history")
    private ResponseEntity getHistory(
            @RequestParam("accountId") long accountId,
            @RequestParam("accountUserId") long accountUserId
    ) {
        PersonalAccount personalAccount = service.getAccountById(accountId);
        PersonalAccount accountUser = service.getAccountById(accountUserId);
        if (personalAccount == null | accountUser == null || personalAccount.isFreeze()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        PersonalAccount changeable = accountUser;

        if (personalAccount.getStatus() != AccountStatus.ADMIN) {
            changeable = personalAccount;
        }
        return ResponseEntity.ok(changeable.getHistory());
    }
}
