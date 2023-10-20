package com.wenxin.sm.controller;


import com.google.code.kaptcha.Producer;
import com.wenxin.sm.entity.Result;
import com.wenxin.sm.valid.SendEmailVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class CommonController {
    private final JavaMailSender javaMailSender;

    private final Producer kaptchaProducer;

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${image-path}")
    private String imagePath;


    public CommonController(JavaMailSender javaMailSender, Producer kaptchaProducer, RedisTemplate<String, Object> redisTemplate) {
        this.javaMailSender = javaMailSender;
        this.kaptchaProducer = kaptchaProducer;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 上传图片
     * @param file 图片
     * @return 响应数据
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {

        try {
            // 创建目标文件
            String fileName = UUID.randomUUID().toString();
            File uploadedFile = new File(imagePath, fileName + ".png");
            // 使用Commons IO保存文件
            FileUtils.writeByteArrayToFile(uploadedFile, file.getBytes());
            return Result.success("图片上传成功！", fileName);
        } catch (Exception e) {
            return Result.error("图片上传失败！");
        }
    }

    @PostMapping("/sendEmail")
    public Result<String> sendEmail(@Validated @RequestBody SendEmailVO vo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            return Result.error(errors.get(0).getDefaultMessage());
        }
        String realCode = (String) redisTemplate.opsForValue().get("kaptcha:" + vo.getTimestamp());
        assert realCode != null;
        if (realCode.equalsIgnoreCase(vo.getPicCode())) {
            String text = kaptchaProducer.createText();
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom("wenxin_web@163.com");
            simpleMailMessage.setTo(vo.getEmail());
            simpleMailMessage.setSubject("修改密码");
            simpleMailMessage.setText("【问心平台】: 您正在修改密码，验证码为：" + text + "，请在5分钟内使用！");
            CompletableFuture.runAsync(() -> javaMailSender.send(simpleMailMessage));
            redisTemplate.opsForValue().set("emailCode:" + vo.getEmail(), text, 5, TimeUnit.MINUTES);
            return Result.success("发送成功");
        }
        return Result.error("图片验证码错误");

    }

    @GetMapping("/image/{uuid}")
    public void getImage(@PathVariable String uuid, HttpServletResponse response) throws IOException {
        try {
            File file = new File(imagePath + uuid + ".png");
            if (file.exists()) {
                response.setContentType(MediaType.IMAGE_PNG_VALUE);
                FileUtils.copyFile(file, response.getOutputStream());
            } else {
                // 图片文件不存在，返回错误提示
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Image not found");
            }
        } catch (Exception e) {
            // 发生异常，返回错误提示
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    @GetMapping("/kaptcha")
    public void getKaptcha(@RequestParam String timestamp, HttpServletResponse response) throws IOException {
        try {
            // 生成验证码
            String code = kaptchaProducer.createText();
            // 将验证码存入redis
            redisTemplate.opsForValue().set("kaptcha:" + timestamp, code, 5, TimeUnit.MINUTES);
            // 生成验证码图片
            BufferedImage image = kaptchaProducer.createImage(code);
            // 将图片写入浏览器
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            ImageIO.write(image, "png", response.getOutputStream());

        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }
}
