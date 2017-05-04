package com.hailan.HaiLanPrint.activity.payment.wechat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WechatHelper {

    private static final String TAG = "WechatHelper";
    private final IWXAPI msgApi;
    private Activity mActivity;
    private WechatModel wechatModel;
    private PayReq req;
    private StringBuffer sb;
    private Map<String, String> resultunifiedorder;

    public WechatHelper(Activity activity, WechatModel wechatModel) {
        this.mActivity = activity;
        this.wechatModel = wechatModel;
        req = new PayReq();
        sb = new StringBuffer();
        msgApi = WXAPIFactory.createWXAPI(activity, null);
    }

    /**
     * 判断是否已安装微信与API是否支持
     *
     * @return
     */
    public boolean isWXAppInstalledAndSupported() {
        msgApi.registerApp(WechatPayUtil.APP_ID);
        return msgApi.isWXAppInstalled() && msgApi.isWXAppSupportAPI();
    }

    /**
     * 生成签名
     */
    private String genPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(WechatPayUtil.API_KEY);

        String packageSign = MD5.getMessageDigest(sb.toString()).toUpperCase();
        Log.e("orion", packageSign);
        return packageSign;
    }

    private String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).getName() + ">");

            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">");
        }
        sb.append("</xml>");

        Log.e("orion", sb.toString());
        try {
            return new String(sb.toString().getBytes(), "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> decodeXml(String content) {
        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if ("xml".equals(nodeName) == false) {
                            //实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion", e.toString());
        }
        return null;
    }

    private String genOutTradNo() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)));
    }

    private String genProductArgs() {
        StringBuffer xml = new StringBuffer();

        try {
            String nonceStr = genNonceStr();

            xml.append("</xml>");
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("appid", WechatPayUtil.APP_ID));
            packageParams.add(new BasicNameValuePair("body", wechatModel.getBody()));
            packageParams.add(new BasicNameValuePair("mch_id", WechatPayUtil.MCH_ID));
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
            packageParams.add(new BasicNameValuePair("notify_url", wechatModel.getNotifyUrl()));
            packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
            packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));
            packageParams.add(new BasicNameValuePair("total_fee", wechatModel.getTotalFee()));
            packageParams.add(new BasicNameValuePair("trade_type", "APP"));


            String sign = genPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));


            String xmlstring = toXml(packageParams);

            return xmlstring;

        } catch (Exception e) {
            Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }
    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)));
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(WechatPayUtil.API_KEY);

        this.sb.append("sign str\n" + sb.toString() + "\n\n");
        String appSign = MD5.getMessageDigest(sb.toString()).toUpperCase();
        Log.e("orion", appSign);
        return appSign;
    }

    private void genPayReq() {

        if (wechatModel.getPayType() == WechatModel.PAY_TYPE_1) {
            req.prepayId = resultunifiedorder.get("prepay_id");
        } else {
            req.prepayId = wechatModel.getPrepayId();
        }

        req.appId = WechatPayUtil.APP_ID;
        req.partnerId = WechatPayUtil.MCH_ID;
        req.packageValue = "Sign=WXPay";
//        req.nonceStr = genNonceStr();
        req.nonceStr = wechatModel.getNonceStr();
//        req.timeStamp = String.valueOf(genTimeStamp());
        req.timeStamp = wechatModel.getTimeStamp();

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

//        req.sign = genAppSign(signParams);
        req.sign = wechatModel.getAppSign();

        sb.append("sign\n" + req.sign + "\n\n");

        Log.e("orion", signParams.toString());
    }

    private void sendPayReq() {
        msgApi.registerApp(WechatPayUtil.APP_ID);
        msgApi.sendReq(req);
    }

    // 服务端获取partnerid的方式调用支付
    public void pay() {
        genPayReq();
        sendPayReq();
    }

    // 客户端获取partnerid的方式调用支付
    public void pays() {
        GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
        getPrepayId.execute();
    }

    private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(mActivity, "提示", "正在获取预支付订单...");
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            if (dialog != null) {
                dialog.dismiss();
            }
            sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");

            resultunifiedorder = result;

            // 获取支付请求和调起支付请求
            genPayReq();
            sendPayReq();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Map<String, String> doInBackground(Void... params) {

            String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
            String entity = genProductArgs();

            Log.e("orion", entity);

            byte[] buf = Util.httpPost(url, entity);

            String content = new String(buf);
            Log.e("orion", content);
            Map<String, String> xml = decodeXml(content);

            return xml;
        }
    }

}
