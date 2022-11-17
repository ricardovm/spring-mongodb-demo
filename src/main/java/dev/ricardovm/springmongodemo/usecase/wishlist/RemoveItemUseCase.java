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
        var result = repository.removeItemFromList(clientId, productId);

        if (!result) {
            throw new NotFoundException("list not found");
        }
    }
}
