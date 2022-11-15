package dev.ricardovm.springmongodemo.domain;

import java.util.Optional;

public interface WishListRepository {

    Optional<WishList> findById(String client);

    WishList save(WishList wishList);
}
