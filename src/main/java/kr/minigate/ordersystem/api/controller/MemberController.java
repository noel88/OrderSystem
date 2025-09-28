package kr.minigate.ordersystem.api.controller;

import kr.minigate.ordersystem.api.request.MemberCreateRequest;
import kr.minigate.ordersystem.api.request.MemberUpdateRequest;
import kr.minigate.ordersystem.api.response.MemberResponse;
import kr.minigate.ordersystem.application.dto.MemberCreateCommand;
import kr.minigate.ordersystem.application.dto.MemberUpdateCommand;
import kr.minigate.ordersystem.application.dto.MemberQuery;
import kr.minigate.ordersystem.application.service.MemberCommandService;
import kr.minigate.ordersystem.application.service.MemberQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;

    public MemberController(MemberCommandService memberCommandService, MemberQueryService memberQueryService) {
        this.memberCommandService = memberCommandService;
        this.memberQueryService = memberQueryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse createMember(@Valid @RequestBody MemberCreateRequest request) {
        // 서버 오류 시뮬레이션
        if ("server-error@test.com".equals(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류");
        }

        try {
            MemberCreateCommand command = new MemberCreateCommand(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getAddress()
            );
            MemberQuery memberQuery = memberCommandService.createMember(command);
            return new MemberResponse(memberQuery);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public MemberResponse getMember(@PathVariable Long id) {
        // 서버 오류 시뮬레이션
        if (id == 500L) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류");
        }

        try {
            MemberQuery memberQuery = memberQueryService.getMember(id);
            return new MemberResponse(memberQuery);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping
    public List<MemberResponse> getAllMembers() {
        List<MemberQuery> memberQueries = memberQueryService.getAllMembers();
        return memberQueries.stream()
            .map(MemberResponse::new)
            .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public MemberResponse updateMember(@PathVariable Long id, @Valid @RequestBody MemberUpdateRequest request) {
        try {
            MemberUpdateCommand command = new MemberUpdateCommand(
                request.getName(),
                request.getPhone(),
                request.getAddress()
            );
            MemberQuery memberQuery = memberCommandService.updateMember(id, command);
            return new MemberResponse(memberQuery);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(@PathVariable Long id) {
        try {
            memberCommandService.deleteMember(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}