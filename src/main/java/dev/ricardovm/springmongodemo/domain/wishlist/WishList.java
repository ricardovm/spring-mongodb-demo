package dev.ricardovm.springmongodemo.domain.wishlist;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Document
public class WishList {

    public static final int ITEMS_LIMIT = 20;

    @Id
    private String clientId;

    private Set<String> items = new HashSet<>();

    public WishList() {

    }

    public WishList(String client) {
        this.clientId = client;
    }

    boolean addProduct(String productId) {
        if (items.size() == ITEMS_LIMIT) {
            return false;
        }

        items.add(productId);

        return true;
    }

    boolean removeItem(String productId) {
        return items.remove(productId);
    }

    public String getClientId() {
        return clientId;
    }

    public Set<String> getItems() {
        return Collections.unmodifiableSet(items);
    }
}
