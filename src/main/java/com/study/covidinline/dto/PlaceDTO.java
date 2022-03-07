package com.study.covidinline.dto;

import com.study.covidinline.constant.PlaceType;
import com.study.covidinline.domain.Place;

import java.time.LocalDateTime;

public record PlaceDTO(
        Long id,
        PlaceType placeType,
        String placeName,
        String address,
        String phoneNumber,
        Integer capacity,
        String memo,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
    public static PlaceDTO of(
            Long id,
            PlaceType placeType,
            String placeName,
            String address,
            String phoneNumber,
            Integer capacity,
            String memo,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt
    ) {
        return new PlaceDTO(id, placeType, placeName, address, phoneNumber, capacity, memo, createdAt, modifiedAt);
    }

    public static PlaceDTO of(Place place) {
        return new PlaceDTO(
                place.getId(),
                place.getPlaceType(),
                place.getPlaceName(),
                place.getAddress(),
                place.getPhoneNumber(),
                place.getCapacity(),
                place.getMemo(),
                place.getCreatedAt(),
                place.getModifiedAt()
        );
    }

    public Place toEntity() {
        return Place.of(placeType, placeName, address, phoneNumber, capacity, memo);
    }

    public Place updateEntity(Place place) {
        if (placeType != null) place.setPlaceType(placeType);
        if (placeName != null) place.setPlaceName(placeName);
        if (address != null) place.setAddress(address);
        if (phoneNumber != null) place.setPhoneNumber(phoneNumber);
        if (capacity != null) place.setCapacity(capacity);
        if (memo != null) place.setMemo(memo);

        return place;
    }
}