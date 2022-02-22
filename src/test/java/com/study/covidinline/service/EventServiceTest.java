package com.study.covidinline.service;

import com.study.covidinline.constant.ErrorCode;
import com.study.covidinline.constant.EventStatus;
import com.study.covidinline.dto.EventDTO;
import com.study.covidinline.exception.GeneralException;
import com.study.covidinline.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("비즈니스 로직 - 이벤트")
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService sut;

    @Mock
    private EventRepository eventRepository;

    @DisplayName("검색 조건 없이 이벤트 검색하면 전체 결과를 보여준다.")
    @Test
    void givenNothing_whenSearchingEvents_thenReturnsEntireEventList() {
        //given
        given(eventRepository.findEvents(null, null, null, null, null))
                .willReturn(List.of(
                        createEventDTO(1L, "오전 운동", true),
                        createEventDTO(1L, "오후 운동", false)
                ));

        //when
        List<EventDTO> list = sut.getEvents(null, null, null, null, null);

        //then
        assertThat(list).hasSize(2);
        then(eventRepository).should().findEvents(null, null, null, null, null);
    }

    @DisplayName("이벤트를 검색하는데 에러가 발생한 경우, 기본 에러로 전환하여 예외 던진다.")
    @Test
    void givenDataRelatedException_whenSearchingEvents_thenThrowsGeneralException() {
        //given
        RuntimeException e = new RuntimeException("This is test.");
        given(eventRepository.findEvents(any(), any(), any(), any(), any())).willThrow(e);

        //when
        Throwable thrown = catchThrowable(() -> sut.getEvents(null, null, null, null, null));

        //then
        assertThat(thrown)
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorCode.DATA_ACCESS_ERROR.getMessage());
        then(eventRepository).should().findEvents(null, null, null, null, null);
    }

    @DisplayName("검색 조건과 함께 이벤트 검색하면 검색된 결과를 보여준다.")
    @Test
    void givenSearchingParams_whenSearchingEvents_thenReturnsEntireEventList() {
        //given
        Long placeId = 1L;
        String eventName = "오전 운동";
        EventStatus eventStatus = EventStatus.OPENED;
        LocalDateTime eventStartDateTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        LocalDateTime eventEndDateTime = LocalDateTime.of(2021, 1, 2, 0, 0, 0);

        given(eventRepository.findEvents(placeId, eventName, eventStatus, eventStartDateTime, eventEndDateTime))
                .willReturn(List.of(
                        createEventDTO(1L, "오전 운동", eventStatus, eventStartDateTime, eventEndDateTime)
                ));

        //when
        List<EventDTO> list = sut.getEvents(placeId, eventName, eventStatus, eventStartDateTime, eventEndDateTime);

        //then
        assertThat(list)
                .hasSize(1)
                .allSatisfy(event -> {
                    assertThat(event)
                            .hasFieldOrPropertyWithValue("placeId", placeId)
                            .hasFieldOrPropertyWithValue("eventName", eventName)
                            .hasFieldOrPropertyWithValue("eventStatus", eventStatus);
                    assertThat(event.eventStartDatetime()).isAfterOrEqualTo(eventStartDateTime);
                    assertThat(event.eventStartDatetime()).isAfterOrEqualTo(eventStartDateTime);
                });
        then(eventRepository).should().findEvents(placeId, eventName, eventStatus, eventStartDateTime, eventEndDateTime);
    }

    @DisplayName("이벤트 ID로 존재하는 이벤트를 조회하면 해당 이벤트 정보를 출력하여 보여준다.")
    @Test
    void givenEventId_whenSearchingExistingEvent_thenReturnEvent() {
        //given
        Long eventId = 1L;
        EventDTO eventDTO = createEventDTO(1L, "오전 운동", true);
        given(eventRepository.findEvent(eventId)).willReturn(Optional.of(eventDTO));

        //when
        Optional<EventDTO> result = sut.getEvent(eventId);

        //then
        assertThat(result).hasValue(eventDTO);
        then(eventRepository).should().findEvent(eventId);
    }

    @DisplayName("이벤트 ID로 이벤트를 조회하면 빈 정보를 출력하여 보여준다.")
    @Test
    void givenEventId_whenSearchingNonexistentEvent_thenReturnEmptyOptional() {
        //given
        Long eventId = 2L;
        given(eventRepository.findEvent(eventId)).willReturn(Optional.empty());

        //when
        Optional<EventDTO> result = sut.getEvent(eventId);

        //then
        assertThat(result).isEmpty();
        then(eventRepository).should().findEvent(eventId);
    }

    @DisplayName("이벤트 정보를 주면 이벤트를 생성하고 결과를 true 로 보여준다.")
    @Test
    void givenEvent_whenCreating_thenCreatesEventAndReturnsTrue() {
        //given
        EventDTO dto = createEventDTO(1L, "오후 운동", false);
        given(eventRepository.insertEvent(dto)).willReturn(true);

        //when
        boolean result = sut.createEvent(dto);

        //then
        assertThat(result).isTrue();
        then(eventRepository).should().insertEvent(dto);
    }

    @DisplayName("이벤트 정보를 주면 이벤트 생성을 중단하고 결과를 false 로 보여준다.")
    @Test
    void givenNothing_whenCreating_thenAbortCreatingAndReTurnFalse() {
        //given
        given(eventRepository.insertEvent(null)).willReturn(false);

        //when
        boolean result = sut.createEvent(null);

        //then
        assertThat(result).isFalse();
        then(eventRepository).should().insertEvent(null);
    }

    @DisplayName("이벤트 ID와 정보를 주면 이벤트 정보를 변경하고 결과를 true 로 보여준다.")
    @Test
    void givenEventIdAndItsInfo_whenModifying_thenModifiesEventAndReturnTrue() {
        //given
        Long eventId = 1L;
        EventDTO dto = createEventDTO(1L, "오후 운동", false);
        given(eventRepository.updateEvent(eventId, dto)).willReturn(true);

        //when
        boolean result = sut.modifyEvent(eventId, dto);

        //then
        assertThat(result).isTrue();
        then(eventRepository).should().updateEvent(eventId, dto);
    }

    @DisplayName("이벤트 ID를 주지 않으면 이벤트 정보 변경을 중단하고 결과를 false 로 보여준다.")
    @Test
    void givenNotEventId_whenModifying_whenAbortModifyingAndReturnFalse() {
        //given
        EventDTO dto = createEventDTO(1L, "오후 운동", false);
        given(eventRepository.updateEvent(null, dto)).willReturn(false);

        //when
        boolean result = sut.modifyEvent(null, dto);

        //then
        assertThat(result).isFalse();
        then(eventRepository).should().updateEvent(null, dto);
    }

    @DisplayName("이벤트 ID만 주고 변경할 정보를 주지 않으면 이벤트 정보 변경 중단하고 결과를 false 로 보여준다.")
    @Test
    void givenEventIdOnly_whenModifying_thenAbortModifyingAndReturnFalse() {
        //given
        Long eventId = 1L;
        given(eventRepository.updateEvent(eventId, null)).willReturn(false);

        //when
        boolean result = sut.modifyEvent(eventId, null);

        //then
        assertThat(result).isFalse();
        then(eventRepository).should().updateEvent(eventId, null);
    }

    @DisplayName("이벤트 ID를 주면, 이벤트 정보를 삭제하고 결과를 true 로 보여준다.")
    @Test
    void givenEventId_whenDeleting_thenDeletesEventAndReturnTrue() {
        //given
        Long eventId = 1L;
        given(eventRepository.deleteEvent(eventId)).willReturn(true);

        //when
        boolean result = sut.removeEvent(eventId);

        //then
        assertThat(result).isTrue();
        then(eventRepository).should().deleteEvent(eventId);
    }
    @DisplayName("이벤트 ID를 주지 않으면 삭제 중단하고 결과를 false 로 보여준다.")
    @Test
    void givenNothing_whenDeleting_then_AbortsDeletingAndReturnFalse() {
        //given
        given(eventRepository.deleteEvent(null)).willReturn(false);

        //when
        boolean result = sut.removeEvent(null);

        //then
        assertThat(result).isFalse();
        then(eventRepository).should().deleteEvent(null);
    }

    private EventDTO createEventDTO(long placeId, String eventName, boolean isMorning) {
        String hourStart = isMorning ? "09" : "13";
        String hourEnd = isMorning ? "12" : "16";

        return createEventDTO(
                placeId,
                eventName,
                EventStatus.OPENED,
                LocalDateTime.parse("2021-01-01T%s:00:00".formatted(hourStart)),
                LocalDateTime.parse("2021-01-01T%s:00:00".formatted(hourEnd))
        );
    }

    private EventDTO createEventDTO(
            Long placeId,
            String eventName,
            EventStatus eventStatus,
            LocalDateTime eventStartDatetime,
            LocalDateTime eventEndDatetime
    ) {
        return EventDTO.of(
                1L,
                placeId,
                eventName,
                eventStatus,
                eventStartDatetime,
                eventEndDatetime,
                0,
                24,
                "마스크 꼭 착용하세요",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

}