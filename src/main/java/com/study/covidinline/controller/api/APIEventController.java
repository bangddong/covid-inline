package com.study.covidinline.controller.api;

import com.study.covidinline.constant.EventStatus;
import com.study.covidinline.dto.APIDataResponse;
import com.study.covidinline.dto.EventRequest;
import com.study.covidinline.dto.EventResponse;
import com.study.covidinline.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data REST 사용중으로 당장은 필요가 없음.
 * 필요에 따라 살릴 예정
 */
@Deprecated
//@Validated
@RequiredArgsConstructor
//@RequestMapping("/api")
//@RestController
public class APIEventController {

    private final EventService eventService;

    @GetMapping("/events")
    public APIDataResponse<List<EventResponse>> getEvents(
            @Positive Long placeId,
            @Size(min = 2) String eventName,
            EventStatus eventStatus,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventStartDatetime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventEndDatetime
    ) {
        List<EventResponse> response = eventService
                .getEvents(placeId, eventName, eventStatus, eventStartDatetime, eventEndDatetime)
                .stream().map(EventResponse::from).toList();

        return APIDataResponse.of(response);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/events")
    public APIDataResponse<String> createEvent(@Valid @RequestBody EventRequest eventRequest) {
        boolean result = eventService.createEvent(eventRequest.toDTO());

        return APIDataResponse.of(Boolean.toString(result));
    }

    @GetMapping("/events/{eventId}")
    public APIDataResponse<EventResponse> getEvent(@Positive @PathVariable Long eventId) {
        EventResponse response = EventResponse.from(eventService.getEvent(eventId).orElse(null));

        return APIDataResponse.of(response);
    }

    @PutMapping("/events/{eventId}")
    public APIDataResponse<String> modifyEvent(
            @Positive @PathVariable Long eventId,
            @Valid @RequestBody EventRequest eventRequest
    ) {
        boolean result = eventService.modifyEvent(eventId, eventRequest.toDTO());

        return APIDataResponse.of(Boolean.toString(result));
    }

    @DeleteMapping("/events/{eventId}")
    public APIDataResponse<String> removeEvent(@Positive @PathVariable Long eventId) {
        boolean result = eventService.removeEvent(eventId);

        return APIDataResponse.of(Boolean.toString(result));
    }

}
