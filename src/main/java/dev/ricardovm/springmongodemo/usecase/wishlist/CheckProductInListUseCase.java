package dev.ricardovm.springmongodemo.usecase.wishlist;

import dev.ricardovm.springmongodemo.domain.exception.NotFoundException;
import dev.ricardovm.springmongodemo.domain.wishlist.WishListRepository;
import dev.ricardovm.springmongodemo.infra.annotations.UseCase;

@UseCase
public class CheckProductInListUseCase {

    private final WishListRepository repository;

    public CheckProductInListUseCase(WishListRepository repository) {
        this.repository = repository;
    }

    public boolean execute(String clientId, String productId) throws NotFoundException {
        var result = repository.findById(clientId)
                        .map(wl -> wl.getItems().contains(productId));

        if (result.isEmpty()) {
            throw new NotFoundException("List not found");
        }

        return result.get();
    }
}
