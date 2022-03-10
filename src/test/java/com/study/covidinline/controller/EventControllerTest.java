package com.study.covidinline.controller;

import com.study.covidinline.constant.EventStatus;
import com.study.covidinline.dto.EventDTO;
import com.study.covidinline.service.EventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("VIEW 컨트롤러 - 이벤트")
@WebMvcTest(EventController.class)
class EventControllerTest {

    private final MockMvc mvc;

    @MockBean
    private EventService eventService;

    public EventControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[view][GET] 이벤트 리스트 페이지")
    @Test
    void givenNothing_whenRequestingEventsPage_thenReturnsEventsPage() throws Exception {
        //given
        given(eventService.getEvents(any())).willReturn(List.of());

        //when & then
        mvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("event/index"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("events"));
        then(eventService).should().getEvents(any());
    }

    @DisplayName("[view][GET] 이벤트 리스트 페이지 - 커스텀 데이터")
    @Test
    void givenNothing_whenRequestingCustomEventsPage_thenReturnsEventsPage() throws Exception {
        // Given
        given(eventService.getEventViewResponse(any(), any(), any(), any(), any(), any())).willReturn(Page.empty());

        // When
        mvc.perform(get("/events/custom"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("event/index"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("events"));

        // Then
        then(eventService).should().getEventViewResponse(any(), any(), any(), any(), any(), any());
    }

    @DisplayName("[view][GET] 이벤트 리스트 페이지 - 커스텀 데이터 + 검색 파라미터")
    @Test
    void givenParams_when_requestingCustomEventsPage_thenReturnsEventsPage() throws Exception {
        // Given
        String placeName = "배드민턴";
        String eventName = "오후";
        EventStatus eventStatus = EventStatus.OPENED;
        LocalDateTime eventStartDatetime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        LocalDateTime eventEndDatetime = LocalDateTime.of(2021, 1, 2, 0, 0, 0);
        given(eventService.getEventViewResponse(
                placeName,
                eventName,
                eventStatus,
                eventStartDatetime,
                eventEndDatetime,
                PageRequest.of(1, 3)
        )).willReturn(Page.empty());

        // When
        mvc.perform(
                get("/events/custom")
                        .queryParam("placeName", placeName)
                        .queryParam("eventName", eventName)
                        .queryParam("eventStatus", eventStatus.name())
                        .queryParam("eventStartDatetime", eventStartDatetime.toString())
                        .queryParam("eventEndDatetime", eventEndDatetime.toString())
                        .queryParam("page", "1")
                        .queryParam("size", "3")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("event/index"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("events"));

        // Then
        then(eventService).should().getEventViewResponse(
                placeName,
                eventName,
                eventStatus,
                eventStartDatetime,
                eventEndDatetime,
                PageRequest.of(1, 3)
        );
    }

    @DisplayName("[view][GET] 이벤트 리스트 페이지 - 커스텀 데이터 + 검색 파라미터 (장소명, 이벤트명 잘못된 입력)")
    @Test
    void givenWrongParams_whenRequestingCustomEventsPage_thenReturnsEventsPage() throws Exception {
        // Given
        String placeName = "배";
        String eventName = "오";
        EventStatus eventStatus = EventStatus.OPENED;
        LocalDateTime eventStartDatetime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        LocalDateTime eventEndDatetime = LocalDateTime.of(2021, 1, 2, 0, 0, 0);
        given(eventService.getEventViewResponse(
                placeName,
                eventName,eventStatus,
                eventStartDatetime,
                eventEndDatetime,
                PageRequest.of(1, 3)
        )).willReturn(Page.empty());

        // When
        mvc.perform(
                get("/events/custom")
                        .queryParam("placeName", placeName)
                        .queryParam("eventName", eventName)
                        .queryParam("eventStatus", eventStatus.name())
                        .queryParam("eventStartDatetime", eventStartDatetime.toString())
                        .queryParam("eventEndDatetime", eventEndDatetime.toString())
                        .queryParam("page", "1")
                        .queryParam("size", "3")
        )
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("error"))
                .andExpect(model().attributeDoesNotExist("events"));

        //Then
        then(eventService).shouldHaveNoInteractions();
    }

    @DisplayName("[view][GET] 이벤트 세부 정보 페이지")
    @Test
    void givenEventId_whenRequestingEventDetailPage_thenReturnsEventDetailPage() throws Exception {
        //given
        long eventId = 1L;
        given(eventService.getEvent(eventId)).willReturn(Optional.of(
                EventDTO.of(eventId, null, null, null, null, null, null, null, null, null, null)
        ));

        //when & then
        mvc.perform(get("/events/" + eventId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("event/detail"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("event"));
        then(eventService).should().getEvent(eventId);
    }

    @DisplayName("[view][GET] 이벤트 세부 정보 페이지 - 데이터 없음")
    @Test
    void givenNonexistentEventId_whenRequestingEventDetailPage_thenReturnsErrorPage() throws Exception {
        // Given
        long eventId = 0L;
        given(eventService.getEvent(eventId)).willReturn(Optional.empty());

        // When & Then
        mvc.perform(get("/events/" + eventId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("error"));

        then(eventService).should().getEvent(eventId);
    }

}
