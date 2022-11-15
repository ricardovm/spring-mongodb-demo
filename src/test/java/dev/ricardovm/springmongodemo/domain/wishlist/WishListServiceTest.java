package dev.ricardovm.springmongodemo.domain.wishlist;

import dev.ricardovm.springmongodemo.domain.exception.NotAddedException;
import dev.ricardovm.springmongodemo.domain.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WishListServiceTest {

    private WishListService instance;

    @Mock
    private WishListRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(repository.save(any())).thenAnswer(a -> a.getArgument(0));

        this.instance = new WishListService(repository);
    }

    @Test
    @DisplayName("GIVEN I have a client id WHEN I search for its wish list THEN I get the wish list")
    void given_i_have_a_client_id_when_i_search_for_its_wish_list_then_i_get_the_wish_list() {
        var id = "client";
        var productId = new WishList(id);

        when(repository.findById(id)).thenReturn(Optional.of(productId));

        var result = instance.get(id);

        assertEquals(productId, result.get());
    }

    @Test
    @DisplayName("GIVEN I have a client id WHEN I search for its wish list and it doesn't exist THEN I get none")
    void given_i_have_a_client_id_when_i_search_for_its_wish_list_and_it_doesnt_exist_then_i_get_none() {
        var id = "client";

        when(repository.findById(id)).thenReturn(Optional.empty());

        var result = instance.get(id);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("GIVEN I have client with no wish list WHEN I try to add a new item THEN it should be added")
    void given_i_have_client_with_no_wish_list_when_i_try_to_add_a_new_item_then_it_should_be_added() throws NotAddedException {
        var clientId =  "client:" + UUID.randomUUID();
        var productId = "productId:" + UUID.randomUUID();

        var result = instance.addItem(clientId, productId);

        assertEquals(clientId, result.getClientId());
        assertTrue(result.getItems().contains(productId));
        verify(repository).save(result);
    }


    @ParameterizedTest(name = "{index} =>  add {0} items THEN it should be added")
    @ValueSource(ints = {18, 19, 20})
    @DisplayName("GIVEN I have a list WHEN I try to...")
    void given_i_have_a_list_when_i_try_to_add_X_items_then_it_should_be_added(int numItems) {
        var clientId =  "client";
        var wishList = new WishList(clientId);

        when(repository.findById(clientId)).thenReturn(Optional.of(wishList));

        for (var i = 0; i < numItems; i++) {
            var productId = "productId:" + UUID.randomUUID();

            try {
                var result = instance.addItem(clientId, productId);
                assertTrue(result.getItems().contains(productId));
                assertEquals(i + 1, result.getItems().size());
            } catch (Exception e) {
                fail(e);
            }
        }
    }

    @Test
    @DisplayName("GIVEN I have a list WHEN I try to add 20 items THEN it should throws NotAddedException")
    void given_i_have_a_list_when_i_try_to_add_20_items_then_it_should_throws_notaddedexception() throws NotAddedException {
        var clientId =  "client";
        var wishList = new WishList(clientId);

        when(repository.findById(clientId)).thenReturn(Optional.of(wishList));

        for (var i = 0; i < 20; i++) {
            instance.addItem(clientId, "productId:" + UUID.randomUUID());
        }

        assertThrows(NotAddedException.class,
                () -> instance.addItem(clientId, "productId:" + UUID.randomUUID()));
    }

    @Test
    @DisplayName("GIVEN I have a list WHEN I try to add an item twice THEN it should not be added")
    void given_i_have_a_list_when_i_try_to_add_an_items_twice_then_it_should_not_be_added() throws NotAddedException {
        var clientId =  "client";
        var wishList = new WishList(clientId);

        when(repository.findById(clientId)).thenReturn(Optional.of(wishList));

        var productId = "productId:" + UUID.randomUUID();

        instance.addItem(clientId, productId);

        var result = instance.addItem(clientId, productId);

        assertEquals(1, result.getItems().size());
    }

    @Test
    @DisplayName("GIVEN I have a list with items WHEN I try to remove an existing one THEN it should be removed")
    void given_i_have_a_list_with_items_when_i_try_to_remove_an_existing_one_then_it_should_be_removed() throws NotFoundException {
        var clientId =  "client";
        var wishList = new WishList(clientId);

        var productId = "productId:" + UUID.randomUUID();

        wishList.addProduct(productId);

        when(repository.findById(clientId)).thenReturn(Optional.of(wishList));

        instance.removeItem(clientId, productId);

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
                () -> instance.removeItem(clientId, productId + ":" + UUID.randomUUID()));
    }

    @Test
    @DisplayName("GIVEN I have a non-existing client WHEN I try to remove an item THEN it should throws NotFoundException")
    void given_i_have_a_non_existing_client_when_i_try_to_remove_an_item_then_it_should_throws_notfoundexception() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> instance.removeItem("client", "productId:" + UUID.randomUUID()));
    }
}