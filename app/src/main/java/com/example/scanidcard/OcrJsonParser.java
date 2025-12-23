package com.example.scanidcard;

import org.json.JSONObject;

public class OcrJsonParser {

    public static OcrResult parseIdCard(String json) throws Exception {

        JSONObject root = new JSONObject(json);
        JSONObject response = root.getJSONObject("Response");

        // ===== 1️⃣ 先判断是否有错误 =====
        if (response.has("Error")) {
            JSONObject error = response.getJSONObject("Error");
            String code = error.optString("Code");
            String message = error.optString("Message");
            throw new Exception("OCR识别失败\n" + code + "\n" + message);
        }

        // ===== 2️⃣ 正常解析 =====
        OcrResult result = new OcrResult();

        result.name = response.optString("Name");
        result.sex = response.optString("Sex");
        result.nation = response.optString("Nation");
        result.birth = response.optString("Birth");
        result.address = response.optString("Address");
        result.idNum = response.optString("IdNum");
        result.authority = response.optString("Authority");
        result.validDate = response.optString("ValidDate");

        return result;
    }
}
