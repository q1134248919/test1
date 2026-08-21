package com.family.assistant.controller;

import com.family.assistant.common.Result;
import com.family.assistant.dto.MemoryVO;
import com.family.assistant.service.MemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/memories")
@RequiredArgsConstructor
public class MemoryController {

    private final MemoryService memoryService;

    @GetMapping
    public Result<List<MemoryVO>> list() {
        return Result.ok(memoryService.list());
    }

    @PostMapping
    public Result<MemoryVO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "caption", required = false) String caption) {
        return Result.ok(memoryService.upload(file, caption));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        memoryService.delete(id);
        return Result.ok();
    }
}
