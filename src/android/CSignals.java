package com.chinamobile.sig;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;


/**
 * Created by liangzhongtai on 2018/5/21.
 */

public class CSignals extends CordovaPlugin {
    public final static String TAG = "CSignals_Plugin";
    public final static int RESULTCODE_PERMISSION = 100;
    public final static int RESULTCODE_PHONE_PROVIDER = 200;
    public final static int START = 0;
    public final static int CLOSE = 1;


    public CordovaInterface cordova;
    public CordovaWebView webView;
    public boolean first = true;
    public int signalType;
    private CallbackContext callbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.cordova = cordova;
        this.webView = webView;
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        Log.d(TAG,"执行方法CSignals");
        if ("coolMethod".equals(action)) {
            signalType = args.getInt(0);
            //权限
            try {
                if (!PermissionHelper.hasPermission(this, Manifest.permission.READ_PHONE_STATE)
                        || !PermissionHelper.hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    PermissionHelper.requestPermissions(this, RESULTCODE_PERMISSION, new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    });
                } else {
                    startWork();
                }
            }catch (Exception e){
                //权限异常
                callbackContext.error("手机信号信息功能异常");
                manager(false);
                return true;
            }
            return true;
        }
        return super.execute(action, args, callbackContext);
    }

    @Override
    public Bundle onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    public void onRestoreStateForActivityResult(Bundle state, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                callbackContext.error("缺少权限,无法获取手机信号信息");
                return;
            }
        }
        switch (requestCode) {
            case RESULTCODE_PERMISSION:
               startWork();
                break;
            default:
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RESULTCODE_PHONE_PROVIDER) {
            startWork();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CSignalsUtil.getInstance(cordova.getActivity(),this).removeCSignalsListener();
    }

    private void startWork() {
        Log.d(TAG,"开启监听");
        if(signalType == CLOSE){
            CSignalsUtil.getInstance(cordova.getActivity(),this).removeCSignalsListener();
        }else if(signalType == START){
            CSignalsUtil.getInstance(cordova.getActivity(), this).listener = new CSignalsListener() {
                @Override
                public void sendSignalsMessage(JSONArray array) {
                    CSignals.this.sendSignalsMessage(array);
                }

                @Override
                public void sendFaileMessage(String meassage) {
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR,meassage);
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);
                }
            };
            if(!first){
                CSignalsUtil.getInstance(cordova.getActivity(),this).start();
            }
            first = false;
        }
    }


    private void manager(boolean start){
       if(start){
            CSignalsUtil.getInstance(cordova.getActivity(),this).start();
       }
    }

    private int count = 0;

    public void sendSignalsMessage(JSONArray array) {
        Log.d(TAG,"最终array="+array);
        //array数组:
        // 0. LAC 1. CI 2.PCI 3.RSRP 4.SINR 5.CQI 6.RSSNR 7.RSRP 8.ENVO_SNR
        // 9.LEVEL 10.ASU_LEVEL 11.TIMING_ADVANCE 12.DBM
        PluginResult pluginResult;
        if(array==null){
            pluginResult = new PluginResult(PluginResult.Status.ERROR,"无法获取手机信号信息");
        }else{
            pluginResult = new PluginResult(PluginResult.Status.OK,array);
        }
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
    }



    public interface CSignalsListener{
        void sendSignalsMessage(JSONArray array);
        void sendFaileMessage(String meassage);
    }
}
