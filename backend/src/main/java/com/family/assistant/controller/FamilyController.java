package com.family.assistant.controller;

import com.family.assistant.common.Result;
import com.family.assistant.dto.CreateFamilyRequest;
import com.family.assistant.dto.FamilyVO;
import com.family.assistant.dto.JoinFamilyRequest;
import com.family.assistant.service.FamilyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/family")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;

    @PostMapping
    public Result<FamilyVO> create(@Valid @RequestBody CreateFamilyRequest req) {
        return Result.ok(familyService.create(req));
    }

    @PostMapping("/join")
    public Result<FamilyVO> join(@Valid @RequestBody JoinFamilyRequest req) {
        return Result.ok(familyService.join(req));
    }

    @GetMapping("/mine")
    public Result<FamilyVO> mine() {
        return Result.ok(familyService.mine());
    }

    @PostMapping("/invite-code")
    public Result<FamilyVO> refreshInviteCode() {
        return Result.ok(familyService.refreshInviteCode());
    }

    @PostMapping("/leave")
    public Result<Void> leave() {
        familyService.leave();
        return Result.ok();
    }

    @DeleteMapping
    public Result<Void> dissolve() {
        familyService.dissolve();
        return Result.ok();
    }
}
