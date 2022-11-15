package dev.ricardovm.springmongodemo.domain.wishlist;

import dev.ricardovm.springmongodemo.domain.exception.NotAddedException;
import dev.ricardovm.springmongodemo.domain.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WishListService {

    private static final Logger logger = LoggerFactory.getLogger(WishListService.class);

    private final WishListRepository repository;

    public WishListService(WishListRepository repository) {
        this.repository = repository;
    }

    public Optional<WishList> get(String clientId) {
        return repository.findById(clientId);
    }

    public WishList addItem(String clientId, String productId) throws NotAddedException {
        var wishList = repository.findById(clientId).orElseGet(() -> new WishList(clientId));

        var wasAdded = wishList.addProduct(productId);

        if (!wasAdded) {
            throw new NotAddedException(
                    "This item could not be added to the list, the list is full");
        }

        return repository.save(wishList);
    }

    public void removeItem(String clientId, String productId) throws NotFoundException {
        var wishList = repository.findById(clientId);

        if (wishList.isEmpty()) {
            logger.warn("client {} not found", clientId);
            throw new NotFoundException("List not found");
        }

        var list = wishList.get();

        var wasRemoved = list.removeItem(productId);

        if (!wasRemoved) {
            throw new NotFoundException("Item not found");
        }

        repository.save(list);
    }
}
