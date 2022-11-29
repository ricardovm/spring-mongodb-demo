package dev.ricardovm.springmongodemo.usecase.wishlist;

import dev.ricardovm.springmongodemo.domain.wishlist.WishList;
import dev.ricardovm.springmongodemo.domain.wishlist.WishListRepository;
import dev.ricardovm.springmongodemo.infra.annotations.UseCase;

import java.util.Optional;

@UseCase
public class GetListUseCase {

    private final WishListRepository repository;

    public GetListUseCase(WishListRepository repository) {
        this.repository = repository;
    }

    public Optional<WishList> execute(String clientId) {
        return repository.findById(clientId);
    }
}
