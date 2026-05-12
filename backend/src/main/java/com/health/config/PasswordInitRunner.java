package com.health.config;

import com.health.module.user.entity.SysUser;
import com.health.module.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动时强制将所有用户密码重置为 123456
 * 解决 SQL 脚本中 BCrypt 哈希不匹配导致无法登录的问题
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordInitRunner implements CommandLineRunner {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public void run(String... args) {
        List<SysUser> users = userMapper.selectList(null);
        log.warn("===== [PasswordInit] 共发现 {} 个用户，开始检测/重置密码 =====", users.size());
        int reset = 0;
        for (SysUser u : users) {
            boolean ok = false;
            try {
                ok = passwordEncoder.matches(DEFAULT_PASSWORD, u.getPassword());
            } catch (Exception e) {
                log.error("[PasswordInit] 用户 {} 密码哈希格式异常: {}", u.getUsername(), e.getMessage());
            }
            if (!ok) {
                String encoded = passwordEncoder.encode(DEFAULT_PASSWORD);
                u.setPassword(encoded);
                userMapper.updateById(u);
                log.warn("[PasswordInit] 已重置用户 [{}] 密码 -> 新哈希: {}", u.getUsername(), encoded);
                reset++;
            } else {
                log.info("[PasswordInit] 用户 [{}] 密码已正确，跳过", u.getUsername());
            }
        }
        log.warn("===== [PasswordInit] 完成，重置 {} 个用户的密码 =====", reset);
    }
}
