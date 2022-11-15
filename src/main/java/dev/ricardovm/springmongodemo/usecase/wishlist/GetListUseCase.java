package dev.ricardovm.springmongodemo.usecase.wishlist;

import dev.ricardovm.springmongodemo.domain.wishlist.WishList;
import dev.ricardovm.springmongodemo.domain.wishlist.WishListRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetListUseCase {

    private final WishListRepository repository;

    public GetListUseCase(WishListRepository repository) {
        this.repository = repository;
    }

    public Optional<WishList> execute(String clientId) {
        return repository.findById(clientId);
    }
}
