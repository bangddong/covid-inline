package com.study.covidinline.dto;

import com.study.covidinline.constant.PlaceType;

public record PlaceResponse(
        Long id,
        PlaceType placeType,
        String placeName,
        String address,
        String phoneNumber,
        Integer capacity,
        String memo
) {
    public static PlaceResponse of(
            Long id,
            PlaceType placeType,
            String placeName,
            String address,
            String phoneNumber,
            Integer capacity,
            String memo
    ) {
        return new PlaceResponse(id, placeType, placeName, address, phoneNumber, capacity, memo);
    }

    public static PlaceResponse from(PlaceDTO placeDTO) {
        if (placeDTO == null) return null;
        return PlaceResponse.of(
                placeDTO.id(),
                placeDTO.placeType(),
                placeDTO.placeName(),
                placeDTO.address(),
                placeDTO.phoneNumber(),
                placeDTO.capacity(),
                placeDTO.memo()
        );
    }
}