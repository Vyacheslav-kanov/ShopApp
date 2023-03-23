package app.shop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Organization {

    private String name;
    private String description;
    private String logo;
    private List<Long> products;

    public Organization(String name, String description, String logo) {
        this.name = name;
        this.description = description;
        this.logo = logo;
        this.products = new ArrayList<>();
    }

    public Organization(String name, String description, String logo, List<Long> products) {
        this.name = name;
        this.description = description;
        this.logo = logo;
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLogo() {
        return logo;
    }

    public List<Long> getProducts() {
        return products;
    }
}
