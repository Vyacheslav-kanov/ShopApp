package app.shop.model;

import java.util.Date;
import java.util.List;

public class Discount {

    private List<Long> products;
    private float discount;
    private Date duration;

    public Discount(List<Long> products, float discount, Date duration) {
        this.products = products;
        this.discount = discount;
        this.duration = duration;
    }

    public List<Long> getProducts() {
        return products;
    }

    public float getDiscount() {
        return discount;
    }

    public Date getDuration() {
        return duration;
    }
}
