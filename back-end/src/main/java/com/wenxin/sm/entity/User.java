package com.wenxin.sm.entity;

import com.mybatisflex.annotation.ColumnMask;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wenxin.sm.valid.ValidGroup;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 管理者
 */
@Data
@ToString
@Table("user")
public class User extends BaseEntity {
    @Id(keyType = KeyType.Auto)
    private Integer id;
    @NotBlank(message = "账号不得为空")
    private String username;
    private String nickname;
    @NotBlank(message = "密码不得为空",groups = ValidGroup.Crud.Create.class)
    @ColumnMask("EMPTY")
    private String password;
    @Email(message = "邮箱账号非法")
    private String email;
    private Integer role;
    private Integer state;
    private String avatar;
}
