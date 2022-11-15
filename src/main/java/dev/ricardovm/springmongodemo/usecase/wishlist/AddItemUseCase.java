package dev.ricardovm.springmongodemo.usecase.wishlist;

import dev.ricardovm.springmongodemo.domain.exception.NotAddedException;
import dev.ricardovm.springmongodemo.domain.wishlist.WishList;
import dev.ricardovm.springmongodemo.domain.wishlist.WishListRepository;
import org.springframework.stereotype.Service;

@Service
public class AddItemUseCase {

    private final WishListRepository repository;

    public AddItemUseCase(WishListRepository repository) {
        this.repository = repository;
    }

    public WishList execute(String clientId, String productId) throws NotAddedException {
        var wishList = repository.findById(clientId).orElseGet(() -> new WishList(clientId));

        var wasAdded = wishList.addProduct(productId);

        if (!wasAdded) {
            throw new NotAddedException("This item could not be added to the list, the list is full");
        }

        return repository.save(wishList);
    }
}
