package com.test.proxy;

/**
 * @author zhangmingshuang
 * @since 2019/7/9
 */
public class ProxyNewMain {

    public static void main(String[] args) {
        validate();

        warmup();

        test(1000);
        test(1_0000);
        test(10_0000);
        test(100_0000);
        test(1000_0000);
        test(10000_0000);
        test(50000_0000);

    }

    private static void test(int count) {
        long t1 = run(count);
        long t2 = runJdkProxy(count);
        long t3 = runCglibProxy(count);
        System.out.println("-------------------------------------");
        String version = System.getProperty("java.version");
        System.out.println("JDK:" + version);
        System.out.println("原生执行" + count + "次，共计用时：" + t1);
        System.out.println("JDKProxy执行" + count + "次，共计用时：" + t2);
        System.out.println("CGLibProxy执行" + count + "次，共计用时：" + t3);
    }

    private static long runCglibProxy(int count) {
        long s = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            ((Energizer) CGLibProxy.create(new EnergizerImpl())).incr();
        }
        return System.currentTimeMillis() - s;
    }

    private static long runJdkProxy(int count) {
        long s = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            ((Energizer) JDKProxy.create(new EnergizerImpl())).incr();
        }
        return System.currentTimeMillis() - s;
    }

    private static long run(int count) {
        long s = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            new EnergizerImpl().incr();
        }
        return System.currentTimeMillis() - s;
    }

    private static void warmup() {
        for (int i = 0; i < 1000; i++) {
            JDKProxy.create(new EnergizerImpl());
            CGLibProxy.create(new EnergizerImpl());
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
