package dev.ricardovm.springmongodemo.domain.exception;

public class NotFoundException extends WishListException {

    public NotFoundException(String message) {
        super(message);
    }
}
