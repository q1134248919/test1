package com.family.assistant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.family.assistant.common.BusinessException;
import com.family.assistant.dto.CreateTodoRequest;
import com.family.assistant.dto.TodoVO;
import com.family.assistant.entity.FamilyTodo;
import com.family.assistant.entity.User;
import com.family.assistant.mapper.FamilyTodoMapper;
import com.family.assistant.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("MM-dd HH:mm");

    private final FamilyService familyService;
    private final FamilyTodoMapper familyTodoMapper;
    private final UserMapper userMapper;

    public List<TodoVO> list() {
        User user = familyService.requireMember();
        List<FamilyTodo> todos = familyTodoMapper.selectList(
                new LambdaQueryWrapper<FamilyTodo>()
                        .eq(FamilyTodo::getFamilyId, user.getFamilyId())
                        .orderByAsc(FamilyTodo::getDone)
                        .orderByDesc(FamilyTodo::getCreateTime));
        Map<Long, User> users = loadCreators(todos);
        return todos.stream().map(t -> toVO(t, users)).toList();
    }

    public TodoVO add(CreateTodoRequest req) {
        User user = familyService.requireMember();
        FamilyTodo todo = new FamilyTodo();
        todo.setFamilyId(user.getFamilyId());
        todo.setTitle(req.getTitle().trim());
        todo.setDone(0);
        todo.setCreatorId(user.getId());
        familyTodoMapper.insert(todo);
        return toVO(todo, Map.of(user.getId(), user));
    }

    public TodoVO toggle(Long id) {
        FamilyTodo todo = requireOwnedTodo(id);
        todo.setDone(todo.getDone() != null && todo.getDone() == 1 ? 0 : 1);
        familyTodoMapper.updateById(todo);
        User creator = userMapper.selectById(todo.getCreatorId());
        return toVO(todo, creator == null ? Map.of() : Map.of(creator.getId(), creator));
    }

    public void delete(Long id) {
        requireOwnedTodo(id);
        familyTodoMapper.deleteById(id);
    }

    private FamilyTodo requireOwnedTodo(Long id) {
        User user = familyService.requireMember();
        FamilyTodo todo = familyTodoMapper.selectById(id);
        if (todo == null || !todo.getFamilyId().equals(user.getFamilyId())) {
            throw new BusinessException("待办不存在");
        }
        return todo;
    }

    private Map<Long, User> loadCreators(List<FamilyTodo> todos) {
        List<Long> ids = todos.stream().map(FamilyTodo::getCreatorId).distinct().toList();
        if (ids.isEmpty()) {
            return Map.of();
        }
        return userMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    private TodoVO toVO(FamilyTodo todo, Map<Long, User> users) {
        User creator = users.get(todo.getCreatorId());
        return new TodoVO(
                String.valueOf(todo.getId()),
                todo.getTitle(),
                Objects.equals(todo.getDone(), 1),
                creator == null ? "已退出成员" : creator.getNickname(),
                todo.getCreateTime() == null ? "" : todo.getCreateTime().format(TIME_FMT));
    }
}
