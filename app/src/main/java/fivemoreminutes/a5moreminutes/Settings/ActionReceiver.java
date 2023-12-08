package fivemoreminutes.a5moreminutes.Settings;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import Database.DatabaseAccess;
import fivemoreminutes.a5moreminutes.Entity.App;

public class ActionReceiver extends BroadcastReceiver {
    private DatabaseAccess databaseAccess;
    public int NOTIFICATION_ID = 1515;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.databaseAccess = DatabaseAccess.getInstance(context);
        String action = intent.getStringExtra("action");
        Log.d("onReceive: ", action);
        String[] list = action.split(",");
        for (int i = 0;i<list.length;i++) {
            databaseAccess.open();
            App single_db = databaseAccess.selectOneByStr(list[i]);
            databaseAccess.close();
            if (single_db.getLimit_m()+5>=60){
                single_db.setLimit_h(single_db.getLimit_h()+1);
                single_db.setLimit_m((single_db.getLimit_m()+5)-60);
            }
            else{
                single_db.setLimit_m((single_db.getLimit_m()+5));
            }
            databaseAccess.open();
            databaseAccess.update_limit(single_db);
            databaseAccess.close();
        }
        /*Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);*/
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
    }
}
