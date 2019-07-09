package com.test.proxy;

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

        test(energizer, proxy, cgProxy, 1000);
        test(energizer, proxy, cgProxy, 1_0000);
        test(energizer, proxy, cgProxy, 10_0000);
        test(energizer, proxy, cgProxy, 100_0000);
        test(energizer, proxy, cgProxy, 1000_0000);
        test(energizer, proxy, cgProxy, 10000_0000);
        test(energizer, proxy, cgProxy, 50000_0000);

    }

    private static void test(Energizer energizer, Energizer proxy, Energizer cgProxy, int count) {
        long t1 = run(energizer, count);
        long t2 = run(proxy, count);
        long t3 = run(cgProxy, count);
        System.out.println("-------------------------------------");
        String version = System.getProperty("java.version");
        System.out.println("JDK:" + version);
        System.out.println("原生执行" + count + "次，共计用时：" + t1);
        System.out.println("JDKProxy执行" + count + "次，共计用时：" + t2);
        System.out.println("CGLibProxy执行" + count + "次，共计用时：" + t3);
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
