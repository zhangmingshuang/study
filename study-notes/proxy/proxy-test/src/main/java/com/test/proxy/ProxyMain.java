package com.test.proxy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangmingshuang
 * @since 2019/7/9
 */
public class ProxyMain {

    public static void main(String[] args) {
        validate();

        Energizer energizer = new EnergizerImpl();
        Energizer proxy = JDKProxy.create(new EnergizerImpl());
        Energizer cgProxy = CGLibProxy.create(new EnergizerImpl());

        warmup(energizer, proxy, cgProxy);

        String version = System.getProperty("java.version");

        long[] t = test(energizer, proxy, cgProxy, 1000);
        String s1 = "|原生|" + t[0] + "|";
        String s2 = "|JDK|" + t[1] + "|";
        String s3 = "|CGLib|" + t[2] + "|";
        t = test(energizer, proxy, cgProxy, 1_0000);
        s1 += t[0] + "|";
        s2 += t[1] + "|";
        s3 += t[2] + "|";
        t = test(energizer, proxy, cgProxy, 10_0000);
        s1 += t[0] + "|";
        s2 += t[1] + "|";
        s3 += t[2] + "|";
        t = test(energizer, proxy, cgProxy, 100_0000);
        s1 += t[0] + "|";
        s2 += t[1] + "|";
        s3 += t[2] + "|";
        t = test(energizer, proxy, cgProxy, 1000_0000);
        s1 += t[0] + "|";
        s2 += t[1] + "|";
        s3 += t[2] + "|";
        t = test(energizer, proxy, cgProxy, 10000_0000);
        s1 += t[0] + "|";
        s2 += t[1] + "|";
        s3 += t[2] + "|";
        t = test(energizer, proxy, cgProxy, 50000_0000);
        s1 += t[0] + "|";
        s2 += t[1] + "|";
        s3 += t[2] + "|";

        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s3);
    }

    private static long[] test(Energizer energizer,
                               Energizer proxy,
                               Energizer cgProxy,
                               int count) {
        long t1 = run(energizer, count);
        long t2 = run(proxy, count);
        long t3 = run(cgProxy, count);
        return new long[]{t1, t2, t3};
    }

    private static long run(Energizer energizer, int count) {
        long s = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            energizer.incr();
        }
        return System.currentTimeMillis() - s;
    }

    private static void warmup(Energizer... energizers) {
        for (Energizer energizer : energizers) {
            for (int i = 0; i < 1000; i++) {
                energizer.incr();
            }
        }
    }

    private static void validate() {
        Energizer energizer = new EnergizerImpl();
        System.out.println(energizer.incr());

        Energizer proxy = JDKProxy.create(new EnergizerImpl());
        System.out.println(proxy.incr());

        Energizer cgProxy = CGLibProxy.create(new EnergizerImpl());
        System.out.println(cgProxy.incr());
    }
}
