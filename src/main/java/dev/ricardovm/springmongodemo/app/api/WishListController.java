package dev.ricardovm.springmongodemo.app.api;

import dev.ricardovm.springmongodemo.app.dto.WishListDTO;
import dev.ricardovm.springmongodemo.domain.exception.NotAddedException;
import dev.ricardovm.springmongodemo.domain.exception.NotFoundException;
import dev.ricardovm.springmongodemo.usecase.wishlist.AddItemUseCase;
import dev.ricardovm.springmongodemo.usecase.wishlist.CheckProductInListUseCase;
import dev.ricardovm.springmongodemo.usecase.wishlist.GetListUseCase;
import dev.ricardovm.springmongodemo.usecase.wishlist.RemoveItemUseCase;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("wishlist")
@OpenAPIDefinition(
        info = @Info(
                title = "Wish List API",
                description = "API for wish list management"))
@Tag(name = "wish list controller")
public class WishListController {

    private final AddItemUseCase addItemUseCase;
    private final RemoveItemUseCase removeItemUseCase;
    private final GetListUseCase getListUseCase;
    private final CheckProductInListUseCase checkProductInListUseCase;

    public WishListController(AddItemUseCase addItemUseCase, RemoveItemUseCase removeItemUseCase, GetListUseCase getListUseCase, CheckProductInListUseCase checkProductInListUseCase) {
        this.addItemUseCase = addItemUseCase;
        this.removeItemUseCase = removeItemUseCase;
        this.getListUseCase = getListUseCase;
        this.checkProductInListUseCase = checkProductInListUseCase;
    }

    @GetMapping("{clientId}")
    @Operation(
            summary = "Gets the clients wish list",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WishListDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "When list doesn't exist",
                            content = @Content()
                    )
            }
    )
    public ResponseEntity<WishListDTO> get(@PathVariable String clientId) {
        var wishList = getListUseCase.execute(clientId);

        if (wishList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new WishListDTO(wishList.get()));
    }

    @GetMapping("{clientId}/{productId}")
    @Operation(
            summary = "Gets true if an item/product exists in a client's wish list",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "When list doesn't exist or doesn't have this product",
                            content = @Content()
                    )
            }
    )
    public ResponseEntity<Boolean> checkProductInList(@PathVariable String clientId, @PathVariable String productId) {
        try {
            var result = checkProductInListUseCase.execute(clientId, productId);
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("{clientId}/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Adds an item/product to a list. The list will be created if doen't exist",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "The item was added to the clients's wish list. If the list doesn't exist, it is created."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "When the list is full or the item was already added into the list",
                            content = @Content()
                    )
            }
    )
    public void addItem(@PathVariable String clientId, @PathVariable String productId) {
        try {
            addItemUseCase.execute(clientId, productId);
        } catch (NotAddedException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage(),
                    e);
        }
    }

    @DeleteMapping("{clientId}/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Removes an item/product from a list",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "The item was removed from the clients's wish list"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "When the list doesn't exist or doesn't have this item"
                    )
            }
    )
    public void deleteItem(@PathVariable String clientId, @PathVariable String productId) {
        try {
            removeItemUseCase.execute(clientId, productId);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    e.getMessage(),
                    e);
        }
    }
}
