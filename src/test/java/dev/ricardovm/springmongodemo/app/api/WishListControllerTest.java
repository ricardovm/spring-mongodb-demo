package dev.ricardovm.springmongodemo.app.api;

import dev.ricardovm.springmongodemo.app.dto.WishListDTO;
import dev.ricardovm.springmongodemo.domain.exception.NotAddedException;
import dev.ricardovm.springmongodemo.domain.exception.NotFoundException;
import dev.ricardovm.springmongodemo.domain.wishlist.WishList;
import dev.ricardovm.springmongodemo.domain.wishlist.WishListRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ContextConfiguration(initializers = WishListControllerTest.Initializer.class)
class WishListControllerTest {

    @LocalServerPort
    private int port = 0;

    @Autowired
    public TestRestTemplate restTemplate;

    @Autowired
    public WishListRepository repository;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.3");

    @BeforeAll
    static void initAll() {
        mongoDBContainer.start();
    }

    @AfterAll
    static void clean() {
        mongoDBContainer.stop();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(configurableApplicationContext,
                    String.format("spring.data.mongodb.uri=mongodb://%s:%s/db", mongoDBContainer.getHost(), mongoDBContainer.getMappedPort(27017)));
        }
    }

    @Test
    @DisplayName("GIVEN I have a clientId WHEN I search for this client's list THEN it shound return it")
    void given_i_have_a_clientid_when_i_search_for_this_clients_list_then_it_shound_return_it() {
        var clientId = UUID.randomUUID().toString();

        repository.save(new WishList(clientId));

        var response = restTemplate.getForEntity(
                "http://localhost:%d/wishlist/%s".formatted(port, clientId),
                WishListDTO.class);
        var result = response.getBody();

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(clientId, result.getClientId());
    }

    @Test
    @DisplayName("GIVEN I have a clientId WHEN I search for an non-existing list THEN it shound return empty")
    void given_i_have_a_clientid_when_i_search_for_an_non_existing_list_then_it_shound_return_empty() {
        var clientId = UUID.randomUUID().toString();

        var response = restTemplate.getForEntity(
                "http://localhost:%d/wishlist/%s".formatted(port, clientId),
                Void.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    @DisplayName("GIVEN I have client with no wish list WHEN I try to add a new item THEN it should be added")
    void given_i_have_client_with_no_wish_list_when_i_try_to_add_a_new_item_then_it_should_be_added() throws NotAddedException {
        var clientId = "clientId:" + UUID.randomUUID();
        var productId = "productId:" + UUID.randomUUID();

        var response = restTemplate.postForEntity(
                "http://localhost:%d/wishlist/%s/%s".formatted(port, clientId, productId),
                null,
                Void.class);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());

        assertTrue(repository.findById(clientId).isPresent());
    }

    @ParameterizedTest(name = "{index} =>  add {0} items THEN it should be added")
    @ValueSource(ints = {18, 19, 20})
    @DisplayName("GIVEN I have a list WHEN I try to...")
    void given_i_have_a_list_when_i_try_to_add_X_items_then_it_should_be_added(int numItems) {
        var clientId = "clientId:" + UUID.randomUUID();

        for (var i = 0; i < numItems; i++) {
            var productId = "productId:" + UUID.randomUUID();

            var response = restTemplate.postForEntity(
                    "http://localhost:%d/wishlist/%s/%s".formatted(port, clientId, productId),
                    null,
                    Void.class);

            assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());

            var result = repository.findById(clientId).get();
            assertTrue(result.getItems().contains(productId));
            assertEquals(i + 1, result.getItems().size());
        }
    }

    @Test
    @DisplayName("GIVEN I have a list WHEN I try to add 20 items THEN it should return bad request")
    void given_i_have_a_list_when_i_try_to_add_20_items_then_it_should_return_bad_request() {
        var clientId = "clientId:" + UUID.randomUUID();
        var wishList = new WishList(clientId);

        for (var i = 0; i < 20; i++) {
            wishList.addProduct("productId:" + UUID.randomUUID());
        }

        repository.save(wishList);

        var productId = "productId:" + UUID.randomUUID();
        var response = restTemplate.postForEntity(
                "http://localhost:%d/wishlist/%s/%s".formatted(port, clientId, productId),
                null,
                Void.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    @DisplayName("GIVEN I have a list WHEN I try to add an item twice THEN it should not be added")
    void given_i_have_a_list_when_i_try_to_add_an_items_twice_then_it_should_not_be_added() {
        var clientId = "clientId:" + UUID.randomUUID();
        var wishList = new WishList(clientId);

        var productId = "productId:" + UUID.randomUUID();

        wishList.addProduct(productId);
        repository.save(wishList);

        var response = restTemplate.postForEntity(
                "http://localhost:%d/wishlist/%s/%s".formatted(port, clientId, productId),
                null,
                Void.class);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());

        var result = repository.findById(clientId).get();

        assertEquals(1, result.getItems().size());
    }

    @Test
    @DisplayName("GIVEN I have a list with items WHEN I try to remove an existing one THEN it should be removed")
    void given_i_have_a_list_with_items_when_i_try_to_remove_an_existing_one_then_it_should_be_removed() throws NotFoundException {
        var clientId = "clientId:" + UUID.randomUUID();
        var wishList = new WishList(clientId);

        var productId = "productId:" + UUID.randomUUID();

        wishList.addProduct(productId);
        repository.save(wishList);

        var response = restTemplate.exchange(
                "http://localhost:%d/wishlist/%s/%s".formatted(port, clientId, productId),
                HttpMethod.DELETE,
                null,
                Object.class);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCodeValue());

        var result = repository.findById(clientId).get();

        assertFalse(result.getItems().contains(productId));
    }

    @Test
    @DisplayName("GIVEN I have a list with items WHEN I try to remove an non-existing one THEN it should return not found")
    void given_i_have_a_list_with_items_when_i_try_to_remove_an_non_existing_one_then_it_should_return_notfoundexception() {
        var clientId = "clientId:" + UUID.randomUUID();
        var wishList = new WishList(clientId);

        var productId = "productId:" + UUID.randomUUID();

        wishList.addProduct(productId);
        repository.save(wishList);

        var removingProductId = "productId:1234";

        var response = restTemplate.exchange(
                "http://localhost:%d/wishlist/%s/%s".formatted(port, clientId, removingProductId),
                HttpMethod.DELETE,
                null,
                Object.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    @DisplayName("GIVEN I have a non-existing client WHEN I try to remove an item THEN it should return NotFoundException")
    void given_i_have_a_non_existing_client_when_i_try_to_remove_an_item_then_it_should_return_notfoundexception() {
        var clientId = "clientId:" + UUID.randomUUID();

        var removingProductId = "productId:1234";

        var response = restTemplate.exchange(
                "http://localhost:%d/wishlist/%s/%s".formatted(port, clientId, removingProductId),
                HttpMethod.DELETE,
                null,
                Object.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    @DisplayName("GIVEN I have a clientId WHEN I check for a existing item THEN it should return true")
    void given_i_have_a_clientid_when_i_check_for_a_existing_item_then_it_should_return_true() throws NotFoundException {
        var clientId = "clientId:" + UUID.randomUUID();
        var productId = "productId:" + UUID.randomUUID();

        var wishList = new WishList(clientId);
        wishList.addProduct(productId);
        repository.save(wishList);

        var response = restTemplate.getForEntity(
                "http://localhost:%d/wishlist/%s/%s".formatted(port, clientId, productId),
                Boolean.class);
        var result = response.getBody();

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertTrue(result);
    }

    @Test
    @DisplayName("GIVEN I have a clientId WHEN I check for a non-existing item THEN it should return false")
    void given_i_have_a_clientid_when_i_check_for_a_non_existing_item_then_it_should_return_false() throws NotFoundException {
        var clientId = "clientId:" + UUID.randomUUID();
        var productId = "productId:" + UUID.randomUUID();

        var wishList = new WishList(clientId);
        wishList.addProduct(productId);
        repository.save(wishList);

        var searchingProductId = "productId:" + UUID.randomUUID();

        var response = restTemplate.getForEntity(
                "http://localhost:%d/wishlist/%s/%s".formatted(port, clientId, searchingProductId),
                Boolean.class);
        var result = response.getBody();

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertFalse(result);
    }

    @Test
    @DisplayName("GIVEN I have a clientId WHEN I check for in non-existing list THEN it should throw NotFoundException")
    void given_i_have_a_clientid_when_i_check_for_in_non_existing_list_then_it_should_return_notfoundexception() {
        var clientId = "clientId:" + UUID.randomUUID();
        var productId = "productId:" + UUID.randomUUID();

        var response = restTemplate.getForEntity(
                "http://localhost:%d/wishlist/%s/%s".formatted(port, clientId, productId),
                Boolean.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }
}