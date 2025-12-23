package com.example.scanidcard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_SELECT_IMAGE = 1001;

    private ImageView imgIdCard;
    private TextView tvInfo;

    // 后面要用的 Base64
    private String imageBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgIdCard = findViewById(R.id.img_idcard);
        tvInfo = findViewById(R.id.tv_info);
        Button btnSelect = findViewById(R.id.btn_select);
        Button btnRecognize = findViewById(R.id.btn_recognize);

        // 选择图片
        btnSelect.setOnClickListener(v -> openGallery());

        // 识别按钮
        btnRecognize.setOnClickListener(v -> {
            if (imageBase64 == null) {
                tvInfo.setText("请先选择身份证图片");
                return;
            }

            try {
                OcrApi.requestIdCardOcr(
                        imageBase64,
                        "FRONT",   // 身份证正面
                        new okhttp3.Callback() {
                            @Override
                            public void onFailure(okhttp3.Call call, IOException e) {
                                runOnUiThread(() ->
                                        tvInfo.setText("请求失败：" + e.getMessage())
                                );
                            }

                            @Override
                            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                                    throws IOException {

                                String json = response.body().string();

                                runOnUiThread(() -> {
                                    try {
                                        OcrResult ocrResult = OcrJsonParser.parseIdCard(json);
                                        tvInfo.setText(ocrResult.toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        tvInfo.setText("解析失败\n" + json);
                                    }
                                });
                            }

                        }
                );
            } catch (Exception e) {
                e.printStackTrace();

                StringBuilder sb = new StringBuilder();
                sb.append("异常类型：").append(e.getClass().getName()).append("\n");
                sb.append("异常信息：").append(e.getMessage()).append("\n");

                for (StackTraceElement el : e.getStackTrace()) {
                    sb.append(el.toString()).append("\n");
                }

                tvInfo.setText(sb.toString());
            }

        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQ_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            handleImage(imageUri);
        }
    }

    private void handleImage(Uri uri) {
        try {
            // 1. Uri → Bitmap
            Bitmap bitmap = ImageUtils.decodeBitmapFromUri(this, uri);

            // 2. 显示
            imgIdCard.setImageBitmap(bitmap);

            // 3. 压缩 + Base64
            imageBase64 = ImageUtils.bitmapToBase64(bitmap);

            tvInfo.setText("图片处理完成\nBase64长度：" + imageBase64.length());

        } catch (Exception e) {
            e.printStackTrace();
            tvInfo.setText("图片处理失败");
        }
    }
}
