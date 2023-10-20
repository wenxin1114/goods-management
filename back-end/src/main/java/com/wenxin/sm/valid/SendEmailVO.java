package com.wenxin.sm.valid;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
@Data
public class SendEmailVO {
    @NotBlank(message = "邮箱账号非法")
    @Email
    private String email;
    @NotBlank(message = "验证码不能为空")
    private String picCode;
    @NotBlank(message = "时间戳不能为空")
    private String timestamp;
}
