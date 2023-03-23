package app.shop.model;

import java.util.Set;

public class Product {

    private String name;
    private String description;
    private String organization;
    private long price;
    private long quantitiesInStock;
    private Discount discount;
    private String review;
    private Set<String> keyWords;
    private String[][] tablesCharacteristics;
    private int ratings;

    public Product(String name, String description, String organization, long price, long quantitiesInStock, Discount discount, String review, Set<String> keyWords, String[][] tablesCharacteristics, int ratings) {
        this.name = name;
        this.description = description;
        this.organization = organization;
        this.price = price;
        this.quantitiesInStock = quantitiesInStock;
        this.discount = discount;
        this.review = review;
        this.keyWords = keyWords;
        this.tablesCharacteristics = tablesCharacteristics;
        this.ratings = ratings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getQuantitiesInStock() {
        return quantitiesInStock;
    }

    public void setQuantitiesInStock(long quantitiesInStock) {
        this.quantitiesInStock = quantitiesInStock;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Set<String> getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(Set<String> keyWords) {
        this.keyWords = keyWords;
    }

    public String[][] getTablesCharacteristics() {
        return tablesCharacteristics;
    }

    public void setTablesCharacteristics(String[][] tablesCharacteristics) {
        this.tablesCharacteristics = tablesCharacteristics;
    }

    public int getRatings() {
        return ratings;
    }

    public void setRatings(int ratings) {
        this.ratings = ratings;
    }
}
