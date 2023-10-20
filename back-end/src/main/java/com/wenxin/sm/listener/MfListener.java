package com.wenxin.sm.listener;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
import com.wenxin.sm.entity.BaseEntity;
import java.time.LocalDateTime;

public class MfListener implements InsertListener, UpdateListener {
    @Override
    public void onInsert(Object entity) {
        if (entity instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) entity;
            baseEntity.setCreateTime(LocalDateTime.now());
        }
        System.out.println("onInsert...");
    }
    @Override
    public void onUpdate(Object entity) {
        if (entity instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) entity;
            baseEntity.setUpdateTime(LocalDateTime.now());
        }
        System.out.println("onUpdate...");
    }
}
