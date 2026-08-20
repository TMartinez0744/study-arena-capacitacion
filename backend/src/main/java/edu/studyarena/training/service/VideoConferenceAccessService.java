package edu.studyarena.training.service;

import edu.studyarena.training.dto.VideoConferenceAccess;
import edu.studyarena.training.entity.Meeting;
import edu.studyarena.training.entity.User;

//para la integracion con videollamadas
public interface VideoConferenceAccessService {

    VideoConferenceAccess createAccess(Meeting meeting, User user);
}
