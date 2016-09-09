package com.justep.cordova.plugin.unionpay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

public class UnionPay extends CordovaPlugin{
	/**
     * 银联支付控件或者无卡余额查询控件启动时所使用的requestCode，与UPPayPluginEx.jar和UPPluginWidget.jar中的实际值一致
     */
    private static final int UPPAY_PLUGIN_REQUEST_CODE = 10;
	
	
	
	public static final String LOG_TAG = "UnionPay";
	private CallbackContext currentCallbackContext;
	private String unionpayTnUrl;
	// 01 测试模式  00线上模式
	private  String mMode = "01";

	@Override
	public boolean execute(String action, CordovaArgs args,
			CallbackContext callbackContext) throws JSONException {
		// save the current callback context
		currentCallbackContext = callbackContext;
		if (action.equals("pay")) {
			pay(args);
		}
		return true;
	}
	
	private void pay(CordovaArgs args) {
		try {
			final JSONObject orderInfoArgs =  args.getJSONObject(0);
			final String tn = (String)orderInfoArgs.get("tn");
			Runnable payRunnable = new Runnable() {
				@Override
				public void run() {
					if(tn!=null && tn.length() >0){
						Message msg = mHandler.obtainMessage();
						msg.obj = tn;
				        mHandler.sendMessage(msg);
					}else{
						String response = HttpUtils.sendRequest(unionpayTnUrl, orderInfoArgs);
						Message msg = mHandler.obtainMessage();
						msg.obj = response;
				        mHandler.sendMessage(msg);
					}
				}
			};
			cordova.getThreadPool().execute(payRunnable);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		unionpayTnUrl = webView.getPreferences().getString("unionpay_tn_url", "");
		mMode = webView.getPreferences().getString("unionpay_mmode", "01");
	}
	
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Log.e(LOG_TAG, " " + "" + msg.obj);
	        String tn = "";
	        if (msg.obj == null || ((String) msg.obj).length() == 0) {
	            currentCallbackContext.error(-3);
	        } else {
	            tn = (String) msg.obj;
	            /*************************************************
	             * 步骤2：通过银联工具类启动支付插件
	             ************************************************/
	            cordova.setActivityResultCallback(UnionPay.this);
	            UPPayAssistEx.startPayByJAR(cordova.getActivity(), PayActivity.class, null, null,
	                    tn, mMode);
	        }
		}
	};	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(UPPAY_PLUGIN_REQUEST_CODE == requestCode){
			/*************************************************
	         * 步骤3：处理银联手机支付控件返回的支付结果
	         ************************************************/
	        if (data == null) {
	        	currentCallbackContext.error(-3);
	            return;
	        }
	        /*
	         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
	         */
	        String str = data.getExtras().getString("pay_result");
	        currentCallbackContext.success(str);
	        cordova.setActivityResultCallback(null);
		}
    }
}
