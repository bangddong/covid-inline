package com.study.covidinline.controller.api;

import com.study.covidinline.constant.PlaceType;
import com.study.covidinline.dto.APIDataResponse;
import com.study.covidinline.dto.PlaceDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
public class APIPlaceController {

    @GetMapping("/places")
    public APIDataResponse<List<PlaceDTO>> getPlaces() {
        return APIDataResponse.of(List.of(PlaceDTO.of(
                PlaceType.COMMON,
                "찰리베드민턴장",
                "서울시 강남구 강남대로 1234",
                "010-1234-5678",
                30,
                "신장개업"
        )));
    }

    @PostMapping("/places")
    public Boolean createPlace() {
        return true;
    }

    @GetMapping("/places/{placeId}")
    public APIDataResponse<PlaceDTO> getPlace(@PathVariable Integer placeId) {
        if (placeId.equals(2)) {
            return APIDataResponse.of(null);
        }

        return APIDataResponse.of(PlaceDTO.of(
                PlaceType.COMMON,
                "찰리베드민턴장",
                "서울시 강남구 강남대로 1234",
                "010-1234-5678",
                30,
                "신장개업"
        ));
    }

    @PostMapping("/places/{placeId}")
    public boolean modifyPlace(@PathVariable Integer placeId) {
        return true;
    }

    @DeleteMapping("/places/{placeId}")
    public boolean deletePlace(@PathVariable Integer placeId) {
        return true;
    }

}
