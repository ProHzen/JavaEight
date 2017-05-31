package com.example;

/**
 * Desc:
 * Author: YangShangZhen
 * Time:   2017/5/31 16:43
 * Email:  369013520@qq.com
 */

public interface PersonFactory<P extends JavaEight.Person> {
    P create(String firstName, String lastName);
}
