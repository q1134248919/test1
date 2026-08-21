package com.family.assistant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.family.assistant.common.BusinessException;
import com.family.assistant.common.UserContext;
import com.family.assistant.dto.CreateFamilyRequest;
import com.family.assistant.dto.FamilyVO;
import com.family.assistant.dto.JoinFamilyRequest;
import com.family.assistant.dto.MemberVO;
import com.family.assistant.entity.FamilyGroup;
import com.family.assistant.entity.FamilyMemory;
import com.family.assistant.entity.FamilyTodo;
import com.family.assistant.entity.User;
import com.family.assistant.mapper.FamilyGroupMapper;
import com.family.assistant.mapper.FamilyMemoryMapper;
import com.family.assistant.mapper.FamilyTodoMapper;
import com.family.assistant.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 12;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserMapper userMapper;
    private final FamilyGroupMapper familyGroupMapper;
    private final FamilyTodoMapper familyTodoMapper;
    private final FamilyMemoryMapper familyMemoryMapper;

    @Transactional
    public FamilyVO create(CreateFamilyRequest req) {
        User user = currentUser();
        if (user.getFamilyId() != null) {
            throw new BusinessException("你已在家庭群中，请先退出再创建");
        }
        FamilyGroup group = new FamilyGroup();
        group.setName(req.getName().trim());
        group.setOwnerId(user.getId());
        group.setInviteCode(nextInviteCode());
        familyGroupMapper.insert(group);
        bindFamily(user.getId(), group.getId());
        return toVO(group, user.getId());
    }

    @Transactional
    public FamilyVO join(JoinFamilyRequest req) {
        User user = currentUser();
        if (user.getFamilyId() != null) {
            throw new BusinessException("你已在家庭群中，请先退出再加入");
        }
        String code = normalizeCode(req.getInviteCode());
        FamilyGroup group = familyGroupMapper.selectOne(
                new LambdaQueryWrapper<FamilyGroup>().eq(FamilyGroup::getInviteCode, code));
        if (group == null) {
            throw new BusinessException("邀请码无效");
        }
        bindFamily(user.getId(), group.getId());
        return toVO(group, user.getId());
    }

    public FamilyVO mine() {
        User user = currentUser();
        if (user.getFamilyId() == null) {
            return null;
        }
        FamilyGroup group = familyGroupMapper.selectById(user.getFamilyId());
        if (group == null) {
            bindFamily(user.getId(), null);
            return null;
        }
        return toVO(group, user.getId());
    }

    @Transactional
    public FamilyVO refreshInviteCode() {
        User user = currentUser();
        FamilyGroup group = requireGroup(user);
        if (!group.getOwnerId().equals(user.getId())) {
            throw new BusinessException("只有群主可以刷新邀请码");
        }
        group.setInviteCode(nextInviteCode());
        familyGroupMapper.updateById(group);
        return toVO(group, user.getId());
    }

    @Transactional
    public void leave() {
        User user = currentUser();
        FamilyGroup group = requireGroup(user);
        if (group.getOwnerId().equals(user.getId())) {
            throw new BusinessException("群主请使用解散家庭");
        }
        bindFamily(user.getId(), null);
    }

    @Transactional
    public void dissolve() {
        User user = currentUser();
        FamilyGroup group = requireGroup(user);
        if (!group.getOwnerId().equals(user.getId())) {
            throw new BusinessException("只有群主可以解散家庭");
        }
        List<User> members = userMapper.selectList(
                new LambdaQueryWrapper<User>().eq(User::getFamilyId, group.getId()));
        for (User member : members) {
            bindFamily(member.getId(), null);
        }
        familyTodoMapper.delete(
                new LambdaQueryWrapper<FamilyTodo>().eq(FamilyTodo::getFamilyId, group.getId()));
        familyMemoryMapper.delete(
                new LambdaQueryWrapper<FamilyMemory>().eq(FamilyMemory::getFamilyId, group.getId()));
        familyGroupMapper.deleteById(group.getId());
    }

    public User requireMember() {
        User user = currentUser();
        requireGroup(user);
        return user;
    }

    private FamilyGroup requireGroup(User user) {
        if (user.getFamilyId() == null) {
            throw new BusinessException("请先加入或创建家庭群");
        }
        FamilyGroup group = familyGroupMapper.selectById(user.getFamilyId());
        if (group == null) {
            bindFamily(user.getId(), null);
            throw new BusinessException("家庭群不存在，请重新加入");
        }
        return group;
    }

    private User currentUser() {
        User user = userMapper.selectById(UserContext.getUserId());
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }
        return user;
    }

    private void bindFamily(Long userId, Long familyId) {
        userMapper.update(null, new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getFamilyId, familyId));
    }

    private String normalizeCode(String raw) {
        return raw == null ? "" : raw.trim().toUpperCase().replace("-", "");
    }

    private String nextInviteCode() {
        for (int i = 0; i < 20; i++) {
            StringBuilder sb = new StringBuilder(CODE_LENGTH);
            for (int j = 0; j < CODE_LENGTH; j++) {
                sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
            }
            String code = sb.toString();
            Long count = familyGroupMapper.selectCount(
                    new LambdaQueryWrapper<FamilyGroup>().eq(FamilyGroup::getInviteCode, code));
            if (count == 0) {
                return code;
            }
        }
        throw new BusinessException("邀请码生成失败，请重试");
    }

    private FamilyVO toVO(FamilyGroup group, Long currentUserId) {
        List<MemberVO> members = userMapper.selectList(
                        new LambdaQueryWrapper<User>().eq(User::getFamilyId, group.getId()))
                .stream()
                .map(u -> new MemberVO(
                        String.valueOf(u.getId()),
                        u.getNickname(),
                        u.getUsername(),
                        group.getOwnerId().equals(u.getId())))
                .toList();
        FamilyVO vo = new FamilyVO();
        vo.setId(String.valueOf(group.getId()));
        vo.setName(group.getName());
        vo.setInviteCode(group.getInviteCode());
        vo.setOwnerId(String.valueOf(group.getOwnerId()));
        vo.setOwner(group.getOwnerId().equals(currentUserId));
        vo.setMembers(members);
        return vo;
    }
}
