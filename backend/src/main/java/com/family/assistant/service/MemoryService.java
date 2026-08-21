package com.family.assistant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.family.assistant.common.BusinessException;
import com.family.assistant.dto.MemoryVO;
import com.family.assistant.entity.FamilyMemory;
import com.family.assistant.entity.User;
import com.family.assistant.mapper.FamilyMemoryMapper;
import com.family.assistant.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoryService {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private final FamilyService familyService;
    private final FamilyMemoryMapper familyMemoryMapper;
    private final UserMapper userMapper;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Value("${app.file-base-url:/api/files}")
    private String fileBaseUrl;

    public List<MemoryVO> list() {
        User user = familyService.requireMember();
        List<FamilyMemory> list = familyMemoryMapper.selectList(
                new LambdaQueryWrapper<FamilyMemory>()
                        .eq(FamilyMemory::getFamilyId, user.getFamilyId())
                        .orderByDesc(FamilyMemory::getCreateTime));
        Map<Long, User> users = loadUploaders(list);
        return list.stream().map(m -> toVO(m, users)).toList();
    }

    public MemoryVO upload(MultipartFile file, String caption) {
        User user = familyService.requireMember();
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择图片");
        }
        String ext = extension(file.getOriginalFilename());
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BusinessException("仅支持 jpg / png / gif / webp");
        }
        String relative = "memories/" + UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Path dest = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(relative);
        try {
            Files.createDirectories(dest.getParent());
            file.transferTo(dest.toFile());
        } catch (IOException e) {
            throw new BusinessException("图片保存失败");
        }
        FamilyMemory memory = new FamilyMemory();
        memory.setFamilyId(user.getFamilyId());
        memory.setFilePath(relative);
        memory.setCaption(StringUtils.hasText(caption) ? caption.trim() : "");
        memory.setUploaderId(user.getId());
        familyMemoryMapper.insert(memory);
        return toVO(memory, Map.of(user.getId(), user));
    }

    public void delete(Long id) {
        User user = familyService.requireMember();
        FamilyMemory memory = familyMemoryMapper.selectById(id);
        if (memory == null || !memory.getFamilyId().equals(user.getFamilyId())) {
            throw new BusinessException("记忆不存在");
        }
        familyMemoryMapper.deleteById(id);
    }

    private String fileUrl(String relativePath) {
        String base = fileBaseUrl.endsWith("/") ? fileBaseUrl.substring(0, fileBaseUrl.length() - 1) : fileBaseUrl;
        return base + "/" + relativePath;
    }

    private String extension(String name) {
        if (!StringUtils.hasText(name) || !name.contains(".")) {
            return "jpg";
        }
        return name.substring(name.lastIndexOf('.') + 1).toLowerCase();
    }

    private Map<Long, User> loadUploaders(List<FamilyMemory> list) {
        List<Long> ids = list.stream().map(FamilyMemory::getUploaderId).distinct().toList();
        if (ids.isEmpty()) {
            return Map.of();
        }
        return userMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    private MemoryVO toVO(FamilyMemory memory, Map<Long, User> users) {
        User uploader = users.get(memory.getUploaderId());
        return new MemoryVO(
                String.valueOf(memory.getId()),
                fileUrl(memory.getFilePath()),
                memory.getCaption(),
                uploader == null ? "家人" : uploader.getNickname(),
                memory.getCreateTime() == null ? "" : memory.getCreateTime().format(TIME_FMT));
    }
}
