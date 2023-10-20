package com.wenxin.sm.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class InitConfiguration implements ApplicationRunner {
    @Value("${image-path}")
    private String imagePath;

    @Override
    public void run(ApplicationArguments args) {
        File folder = new File(imagePath);

        // 判断文件夹是否存在
        if (folder.exists()) {
            log.info("文件夹已经存在，无需创建。");
        } else {
            // 创建文件夹
            boolean result = folder.mkdirs();
            if (result) {
                log.info("文件夹创建成功！");
            } else {
                log.error("文件夹创建失败！");
            }
        }
    }
}