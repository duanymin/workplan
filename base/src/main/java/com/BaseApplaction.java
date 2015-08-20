package com;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.net.DownloadThread;
import com.squareup.otto.Bus;

/**
 * Created by Duan on 6月29日.
 */
public class BaseApplaction extends Application {
    private static final Bus ACTIVITYBUS = new Bus();
    private static final Bus FRAGMENTBUS = new Bus();
    private static final Bus DOWNLOADBUS = new Bus();
    public static final int ACTIVITY = 1, FRAGMENT = 2, DOWNLOAD = 3;
    private static DownloadThread thread;
    private static BaseApplaction applaction;

    /**
     * 得到Bus的单例，根据type不同，返回不同的单例对象
     *
     * @param type 1 页面相关 2碎片相关 3下载相关
     * @return
     */
    public static Bus getBusInstance(int type) {
        switch (type) {
            case ACTIVITY:
                return ACTIVITYBUS;
            case FRAGMENT:
                return FRAGMENTBUS;
            case DOWNLOAD:
                return DOWNLOADBUS;
            default:
                return ACTIVITYBUS;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化
        Fresco.initialize(this);
        applaction = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (thread != null) {
            DOWNLOADBUS.unregister(thread);
        }
    }

    /***
     * 开启下载线程
     */
    public static void startDLThread() {
        if (thread == null) {
            thread = new DownloadThread(applaction);
            thread.start();
            DOWNLOADBUS.register(thread);
        }
    }
}
