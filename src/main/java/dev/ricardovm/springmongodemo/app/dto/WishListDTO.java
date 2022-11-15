package dev.ricardovm.springmongodemo.app.dto;

import dev.ricardovm.springmongodemo.domain.wishlist.WishList;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(name = "WishList")
public class WishListDTO {

    @Schema(description = "client identification")
    private String clientId;

    @Schema(description = "items of the list")
    private Set<String> items;

    public WishListDTO() {
    }

    public WishListDTO(WishList wishList) {
        this.clientId = wishList.getClientId();
        this.items = wishList.getItems();
    }

    public String getClientId() {
        return clientId;
    }

    public Set<String> getItems() {
        return items;
    }
}
