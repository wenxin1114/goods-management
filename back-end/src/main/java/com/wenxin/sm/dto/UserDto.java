package com.wenxin.sm.dto;

import com.wenxin.sm.entity.User;
import lombok.Data;


@Data
public class UserDto extends User {
    private String code;
}
