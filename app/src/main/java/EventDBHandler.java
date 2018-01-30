import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Cristina Borri
 */

public class EventDBHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "event.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_EVENT = "event";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DETAILS= "details";

    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_EVENT + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_LOCATION + " TEXT, " +
            COLUMN_DATE + " TEXT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_DETAILS + " TEXT " +
            ")";

    public EventDBHandler(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_EVENT);
        db.execSQL(TABLE_CREATE);
    }
}
