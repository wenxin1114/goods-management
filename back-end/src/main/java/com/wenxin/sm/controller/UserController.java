package com.wenxin.sm.controller;


import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.wenxin.sm.entity.Goods;
import com.wenxin.sm.entity.PageRequest;
import com.wenxin.sm.entity.Result;
import com.wenxin.sm.entity.User;
import com.wenxin.sm.mapper.UserMapper;
import com.wenxin.sm.utils.StringUtils;
import com.wenxin.sm.utils.TokenUtils;
import com.wenxin.sm.valid.PwdEditVO;
import com.wenxin.sm.valid.ValidGroup;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.wenxin.sm.entity.table.UserTableDef.USER;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserMapper userMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    public UserController(UserMapper userMapper, RedisTemplate<String, Object> redisTemplate) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 登录接口
     * @param user 用户数据
     * @return token信息
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody User user) {
        // 判断登录数据是否有效
        String username = user.getUsername();
        String password = user.getPassword();
        if (StringUtils.isNullOrEmpty(username) || StringUtils.isNullOrEmpty(password)) {
            return Result.error("账号或密码不能为空");
        }
        // MD5 加密
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        // 构建查询器
        User userOne = userMapper.selectOneByQuery(QueryWrapper.create()
                .from(USER)
                .where(USER.USERNAME.eq(username).and(USER.PASSWORD.eq(encryptedPassword))));
        // 判断用户是否为空
        if (userOne == null) {
            // 账号密码不正确
            return Result.error("账号或密码错误");
        }
        log.info("登录成功");
        // 用jwt生成一个小token
        String token = TokenUtils.createToken(userOne);
        // 将 token 保存在 redis 缓存中
        redisTemplate.opsForValue().set("loginUser:" + userOne.getId(), token, 1, TimeUnit.DAYS);
        //3 如登录成功则 生成小token
        return Result.success("登录成功", token);
    }


    /**
     * 添加用户
     *
     * @param user 用户信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<String> register(@Validated(ValidGroup.Crud.Update.class) @RequestBody User user, HttpServletRequest request, BindingResult bindingResult) {
        // 校验请求数据
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            return Result.error(errors.get(0).getDefaultMessage());  // 返回第一个错误消息
        }
        // 看是否为管理员用户 only管理员才能添加
        String token = (String) request.getAttribute("token");
        Claims parse = TokenUtils.parse(token);
        assert parse != null;
        Integer role = parse.get("role", Integer.class);
        if (role == 0) {
            return Result.error("你配吗？");
        }
        QueryWrapper queryWrapper = QueryWrapper.create()
                .from(USER).where(USER.USERNAME.eq("username"));
        User userOne = userMapper.selectOneByQuery(queryWrapper);
        if (userOne == null) {
            // MD5 加密
            String encryptedPassword = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
            user.setPassword(encryptedPassword);
            userMapper.insert(user);
            return Result.success("添加用户成功");
        }
        return Result.error("该用户已存在");
    }

    /**
     * 用户退出登录
     *
     * @param request 请求
     * @return 响应数据
     */
    @RequestMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        // 获取请求头中的token
        String token = (String) request.getAttribute("token");

        if (!StringUtils.isNullOrEmpty(token)) {
            Claims claims = TokenUtils.parse(token);
            assert claims != null;
            Integer userId = (Integer) claims.get("userid");
            // 判断用户是否登录
            // 从 redis 里获取token，进行比对
            String realToken = (String) redisTemplate.opsForValue().get("loginUser:" + userId);
            if (token.equals(realToken) && Boolean.TRUE.equals(redisTemplate.delete("loginUser:" + userId))) {
                // 用户是登录状态
                // 删除成功
                return Result.success("用户退出登录");
            } else {
                return Result.Exception();
            }
        } else {
            // 用户未登录，返回未登录的信息
            return Result.NoLogin();
        }
    }

    @PostMapping("/pwd/edit")
    public Result<String> changePassword(@RequestBody PwdEditVO vo, HttpServletRequest request) {
        // 判断
        String realEmailCode = (String) redisTemplate.opsForValue().get("emailCode:" + vo.getEmail());
        String token = (String) request.getAttribute("token");
        Claims claims = TokenUtils.parse(token);
        assert claims != null;
        String username = (String) claims.get("username");
        if (vo.getEmailCode() != null && realEmailCode != null && realEmailCode.equalsIgnoreCase(vo.getEmailCode())) {
            // 修改
            UpdateChain.of(User.class)
                    .from(USER)
                    .set(USER.PASSWORD, vo.getPassword())
                    .where(USER.USERNAME.eq(username))
                    .update();
        } else {
            return Result.error("验证码错误");
        }
        return Result.success("修改成功");
    }

    /**
     * 删除用户
     *
     * @param ids 用户的id
     * @return 操作结果
     */
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody ArrayList<Integer> ids) {
        try {
            userMapper.deleteBatchByIds(ids);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.Exception();
        }
    }

    /**
     * 获取用户信息
     *
     * @param request 获取token
     * @return 返回用户信息
     */
    @GetMapping("/info")
    public Result<Object> getInfo(HttpServletRequest request) {
        String token = (String) request.getAttribute("token");
        Claims claims = TokenUtils.parse(token);
        assert claims != null;
        Integer userId = (Integer) claims.get("userid");
        User user = userMapper.selectOneById(userId);
        if (user == null) {
            return Result.NoLogin();
        }
        return Result.success("用户信息获取成功", user);
    }

    /**
     * 更新用户数据
     *
     * @param user 用户数据
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result<String> update(@RequestBody User user) {
        if (user == null) {
            return Result.error("更新数据不得为空");
        }
        if (!StringUtils.isNullOrEmpty(user.getPassword())) {
            String encryptedPassword = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
            user.setPassword(encryptedPassword);
        }
        userMapper.update(user);
        return Result.success("更新成功");
    }

    /**
     * 查询用户列表
     * @param pageRequest 页码 每页条数 升降排序
     * @return 用户信息
     */
    @GetMapping("/page")
    public Result<Page<User>> paginate(PageRequest pageRequest) {
        Page<User> userPage = userMapper.paginate(pageRequest.getPageNumber()
                , pageRequest.getPageSize(), QueryWrapper.create().from(USER));
        return Result.success("查询成功", userPage);
    }

    @GetMapping("/list")
    public Result<Object> list() {
        List<User> userList = userMapper.selectAll();
        return Result.success("查询成功",userList);
    }
}





