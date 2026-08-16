package edu.studyarena.training.repository;

import edu.studyarena.training.entity.Meeting;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @EntityGraph(attributePaths = "creator")
    List<Meeting> findAllByOrderByScheduledAtAsc();

    @Override
    @EntityGraph(attributePaths = "creator")
    Optional<Meeting> findById(Long id);

    boolean existsByRoomName(String roomName);
}
