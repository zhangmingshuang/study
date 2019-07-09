package com.test.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author zhangmingshuang
 * @since 2019/7/9
 */
public class CGLibProxy implements MethodInterceptor {

    public static <T> T create(Object target) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new CGLibProxy());
        return (T) enhancer.create();
    }

    private CGLibProxy() {

    }

    @Override
    public Object intercept(Object o, Method method,
                            Object[] objects, MethodProxy methodProxy) throws Throwable {
        return methodProxy.invokeSuper(o, objects);
    }
}
