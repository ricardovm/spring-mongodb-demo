package dev.ricardovm.springmongodemo.usecase.wishlist;

import dev.ricardovm.springmongodemo.domain.exception.NotFoundException;
import dev.ricardovm.springmongodemo.domain.wishlist.WishList;
import dev.ricardovm.springmongodemo.domain.wishlist.WishListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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

        when(repository.removeItemFromList(any(), any())).thenReturn(true);

        instance.execute(clientId, productId);

        verify(repository).removeItemFromList(clientId, productId);
    }

    @Test
    @DisplayName("GIVEN I have a list with items WHEN I try to remove an non-existing one THEN it should throw NotFoundException")
    void given_i_have_a_list_with_items_when_i_try_to_remove_an_non_existing_one_then_it_should_throw_notfoundexception() {
        var clientId =  "client";
        var wishList = new WishList(clientId);

        var productId = "productId:1234";

        wishList.addProduct(productId);

        when(repository.removeItemFromList(any(), any())).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> instance.execute(clientId, productId + ":" + UUID.randomUUID()));
    }

    @Test
    @DisplayName("GIVEN I have a non-existing client WHEN I try to remove an item THEN it should throw NotFoundException")
    void given_i_have_a_non_existing_client_when_i_try_to_remove_an_item_then_it_should_throw_notfoundexception() {
        when(repository.removeItemFromList(any(), any())).thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> instance.execute("client", "productId:" + UUID.randomUUID()));
    }
}