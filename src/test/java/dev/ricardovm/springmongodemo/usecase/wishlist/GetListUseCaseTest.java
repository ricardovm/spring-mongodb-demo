package dev.ricardovm.springmongodemo.usecase.wishlist;

import dev.ricardovm.springmongodemo.domain.wishlist.WishList;
import dev.ricardovm.springmongodemo.domain.wishlist.WishListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class GetListUseCaseTest {

    private GetListUseCase instance;

    @Mock
    private WishListRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.instance = new GetListUseCase(repository);
    }

    @Test
    @DisplayName("GIVEN I have a clientId WHEN I search for this client's list THEN it shound return it")
    void given_i_have_a_clientid_when_i_search_for_this_clients_list_then_it_shound_return_it() {
        var clientId =  "client";
        var wishList = new WishList(clientId);

        when(repository.findById(clientId)).thenReturn(Optional.of(wishList));

        var result = instance.execute(clientId);

        assertEquals(wishList, result.get());
    }

    @Test
    @DisplayName("GIVEN I have a clientId WHEN I search for an non-existing list THEN it shound return empty")
    void given_i_have_a_clientid_when_i_search_for_an_non_existing_list_then_it_shound_return_empty() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        var result = instance.execute(UUID.randomUUID().toString());

        assertTrue(result.isEmpty());
    }
}