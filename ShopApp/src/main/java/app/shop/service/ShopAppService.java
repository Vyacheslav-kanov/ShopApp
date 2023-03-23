package app.shop.service;

import app.shop.model.*;
import app.shop.repository.ShopAppRepository;
import org.springframework.stereotype.Service;
import ru.sber.ServiceTransferMoney.model.*;

import java.util.List;

@Service
public class ShopAppService {
    private final ShopAppRepository repository;

    public ShopAppService(ShopAppRepository repository) {
        this.repository = repository;
    }

    public void addAccount(PersonalAccount account){
        repository.addAccount(account);
    }

    public void changeAccount(PersonalAccount account, long id){
        repository.changeAccount(account, id);
    }

    public PersonalAccount getAccountById(long id){
        return repository.getAccountById(id);
    }

    public void addNotification(Notification notification, long id){
        repository.addNotification(notification, id);
    }

    public PersonalAccount authorization(String login, String password){
        return repository.authorization(login, password);
    }

    public PersonalAccount getAccountByMail(String mail){
        return repository.getAccountByMail(mail);
    }

    public void freezeAccount(long id, boolean freezeStatus){
        repository.freezeAccount(id, freezeStatus);
    }

    public void addOrganization(Organization newOrganization, long userId){
        repository.addOrganization(newOrganization, userId);
    }

    public Organization getOrganizationById(long id){
        return repository.getOrganizationById(id);
    }

    public Organization getOrganizationByName(String name){
        return repository.getOrganizationByName(name);
    }

    public void deleteOrganization(long id){
        repository.deleteOrganization(id);
    }

    public void addProduct(Product newProduct){
        repository.addProduct(newProduct);
    }

    public boolean deleteAccountById(long id){
        return repository.deleteAccountById(id);
    }

    public List<Product> getProductAll(){
        return repository.getProductAll();
    }

    public long getProductIdByName(String name){
        return repository.getProductIdByName(name);
    }

    public boolean buyProduct(List<Product> products, long userId){
        return repository.buyProduct(products, userId);
    }

    public boolean refundProducts(long operationId, long accountId){
        return repository.refundProducts(operationId, accountId);
    }

    public void changeProduct(Product newProduct, long id, AccountStatus status){
        repository.changeProduct(newProduct, id, status);
    }

    public List getProductByKeyWord(String keyWord){
        return repository.getProductsByKeyWord(keyWord);
    }

    public Product getProductById(long id){
        return repository.getProductById(id);
    }

    public void deleteProduct(long productId){
        repository.deleteProduct(productId);
    }
    public void addDiscount(Discount discount, List<Product> products){
        repository.addDiscount(discount, products);
    }

    public int getDiscountIdByField(Discount discount){
        return repository.getDiscountIdByField(discount);
    }

    public void changeDiscount(Discount newDiscount, int discountId){
        repository.changeDiscount(newDiscount, discountId);
    }
}
