package com.study.covidinline.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminPlaceMap {

    private Long id;

    private Long adminId;
    private Long placeId;

    private LocalDateTime createAt;
    private LocalDateTime modifyAt;

}
