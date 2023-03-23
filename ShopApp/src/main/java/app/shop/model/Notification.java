package app.shop.model;

import java.util.Date;

public class Notification {

    private String header;
    private Date date;
    private String message;

    public Notification(String header, Date date, String message) {
        this.header = header;
        this.date = date;
        this.message = message;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
