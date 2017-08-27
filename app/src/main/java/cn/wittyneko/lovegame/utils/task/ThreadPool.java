package cn.wittyneko.lovegame.utils.task;

import android.os.Build;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池
 * Created by wittytutu on 17-4-19.
 */

public class ThreadPool {
    private static volatile ThreadPoolExecutor fixedPool;
    private static volatile ThreadPoolExecutor cachedPool;
    private static volatile ThreadPoolExecutor scheduledPool;
    private static volatile ThreadPoolExecutor singlePool;

    public static ThreadPoolExecutor fixedThread() {
        if (fixedPool == null) {
            synchronized (ThreadPool.class) {
                if (fixedPool == null) {
                    int core = getNumberOfCPUCores();
                    core = core > 5 ? core / 2 : core;
                    fixedPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(core);
                }
            }
        }
        return fixedPool;
    }

    public static ThreadPoolExecutor cachedThread() {
        if (cachedPool == null) {
            synchronized (ThreadPool.class) {
                if (cachedPool == null) {
                    cachedPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
                }
            }
        }
        return cachedPool;
    }

    public static ThreadPoolExecutor scheduledThread() {
        if (scheduledPool == null) {
            synchronized (ThreadPool.class) {
                if (scheduledPool == null) {
                    int core = getNumberOfCPUCores();
                    core = core > 5 ? core / 2 : core;
                    scheduledPool = (ThreadPoolExecutor) Executors.newScheduledThreadPool(core);
                }
            }
        }
        return scheduledPool;
    }

    public static ThreadPoolExecutor singleThread() {
        if (singlePool == null) {
            synchronized (ThreadPool.class) {
                if (singlePool == null) {
                    singlePool = (ThreadPoolExecutor) Executors.newSingleThreadExecutor();
                }
            }
        }
        return singlePool;
    }

    /**
     * 获取CPU核心数
     *
     * @return
     */
    public static int getNumberOfCPUCores() {
        int defNumber = 2;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            // Gingerbread doesn't support giving a single application access to both cores, but a
            // handful of devices (Atrix 4G and Droid X2 for example) were released with a dual-core
            // chipset and Gingerbread; that can let an app in the background run without impacting
            // the foreground application. But for our purposes, it makes them single core.
            return 2;
        }
        int cores = defNumber;
        try {
            cores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
            cores = cores < defNumber ? defNumber : cores;
        } catch (Exception e) {
        }
        return cores;
    }

    private static final FileFilter CPU_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getName();
            //regex is slow, so checking char by char.
            if (path.startsWith("cpu")) {
                for (int i = 3; i < path.length(); i++) {
                    if (path.charAt(i) < '0' || path.charAt(i) > '9') {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    };

}
