package fivemoreminutes.a5moreminutes.Settings;


import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fivemoreminutes.a5moreminutes.Activity.MainActivity;
import fivemoreminutes.a5moreminutes.Activity.SingleAppActivity;
import fivemoreminutes.a5moreminutes.Entity.App;

public class GetStatsSingleWeekly extends AsyncTask<Void, Void, Map<Integer,App>> {

    SingleAppActivity caller;
    String name;
    private static final int flags = PackageManager.GET_META_DATA |
            PackageManager.GET_SHARED_LIBRARY_FILES |
            PackageManager.GET_UNINSTALLED_PACKAGES;

    public GetStatsSingleWeekly(SingleAppActivity caller, String name) {
        this.caller = caller;
        this.name = name;
    }


    protected void onPostExecute(Map<Integer,App> result) {
        caller.onBackgroundTaskCompleted(result);
    }

    private boolean getInstalledApp(UsageStats usageStats) {
        List<ApplicationInfo> infos = caller.getPackageManager().getInstalledApplications(flags);
        for (ApplicationInfo info : infos) {
            if (!isSystemPackage(info))
                if (info.packageName.equals(usageStats.getPackageName()))
                    return true;
        }
        return false;
    }

    private boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private App fromUsageStat(UsageStats usageStats) throws IllegalArgumentException {
        try {
            if (getInstalledApp(usageStats)) {
                ApplicationInfo ai = caller.getPackageManager().getApplicationInfo(usageStats.getPackageName(), 0);
                return new App(usageStats, caller.getPackageManager().getApplicationIcon(ai), caller.getPackageManager().getApplicationLabel(ai).toString(), usageStats.getTotalTimeInForeground(), ai.category);
            }
            else return null;
            } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    protected Map<Integer,App> doInBackground(Void... noargs) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) caller.getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Map<Integer,App> stats_week = new TreeMap<>();
        for (int d = 0; d < 7; d++) {
            Date date = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, -d);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.HOUR_OF_DAY, 0);
            Date st = c.getTime();
            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            int Month = c.get(Calendar.MONTH);
            String date_str;
            if ((Month+1)<10)
                date_str = dayOfMonth+ ".0"+(Month+1);
            else
                date_str = dayOfMonth+ "."+(Month+1);
            Long start = c.getTimeInMillis();
            c.add(Calendar.DATE, 0);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.SECOND, 59);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.HOUR_OF_DAY, 23);
            Long end = c.getTimeInMillis();
            Date en = c.getTime();
            List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
            App find = null;
            int cnt = 0;
            Log.d("DATE", st+"-"+en);
            if (stats.size() > 0) {
                for (int count = 0; count < stats.size(); count++) {
                    if ((start<stats.get(count).getLastTimeUsed())&&(end>stats.get(count).getLastTimeUsed())) {
                        find = fromUsageStat(stats.get(count));
                        if (find != null) {
                            if (name == find.getAppName()) {
                                find.setCategory(date_str);
                                App value = stats_week.get(d);
                                if (value != null) {
                                    value.setTimeInforground(value.getTimeInforground() + find.getTimeInforground());
                                    stats_week.put(d, value);
                                } else {
                                    stats_week.put(d, find);
                                }
                                find = null;
                                cnt++;
                            }
                        }
                    }

                }
            }
            if (cnt==0) {
                find = new App();
                find.setCategory(date_str);
                find.setTimeInforground(0L);
                stats_week.put(d, find);
            }
        }
        return stats_week;
    }

}
