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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import fivemoreminutes.a5moreminutes.Activity.GlobalActivity;
import fivemoreminutes.a5moreminutes.Activity.MainActivity;
import fivemoreminutes.a5moreminutes.Activity.SingleAppActivity;
import fivemoreminutes.a5moreminutes.Entity.App;

public class GetStatsWeekly extends AsyncTask<Void, Void, Map<String,Map<String,App>>> {

    GlobalActivity caller;
    private static final int flags = PackageManager.GET_META_DATA |
            PackageManager.GET_SHARED_LIBRARY_FILES |
            PackageManager.GET_UNINSTALLED_PACKAGES;

    public GetStatsWeekly(GlobalActivity caller) {
        this.caller = caller;
    }


    protected void onPostExecute(Map<String,Map<String,App>> result) {
        caller.onBackgroundTaskCompleted(result);
    }


    @Override
    protected Map<String,Map<String,App>> doInBackground(Void... noargs) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) caller.getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Map<String,Map<String,App>> stats_week = new LinkedHashMap<>();
        List<String> installedApps = getInstalledAppList();

        for (int d = 0; d < 7; d++) {
            Date date = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, -d);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.HOUR_OF_DAY, 0);
            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            int Month = c.get(Calendar.MONTH);
            String date_str;
            if ((Month+1)<10)
                date_str = dayOfMonth+ ".0"+(Month+1);
            else
                date_str = dayOfMonth+ "."+(Month+1);
            Long start = c.getTimeInMillis();
            c.add(Calendar.DATE, +1);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.HOUR_OF_DAY, 0);
            Long end = c.getTimeInMillis();
            List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
            SortedMap<String, App> map = new TreeMap<>();
            for (String name : installedApps) {
                for (UsageStats stat : stats) {
                    if ((start < stat.getLastTimeUsed()) && (end > stat.getLastTimeUsed())) {
                        if (name.equals(stat.getPackageName())) {
                            App value = map.get(fromUsageStat(stat).getAppName());
                            if (value != null) {
                                value.setTimeInforground(value.getTimeInforground() + fromUsageStat(stat).getTimeInforground());
                                map.put(value.getAppName(), value);

                            } else {
                                App ap = fromUsageStat(stat);
                                map.put(ap.getAppName(), ap);
                            }
                        }
                    }
                }
            }
            stats_week.put(date_str,map);
        }

        return stats_week;
    }

    private List<String> getInstalledAppList() {
        List<ApplicationInfo> infos = caller.getPackageManager().getInstalledApplications(flags);
        List<String> installedApps = new ArrayList<>();
        for (ApplicationInfo info : infos) {
            if (!isSystemPackage(info))
                installedApps.add(info.packageName);
        }
        return installedApps;
    }

    private boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private App fromUsageStat(UsageStats usageStats) throws IllegalArgumentException {
        try {
            ApplicationInfo ai = caller.getPackageManager().getApplicationInfo(usageStats.getPackageName(), 0);
            return new App(usageStats, caller.getPackageManager().getApplicationIcon(ai), caller.getPackageManager().getApplicationLabel(ai).toString(), usageStats.getTotalTimeInForeground(), ai.category);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
