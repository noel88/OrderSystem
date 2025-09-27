package kr.minigate.ordersystem.api.controller;

import kr.minigate.ordersystem.api.request.MemberCreateRequest;
import kr.minigate.ordersystem.api.response.MemberResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse createMember(@Valid @RequestBody MemberCreateRequest request) {
        // 서버 오류 시뮬레이션
        if ("server-error@test.com".equals(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류");
        }

        // TODO: Service 계층 연결
        return new MemberResponse(1L, request.getName(), request.getEmail(),
                                request.getPhone(), request.getAddress());
    }

    @GetMapping("/{id}")
    public MemberResponse getMember(@PathVariable Long id) {
        // 존재하지 않는 회원 체크
        if (id == 999L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다");
        }

        // 서버 오류 시뮬레이션
        if (id == 500L) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류");
        }

        // TODO: Service 계층 연결
        return new MemberResponse(id, "홍길동", "hong@test.com", "010-1234-5678", "서울시 강남구");
    }
}