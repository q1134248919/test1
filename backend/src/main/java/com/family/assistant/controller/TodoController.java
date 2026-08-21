package com.family.assistant.controller;

import com.family.assistant.common.Result;
import com.family.assistant.dto.CreateTodoRequest;
import com.family.assistant.dto.TodoVO;
import com.family.assistant.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public Result<List<TodoVO>> list() {
        return Result.ok(todoService.list());
    }

    @PostMapping
    public Result<TodoVO> add(@Valid @RequestBody CreateTodoRequest req) {
        return Result.ok(todoService.add(req));
    }

    @PutMapping("/{id}/toggle")
    public Result<TodoVO> toggle(@PathVariable Long id) {
        return Result.ok(todoService.toggle(id));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        todoService.delete(id);
        return Result.ok();
    }
}
