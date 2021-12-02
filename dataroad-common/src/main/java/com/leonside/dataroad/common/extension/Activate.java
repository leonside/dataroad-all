/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.leonside.dataroad.common.extension;

import java.lang.annotation.*;

/**
 * Activate
 * <p />
 * 对于可以被框架中自动激活加载扩展，此Annotation用于配置扩展被自动激活加载条件。
 * 比如，过滤扩展，有多个实现，使用Activate Annotation的扩展可以根据条件被自动加载。
 * <ol>
 * <li>{@link Activate}生效的Group。具体的有哪些Group值由框架SPI给出。
 * </ol>
 *
 * <p />
 * 底层框架SPI提供者通过{@link com.dragonsoft.duceap.commons.extension.ExtensionLoader}的{@link com.dragonsoft.analyzer.spi.ExtensionLoader#getActivateExtension}方法
 * 获得条件的扩展。
 *
 * @author william.liangf
 * @author ding.lid
 * @export
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Activate {
//    /**
//     * Group过滤条件。
//     * <br />
//     * 包含{@link ExtensionLoader#getActivateExtension}的group参数给的值，则返回扩展。
//     * <br />
//     * 如没有Group设置，则不过滤。
//     */
//    String[] group() default {};
//
//    /**
//     * Key过滤条件。包含{@link ExtensionLoader#getActivateExtension}的URL的参数Key中有，则返回扩展。
//     * <p />
//     * 示例：<br/>
//     * 注解的值 <label>@Activate("cache,validatioin")</label>，
//     * 则{@link ExtensionLoader#getActivateExtension}的URL的参数有<label>cache</label>Key，或是<label>validatioin</label>则返回扩展。
//     * <br/>
//     * 如没有设置，则不过滤。
//     */
//    String[] value() default {};

    /**
     * 排序信息，可以不提供。 默认组件名称META-INF下定义,位于before组件之前
     */
    String[] before() default {};

    /**
     * 排序信息，可以不提供。默认组件名称META-INF下定义,位于after组件之后
     */
    String[] after() default {};

    /**
     * 排序信息，可以不提供。从小到大
     */
    int order() default 0;


}