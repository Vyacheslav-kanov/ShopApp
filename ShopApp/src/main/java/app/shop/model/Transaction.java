package app.shop.model;

import java.util.Date;
import java.util.List;

public class Transaction {

    private Date date;

    private long transactionId;
    private long from;
    private List<Long> toIdList;
    private List<Product> products;
    private double amount;

    public Transaction(Date date, long transactionId, long from, List<Long> toIdList, List<Product> products, double amount) {
        this.date = date;
        this.transactionId = transactionId;
        this.from = from;
        this.toIdList = toIdList;
        this.products = products;
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public List<Long> getToIdList() {
        return toIdList;
    }

    public void setToIdList(List<Long> toIdList) {
        this.toIdList = toIdList;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
