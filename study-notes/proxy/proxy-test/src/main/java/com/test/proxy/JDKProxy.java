package com.test.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author zhangmingshuang
 * @since 2019/7/9
 */
public class JDKProxy implements InvocationHandler {

    public static <T> T create(Object target) {
        return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(), new JDKProxy(target));
    }

    private Object target;

    private JDKProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object res = method.invoke(target, args);
        return res;
    }
}
