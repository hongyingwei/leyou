package com.leyou.goods.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 全文检索的线程池设置
 */
public class ThreadUtils {
    private static final ExecutorService es = Executors.newFixedThreadPool(10);

    public static void execute(Runnable runnable) {
        es.submit(runnable);
    }
}