package Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE = "apps.db";
    public static final String TABLE = "app";
    public static final int VERSION = 1;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE app(name TEXT PRIMARY KEY, cat TEXT, cat_num INTEGER,limit_h INTEGER,limit_m INTEGER,count INTEGER,pack TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}