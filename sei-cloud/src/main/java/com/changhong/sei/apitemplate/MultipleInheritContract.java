package com.changhong.sei.apitemplate;

import feign.MethodMetadata;
import feign.Util;
import org.springframework.cloud.openfeign.support.SpringMvcContract;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static feign.Util.checkState;

/**
 * <strong>实现功能:</strong>
 * <p>支持多重继承的SpringMvcContract</p>
 *
 * @author 王锦光 wangj
 * @version 1.0.1 2020-01-14 22:59
 */
public class MultipleInheritContract extends SpringMvcContract {

    @Override
    public List<MethodMetadata> parseAndValidatateMetadata(Class<?> targetType) {
        checkState(targetType.getTypeParameters().length == 0, "Parameterized types unsupported: %s",
                targetType.getSimpleName());
//        checkState(targetType.getInterfaces().length <= 1, "Only single inheritance supported: %s",
//                targetType.getSimpleName());
        if (targetType.getInterfaces().length == 1) {
            checkState(targetType.getInterfaces()[0].getInterfaces().length == 0,
                    "Only single-level inheritance supported: %s",
                    targetType.getSimpleName());
        }
        // 判断是否为多接口继承
        List<Class<?>> targetTypes = new ArrayList<>();
        targetTypes.add(targetType);
        if (targetType.getInterfaces().length > 1){
            targetTypes.addAll(Arrays.asList(targetType.getInterfaces()));
        }
        Map<String, MethodMetadata> result = new LinkedHashMap<String, MethodMetadata>();
        for (Class<?> parseTargetType: targetTypes){
            for (Method method : parseTargetType.getMethods()) {
                if (method.getDeclaringClass() == Object.class ||
                        (method.getModifiers() & Modifier.STATIC) != 0 ||
                        Util.isDefault(method)) {
                    continue;
                }
                MethodMetadata metadata = parseAndValidateMetadata(parseTargetType, method);
                checkState(!result.containsKey(metadata.configKey()), "Overrides unsupported: %s",
                        metadata.configKey());
                result.put(metadata.configKey(), metadata);
            }
        }
        return new ArrayList<>(result.values());
    }
}
