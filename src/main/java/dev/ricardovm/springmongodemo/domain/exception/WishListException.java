package dev.ricardovm.springmongodemo.domain.exception;

public abstract class WishListException extends Exception {

    protected WishListException(String message) {
        super(message);
    }
}
