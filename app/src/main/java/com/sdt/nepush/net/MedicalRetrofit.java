package com.sdt.nepush.net;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.App;
import com.sdt.nepush.util.NetUtils;
import com.sdt.nepush.util.PackageUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * @desc Http请求
 * 添加缓存，30秒内避免重复请求服务器
 * @created kisdy502
 * @createdDate 2019/2/12 16:52
 * @updated
 * @updatedDate 2019/2/12 16:52
 **/

public class MedicalRetrofit {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    private static final int DEFAULT_TIMEOUT = 20;

    private Retrofit retrofit;

    private MedicalService mService;

    public static MedicalRetrofit getInstance() {
        return Holder.retrofit;
    }

    private MedicalRetrofit() {
        final Context context = App.getInstance();
        File cacheDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cacheDir = context.getExternalCacheDir();
        } else {
            cacheDir = context.getCacheDir();
        }

        final String mac = NetUtils.getMacAddress(context);
        final String versionCode = String.valueOf
                (PackageUtils.getVersionCode(context, context.getPackageName()));

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        String url = chain.request().url().toString();

                        //支持添加公共请求头
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("device", Build.MODEL)
                                .addHeader("mac", mac)
                                .addHeader("packageName", context.getPackageName())
                                .addHeader("versionCode", versionCode)
                                .build();
                        logger.d("请求url:" + url);
                        return chain.proceed(newRequest);
                    }
                })
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        okhttp3.Response orginalResponse = chain.proceed(chain.request());
                        printfHeader(chain.request().headers(), false);
                        printfHeader(orginalResponse.headers(), true);
                        return orginalResponse.newBuilder()
                                .removeHeader("Pragma")
                                .removeHeader("Cache-Control")
                                .header("Cache-Control", "public, max-age=" + 15)
                                .build();
                    }
                })
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .cache(new Cache(new File(cacheDir, "okhttpcache"), 10 * 1024 * 1024))
                .build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(ApiConstant.BASE_URL)
                .build();

        mService = retrofit.create(MedicalService.class);
    }

    public MedicalService getMedicalService() {
        return mService;
    }

    private final static class Holder {
        private final static MedicalRetrofit retrofit = new MedicalRetrofit();
    }


    private void printfHeader(Headers headers, boolean isResponse) {
        int length = headers.size();
        if (isResponse) {
            logger.d("---------------------Response Header------------------------");
        } else {
            logger.d("---------------------Request Header-------------------------");
        }

        for (int i = 0; i < length; i++) {
            String headerName = headers.name(i);
            String headerValue = headers.get(headerName);
            logger.i(headerName + " " + headerValue);
        }
    }
}
