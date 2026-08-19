package edu.studyarena.training.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String email) {
        super("No existe un usuario con el email " + email);
    }
}
