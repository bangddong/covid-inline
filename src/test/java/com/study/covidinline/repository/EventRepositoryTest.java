package com.study.covidinline.repository;

import com.querydsl.core.BooleanBuilder;
import com.study.covidinline.constant.EventStatus;
import com.study.covidinline.constant.PlaceType;
import com.study.covidinline.domain.Event;
import com.study.covidinline.domain.Place;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EventRepositoryTest {

    private final EventRepository sut;
    private final TestEntityManager testEntityManager;

    public EventRepositoryTest(
            @Autowired EventRepository sut,
            @Autowired TestEntityManager testEntityManager) {
        this.sut = sut;
        this.testEntityManager = testEntityManager;
    }

    @DisplayName("")
    @Test
    void test() {
        // Given
        Place place = createPlace();
        Event event = createEvent(place);
        testEntityManager.persist(place);
        testEntityManager.persist(event);

        // When
        Iterable<Event> events = sut.findAll(new BooleanBuilder());

        // Then
        assertThat(events).hasSize(7);
    }

    private Event createEvent(Place place) {
        return createEvent(
                place,
                "test event",
                EventStatus.ABORTED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private Event createEvent(
            Place place,
            String eventName,
            EventStatus eventStatus,
            LocalDateTime eventStartDatetime,
            LocalDateTime eventEndDatetime
    ) {
        return Event.of(
                place,
                eventName,
                eventStatus,
                eventStartDatetime,
                eventEndDatetime,
                0,
                24,
                "마스크 꼭 착용하세요"
        );
    }

    private Place createPlace() {
        return Place.of(PlaceType.COMMON, "test place", "test address", "010-1234-1234", 10, null);
    }

}