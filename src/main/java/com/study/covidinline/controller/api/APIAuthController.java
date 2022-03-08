package com.study.covidinline.controller.api;

import com.study.covidinline.dto.APIDataResponse;
import com.study.covidinline.dto.AdminRequest;
import com.study.covidinline.dto.LoginRequest;
import org.springframework.web.bind.annotation.*;

/**
 * Spring Data REST 사용중으로 당장은 필요가 없음.
 * 필요에 따라 살릴 예정
 */
@Deprecated
//@RequestMapping("/api")
//@RestController
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
