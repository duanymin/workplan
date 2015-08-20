package com.net;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.squareup.otto.Subscribe;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Duan on 7月6日.
 * 一个下载管理线程，使用队列保存所有的下载请求
 * 每隔1秒检查一次队列中是否存在需要下载的地址
 * 如地址存在，将下载地址从队列中移除，并发送一次广播
 * 广播的Intent中包涵名为url的参数 含义为下载的地址
 * 广播的Intent中包涵名为type的参数 具体意义参考DownloadConstant类
 * 需要额外的创建一个广播接收者，对下载线程中发起的广播进行相关的管理
 * 每次最多同时下载"MAXDOWNLOAD"个地址的内容
 * 最大等待数为"MAXNUM"
 */
public class DownloadThread extends Thread {
    private Context context;
    private Queue<String> queue;
    public static final String DOWNLOADACTION = "base.download";
    public static final int MAXDOWNLOAD = 3, MAXWAIT = 100;
    private int currentDownload, currentWait;

    /**
     * 创建一个队列管理对象的实例，建议放在Application中管理
     */
    public DownloadThread(Context context) {
        this.context = context;
        queue = new LinkedList<>();
        currentDownload = 0;
        currentWait = 0;
    }

    @Override
    public void run() {
        super.run();
        while (isAlive()) {
            String url = getUrl();
            currentDownload++;
            currentWait--;
            //发送开始下载的广播
            sendBroadcast(url, DownloadConstant.START);
        }
    }

    /**
     * 向队列中添加一个下载地址,并发送等待下载的广播
     * 通常是提供给外部调用的方法
     *
     * @param url
     */
    public void addUrl(String url) {
        if (currentWait < MAXWAIT && !queue.contains(url)) {
            currentWait++;
            queue.offer(url);
            //发送等待下载的广播
            sendBroadcast(url, DownloadConstant.WAIT);
        }

    }

    /**
     * 从队列中移除一个下载地址,并发送删除的广播
     * 通常是提供给外部调用的方法
     *
     * @param url
     */
    public void removeUrl(String url) {
        currentWait--;
        queue.remove(url);
        //发送删除下载的广播
        sendBroadcast(url, DownloadConstant.DELETE);
    }

    /**
     * 从队列中得到下载地址
     * 每隔1秒检查一次
     *
     * @return
     */
    private String getUrl() {
        String url;
        //队列为空、得到地址为空、或者当前下载数超过最大下载数时等待一秒再检查
        while (queue.isEmpty() || TextUtils.isEmpty(url = queue.poll()) || currentDownload >= MAXDOWNLOAD) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    /**
     * 发送不同类型的广播的广播
     */
    private void sendBroadcast(String url, int type) {
        Intent intent = new Intent(DOWNLOADACTION);
        intent.putExtra("url", url);
        intent.putExtra("type", type);
        context.sendBroadcast(intent);
    }

    @Subscribe
    public void downloadFinish(String url) {
        currentDownload--;
        sendBroadcast(url, DownloadConstant.FINISH);
    }

}
