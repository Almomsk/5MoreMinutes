package fivemoreminutes.a5moreminutes.Settings;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import fivemoreminutes.a5moreminutes.Activity.MainActivity;

public class GetStats extends AsyncTask<Void, Void, List<UsageStats>> {

    MainActivity caller;

    public GetStats(MainActivity caller) {
        this.caller = caller;
    }

    protected void onPostExecute( List<UsageStats>result) {
        caller.onBackgroundTaskCompleted(result);
    }

    @Override
    protected List<UsageStats> doInBackground(Void... noargs) {
        UsageStatsManager usageStatsManager = (UsageStatsManager)caller.getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);

        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 1);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        int Month = c.get(Calendar.MONTH);
        String date_str = dayOfMonth+ "."+(Month+1);
        Date st = c.getTime();
        Long start = c.getTimeInMillis();
        c.add(Calendar.DATE, +1);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 1);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        Long end = c.getTimeInMillis();
        Date en = c.getTime();
        Log.d("date", st+" - "+en);
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);

       List<UsageStats> stats_today = new ArrayList<>();
        if (stats.size()>0)
            for (int i=0;i<stats.size();i++) {
                if (stats.get(i).getTotalTimeInForeground() > 0)
                    if ((start<stats.get(i).getLastTimeUsed())&&(end>stats.get(i).getLastTimeUsed())){
                        stats_today.add(stats.get(i));
                        Log.d(stats.get(i).getPackageName()+"", stats.get(i).getTotalTimeInForeground()+"");

                }

            }
        return stats_today;
    }

}
