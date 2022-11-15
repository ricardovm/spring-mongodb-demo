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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CheckProductInListUseCaseTest {

    private CheckProductInListUseCase instance;

    @Mock
    private WishListRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.instance = new CheckProductInListUseCase(repository);
    }

    @Test
    @DisplayName("GIVEN I have a clientId WHEN I check for a existing item THEN it should return true")
    void given_i_have_a_clientid_when_i_check_for_a_existing_item_then_it_should_return_true() throws NotFoundException {
        var clientId = "client";
        var productId = "productId:" + UUID.randomUUID();
        var wishList = new WishList(clientId);
        wishList.addProduct(productId);

        when(repository.findById(clientId)).thenReturn(Optional.of(wishList));

        var result = instance.execute(clientId, productId);

        assertTrue(result);
    }

    @Test
    @DisplayName("GIVEN I have a clientId WHEN I check for a non-existing item THEN it should return false")
    void given_i_have_a_clientid_when_i_check_for_a_non_existing_item_then_it_should_return_false() throws NotFoundException {
        var clientId = "client";
        var productId = UUID.randomUUID().toString();
        var wishList = new WishList(clientId);
        wishList.addProduct(productId);

        when(repository.findById(clientId)).thenReturn(Optional.of(wishList));

        var result = instance.execute(clientId, "productId:1234");

        assertFalse(result);
    }

    @Test
    @DisplayName("GIVEN I have a clientId WHEN I check for in non-existing list THEN it should throw NotFoundException")
    void given_i_have_a_clientid_when_i_check_for_in_non_existing_list_then_it_should_throw_notfoundexception() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> instance.execute("clientId", "productId:1234"));
    }
}