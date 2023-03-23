package app.shop.repository;

import app.shop.model.*;
import org.springframework.stereotype.Repository;
import app.shop.util.loger.Logger;

import java.sql.*;
import java.util.*;
import java.util.Date;

@Repository
public class ShopAppRepository {

    private static final String url = "jdbc:postgresql://localhost:5432/Shop_app";
    private static final String user = "postgres";
    private static final String password = "kanov2001";

    protected static Connection connection;

    static {
        try {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addAccount(PersonalAccount account) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO users VALUES(default, ?, ?, ?, ?, ?, ?)"
        )) {
            preparedStatement.setString(1, account.getUser().getUserName());
            preparedStatement.setString(2, account.getUser().getMail());
            preparedStatement.setString(3, account.getUser().getPassword());
            preparedStatement.setDouble(4, account.getUser().getBalance());
            preparedStatement.setInt(5, account.getStatus().ordinal());
            preparedStatement.setBoolean(6, account.isFreeze());
            preparedStatement.executeUpdate();

            Logger.debugLogNews("add account " + account.getUser().getUserName());
        } catch (SQLException e) {
            Logger.debugLogNews("add account " + account.getUser().getUserName() + " FAIL");
            e.printStackTrace();
        }
    }

    public boolean deleteAccountById(long id){
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM product WHERE id = " + id
        )) {
            preparedStatement.executeUpdate();

            Logger.debugLogNews("deleted account " + id);
        } catch (SQLException e) {
            Logger.debugLogNews("deleted account " + id + " FAIL");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void changeAccount(PersonalAccount account, long id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE users SET " +
                        "user_name = ? ," +
                        "mail = ? ," +
                        "password = ? ," +
                        "balance = ? ," +
                        "status_id = ? " +
                        "WHERE id = " + id
        )) {
            Array array = null;
            preparedStatement.setString(1, account.getUser().getUserName());
            preparedStatement.setString(2, account.getUser().getMail());
            preparedStatement.setString(3, account.getUser().getPassword());
            preparedStatement.setDouble(4, account.getUser().getBalance());
            preparedStatement.setInt(5, account.getStatus().ordinal());

            preparedStatement.executeUpdate();

            Logger.debugLogNews("update user account " + account.getUser().getUserName());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PersonalAccount getAccountById(long id) {
        PersonalAccount account = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM users WHERE id = " + id
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            account = getAccount(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return account;
    }

    public void addNotification(Notification notification, long id){
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO notification VALUES(default, ?, ?, ?, ?)"
        )) {
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, notification.getHeader());
            preparedStatement.setDate(3, new java.sql.Date(notification.getDate().getTime()));
            preparedStatement.setString(4, notification.getMessage());
            preparedStatement.executeUpdate();

            Logger.debugLogNews("add notification " + id);
        } catch (SQLException e) {
            Logger.debugLogNews("add notification " + id + " FAIL");
            e.printStackTrace();
        }
    }

    public PersonalAccount getAccount(ResultSet resultSet) throws SQLException {
        PersonalAccount account = null;
        List<Notification> notifications = getNotificationsById(resultSet.getLong("id"));
        List<Organization> organizations = getOrganizationsById(resultSet.getLong("id"));
        List<Operation> history = getHistoryById(resultSet.getLong("id"));

        User user = new User(
                resultSet.getString("user_name"),
                resultSet.getString("mail"),
                resultSet.getString("password"),
                resultSet.getDouble("balance")
        );

        account = new PersonalAccount(
                user,
                AccountStatus.values()[resultSet.getInt("status_id")],
                resultSet.getBoolean("freeze_account"),
                notifications,
                organizations,
                history
        );

        return account;
    }

    public List getNotificationsById(long id) {
        List<Notification> notifications = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM notification WHERE user_id = " + id
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                notifications.add(new Notification(
                        resultSet.getString("header"),
                        resultSet.getTimestamp("date_time"),
                        resultSet.getString("header")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notifications;
    }

    public void changeNotifications(Notification notification, long userId){
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE notification SET " +
                        "header = ? " +
                        "date_time = ? " +
                        "message = ? " +
                        "WHERE user_id = " + userId + " AND " + " header = \'" + notification.getHeader() + "\'"
        )) {
            preparedStatement.setString(1, notification.getHeader());
            preparedStatement.setDate(2, new java.sql.Date(notification.getDate().getTime()));
            preparedStatement.setString(3, notification.getMessage());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Organization> getOrganizationsById(long userId) {
        List<Organization> organizations = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM organization WHERE user_id = " + userId
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                organizations.add(new Organization(
                        resultSet.getString("org_name"),
                        resultSet.getString("description"),
                        resultSet.getString("url_logo")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return organizations;
    }

    public void changeOrganization(Organization organization, long userId){
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE organization SET " +
                        "org_name = ? " +
                        "description = ? " +
                        "url_logo = ? " +
                        "WHERE user_id = " + userId + " AND " + " header = \'" + organization.getName() + "\'"
        )) {
            preparedStatement.setString(1, organization.getName());
            preparedStatement.setString(1, organization.getDescription());
            preparedStatement.setString(1, organization.getLogo());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Operation> getHistoryById(long id) {
        List<Operation> history = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM transaction WHERE user_from = " + id + " OR " + "user_to = " + id
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();

            Date date = null;
            long transactionId = 0;
            long pastTransactionId = 0;

            while (resultSet.next()){
                if(transactionId == 0){
                    transactionId = resultSet.getLong("transaction_id");
                }

                date = resultSet.getTimestamp("data_time");
                if(transactionId != pastTransactionId)
                history.add(new Operation(getTransactionListById(transactionId), date));
                pastTransactionId = transactionId;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return history;
    }

    public List<Transaction> getTransactionListById(long transactionId){
        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM transaction WHERE transaction_id = " + transactionId
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                transactions.add(getTransactionById(transactionId));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public PersonalAccount getAccountByMail(String mail) {
        PersonalAccount account = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM users WHERE mail = \'" + mail + "\'"
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            account = getAccount(resultSet);

        } catch (SQLException e) {
            return null;
        }

        return account;
    }

    public PersonalAccount authorization(String login, String password) {
        PersonalAccount account = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM users WHERE (user_name = \'" + login + "\' OR mail = \'" + login + "\') AND " +
                        "password = \'" + password + "\'"
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            account = getAccount(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return account;
    }

    public long getAccountIdByOrgName(String orgName){
        long id = 00;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM organization WHERE org_name = \'" + orgName + "\'"
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            id = resultSet.getLong("user_id");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public PersonalAccount getAccountByOrgName(String orgName){
        PersonalAccount account = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM organization WHERE prod_name = \'" + orgName + "\'"
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            account = getAccount(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }

    public void freezeAccount(long id, boolean freezeStatus) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE users SET " +
                        "freeze_account = ? " +
                        "WHERE id = " + id
        )) {
            preparedStatement.setBoolean(1, freezeStatus);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addOrganization(Organization newOrganization, long userId) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO organization VALUES(default, ?, ?, ?, ?)"
        )) {
            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, newOrganization.getName());
            preparedStatement.setString(3, newOrganization.getDescription());
            preparedStatement.setString(4, newOrganization.getLogo());

            preparedStatement.executeUpdate();
            PersonalAccount account = getAccountById(userId);
            account.setStatus(AccountStatus.ORGANIZATION);
            changeAccount(account, userId);

            Logger.debugLogNews("add newOrganization " + newOrganization.getName());
        } catch (SQLException e) {
            Logger.debugLogNews("add newOrganization " + newOrganization.getName() + " FAIL");
            e.printStackTrace();
        }
    }

    public List<Organization> getOrganizationsByUserId(long userId){
        List<Organization> organization = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM organization WHERE user_id = " + userId
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                String orgName = resultSet.getString("org_name");
                organization.add(new Organization(
                        orgName,
                        resultSet.getString("description"),
                        resultSet.getString("url_logo"),
                        getProductsIdByOrg(orgName)
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return organization;
    }

    public Organization getOrganizationById(long id) {
        Organization organization = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM organization WHERE id = " + id
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            String orgName = resultSet.getString("org_name");
            organization = new Organization(
                    orgName,
                    resultSet.getString("description"),
                    resultSet.getString("url_logo"),
                    getProductsIdByOrg(orgName)
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return organization;
    }

    public Organization getOrganizationByName(String name) {
        Organization organization = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM organization WHERE org_name = \'" + name + "\'"
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            String orgName = resultSet.getString("org_name");
            organization = new Organization(
                    orgName,
                    resultSet.getString("description"),
                    resultSet.getString("url_logo"),
                    getProductsIdByOrg(orgName)
            );

        } catch (SQLException e) {
        }

        return organization;
    }

    public void deleteOrganization(long id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM organization WHERE id = " + id
        )) {
            preparedStatement.executeQuery();

            Logger.debugLogNews("Deleted organization " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addProduct(Product newProduct) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO product VALUES(default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        )) {
            sendProduct(preparedStatement, newProduct);

            Logger.debugLogNews("add product " + newProduct.getName());
        } catch (SQLException e) {
            Logger.debugLogNews("add product " + newProduct.getName() + " FAIL");
            e.printStackTrace();
        }
    }

    public void sendProduct(PreparedStatement preparedStatement, Product newProduct) throws SQLException {
        Array array = null;

        preparedStatement.setString(1, newProduct.getName());
        preparedStatement.setString(2, newProduct.getDescription());
        preparedStatement.setString(3, newProduct.getOrganization());
        preparedStatement.setDouble(4, newProduct.getPrice());
        preparedStatement.setLong(5, newProduct.getQuantitiesInStock());
        preparedStatement.setLong(6, getDiscountIdByField(newProduct.getDiscount()));
        preparedStatement.setString(7, newProduct.getReview());
        array = connection.createArrayOf("varchar", newProduct.getKeyWords().toArray());
        preparedStatement.setArray(8, array);
        array = connection.createArrayOf("varchar", newProduct.getTablesCharacteristics());
        preparedStatement.setArray(9, array);
        preparedStatement.setInt(10, newProduct.getRatings());
        preparedStatement.setString(3, newProduct.getOrganization());
        preparedStatement.setLong(6, getDiscountIdByField(newProduct.getDiscount()));
        preparedStatement.setInt(10, newProduct.getRatings());

        preparedStatement.executeUpdate();
    }

    public List<Product> getProductAll(){
        List<Product> products = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM product WHERE quantitiesInStock > 0"
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();

            Logger.debugLogNews("products...");
            products = getProducts(resultSet);
            Logger.debugLogNews("products = " + products);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public boolean buyProduct(List<Product> products, long userId) {
        PersonalAccount account = getAccountById(userId);
        double amount = 0;
        long transactionId = new Random().nextInt();
        double commission = 0.05;
        double discountPrice = 0;

        try {
            for (Product el: products) {
                amount += el.getPrice() - el.getPrice() * ((el.getDiscount() == null)? 0 :el.getDiscount().getDiscount());
            }

            changeBalanceByUserId(userId, account.getUser().getBalance() - amount); //списание средств за покупку
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        List<Long> orgList = new ArrayList<>();
        Map<Product, PersonalAccount> processed = new HashMap<>();
        long orgAccountId = 0;
        PersonalAccount orgAccount = null;

        try {
            //Начисление денег за проданый товар
            for (Product el: products){

                orgAccountId = getAccountIdByOrgName(el.getOrganization());
                orgAccount = getAccountById(orgAccountId);
                orgList.add(orgAccountId);
                discountPrice = el.getPrice() * ((el.getDiscount() == null)? 0 :el.getDiscount().getDiscount());

                changeBalanceByUserId(
                        orgAccountId,
                        orgAccount.getUser().getBalance() + el.getPrice() - discountPrice - el.getPrice() * commission
                );
                el.setQuantitiesInStock(el.getQuantitiesInStock() - 1);
                changeProduct(el, getProductIdByName(el.getName()), orgAccount.getStatus());
                processed.put(el, orgAccount);
            }
        } catch (SQLException e) {
            try {
                Product product = null;
                if(processed.size() > 0){
                    for (Map.Entry<Product, PersonalAccount> el: processed.entrySet()){
                        orgAccountId = getAccountIdByOrgName(el.getKey().getOrganization());
                        orgAccount = el.getValue();
                        product = el.getKey();
                        discountPrice = product.getPrice() * ((product.getDiscount() == null)? 0 :product.getDiscount().getDiscount());

                        changeBalanceByUserId(
                                orgAccountId,
                                orgAccount.getUser().getBalance() - product.getPrice() - discountPrice
                        );
                        product.setQuantitiesInStock(product.getQuantitiesInStock() - 1);
                        changeProduct(product, getProductIdByName(product.getName()), orgAccount.getStatus());
                    }
                }
                changeBalanceByUserId(userId, account.getUser().getBalance() + amount); //вслучае ошибки возвращаем
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        Transaction transaction = new Transaction(
                new Date(),
                transactionId,
                userId,
                orgList,
                products,
                amount
        );

        addTransaction(transaction);

        return true;
    }

    public void changeBalanceByUserId(long userId, double newBalance) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE users SET " +
                        "balance = ? " +
                        "WHERE id = " + userId
        )) {
            preparedStatement.setDouble(1, newBalance);
            preparedStatement.executeUpdate();

            Logger.debugLogNews("update user balance " + userId + " balance = " + newBalance);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean refundProducts(long transactionId, long accountId) {
        List<Long> orgList = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        double amount = 0;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM transaction WHERE transaction_id = " + transactionId
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            long userId = resultSet.getLong("user_from");
            PersonalAccount user = getAccountById(userId);

            long orgUserId = 0;
            PersonalAccount orgUser = null;


            Product product = null;

            while (resultSet.next()) {
                amount += resultSet.getDouble("amount");

            }
            changeBalanceByUserId(userId, user.getUser().getBalance() + amount);

            while (resultSet.next()){
                orgUserId = resultSet.getLong("user_to");
                orgList.add(orgUserId);
                orgUser = getAccountById(orgUserId);

                product = getProductById(resultSet.getLong("products_id"));
                product.setQuantitiesInStock(product.getQuantitiesInStock() + 1);
                products.add(product);
                changeProduct(product, resultSet.getLong("products_id"), orgUser.getStatus());

                changeBalanceByUserId(orgUserId, orgUser.getUser().getBalance() + resultSet.getDouble("amount"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        Transaction transaction = new Transaction(
                new Date(),
                transactionId,
                accountId,
                orgList,
                products,
                amount
        );

        addTransaction(transaction);

        return true;
    }

    public void changeProduct(Product newProduct, long id, AccountStatus status) {
        Product oldProduct = getProductById(id);

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE product SET " +
                        "prod_name = ?, " +
                        "description = ?, " +
                        "organization = ?, " +
                        "price = ?, " +
                        "quantitiesInStock = ?, " +
                        "discount_id = ?, " +
                        "review = ?, " +
                        "key_words = ?, " +
                        "tablesCharacteristics = ?, " +
                        "rating = ? " +
                        "WHERE id = " + id
        )) {
            Array array = null;
            preparedStatement.setString(1, newProduct.getName());
            preparedStatement.setString(2, newProduct.getDescription());
            preparedStatement.setString(3, newProduct.getOrganization());
            preparedStatement.setDouble(4, newProduct.getPrice());
            preparedStatement.setLong(5, newProduct.getQuantitiesInStock());
            preparedStatement.setLong(6, getDiscountIdByField(newProduct.getDiscount()));
            preparedStatement.setString(7, newProduct.getReview());
            array = connection.createArrayOf("varchar", newProduct.getKeyWords().toArray());
            preparedStatement.setArray(8, array);
            array = connection.createArrayOf("varchar", newProduct.getTablesCharacteristics());
            preparedStatement.setArray(9, array);
            preparedStatement.setInt(10, newProduct.getRatings());

            if(status == AccountStatus.ORGANIZATION){
                preparedStatement.setString(3, oldProduct.getOrganization());
                preparedStatement.setLong(6, getDiscountIdByField(oldProduct.getDiscount()));
                preparedStatement.setInt(10, oldProduct.getRatings());
            }
            preparedStatement.executeQuery();

            Logger.debugLogNews("update product " + newProduct.getName());
        } catch (SQLException e) {
//            e.printStackTrace();
        }
    }

    public List<Long> getProductsIdByOrg(String orgName) {
        List<Long> productsId = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM product WHERE organization = \'" + orgName + "\'"
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                productsId.add(resultSet.getLong("id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productsId;
    }

    public List getProductsByKeyWord(String keyWord) {
        List<Product> products = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM product WHERE \'" + keyWord + "\' = ANY(key_words)"
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            products = getProducts(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public Product getProductById(long id) {
        Product product = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM product WHERE id = " + id
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();

            product = getProducts(resultSet).get(0);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }

    public long getProductIdByName(String name){
        long id = 00;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM product WHERE prod_name = \'" + name + "\'"
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            id = resultSet.getLong("id");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public List getProductsById(long userId) {
        List<Product> products = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM product WHERE user_id = " + userId
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            products = getProducts(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public List<Product> getProductsByDiscountId(long id) {
        List<Product> products = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM product WHERE discount_id = " + id
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            products = getProducts(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public List<Product> getProducts(ResultSet resultSet) throws SQLException {
        List<Product> products = new ArrayList<>();
        Set<String> keyWords;

        while (resultSet.next()) {
            keyWords = new HashSet<>();
            String[] array = (String[]) resultSet.getArray("key_words").getArray();
            for (int i = 0; i < array.length; i++) {
                keyWords.add(array[i]);
            }

            products.add(new Product(
                    resultSet.getString("prod_name"),
                    resultSet.getString("description"),
                    resultSet.getString("organization"),
                    resultSet.getLong("price"),
                    resultSet.getLong("quantitiesInStock"),
                    getDiscountByProductId(resultSet.getLong("discount_id")),
                    resultSet.getString("review"),
                    keyWords,
                    (String[][]) resultSet.getArray("tablesCharacteristics").getArray(),
                    resultSet.getInt("rating")
            ));
        }

        return products;
    }

    public void addDiscount(Discount discount, List<Product> products){

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO discount VALUES(default, ?, ?, ?)"
        )) {
            preparedStatement.setInt(1, new Random().nextInt());
            preparedStatement.setFloat(2, discount.getDiscount());
            preparedStatement.setDate(3, new java.sql.Date(discount.getDuration().getTime()));
            preparedStatement.executeQuery();

            for (Product e: products){
                e.setDiscount(discount);
                changeProduct(e, getProductIdByName(e.getName()), AccountStatus.ADMIN);
            }

        } catch (SQLException e) {
        }
    }
    
    public void changeDiscount(Discount newDiscount, int discountId){
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE discount SET " +
                        "discount_value = ?, " +
                        "duration = ?, " +
                        "WHERE id = " + discountId
        )) {
            preparedStatement.setFloat(1, newDiscount.getDiscount());
            preparedStatement.setDate(2, new java.sql.Date(newDiscount.getDuration().getTime()));

            for (Product e: getProductsByDiscountId(discountId)){
                if(!newDiscount.getProducts().contains(e)){
                    e.setDiscount(null);
                    changeProduct(e,getProductIdByName(e.getName()), AccountStatus.ADMIN);
                }
            }
            preparedStatement.executeQuery();

            Logger.debugLogNews("update discount " + discountId);
        } catch (SQLException e) {
//            e.printStackTrace();
        }
    }

    public Discount getDiscountByProductId(long prodId) {
        Discount discount = null;
        List<Long> products_id = new ArrayList<>();
        if(prodId < 0) return discount;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM product WHERE discount_id = " + prodId
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                products_id.add(resultSet.getLong("id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM discount WHERE user_id = " + prodId
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            discount = new Discount(
                    products_id,
                    resultSet.getLong("discount_value"),
                    resultSet.getTimestamp("duration")
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return discount;
    }

    public int getDiscountIdByField(Discount discount) {
        int id = 0;

        if(discount == null) return id;
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM discount WHERE discount_value = " + discount.getDiscount() + " AND " +
                        "duration = \'" + discount.getDuration() + "\'"
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            id = resultSet.getInt("discount_id");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public Discount getDiscountById(long id){
        Discount discount = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM discount WHERE discount_id = " + id
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            List<Long> productsId = new ArrayList<>();
            for (Product e: getProductsByDiscountId(id)){
                productsId.add(getProductIdByName(e.getName()));
            }

            discount = new Discount(
                    productsId,
                    resultSet.getFloat("discount_value"),
                    resultSet.getTimestamp("duration")
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return discount;
    }

    public void deleteProduct(long productId) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM product WHERE id = " + productId
        )) {
            preparedStatement.executeQuery();
            Logger.debugLogNews("Deleted product " + productId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Transaction getTransactionById(long transactionId){ //изменить по надобности с id
        Transaction transaction = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM transaction WHERE transaction_id = " + transactionId
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Long> toList = new ArrayList<>();

            while (resultSet.next()){

                toList.add(resultSet.getLong("user_to"));
                if(transaction == null){
                    Date date = resultSet.getTimestamp("data_time");
                    long userFromId = resultSet.getLong("transaction_id");
                    transaction = new Transaction(
                            date,
                            userFromId,
                            resultSet.getLong("user_from"),
                            null,
                            getTransactionProducts(transactionId),
                            resultSet.getDouble("amount")
                    );
                }
            }
            transaction.setToIdList(toList);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transaction;
    }

    public void addTransaction(Transaction transaction){
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO transaction VALUES(default, ?, ?, ?, ?, ?, ?)"
        )) {
            preparedStatement.setLong(1, transaction.getTransactionId());
            preparedStatement.setDouble(3, transaction.getAmount());
            preparedStatement.setLong(4, transaction.getFrom());

            List<Product> products = transaction.getProducts();

            for (int i = 0; i < products.size(); i++) {
                preparedStatement.setLong(2, getProductIdByName(products.get(i).getName()));
                preparedStatement.setLong(5, transaction.getToIdList().get(i));
                preparedStatement.setDate(6, new java.sql.Date(transaction.getDate().getTime()));
                preparedStatement.executeUpdate();
            }

            Logger.debugLogNews("add transaction " + transaction.getFrom() + " to ");
        } catch (SQLException e) {
            Logger.debugLogNews("add transaction "  + transaction.getFrom() + " to " + " FAIL");
            e.printStackTrace();
        }
    }

    public List<Product> getTransactionProducts(long transactionId){
        List<Product> products = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM transaction WHERE transaction_Id = " + transactionId
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                products.add(getProductById(resultSet.getLong("id")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }
}
