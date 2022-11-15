package dev.ricardovm.springmongodemo.external.db;

import dev.ricardovm.springmongodemo.domain.wishlist.WishList;
import dev.ricardovm.springmongodemo.domain.wishlist.WishListRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListRepositoryImpl extends WishListRepository, MongoRepository<WishList, String> {

}
