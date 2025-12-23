package com.example.scanidcard;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TencentSigner {

    private static final String ALGORITHM = "TC3-HMAC-SHA256";

    public static String sign(
            String secretId,
            String secretKey,
            String service,
            String host,
            String action,
            String payload,
            long timestamp
    ) throws Exception {

        // ===== 1. 日期（UTC）=====
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = sdf.format(new Date(timestamp * 1000));

        // ===== 2. CanonicalRequest =====
        String canonicalRequest =
                "POST\n" +
                        "/\n" +
                        "\n" +
                        "content-type:application/json; charset=utf-8\n" +
                        "host:" + host + "\n" +
                        "x-tc-action:" + action.toLowerCase() + "\n" +
                        "\n" +
                        "content-type;host;x-tc-action\n" +
                        sha256Hex(payload);

        // ===== 3. StringToSign =====
        String credentialScope = date + "/" + service + "/tc3_request";
        String stringToSign =
                ALGORITHM + "\n" +
                        timestamp + "\n" +
                        credentialScope + "\n" +
                        sha256Hex(canonicalRequest);

        // ===== 4. 计算签名 =====
        byte[] secretDate = hmacSha256(("TC3" + secretKey).getBytes(), date);
        byte[] secretService = hmacSha256(secretDate, service);
        byte[] secretSigning = hmacSha256(secretService, "tc3_request");

        String signature = bytesToHex(
                hmacSha256(secretSigning, stringToSign)
        );

        // ===== 5. Authorization =====
        return ALGORITHM +
                " Credential=" + secretId + "/" + credentialScope +
                ", SignedHeaders=content-type;host;x-tc-action" +
                ", Signature=" + signature;
    }

    private static byte[] hmacSha256(byte[] key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private static String sha256Hex(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
