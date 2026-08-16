package edu.studyarena.training.repository;

import edu.studyarena.training.entity.Meeting;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

//Acceso a la tabla de meetings, Spring escribe las consultas a partir del nombre de cada metodo
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @EntityGraph(attributePaths = "creator")
    List<Meeting> findAllByOrderByScheduledAtAsc();

    //Busca una reunion y trae a su creador en la misma consulta
    @Override
    @EntityGraph(attributePaths = "creator")
    Optional<Meeting> findById(Long id);

    boolean existsByRoomName(String roomName);
}
