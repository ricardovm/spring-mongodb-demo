package dev.ricardovm.springmongodemo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Document
public class WishList {

    public static final int ITEMS_LIMIT = 20;

    @Id
    private String client;

    private Instant createdAt = Instant.now();

    private List<Item> items = new ArrayList<>();

    public WishList() {

    }

    public WishList(String client) {
        this.client = client;
    }

    boolean addItem(Item item) {
        if (items.size() == ITEMS_LIMIT
                || items.stream().anyMatch(i -> Objects.equals(i.getProductId(), item.getProductId()))) {
            return false;
        }

        items.add(item);

        return true;
    }

    boolean removeItem(Item item) {
        return items.remove(item);
    }

    boolean removeItem(String productId) {
        return items.removeIf(item -> Objects.equals(item.productId, productId));
    }

    public String getClient() {
        return client;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public static class Item {

        private String productId;
        private String productName;
        private Instant includedAt = Instant.now();

        public Item() {

        }

        public Item(String productId, String productName) {
            this.productId = productId;
            this.productName = productName;
        }

        public String getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public Instant getIncludedAt() {
            return includedAt;
        }
    }
}
