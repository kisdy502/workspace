package com.sdt.nepush.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.sdt.nepush.App;

/**
 * <p>@author:          ${Author}</p>
 * <p>@date:            ${Date}</p>
 * <p>@email:           ${Email}</p>
 * <b>
 * <p>@Description:     ${Description}</p>
 * </b>
 */
public class NotificationsUtils {

    public static void checkNotificationOpend(Activity activity) {
        if (!NotificationManagerCompat.from(App.getInstance()).areNotificationsEnabled()) {
            AlertDialog alertDialog = new AlertDialog.Builder(activity)
                    .setTitle("您尚未允许应用接收通知，是否前往打开?")
                    .setPositiveButton("前往", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                                Intent intent = new Intent();
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("app_package", activity.getPackageName());
                                intent.putExtra("app_uid", activity.getApplicationInfo().uid);
                                activity.startActivity(intent);
                            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                                activity.startActivity(intent);
                            } else {
                                Intent localIntent = new Intent();
                                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                                activity.startActivity(localIntent);
                            }
                            Log.e("onClick", "同意: ");
                        }
                    })
                    .setNegativeButton("下次打开", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.e("onClick", "不同意: ");
                        }
                    }).show();
        }

    }
}

