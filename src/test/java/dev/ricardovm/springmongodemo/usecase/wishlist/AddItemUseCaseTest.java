package dev.ricardovm.springmongodemo.usecase.wishlist;

import dev.ricardovm.springmongodemo.domain.exception.NotAddedException;
import dev.ricardovm.springmongodemo.domain.wishlist.WishList;
import dev.ricardovm.springmongodemo.domain.wishlist.WishListRepository;
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

class AddItemUseCaseTest {

    private AddItemUseCase instance;

    @Mock
    private WishListRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(repository.save(any())).thenAnswer(a -> a.getArgument(0));

        this.instance = new AddItemUseCase(repository);
    }
    
    @Test
    @DisplayName("GIVEN I have client with no wish list WHEN I try to add a new item THEN it should be added")
    void given_i_have_client_with_no_wish_list_when_i_try_to_add_a_new_item_then_it_should_be_added() throws NotAddedException {
        var clientId =  "client:" + UUID.randomUUID();
        var productId = "productId:" + UUID.randomUUID();

        var result = instance.execute(clientId, productId);

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
                var result = instance.execute(clientId, productId);
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
            instance.execute(clientId, "productId:" + UUID.randomUUID());
        }

        assertThrows(NotAddedException.class,
                () -> instance.execute(clientId, "productId:" + UUID.randomUUID()));
    }

    @Test
    @DisplayName("GIVEN I have a list WHEN I try to add an item twice THEN it should not be added")
    void given_i_have_a_list_when_i_try_to_add_an_items_twice_then_it_should_not_be_added() throws NotAddedException {
        var clientId =  "client";
        var wishList = new WishList(clientId);

        when(repository.findById(clientId)).thenReturn(Optional.of(wishList));

        var productId = "productId:" + UUID.randomUUID();

        instance.execute(clientId, productId);

        var result = instance.execute(clientId, productId);

        assertEquals(1, result.getItems().size());
    }

}