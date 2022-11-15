package dev.ricardovm.springmongodemo.usecase.wishlist;

import dev.ricardovm.springmongodemo.domain.exception.NotFoundException;
import dev.ricardovm.springmongodemo.domain.wishlist.WishListRepository;
import org.springframework.stereotype.Service;

@Service
public class RemoveItemUseCase {

    private final WishListRepository repository;

    public RemoveItemUseCase(WishListRepository repository) {
        this.repository = repository;
    }

    public void execute(String clientId, String productId) throws NotFoundException {
        var wishList = repository.findById(clientId);

        if (wishList.isEmpty()) {
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
