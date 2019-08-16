package com.sdt.nepush.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.sdt.libcommon.esc.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.UUID;


public class NetUtils {

    private final static String TAG = "NetUtils";

    private static final int SIZE_KB = 1024;

    private final static String NOUSE_MAC = "02:00:00:00:00:00";

    public static String loadFileAsString(String fileName) {
        FileReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new FileReader(fileName);
            char[] buffer = new char[4096];
            int readLength = reader.read(buffer);
            while (readLength >= 0) {
                builder.append(buffer, 0, readLength);
                readLength = reader.read(buffer);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(reader);
        }
        return builder.toString();
    }

    /**
     * 获取mac地址
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getMacAddress(Context context) {
        String ret = null;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            ret = getMacEth();
        }
        if (TextUtils.isEmpty(ret)) {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            ret = wm.getConnectionInfo().getMacAddress();
            if (NOUSE_MAC.equalsIgnoreCase(ret)) {
                ret = getUUID();
            }
        }
        if (TextUtils.isEmpty(ret)) {
            ret = getUUID();
        }
        return ret;
    }

    private static String getMacEth() {
        String macEth = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> net = intf.getInetAddresses(); net
                        .hasMoreElements(); ) {
                    InetAddress iaddr = net.nextElement();
                    if (iaddr instanceof Inet4Address) {
                        if (!iaddr.isLoopbackAddress()) {
                            byte[] data = intf.getHardwareAddress();
                            StringBuilder sb = new StringBuilder();
                            if (data != null && data.length > 1) {
                                sb.append(parseByte(data[0])).append(":")
                                        .append(parseByte(data[1])).append(":")
                                        .append(parseByte(data[2])).append(":")
                                        .append(parseByte(data[3])).append(":")
                                        .append(parseByte(data[4])).append(":")
                                        .append(parseByte(data[5]));
                            }
                            macEth = sb.toString();
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return macEth;
    }


    /**
     * 得到全局唯一UUID
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().replaceAll("\\-", "");
        return uuid;
    }


    public static String getIpAddress(Context context) {
        String ip = null;
        if (isNetworkAvailable(context)) {
            if (isCablePlugin()) {
                ip = getEthernetIpAddress();
            } else {
                ip = getWifiIpAddress(context);
            }
        } else {
            ip = "0.0.0.0";
        }
        return ip;
    }


    public static String getWifiIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null && wifiInfo.getIpAddress() > 0) {
                return intToIp(wifiInfo.getIpAddress());
            }
        }
        return "0.0.0.0";
    }


    public static String getEthernetIpAddress() {
        String localIp = "0.0.0.0";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();

                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address && !inetAddress.getHostAddress().toString().equals("0.0.0.0")) {
                        localIp = inetAddress.getHostAddress().toString();
                        if (!localIp.equals("192.168.43.1")) {
                            return localIp;
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localIp;
    }

    /**
     * 检查当有线是否插入,仅针对Android电视或者Android OTT盒子
     *
     * @return
     */
    public static boolean isCablePlugin() {
        final String netFile = "/sys/class/net/eth0/operstate";
        String res = "";
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(netFile);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = new String(buffer, "UTF-8");
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fin);
        }
        if (null != res) {
            if ("up".equals(res.trim()) || "unknown".equals(res.trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = null;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    private static String parseByte(byte b) {
        int intValue = 0;
        if (b >= 0) {
            intValue = b;
        } else {
            intValue = 256 + b;
        }
        String str = Integer.toHexString(intValue);
        if (str.length() == 1) {
            return "0".concat(str);
        } else {
            return str;
        }
    }

}
