package common;

import net.sf.json.JSONObject;
import org.apache.commons.codec.Charsets;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyImage {

    /**
     * 图片识别
     */
    public static JSONObject imageShow(byte[] images, int type) throws Exception {
        Map<String, String> param = new HashMap<>();
        param.put("image", new String(Base64.encode(images)));
        param.put("image_type", "BASE64");

        JSONObject jsonResult = null;
        if (type == 0) {
            //通用文字识别（高精度版）
            jsonResult = JSONObject.fromObject(HttpClientUtil.postKV(
                    "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic?access_token=" + AccessToken.getToken(),
                    param, Charsets.UTF_8, new BasicHeader("Content-Type", "application/x-www-form-urlencoded")));
        } else if (type == 1) {
            //车牌识别
            param.put("multi_detect", "true");
            jsonResult = JSONObject.fromObject(HttpClientUtil.postKV(
                    "https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate?access_token=" + AccessToken.getToken(),
                    param, Charsets.UTF_8, new BasicHeader("Content-Type", "application/x-www-form-urlencoded")));
        } else if (type == 2) {
            // 发票识别
            jsonResult = JSONObject.fromObject(HttpClientUtil.postKV(
                    "https://aip.baidubce.com/rest/2.0/ocr/v1/vat_invoice?access_token=" + AccessToken.getToken(),
                    param, Charsets.UTF_8, new BasicHeader("Content-Type", "application/x-www-form-urlencoded")));
        } else {
            // 身份证正面识别
            param.put("id_card_side", "front");
            jsonResult = JSONObject.fromObject(HttpClientUtil.postKV(
                    "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard?access_token=" + AccessToken.getToken(),
                    param, Charsets.UTF_8, new BasicHeader("Content-Type", "application/x-www-form-urlencoded")));
        }

        return jsonResult;
    }


    /**
     * 获取百度token类
     */
    private static class AccessToken {
        private static String access_token = "";
        private static Date expires = null;
        private static final String client_id = "pu1rCcV3irjIQO54gVssGT6v";
        private static final String client_secret = "yvzNeUTl07CbpYCw1SScNkfG0VESkEsm";

        public static synchronized String getToken() throws Exception {
            if (expires == null || expires.before(new Date())) {
                Map<String, String> param = new HashMap<>();
                param.put("grant_type", "client_credentials");
                param.put("client_id", client_id);
                param.put("client_secret", client_secret);
                JSONObject resultJson = JSONObject.fromObject(
                        HttpClientUtil.postKV("https://aip.baidubce.com/oauth/2.0/token", param, Charsets.UTF_8));
                if (resultJson.containsKey("error")) {
                    throw new RuntimeException(
                            resultJson.getString("error") + ":" + resultJson.getString("error_description"));
                } else {
                    access_token = resultJson.getString("access_token");
                    // 设置20天过期时间
                    expires = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(20));
                }
            }
            return access_token;
        }
    }

    /**
     * 传入图片地址，返回JSON
     */
    public static JSONObject testImage(String path, int type) {
        File file = new File(path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len = -1;
            while ((len = fis.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            byte[] fileByte = bos.toByteArray();

            // 将下面一行，表示进行图片文字识别
            JSONObject jsonObject = imageShow(fileByte, type);

            System.out.println("图片识别返回结果：" + jsonObject);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
