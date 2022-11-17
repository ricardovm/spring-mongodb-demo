package dev.ricardovm.springmongodemo.external.db;

public interface CustomWishListRepository {

    boolean removeItemFromList(String clientId, String productId);
}

