package io.grabity.planetwallet.MiniFramework.managers;

/**
 * Created by Administrator on 2015-03-11.
 */
public class DataBaseManager {

   /* private static DataBaseManager dbManager;

    private OpenHelper openHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    private DataBaseManager( Context context ) {
        this.context = context;
        this.openHelper = new OpenHelper( context, DB_NAME, null, DB_VERSION );
        this.sqLiteDatabase = this.openHelper.getWritableDatabase( );
    }

    public static void init( Context context ) {
        dbManager = new DataBaseManager( context );
    }

    public static DataBaseManager getInstance( Context context ) {
        if ( dbManager == null ) dbManager = new DataBaseManager( context );
        return dbManager;
    }


    public static DataBaseManager getInstance( ) {
        return dbManager;
    }

    private class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper( Context context, String name, SQLiteDatabase.CursorFactory factory, int version ) {
            super( context, name, factory, version );
        }

        @Override
        public void onCreate( SQLiteDatabase db ) {
            db.execSQL( CREATE_TABLE_ETH );
        }

        @Override
        public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
            db.execSQL( "DROP TABLE IF EXISTS " + TABLE_ETH );
            onCreate( db );
        }
    }*/
}
