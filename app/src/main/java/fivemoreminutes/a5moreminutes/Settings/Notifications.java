package fivemoreminutes.a5moreminutes.Settings;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import Database.DatabaseAccess;
import fivemoreminutes.a5moreminutes.Activity.MainActivity;
import fivemoreminutes.a5moreminutes.Entity.App;
import fivemoreminutes.a5moreminutes.R;

public class Notifications {
    private MainActivity caller;
    public NotificationChannel channel;
    public SharPreferences pref;
    public static final String APP_PREFERENCES = "mysettings";
    public String CHANNEL_ID = "5MM";
    public int NOTIFICATION_ID = 1515;
    public String str_send;
    private DatabaseAccess databaseAccess;
    public String mess;
    public boolean flag;

    public Notifications(MainActivity caller) {
        this.caller = caller;
        this.databaseAccess = DatabaseAccess.getInstance(caller);
        pref = new SharPreferences(caller.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE));
        pref.loadLimitSwitch(caller.getIntent());
        pref.loadLimit(caller.getIntent());
        pref.loadNotific(caller.getIntent());
        pref.loadPopNotific(caller.getIntent());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "5MM";
            String description = "description_5mm";
            int importance = 0;
            if (pref.isPopNotific())
                importance = NotificationManager.IMPORTANCE_MAX;
            else
                importance = NotificationManager.IMPORTANCE_DEFAULT;
            channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = caller.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void addFiveMin(){
        Log.d("addFiveMin", "instance initializer: ");
    }

    private void send(String str){
        Intent intent = new Intent(caller, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(caller, 0, intent, 0);


        Intent addIntent = new Intent(caller, ActionReceiver.class);
        addIntent.putExtra("action",str_send);
        PendingIntent addPendingIntent = PendingIntent.getBroadcast(caller,0,addIntent,0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(caller, CHANNEL_ID)

                .setSmallIcon(R.drawable.ic_timer_black_24dp)
                .setContentTitle("5 Minute More")
                .setContentText("Limit is exceeded")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(caller.getApplicationContext().getResources(), R.drawable.ic_vector_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(str));

if (flag)
    mBuilder.addAction(R.drawable.ic_add_alert_black_24dp, "Add 5 more minutes", addPendingIntent);

        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS );
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(caller);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void sendAllLimitNotification(float allTime) {
        mess="";
        pref.loadLimitSwitch(caller.getIntent());
        pref.loadLimit(caller.getIntent());
        pref.loadNotific(caller.getIntent());
        pref.loadPopNotific(caller.getIntent());
        if (((pref.isNotific())||(pref.isPopNotific())) && (pref.isAllLimit())) {
            float limit_all = (pref.getLimit_hour() * 60) + pref.getLimit_min();
            if (allTime > limit_all) {
                mess+="Total application limit is exceeded";
            }
        }
    }

    public void sendAppLimitNotification(Map<String,App> result){
        pref.loadLimitSwitch(caller.getIntent());
        pref.loadLimit(caller.getIntent());
        pref.loadNotific(caller.getIntent());
        pref.loadPopNotific(caller.getIntent());
        flag = false;
        str_send="";
        if ((pref.isNotific())||(pref.isPopNotific())) {
            if (result.size() > 0) {
                String string = "";
                databaseAccess.open();
                List<App> apps = databaseAccess.getAllApps();
                databaseAccess.close();
                if (apps.size() > 0) {
                    for (Map.Entry<String, App> app : result.entrySet()) {
                        for (App list_app : apps) {
                            if (list_app.getAppName().equals(app.getValue().getAppName())) {
                                float limit = (list_app.getLimit_h() * 60) + list_app.getLimit_m();
                                if (limit < app.getValue().CalcTimeCompare()) {
                                    string += list_app.getAppName() + ", ";
                                    databaseAccess.open();
                                    databaseAccess.update_count(list_app);
                                    databaseAccess.close();
                                }
                            }
                        }
                    }
                    if (string.length() > 0) {
                        flag = true;
                        string = string.substring(0, string.length() - 2);
                        str_send = string;
                        mess+="\n"+string+" limit is exceeded";
                        Class clazz = R.string.class;
                        int rnd = (int) (30 * Math.random());
                        try {
                            Field field = clazz.getField("notifi_"+rnd);
                            mess+="\n" + caller.getString(field.getInt(null));
                        }
                        catch (Exception ex){

                        }

                    }
                }
            }
            if (mess.length()>0)
                send(mess);
        }
    }


}
