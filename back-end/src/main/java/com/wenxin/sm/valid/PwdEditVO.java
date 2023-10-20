package com.wenxin.sm.valid;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Data
public class PwdEditVO {
    @Email(message = "邮箱账号非法")
    private String email;

    @NotBlank(message = "密码不得为空")
    private String password;

    @NotBlank(message = "验证码不得为空")
    private String emailCode;
}
