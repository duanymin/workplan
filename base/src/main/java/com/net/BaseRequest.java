package com.net;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by Duan on 5月20日.
 */
public class BaseRequest {
    /**
     * 得到请求适配器
     *
     * @return
     */
    protected static RestAdapter getAdapter(String ctx, RequestInterceptor interceptor) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setEndpoint(ctx).setRequestInterceptor(interceptor).setConverter(new BaseConverter())
                .build();
        return restAdapter;
    }
}
