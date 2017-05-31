package com.example;

/**
 * Desc:
 * Author: YangShangZhen
 * Time:   2017/5/31 15:39
 * Email:  369013520@qq.com
 */

@FunctionalInterface
public interface Converter<F, T> {
    T convert(F from);
}
