package com.test.proxy;

/**
 * @author zhangmingshuang
 * @since 2019/7/9
 */
public class EnergizerImpl implements Energizer {

    private int i = 0;

    @Override
    public int incr() {
        return ++i;
    }
}
