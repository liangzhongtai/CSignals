package com.chinamobile.sig;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;


import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created by liangzhongtai on 2018/5/22.
 */

public class CSignalsUtil {
    private volatile static CSignalsUtil uniqueInstance;
    private TelephonyManager telManager;
    private JSONArray array;
    private Context mContext;
    public CordovaPlugin plugin;
    public CSignals.CSignalsListener listener;

    private CSignalsUtil(Context context, CordovaPlugin plugin) {
        mContext = context;
        this.plugin = plugin;
        getSignals();
    }

    //采用Double CheckLock(DCL)实现单例
    public static CSignalsUtil getInstance(Context context, CordovaPlugin plugin) {
        if (uniqueInstance == null) {
            synchronized (CSignalsUtil.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new CSignalsUtil(context, plugin);
                }
            }
        }
        return uniqueInstance;
    }


    public void getSignals() {
        telManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        array = new JSONArray();
        Log.d(CSignals.TAG,"hasPermission="+checkPermission());
        if (checkPermission()) {
            //获取小区信息
            telManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            List<CellInfo> list = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                list = telManager.getAllCellInfo();

                for (int i = 0, len = list == null ? 0 : list.size(); i < len; i++) {
                    CellInfo info = list.get(i);
                    //获取�?有的Gsm网络
                    if (info instanceof CellInfoLte) {
                        try {
                            //CI
                            CellInfoLte lteInfo = (CellInfoLte) info;
                            CellIdentityLte ciLTE = lteInfo.getCellIdentity();
                            array.put(1, ciLTE.getCi());

                            //PCI
                            array.put(2, ciLTE.getPci());

                            CellSignalStrengthLte cssLTE = lteInfo.getCellSignalStrength();
                            Log.d(CSignals.TAG, "cssLTE=" + cssLTE.toString());
                            //RSRP
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                //RSRP
                                array.put(3, cssLTE.getRsrp());
                                //CQI
                                array.put(5, cssLTE.getCqi());
                                //RSSNR
                                array.put(6, cssLTE.getRssnr());
                                //RSRP
                                array.put(7, cssLTE.getRsrp());
                                //LEVEL
                                array.put(9, cssLTE.getLevel());
                                //ASU_LEVEL
                                array.put(10, cssLTE.getAsuLevel());
                                //TIMING_ADVANCE
                                array.put(11, cssLTE.getTimingAdvance());
                                //DBM
                                array.put(12, cssLTE.getDbm());
                            } else {
                                array.put(3, -1);
                                array.put(5, -1);
                                array.put(6, -1);
                                array.put(7, -1);
                                array.put(9, -1);
                                array.put(10, -1);
                                array.put(11, -1);
                                array.put(12, -1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                if(listener!=null)
                listener.sendFaileMessage("获取LTE信息失败");
            }
        }
    }

    public void start() {
        if (checkPermission())
            getSignals();
    }


    private boolean checkPermission() {
        if(plugin==null){
            return false;
        }else if (Build.VERSION.SDK_INT >= 23) {
            return PermissionHelper.hasPermission(plugin, Manifest.permission.READ_PHONE_STATE) &&
                    PermissionHelper.hasPermission(plugin, Manifest.permission.ACCESS_COARSE_LOCATION);
        } else {
            return true;
        }
    }

    //移除定位监听
    public void removeCSignalsListener() {
        uniqueInstance = null;
        //检查权限,否则编译不过
        if (!checkPermission()) {
            return;
        }
        listener = null;
        plugin = null;
    }

    PhoneStateListener phoneStateListener = new PhoneStateListener() {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            //Log.d(CSignals.TAG,"signalStrength="+signalStrength.toString());
            int gsm_sign = -113 + 2 * signalStrength.getGsmSignalStrength();
            int lte_sinr = 99;
            int lte_rsrp = 99;
            int lte_rsrq = 99;
            int lte_rssnr = 0;
            int lte_cqi = 0;
            try {
                if( telManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE){
                    lte_sinr = (Integer) signalStrength.getClass().getMethod("getLteSignalStrength").invoke(signalStrength);
                    lte_rsrp = (Integer) signalStrength.getClass().getMethod("getLteRsrp").invoke(signalStrength);
                    lte_rsrq = (Integer) signalStrength.getClass().getMethod("getLteRsrq").invoke(signalStrength);
                    lte_rssnr = (Integer) signalStrength.getClass().getMethod("getLteRssnr").invoke(signalStrength);
                    lte_cqi = (Integer) signalStrength.getClass().getMethod("getLteCqi").invoke(signalStrength);
                }else if(telManager.getNetworkType() ==TelephonyManager.NETWORK_TYPE_GSM)
                {
                    lte_sinr= signalStrength.getGsmSignalStrength();
                    lte_rsrp = (Integer) signalStrength.getClass().getMethod("getGsmDbm").invoke(signalStrength);
                }else if(telManager.getNetworkType()== TelephonyManager.NETWORK_TYPE_TD_SCDMA){
                    lte_sinr = (Integer) signalStrength.getClass().getMethod("getTdScdmaLevel").invoke(signalStrength);
                    lte_rsrp = (Integer) signalStrength.getClass().getMethod("getTdScdmaDbm").invoke(signalStrength);
                }
                if (listener != null) {
                    if(checkPermission()) {
                    //LAC
                    CellLocation location = telManager.getCellLocation();
                        if (location != null && location instanceof GsmCellLocation) {
                            GsmCellLocation gsmLocation = (GsmCellLocation) location;
                            try {
                                array.put(0, gsmLocation.getLac());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //RSRP
                        array.put(3,lte_rsrp);
                        //SINR
                        array.put(4,lte_sinr);
                        //CQI
                        array.put(5,lte_cqi);
                        //RSSNR
                        array.put(6,lte_rssnr);
                        //RSRQ
                        array.put(7,lte_rsrq);
                        //ENVO_SNR
                        array.put(8,signalStrength.getEvdoSnr());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            array.put(9,signalStrength.getLevel());
                        }

                        listener.sendSignalsMessage(array);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    };
}
