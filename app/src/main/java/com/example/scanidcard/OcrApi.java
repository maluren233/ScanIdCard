package com.example.scanidcard;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import com.example.scanidcard.BuildConfig;


public class OcrApi {

    // ===== 腾讯云id和密钥 =====
    private static final String SECRET_ID = BuildConfig.TENCENT_SECRET_ID;  //这两处改成自己的id和密钥，作者maluren的信息保密
    private static final String SECRET_KEY = BuildConfig.TENCENT_SECRET_KEY;  //别想用我的腾讯云账号

    private static final String SERVICE = "ocr";
    private static final String HOST = "ocr.tencentcloudapi.com";
    private static final String ACTION = "IDCardOCR";
    private static final String VERSION = "2018-11-19";

    private static final OkHttpClient client = new OkHttpClient();

    public static void requestIdCardOcr(
            String imageBase64,
            String cardSide,
            Callback callback
    ) throws Exception {

        // ===== 1. 构造请求体（⚠ 必须和签名用的完全一致）=====
        String payload = "{"
                + "\"ImageBase64\":\"" + imageBase64 + "\","
                + "\"CardSide\":\"" + cardSide + "\""
                + "}";

        long timestamp = System.currentTimeMillis() / 1000;

        // ===== 2. 生成 v3 签名 =====
        String authorization = TencentSigner.sign(
                SECRET_ID,
                SECRET_KEY,
                SERVICE,
                HOST,
                ACTION,
                payload,
                timestamp
        );

        // ===== 3. 构造 HTTP 请求 =====
        Request request = new Request.Builder()
                .url("https://" + HOST)
                .post(
                        RequestBody.create(
                                MediaType.parse("application/json; charset=utf-8"),
                                payload     // ⚠ 注意：MediaType 在前，payload 在后
                        )
                )
                .addHeader("Authorization", authorization)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Host", HOST)
                .addHeader("X-TC-Action", ACTION)
                .addHeader("X-TC-Version", VERSION)
                .addHeader("X-TC-Timestamp", String.valueOf(timestamp))
                .build();

        client.newCall(request).enqueue(callback);
    }
}
