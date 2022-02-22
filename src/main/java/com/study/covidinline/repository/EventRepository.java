package com.study.covidinline.repository;

import com.study.covidinline.constant.EventStatus;
import com.study.covidinline.domain.Event;
import com.study.covidinline.dto.EventDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// TODO: 인스턴스 생성 편의를 위해 임시로 default 사용.
// TODO: repository layer 구현시 삭제예정
public interface EventRepository extends JpaRepository<Event, Long> {

    default List<EventDTO> findEvents(
            Long placeId,
            String eventName,
            EventStatus eventStatus,
            LocalDateTime eventStartDatetime,
            LocalDateTime eventEndDatetime
    ) {
        return List.of();
    }

    default Optional<EventDTO> findEvent(Long eventId) {
        return Optional.empty();
    }

    default boolean insertEvent(EventDTO eventDTO) {
        return false;
    }

    default boolean updateEvent(Long eventId, EventDTO eventDTO) {
        return false;
    }

    default boolean deleteEvent(Long eventId) {
        return false;
    }

}
