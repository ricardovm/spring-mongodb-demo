package dev.ricardovm.springmongodemo.usecase.wishlist;

import dev.ricardovm.springmongodemo.domain.exception.NotFoundException;
import dev.ricardovm.springmongodemo.domain.wishlist.WishList;
import dev.ricardovm.springmongodemo.domain.wishlist.WishListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RemoveItemUseCaseTest {

    private RemoveItemUseCase instance;

    @Mock
    private WishListRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(repository.save(any())).thenAnswer(a -> a.getArgument(0));

        this.instance = new RemoveItemUseCase(repository);
    }

    @Test
    @DisplayName("GIVEN I have a list with items WHEN I try to remove an existing one THEN it should be removed")
    void given_i_have_a_list_with_items_when_i_try_to_remove_an_existing_one_then_it_should_be_removed() throws NotFoundException {
        var clientId =  "client";
        var wishList = new WishList(clientId);

        var productId = "productId:" + UUID.randomUUID();

        wishList.addProduct(productId);

        when(repository.findById(clientId)).thenReturn(Optional.of(wishList));

        instance.execute(clientId, productId);

        assertFalse(wishList.getItems().contains(productId));
    }

    @Test
    @DisplayName("GIVEN I have a list with items WHEN I try to remove an non-existing one THEN it should throws NotFoundException")
    void given_i_have_a_list_with_items_when_i_try_to_remove_an_non_existing_one_then_it_should_throws_notfoundexception() {
        var clientId =  "client";
        var wishList = new WishList(clientId);

        var productId = "productId:1234";

        wishList.addProduct(productId);

        when(repository.findById(clientId)).thenReturn(Optional.of(wishList));

        assertThrows(
                NotFoundException.class,
                () -> instance.execute(clientId, productId + ":" + UUID.randomUUID()));
    }

    @Test
    @DisplayName("GIVEN I have a non-existing client WHEN I try to remove an item THEN it should throws NotFoundException")
    void given_i_have_a_non_existing_client_when_i_try_to_remove_an_item_then_it_should_throws_notfoundexception() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> instance.execute("client", "productId:" + UUID.randomUUID()));
    }
}