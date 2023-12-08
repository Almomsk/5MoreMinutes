package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fivemoreminutes.a5moreminutes.Entity.App;

public class DatabaseAccess {
    private SQLiteDatabase database;
    private DatabaseOpenHelper openHelper;
    private static volatile DatabaseAccess instance;

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static synchronized DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public void save(App app) {
        if (selectOne(app)==null) {
            ContentValues values = new ContentValues();
            values.put("name", app.getAppName());
            values.put("cat", app.getCategory());
            values.put("cat_num", app.getNumCategory());
            values.put("limit_h", app.getLimit_h());
            values.put("limit_m", app.getLimit_m());
            values.put("count",app.getCount_not());
            values.put("pack",app.getUsageStats().getPackageName());
            database.insert(DatabaseOpenHelper.TABLE, null, values);
        }
        else update_limit(app);

    }

    public void update_count(App app) {
        ContentValues values = new ContentValues();
        if (app.getCount_not()>=99)
        values.put("count", 1);
        else
        values.put("count", app.getCount_not()+1);
        database.update(DatabaseOpenHelper.TABLE, values, "name = ?", new String[]{app.getAppName()});
    }

    public void update_limit(App app) {
        ContentValues values = new ContentValues();
        values.put("limit_h", app.getLimit_h());
        values.put("limit_m", app.getLimit_m());
        database.update(DatabaseOpenHelper.TABLE, values, "name = ?", new String[]{app.getAppName()});
    }

    public void delete(App app) {
        database.delete(DatabaseOpenHelper.TABLE, "name = ?", new String[]{app.getAppName()});
    }

    public App selectOne(App app) {
        Cursor cursor = null;
        if (app.getAppName().contains("'")) {
            String name = app.getAppName().substring(0,app.getAppName().indexOf("'"));
            cursor = database.rawQuery("SELECT * From app WHERE TRIM(name) Like '" +name+"%'",null);
        }
        else {
            cursor = database.rawQuery("SELECT * From app WHERE TRIM(name) = '" + app.getAppName() + "'", null);
        }
        if (cursor.moveToFirst()) {
            App app1 = new App();
            app1.setAppName(cursor.getString(0));
            app1.setCategory(cursor.getString(1));
            app1.setNumCategory(cursor.getInt(2));
            app1.setLimit_h(cursor.getInt(3));
            app1.setLimit_m(cursor.getInt(4));
            app1.setCount_not(cursor.getInt(5));
            return app1;
        }
        else
           return null;
    }

    public App selectOneByStr(String str) {
        Cursor cursor = database.rawQuery("SELECT * From app WHERE TRIM(name) = '" +str+"'",null);
        if (cursor.moveToFirst()) {
            App app1 = new App();
            app1.setAppName(cursor.getString(0));
            app1.setCategory(cursor.getString(1));
            app1.setNumCategory(cursor.getInt(2));
            app1.setLimit_h(cursor.getInt(3));
            app1.setLimit_m(cursor.getInt(4));
            app1.setCount_not(cursor.getInt(5));
            return app1;
        }
        else
            return null;
    }

    public List<App>  getAllApps() {
        List<App> apps = new ArrayList();
        Cursor cursor = database.rawQuery("SELECT * From app ORDER BY limit_h DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            App app1 = new App();
            app1.setAppName(cursor.getString(0));
            app1.setCategory(cursor.getString(1));
            app1.setNumCategory(cursor.getInt(2));
            app1.setLimit_h(cursor.getInt(3));
            app1.setLimit_m(cursor.getInt(4));
            app1.setCount_not(cursor.getInt(5));
            apps.add(app1);
            cursor.moveToNext();
        }
        cursor.close();
        return apps;
    }

    public List<App>  getAllAppsByCount() {
        List<App> apps = new ArrayList();
        Cursor cursor = database.rawQuery("SELECT * From app ORDER BY count DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            App app1 = new App();
            app1.setAppName(cursor.getString(0));
            app1.setCategory(cursor.getString(1));
            app1.setNumCategory(cursor.getInt(2));
            app1.setLimit_h(cursor.getInt(3));
            app1.setLimit_m(cursor.getInt(4));
            app1.setCount_not(cursor.getInt(5));
            app1.setPackName(cursor.getString(6));
            apps.add(app1);
            cursor.moveToNext();
        }
        cursor.close();
        return apps;
    }

    public List<App> getAllAppsByCategoty(int id) {
        List<App> apps = new ArrayList();
        Cursor cursor = database.rawQuery("SELECT * From app WHERE TRIM(cat_num) = '" +id+"'",null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            App app1 = new App();
            app1.setAppName(cursor.getString(0));
            app1.setCategory(cursor.getString(1));
            app1.setNumCategory(cursor.getInt(2));
            app1.setLimit_h(cursor.getInt(3));
            app1.setLimit_m(cursor.getInt(4));
            app1.setCount_not(cursor.getInt(5));
            apps.add(app1);
            cursor.moveToNext();
        }
        cursor.close();
        return apps;
    }
}