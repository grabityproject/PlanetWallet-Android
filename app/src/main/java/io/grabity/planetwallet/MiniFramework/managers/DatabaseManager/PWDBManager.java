package io.grabity.planetwallet.MiniFramework.managers.DatabaseManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class PWDBManager extends DBManager {

    private static PWDBManager dbManager;

    private PWDBManager( Context context ) {
        super( context );
    }

    public static void init( Context context ) {
        dbManager = new PWDBManager( context );
    }

    public static DBManager getInstance( ) {
        return dbManager;
    }


    @Override
    protected String getDatabaseName( ) {
        return "DATABASE_VERSION4";
    }

    @Override
    protected int getDatabaseVersion( ) {
        return 1;
    }

    @Override
    protected void createDatabase( SQLiteDatabase db ) {

        String createPlanetTable = "CREATE TABLE Planet( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "keyId TEXT," +
                "pathIndex TEXT," +
                "name TEXT," +
                "address TEXT," +
                "coinType TEXT," +
                "coinName TEXT," +
                "symbol TEXT," +
                "hide TEXT," +
                "decimals TEXT" +
                ")";

        String createMainItemTable = "CREATE TABLE MainItem( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "keyId TEXT," +
                "coinType INTEGER," +
                "hide TEXT," +
                "balance TEXT," +
                "name TEXT," +
                "symbol TEXT," +
                "decimals TEXT," +
                "img_path TEXT," +
                "contract TEXT" +
                ")";

        String createKeyPairTable = "CREATE TABLE KeyPair( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "keyId TEXT," +
                "value TEXT," +
                "master TEXT" +
                ")";

        String createSearchTable = "CREATE TABLE Search( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "keyId TEXT," +
                "coinType Integer," +
                "name TEXT," +
                "address TEXT," +
                "symbol TEXT," +
                "date INTEGER DEFAULT 0" +
                ")";

        db.execSQL( createPlanetTable );
        db.execSQL( createMainItemTable );
        db.execSQL( createKeyPairTable );
        db.execSQL( createSearchTable );

    }

    @Override
    protected void updateDatabase( SQLiteDatabase db, int oldVersion, int newVersion ) {


    }

}