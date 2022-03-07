package com.study.covidinline.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.study.covidinline.constant.ErrorCode;
import com.study.covidinline.constant.EventStatus;
import com.study.covidinline.constant.PlaceType;
import com.study.covidinline.domain.Event;
import com.study.covidinline.domain.Place;
import com.study.covidinline.dto.EventDTO;
import com.study.covidinline.exception.GeneralException;
import com.study.covidinline.repository.EventRepository;
import com.study.covidinline.repository.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - 이벤트")
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService sut;

    @Mock
    private EventRepository eventRepository;
    @Mock private PlaceRepository placeRepository;

    @DisplayName("이벤트를 검색하면, 결과를 출력하여 보여준다.")
    @Test
    void givenNothing_whenSearchingEvents_thenReturnsEntireEventList() {
        //given
        given(eventRepository.findAll(any(Predicate.class)))
                .willReturn(List.of(
                        createEvent("오전 운동", true),
                        createEvent("오후 운동", false)
                ));
        //when
        List<EventDTO> list = sut.getEvents(new BooleanBuilder());

        //then
        assertThat(list).hasSize(2);
        then(eventRepository).should().findAll(any(Predicate.class));
    }

    @DisplayName("이벤트를 검색하는데 에러가 발생한 경우, 기본 에러로 전환하여 예외 던진다.")
    @Test
    void givenDataRelatedException_whenSearchingEvents_thenThrowsGeneralException() {
        //given
        RuntimeException e = new RuntimeException("This is test.");
        given(eventRepository.findAll(any(Predicate.class))).willThrow(e);

        //when
        Throwable thrown = catchThrowable(() -> sut.getEvents(new BooleanBuilder()));

        //then
        assertThat(thrown)
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorCode.DATA_ACCESS_ERROR.getMessage());
        then(eventRepository).should().findAll(any(Predicate.class));
    }

    @DisplayName("이벤트 ID로 존재하는 이벤트를 조회하면 해당 이벤트 정보를 출력하여 보여준다.")
    @Test
    void givenEventId_whenSearchingExistingEvent_thenReturnEvent() {
        //given
        Long eventId = 1L;
        Event event = createEvent("오전 운동", true);
        given(eventRepository.findById(eventId)).willReturn(Optional.of(event));

        //when
        Optional<EventDTO> result = sut.getEvent(eventId);

        //then
        assertThat(result).hasValue(EventDTO.of(event));
        then(eventRepository).should().findById(eventId);
    }

    @DisplayName("이벤트 ID로 이벤트를 조회하면 빈 정보를 출력하여 보여준다.")
    @Test
    void givenEventId_whenSearchingNonexistentEvent_thenReturnEmptyOptional() {
        //given
        Long eventId = 2L;
        given(eventRepository.findById(eventId)).willReturn(Optional.empty());

        //when
        Optional<EventDTO> result = sut.getEvent(eventId);

        //then
        assertThat(result).isEmpty();
        then(eventRepository).should().findById(eventId);
    }

    @DisplayName("이벤트 ID로 이벤트를 조회하는데 데이터 관련 에러가 발생한 경우, 줄서기 프로젝트 기본 에러로 전환하여 예외 던진다.")
    @Test
    void givenDataRelatedException_whenSearchingEvent_thenThrowsGeneralException() {
        //given
        RuntimeException e = new RuntimeException("This is test.");
        given(eventRepository.findById(any())).willThrow(e);

        //when
        Throwable thrown = catchThrowable(() -> sut.getEvent(null));
        assertThat(thrown)
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorCode.DATA_ACCESS_ERROR.getMessage());

        //then
        then(eventRepository).should().findById(any());
    }

    @DisplayName("이벤트 정보를 주면 이벤트를 생성하고 결과를 true 로 보여준다.")
    @Test
    void givenEvent_whenCreating_thenCreatesEventAndReturnsTrue() {
        //given
        EventDTO eventDto = EventDTO.of(createEvent("오후 운동", false));
        given(placeRepository.findById(eventDto.placeDTO().id())).willReturn(Optional.of(createPlace()));
        given(eventRepository.save(any(Event.class))).willReturn(any());

        //when
        boolean result = sut.createEvent(eventDto);

        //then
        assertThat(result).isTrue();
        then(placeRepository).should().findById(eventDto.placeDTO().id());
        then(eventRepository).should().save(any(Event.class));
    }

    @DisplayName("이벤트 정보를 주지 않으면 이벤트 생성을 중단하고 결과를 false 로 보여준다.")
    @Test
    void givenNothing_whenCreating_thenAbortCreatingAndReTurnFalse() {
        //given

        //when
        boolean result = sut.createEvent(null);

        //then
        assertThat(result).isFalse();
        then(placeRepository).shouldHaveNoInteractions();
        then(eventRepository).shouldHaveNoInteractions();
    }

    @DisplayName("이벤트 생성 중 장소 정보가 틀리거나 없으면, 줄서기 프로젝트 기본 에러로 전환하여 예외 던진다")
    @Test
    void givenWrongPlaceId_whenCreating_thenThrowsGeneralException() {
        // Given
        Event event = createEvent(null, false);
        given(placeRepository.findById(event.getPlace().getId())).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> sut.createEvent(EventDTO.of(event)));

        // Then
        assertThat(thrown)
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorCode.DATA_ACCESS_ERROR.getMessage());
        then(placeRepository).should().findById(event.getPlace().getId());
        then(eventRepository).shouldHaveNoInteractions();
    }

    @DisplayName("이벤트 생성 중 데이터 예외가 발생하면, 줄서기 프로젝트 기본 에러로 전환하여 예외 던진다")
    @Test
    void givenDataRelatedException_whenCreating_thenThrowsGeneralException() {
        //given
        Event event = createEvent(null, false);
        RuntimeException e = new RuntimeException("This is test.");
        given(placeRepository.findById(event.getPlace().getId())).willReturn(Optional.of(createPlace()));
        given(eventRepository.save(any())).willThrow(e);

        //when
        Throwable thrown = catchThrowable(() -> sut.createEvent(EventDTO.of(event)));

        //then
        assertThat(thrown)
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorCode.DATA_ACCESS_ERROR.getMessage());
        then(placeRepository).should().findById(event.getPlace().getId());
        then(eventRepository).should().save(any());
    }

    @DisplayName("이벤트 ID와 정보를 주면 이벤트 정보를 변경하고 결과를 true 로 보여준다.")
    @Test
    void givenEventIdAndItsInfo_whenModifying_thenModifiesEventAndReturnTrue() {
        //given
        Long eventId = 1L;
        Event originalEvent = createEvent("오후 운동", false);
        Event changedEvent = createEvent("오전 운동", true);
        given(eventRepository.findById(eventId)).willReturn(Optional.of(originalEvent));
        given(eventRepository.save(changedEvent)).willReturn(changedEvent);

        //when
        boolean result = sut.modifyEvent(eventId, EventDTO.of(changedEvent));

        //then
        assertThat(result).isTrue();
        assertThat(originalEvent.getEventName()).isEqualTo(changedEvent.getEventName());
        assertThat(originalEvent.getEventStartDatetime()).isEqualTo(changedEvent.getEventStartDatetime());
        assertThat(originalEvent.getEventEndDatetime()).isEqualTo(changedEvent.getEventEndDatetime());
        assertThat(originalEvent.getEventStatus()).isEqualTo(changedEvent.getEventStatus());
        then(eventRepository).should().findById(eventId);
        then(eventRepository).should().save(changedEvent);
    }

    @DisplayName("이벤트 ID를 주지 않으면 이벤트 정보 변경을 중단하고 결과를 false 로 보여준다.")
    @Test
    void givenNotEventId_whenModifying_whenAbortModifyingAndReturnFalse() {
        //given
        Event event = createEvent("오후 운동", false);


        //when
        boolean result = sut.modifyEvent(null, EventDTO.of(event));

        //then
        assertThat(result).isFalse();
        then(eventRepository).shouldHaveNoInteractions();
    }

    @DisplayName("이벤트 ID만 주고 변경할 정보를 주지 않으면 이벤트 정보 변경 중단하고 결과를 false 로 보여준다.")
    @Test
    void givenEventIdOnly_whenModifying_thenAbortModifyingAndReturnFalse() {
        //given
        Long eventId = 1L;

        //when
        boolean result = sut.modifyEvent(eventId, null);

        //then
        assertThat(result).isFalse();
        then(eventRepository).shouldHaveNoInteractions();
    }

    @DisplayName("이벤트 변경 중 데이터 오류가 발생하면, 줄서기 프로젝트 기본 에러로 전환하여 예외 던진다.")
    @Test
    void givenDataRelatedException_whenModifying_thenThrowsGeneralException() {
        //given
        long eventId = 1L;
        Event originalEvent = createEvent("오후 운동", false);
        Event wrongEvent = createEvent(null, false);
        RuntimeException e = new RuntimeException("This is test.");
        given(eventRepository.findById(eventId)).willReturn(Optional.of(originalEvent));
        given(eventRepository.save(any())).willThrow(e);

        //when
        Throwable thrown = catchThrowable(() -> sut.modifyEvent(eventId, EventDTO.of(wrongEvent)));

        //then
        assertThat(thrown)
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorCode.DATA_ACCESS_ERROR.getMessage());
        then(eventRepository).should().findById(eventId);
        then(eventRepository).should().save(any());
    }

        @DisplayName("이벤트 ID를 주면, 이벤트 정보를 삭제하고 결과를 true 로 보여준다.")
    @Test
    void givenEventId_whenDeleting_thenDeletesEventAndReturnTrue() {
        //given
        Long eventId = 1L;
        willDoNothing().given(eventRepository).deleteById(eventId);

        //when
        boolean result = sut.removeEvent(eventId);

        //then
        assertThat(result).isTrue();
            then(eventRepository).should().deleteById(eventId);
    }
    @DisplayName("이벤트 ID를 주지 않으면 삭제 중단하고 결과를 false 로 보여준다.")
    @Test
    void givenNothing_whenDeleting_then_AbortsDeletingAndReturnFalse() {
        //given

        //when
        boolean result = sut.removeEvent(null);

        //then
        assertThat(result).isFalse();
        then(eventRepository).shouldHaveNoInteractions();
    }

    @DisplayName("이벤트 삭제 중 데이터 오류가 발생하면, 줄서기 프로젝트 기본 에러로 전환하여 예외 던진다.")
    @Test
    void givenDataRelatedException_whenDeleting_thenThrowsGeneralException() {
        //given
        long eventId = 0L;
        RuntimeException e = new RuntimeException("This is test.");
        willThrow(e).given(eventRepository).deleteById(eventId);

        //when
        Throwable thrown = catchThrowable(() -> sut.removeEvent(eventId));

        //then
        assertThat(thrown)
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorCode.DATA_ACCESS_ERROR.getMessage());
        then(eventRepository).should().deleteById(eventId);
    }

    private Event createEvent(String eventName, boolean isMorning) {
        String hourStart = isMorning ? "09" : "13";
        String hourEnd = isMorning ? "12" : "16";

        return createEvent(
                1L,
                1L,
                eventName,
                EventStatus.OPENED,
                LocalDateTime.parse("2021-01-01T%s:00:00".formatted(hourStart)),
                LocalDateTime.parse("2021-01-01T%s:00:00".formatted(hourEnd))
        );
    }

    private Event createEvent(
            long id,
            Long placeId,
            String eventName,
            EventStatus eventStatus,
            LocalDateTime eventStartDatetime,
            LocalDateTime eventEndDatetime
    ) {
        Event event =  Event.of(
                createPlace(placeId),
                eventName,
                eventStatus,
                eventStartDatetime,
                eventEndDatetime,
                0,
                24,
                "마스크 꼭 착용하세요"
        );
        ReflectionTestUtils.setField(event, "id", id);

        return event;
    }

    private Place createPlace() {
        return createPlace(1L);
    }

    private Place createPlace(long id) {
        Place place = Place.of(PlaceType.COMMON, "test place", "test address", "010-1234-1234", 10, null);
        ReflectionTestUtils.setField(place, "id", id);

        return place;
    }

}