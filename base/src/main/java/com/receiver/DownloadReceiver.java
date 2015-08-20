package com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.net.DownloadConstant;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;

public class DownloadReceiver extends BroadcastReceiver {

    public DownloadReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String url = intent.getStringExtra("url");
        int type = intent.getIntExtra("type", 0);
        if (type == DownloadConstant.START) {
            String savePath = "";
            FinalHttp finalHttp = new FinalHttp();
            //调用download方法开始下载
            HttpHandler handler = finalHttp.download(url, //这里是下载的路径
                    savePath, true//true:断点续传 false:不断点续传（全新下载）
                    , //这是保存到本地的路径
                    new AjaxCallBack() {
                        @Override
                        public void onLoading(long count, long current) {
                        }

                        @Override
                        public void onSuccess(Object o) {
                            super.onSuccess(o);
                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            super.onFailure(t, errorNo, strMsg);
                        }
                    });
            //调用stop()方法停止下载
            handler.stop();
        }
    }
}
