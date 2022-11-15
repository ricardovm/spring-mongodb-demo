package dev.ricardovm.springmongodemo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class WishListTest {

    @Test
    @DisplayName("GIVEN I have no list WHEN I create one THEN its id should be set and its createAt should be now AND its items should be empty")
    void given_i_have_no_list_when_i_create_one_then_its_id_should_be_set_and_its_createAt_should_be_now_and_its_items_should_be_empty() {
        var before = Instant.now();
        var wishList = new WishList("client");
        var after = Instant.now();

        assertEquals("client", wishList.getClient());

        assertTrue(wishList.getCreatedAt().isAfter(before) || before.equals(wishList.getCreatedAt()));
        assertTrue(wishList.getCreatedAt().isBefore(after) || after.equals(wishList.getCreatedAt()));

        assertTrue(wishList.getItems().isEmpty());
    }

    @Test
    @DisplayName("GIVEN I have a list WHEN I try to add a new item THEN it should be added")
    void given_i_have_a_list_when_i_try_to_add_a_new_item_then_it_should_be_added() {
        var wishList = new WishList();
        var item = new WishList.Item(
                "productId:" + UUID.randomUUID(),
                "productName:" + UUID.randomUUID());

        wishList.addItem(item);

        assertTrue(wishList.getItems().contains(item));
    }

    @ParameterizedTest(name = "{index} =>  add {0} items THEN it should return {1}")
    @MethodSource("numItemsResultProvider")
    @DisplayName("GIVEN I have a list WHEN I try to...")
    void given_i_have_a_list_when_i_try_to_add_X_items_then_it_should_return_Y(int numItems, boolean expectedResult) {
        var wishList = new WishList();

        Boolean lastResult = null;

        for (var i = 0; i < numItems; i++) {
            var item = new WishList.Item(
                    "productId:" + UUID.randomUUID(),
                    "productName:" + UUID.randomUUID());

            lastResult = wishList.addItem(item);

            assertEquals(lastResult, wishList.getItems().contains(item));
        }

        assertEquals(expectedResult, lastResult);
    }

    private static Stream<Arguments> numItemsResultProvider() {
        return Stream.of(
                Arguments.of(18, true),
                Arguments.of(19, true),
                Arguments.of(20, true),
                Arguments.of(21, false),
                Arguments.of(22, false),
                Arguments.of(23, false));
    }

    @Test
    @DisplayName("GIVEN I have a list WHEN I try to add an item twice THEN it should not be added")
    void given_i_have_a_list_when_i_try_to_add_an_item_twice_then_it_should_not_be_added() {
        var wishList = new WishList();
        var item = new WishList.Item(
                "productId:" + UUID.randomUUID(),
                "productName:" + UUID.randomUUID());

        var firstResult = wishList.addItem(item);
        assertTrue(firstResult);
        assertTrue(wishList.getItems().contains(item));

        var secondResult = wishList.addItem(item);
        assertFalse(secondResult);
        assertEquals(1, wishList.getItems().size());
    }

    @Test
    @DisplayName("GIVEN I have a list WHEN I try to add an item with same id THEN it should not be added")
    void given_i_have_a_list_when_i_try_to_add_an_item_with_same_id_then_it_should_not_be_added() {
        var wishList = new WishList();
        var item = new WishList.Item(
                "productId:" + UUID.randomUUID(),
                "productName:" + UUID.randomUUID());

        var firstResult = wishList.addItem(item);
        assertTrue(firstResult);
        assertTrue(wishList.getItems().contains(item));

        var secondItem = new WishList.Item(
                item.getProductId(),
                item.getProductName());
        var secondResult = wishList.addItem(secondItem);
        assertFalse(secondResult);
        assertEquals(1, wishList.getItems().size());
    }

    @Test
    @DisplayName("GIVEN I have a list with items WHEN I try to remove an existing one THEN it should be removed")
    void given_i_have_a_list_with_items_when_i_try_to_remove_an_existing_one_then_it_should_be_removed() {
        var wishList = new WishList();
        var item = new WishList.Item(
                "productId:" + UUID.randomUUID(),
                "productName:" + UUID.randomUUID());

        wishList.addItem(item);
        var removed = wishList.removeItem(item);

        assertTrue(removed);
        assertFalse(wishList.getItems().contains(item));
    }

    @Test
    @DisplayName("GIVEN I have a list with items WHEN I try to remove an non-existing one THEN it should return false")
    void given_i_have_a_list_with_items_when_i_try_to_remove_an_non_existing_one_then_it_should_return_false() {
        var wishList = new WishList();
        var item = new WishList.Item(
                "productId:" + UUID.randomUUID(),
                "productName:" + UUID.randomUUID());

        wishList.addItem(item);

        var removingItem = new WishList.Item(
                "productId:" + UUID.randomUUID(),
                "productName:" + UUID.randomUUID());
        var removed = wishList.removeItem(removingItem);

        assertFalse(removed);
        assertTrue(wishList.getItems().contains(item));
        assertEquals(1, wishList.getItems().size());
    }

    @Test
    @DisplayName("GIVEN I have a list with items WHEN I try to remove an existing one by its id THEN it should be removed")
    void given_i_have_a_list_with_items_when_i_try_to_remove_an_existing_one_by_its_id_then_it_should_be_removed() {
        var wishList = new WishList();
        var item = new WishList.Item(
                "productId:1234",
                "productName:" + UUID.randomUUID());

        wishList.addItem(item);
        var removed = wishList.removeItem("productId:1234");

        assertTrue(removed);
        assertFalse(wishList.getItems().contains(item));
    }

    @Test
    @DisplayName("GIVEN I have a list with items WHEN I try to remove an non_existing one by its id THEN it should return false")
    void given_i_have_a_list_with_items_when_i_try_to_remove_an_non_existing_one_by_its_id_then_it_should_return_false() {
        var wishList = new WishList();
        var item = new WishList.Item(
                "productId:" + UUID.randomUUID(),
                "productName:" + UUID.randomUUID());

        wishList.addItem(item);
        var removed = wishList.removeItem("productId:1234");

        assertFalse(removed);
        assertTrue(wishList.getItems().contains(item));
        assertEquals(1, wishList.getItems().size());
    }
}