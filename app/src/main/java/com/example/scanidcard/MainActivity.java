package com.example.scanidcard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_SELECT_IMAGE = 1001;
    private static final int REQ_TAKE_PHOTO = 1002;
    private Uri photoUri;  // 存储拍照的图片Uri

    private ImageView imgIdCard;
    private TextView tvInfo;
    private Button btnSelect,btnRecognize,btnTakePhoto;

    // 后面要用的 Base64
    private String imageBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgIdCard = findViewById(R.id.img_idcard);
        tvInfo = findViewById(R.id.tv_info);
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnSelect = findViewById(R.id.btn_select);
        btnRecognize = findViewById(R.id.btn_recognize);

        // 拍照
        btnTakePhoto.setOnClickListener(v -> openCamera());

        // 选择图片
        btnSelect.setOnClickListener(v -> openGallery());

        // 识别按钮
        btnRecognize.setOnClickListener(v -> {

            if (imageBase64 == null) {
                tvInfo.setText("请先选择身份证图片");
                return;
            }

            tvInfo.setText("正在识别身份证…");

            recognizeIdCard(imageBase64, "FRONT");  // 默认先识别正面 //测试时可在这手动更改，避免浪费账号识别次数
        });

    }

        //打开摄像头
    private void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQ_TAKE_PHOTO);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQ_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == REQ_SELECT_IMAGE) {
            Uri imageUri = data.getData();
            handleImage(imageUri);
        } else if (requestCode == REQ_TAKE_PHOTO) {
            // 拍照返回直接拿 Bitmap
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            if (bitmap != null) {
                imgIdCard.setImageBitmap(bitmap);
                imageBase64 = ImageUtils.bitmapToBase64(bitmap);
                tvInfo.setText("图片处理完成\nBase64长度：" + imageBase64.length());
            }
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

        //  加入自动识别正反面，默认为"FRONT"，失败后尝试"BACK"
        private void recognizeIdCard(String base64, String side) {
        try {
            OcrApi.requestIdCardOcr(
                    base64,
                    side,
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
                                    // 如果返回 CardSideError，并且当前是 FRONT，则尝试 BACK
                                    if (json.contains("CardSideError") && "FRONT".equals(side)) {
                                        tvInfo.setText("正面识别失败，尝试识别反面...");
                                        recognizeIdCard(base64, "BACK");
                                        return;
                                    }

                                    OcrResult ocrResult = OcrJsonParser.parseIdCard(json);

                                    boolean hasIdNum = ocrResult.idNum != null && !ocrResult.idNum.isEmpty();
                                    boolean hasName = ocrResult.name != null && !ocrResult.name.isEmpty();

                                    // 正面识别结果为空，再尝试识别反面
                                    if (!hasIdNum && !hasName && "FRONT".equals(side)) {
                                        tvInfo.setText("正面信息为空，尝试识别反面...");
                                        recognizeIdCard(base64, "BACK");
                                        return;
                                    }

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

            // 详细异常输出
            StringBuilder sb = new StringBuilder();
            sb.append("异常类型：").append(e.getClass().getName()).append("\n");
            sb.append("异常信息：").append(e.getMessage()).append("\n");
            for (StackTraceElement el : e.getStackTrace()) {
                sb.append(el.toString()).append("\n");
            }
            tvInfo.setText(sb.toString());
        }
    }

}
