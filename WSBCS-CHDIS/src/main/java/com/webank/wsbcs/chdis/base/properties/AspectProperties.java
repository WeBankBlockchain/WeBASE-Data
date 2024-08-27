/**
 * Copyright 2014-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.webank.wsbcs.chdis.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Aspect properties.
 * 限流配置
 * @author v_sflkchen
 * @date 2022年1月6日
 */
@Data
@Component
@ConfigurationProperties(prefix = AspectProperties.ASPECT_PREFIX)
public class AspectProperties {
    public static final String ASPECT_PREFIX = "aspect";
    // 单用户限流，多久最多一次请求（ms）
    private long singleLimitInterval = 1000;
    // 分布式锁失效时间：超时未自行解锁，则锁失效（ms）
    private long defLockExpireTime = 10 * 1000;
    // 抢锁时间间隔（ms）
    private long requestLockInterval = 200;
    // 单进程最大抢锁线程数，超过则会在取得抢锁线程时阻塞
    private Integer maxLockPoolSize = 500;
}
