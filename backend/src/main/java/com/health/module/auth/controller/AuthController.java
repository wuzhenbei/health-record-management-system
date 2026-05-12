package com.health.module.auth.controller;

import com.health.common.BusinessException;
import com.health.common.Result;
import com.health.module.auth.dto.LoginDTO;
import com.health.module.auth.vo.LoginVO;
import com.health.module.user.entity.SysUser;
import com.health.module.user.service.SysUserService;
import com.health.security.JwtUtil;
import com.health.security.LoginUser;
import com.health.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        SysUser user = userService.getByUsername(dto.getUsername());
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        LoginVO vo = LoginVO.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole())
                .avatar(user.getAvatar())
                .build();
        return Result.ok(vo);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<LoginVO> me() {
        LoginUser loginUser = SecurityUtil.getLoginUser();
        SysUser user = userService.getById(loginUser.getUserId());
        LoginVO vo = LoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole())
                .avatar(user.getAvatar())
                .build();
        return Result.ok(vo);
    }

    @Operation(summary = "修改个人密码")
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody java.util.Map<String, String> body) {
        LoginUser loginUser = SecurityUtil.getLoginUser();
        SysUser user = userService.getById(loginUser.getUserId());
        String oldPwd = body.get("oldPassword");
        String newPwd = body.get("newPassword");
        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        userService.resetPassword(user.getId(), newPwd);
        return Result.ok();
    }
}
