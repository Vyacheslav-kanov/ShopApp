package app.shop.model;

import java.util.ArrayList;
import java.util.List;

public class PersonalAccount {

    private User user;
    private AccountStatus status;
    private boolean isFreeze;
    private List<Notification> notifications;
    private List<Organization> organizations;

    private List<Operation> history;

    public PersonalAccount(User user) {
        this.user = user;
        this.isFreeze = false;
        this.status = AccountStatus.USER;
        this.notifications = new ArrayList<>();
        this.organizations = new ArrayList<>();
    }

    public PersonalAccount(User user, AccountStatus status, boolean isFreeze, List<Notification> notifications, List<Organization> organizations, List<Operation> history) {
        this.user = user;
        this.status = status;
        this.isFreeze = isFreeze;
        this.notifications = notifications;
        this.organizations = organizations;
        this.history = history;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isFreeze() {
        return isFreeze;
    }

    public void setFreeze(boolean freeze) {
        isFreeze = freeze;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public List<Operation> getHistory() {
        return history;
    }
}
