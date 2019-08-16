package com.sdt.nepush.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.util.Log;

import com.sdt.libcommon.esc.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by Administrator on 2018/7/4.
 */

public class PackageUtils {

    private final static String TAG = "PackageUtils";

    public static String getSignature(Context context, String pkgName) {
        Signature[] signatures = getApkSignature(context, pkgName);
        if (signatures != null) {
            return MD5Utils.getBytesMD5(signatures[0].toByteArray());
        }
        return "";
    }

    private static Signature[] getApkSignature(Context context, String pkgName) {
        PackageInfo localPackageInfo;
        PackageManager pm = context.getPackageManager();
        try {
            localPackageInfo = pm.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
            if (localPackageInfo == null) {
                return null;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return localPackageInfo.signatures;
    }

    public static X509Certificate getPackageX509Certificate(Context context, final String packageName) {
        PackageManager pm = context.getPackageManager();
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        Signature[] signatures = packageInfo.signatures;
        if (signatures.length > 0) {
            byte[] cert = signatures[0].toByteArray();
            InputStream input = new ByteArrayInputStream(cert);
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X509");
                X509Certificate c = (X509Certificate) cf.generateCertificate(input);
                return c;
            } catch (CertificateException e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(input);
            }
        } else {
            Log.e(TAG, "apk:" + packageName + " no signatures info!");
        }
        return null;
    }

    public static String getFingerprintMd5(final String packageName, Context context) {
        X509Certificate cf = getPackageX509Certificate(context, packageName);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] publicKey = md.digest(cf.getEncoded());
            String hex = byte2HexFormatted(publicKey);
            return hex;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFingerprintSha1(final String packageName, Context context) {
        X509Certificate cf = getPackageX509Certificate(context, packageName);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cf.getEncoded());
            String hex = byte2HexFormatted(publicKey);
            return hex;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getVersionCode(Context context, @NonNull final String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String getVersionName(Context context, @NonNull final String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String byte2HexFormatted(byte[] bytes) {
        final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        char[] hexChars = new char[bytes.length * 3 - 1];
        int v = 0x00;
        for (int i = 0; i < bytes.length; i++) {
            v = bytes[i] & 0xff; // 保留最后两位，即两个16进制位
            // high 4bit
            hexChars[i * 3] = HEX[v >>> 4]; // 忽略符号右移，空出补0
            // low 4bit
            hexChars[i * 3 + 1] = HEX[v & 0x0f];
            if (i < bytes.length - 1) {
                hexChars[i * 3 + 2] = ':';
            }
        }
        return String.valueOf(hexChars);
    }
}
