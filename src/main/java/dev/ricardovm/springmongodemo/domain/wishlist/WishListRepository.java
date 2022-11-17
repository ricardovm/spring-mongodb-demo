package dev.ricardovm.springmongodemo.domain.wishlist;

import java.util.Optional;

public interface WishListRepository {

    Optional<WishList> findById(String clientId);

    boolean removeItemFromList(String clientId, String productId);

    WishList save(WishList wishList);
}
