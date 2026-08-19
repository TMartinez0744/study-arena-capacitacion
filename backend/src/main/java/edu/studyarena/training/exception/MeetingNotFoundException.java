package edu.studyarena.training.exception;

public class MeetingNotFoundException extends RuntimeException {

    public MeetingNotFoundException(Long id) {
        super("No existe una reunion con el id " + id);
    }
}
