package app.shop.model;

import java.util.Date;
import java.util.List;

public class Operation {

    private List<Transaction> transactionList;
    private Date date;

    public Operation(List<Transaction> transactionList, Date date) {
        this.transactionList = transactionList;
        this.date = date;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
