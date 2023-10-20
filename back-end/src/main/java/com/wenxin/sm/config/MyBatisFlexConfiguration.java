package com.wenxin.sm.config;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.mask.MaskManager;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import com.wenxin.sm.entity.Goods;
import com.wenxin.sm.entity.User;
import com.wenxin.sm.listener.MfListener;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MyBatisFlexConfiguration implements MyBatisFlexCustomizer {
    @Override
    public void customize(FlexGlobalConfig globalConfig) {
        // 实体类 insert and update 全局处理
        MfListener mfListener = new MfListener();
        //为 User 和 Goods 注册 insertListener
        globalConfig.registerInsertListener(mfListener, User.class, Goods.class);
        //为 User 和 Goods 注册 updateListener
        globalConfig.registerUpdateListener(mfListener, User.class, Goods.class);
        // 自定义脱敏注册
        MaskManager.registerMaskProcessor("EMPTY"
                , data -> null);
    }
}