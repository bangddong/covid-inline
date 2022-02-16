package com.study.covidinline.controller.api;

import com.study.covidinline.dto.APIDataResponse;
import com.study.covidinline.dto.AdminRequest;
import com.study.covidinline.dto.LoginRequest;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
public class APIAuthController {

    @PostMapping("/sign-up")
    public APIDataResponse<String> signUp(@RequestBody AdminRequest adminRequest) {
        return APIDataResponse.empty();
    }

    @PostMapping("/login")
    public APIDataResponse<String> login(@RequestBody LoginRequest loginRequest) {
        return APIDataResponse.empty();
    }

}
