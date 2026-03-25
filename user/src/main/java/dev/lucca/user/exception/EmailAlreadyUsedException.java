package dev.lucca.user.exception;

public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException(String email) {
        super("Email ja cadastrado: " + email);
    }
}

