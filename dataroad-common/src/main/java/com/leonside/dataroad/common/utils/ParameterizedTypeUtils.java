package com.leonside.dataroad.common.utils;

import com.leonside.dataroad.common.exception.JobException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author leon
 */
@Slf4j
public class ParameterizedTypeUtils {

    /**
     *
     * @param clazz clazz
     * @param assignInterface  指定的接口
     * @param index ParameterizedType index
     * @return
     */
    public static Class getClassGenericType(final Class clazz, Class assignInterface, int index){
        if(!assignInterface.isAssignableFrom(clazz) ){
            throw new JobException(clazz.getSimpleName() + " is not the interface of " + assignInterface.getSimpleName());
        }

        Optional<Type> interfaceType = Arrays.stream(clazz.getGenericInterfaces())
                .filter(type -> type instanceof ParameterizedType && ((ParameterizedType) type).getRawType().getTypeName().equals(assignInterface.getName()))
                .findFirst();


        if(!interfaceType.isPresent()){
            interfaceType = ClassUtils.getAllSuperclasses(clazz)
                    .stream()
                    .flatMap(superClazz -> Arrays.stream(((Class) superClazz).getGenericInterfaces()))
                    .filter(type -> type instanceof ParameterizedType && ((ParameterizedType) type).getRawType().getTypeName().equals(assignInterface.getName()))
                    .findFirst();
        }


        if(interfaceType.isPresent()){
            return (Class) ((ParameterizedType) interfaceType.get()).getActualTypeArguments()[index];
        }else{
            return Object.class;
        }
    }

    public static Class getClassGenericType(final Class clazz, final int index) {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            log.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if ((index >= params.length) || (index < 0)) {
            log.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
                    + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            log.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        return (Class) params[index];
    }

    public static Class<?> getClass(Type type) {
        if (null != type) {
            if (type instanceof Class) {
                return (Class)type;
            }

            if (type instanceof ParameterizedType) {
                return (Class)((ParameterizedType)type).getRawType();
            }
        }

        return null;
    }

    public static Type getTypeArgument(Type type, int index) {
        Type[] typeArguments = getTypeArguments(type);
        return null != typeArguments && typeArguments.length > index ? typeArguments[index] : null;
    }

    public static Type[] getTypeArguments(Type type) {
        if (null == type) {
            return null;
        } else {
            Type[] types = null;
            if (type instanceof ParameterizedType) {
                ParameterizedType genericSuperclass = (ParameterizedType)type;
                types = genericSuperclass.getActualTypeArguments();
            } else if (type instanceof Class) {
                types = getTypeArguments(((Class)type).getGenericSuperclass());
            }

            return types;
        }
    }

}
