package dev.ricardovm.springmongodemo.domain;

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

    public Optional<WishList> get(String client) {
        return repository.findById(client);
    }

    public WishList addItem(String client, WishList.Item item) throws NotAddedException {
        var wishList = repository.findById(client).orElseGet(() -> new WishList(client));

        var wasAdded = wishList.addItem(item);

        if (!wasAdded) {
            throw new NotAddedException(
                    "This item could not be added to the list. It may already have been added or the list is full");
        }

        return repository.save(wishList);
    }

    public void removeItem(String client, String productId) throws NotFoundException {
        var wishList = repository.findById(client);

        if (wishList.isEmpty()) {
            logger.warn("client {} not found", client);
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
