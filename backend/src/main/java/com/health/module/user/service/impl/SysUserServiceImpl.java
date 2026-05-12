package com.health.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.common.BusinessException;
import com.health.module.user.dto.UserCreateDTO;
import com.health.module.user.dto.UserQueryDTO;
import com.health.module.user.dto.UserUpdateDTO;
import com.health.module.user.entity.SysUser;
import com.health.module.user.mapper.SysUserMapper;
import com.health.module.user.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<SysUser> page(UserQueryDTO dto) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.hasText(dto.getUsername()), SysUser::getUsername, dto.getUsername())
                .like(StringUtils.hasText(dto.getRealName()), SysUser::getRealName, dto.getRealName())
                .eq(StringUtils.hasText(dto.getRole()), SysUser::getRole, dto.getRole())
                .eq(dto.getStatus() != null, SysUser::getStatus, dto.getStatus())
                .orderByDesc(SysUser::getCreateTime);
        return userMapper.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
    }

    @Override
    public SysUser getById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        return user;
    }

    @Override
    public SysUser getByUsername(String username) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
    }

    @Override
    public void create(UserCreateDTO dto) {
        if (getByUsername(dto.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setStatus(1);
        userMapper.insert(user);
    }

    @Override
    public void update(Long id, UserUpdateDTO dto) {
        SysUser user = getById(id);
        if (dto.getRealName() != null) user.setRealName(dto.getRealName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        if (dto.getRole() != null) user.setRole(dto.getRole());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());
        userMapper.updateById(user);
    }

    @Override
    public void delete(Long id) {
        if (userMapper.selectById(id) == null) throw new BusinessException("用户不存在");
        userMapper.deleteById(id);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        SysUser user = getById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysUser user = getById(id);
        user.setStatus(status);
        userMapper.updateById(user);
    }
}
