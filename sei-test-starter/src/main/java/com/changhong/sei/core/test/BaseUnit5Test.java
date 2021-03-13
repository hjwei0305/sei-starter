package com.changhong.sei.core.test;

import com.changhong.sei.core.config.properties.mock.MockUserProperties;
import com.changhong.sei.core.context.mock.MockUser;
import com.changhong.sei.util.thread.ThreadLocalHolder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * 实现功能： Unit5单元测试基类
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2020-01-09 15:59
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BaseUnit5Test {
    protected static final Logger LOG = LoggerFactory.getLogger(BaseUnit5Test.class);
    @Autowired
    public MockUserProperties properties;
    @Autowired
    public MockUser mockUser;

    @BeforeAll
    @DisplayName("单元测试初始化")
    public static void setup() {
        // 初始化
        ThreadLocalHolder.begin();
        LOG.debug("开始进入单元测试.......");
    }

    @BeforeEach
    @DisplayName("单元测试模拟用户")
    public void mock() {
        LOG.info("当前模拟用户: {}", mockUser.mockUser(properties));
    }


    @AfterAll
    @DisplayName("单元测试完成资源释放")
    public static void cleanup() {
        // 释放
        ThreadLocalHolder.end();
        LOG.info("单元测试资源释放.......");
    }
}
