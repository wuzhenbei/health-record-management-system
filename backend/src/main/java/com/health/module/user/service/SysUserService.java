package com.health.module.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.module.user.dto.UserCreateDTO;
import com.health.module.user.dto.UserQueryDTO;
import com.health.module.user.dto.UserUpdateDTO;
import com.health.module.user.entity.SysUser;

public interface SysUserService {

    Page<SysUser> page(UserQueryDTO dto);

    SysUser getById(Long id);

    SysUser getByUsername(String username);

    void create(UserCreateDTO dto);

    void update(Long id, UserUpdateDTO dto);

    void delete(Long id);

    void resetPassword(Long id, String newPassword);

    void updateStatus(Long id, Integer status);
}
