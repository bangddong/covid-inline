package com.study.covidinline.domain;

import com.study.covidinline.constant.EventStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Event {

    private Long id;

    private Long placeId;
    private EventStatus eventStatus;
    private LocalDateTime eventStartDateTime;
    private LocalDateTime eventEndDateTime;
    private Integer currentNumberOfPeople;
    private Integer capacity;
    private String memo;

    private LocalDateTime createAt;
    private LocalDateTime modifyAt;

}
