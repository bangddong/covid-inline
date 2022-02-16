package com.study.covidinline.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlaceController.class)
class PlaceControllerTest {

    private final MockMvc mvc;

    public PlaceControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[view][GET] 장소 리스트 페이지")
    @Test
    void givenNothing_whenRequestingPlacesPage_thenReturnsPlacesPage() throws Exception {
        //given

        //when & then
        mvc.perform(get("/places"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("place/index"));
    }

    @DisplayName("[view][GET] 장소 세부 정보 페이지")
    @Test
    void givenNothing_whenRequestingPlaceDetailPage_thenReturnsPlaceDetailPage() throws Exception {
        //given
        long placeId = 1L;

        //when & then
        mvc.perform(get("/places/" + placeId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("place/detail"));
    }

}
