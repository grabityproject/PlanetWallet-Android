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
        return "TESSss2C_DATABASE";
    }

    @Override
    protected int getDatabaseVersion( ) {
        return 8;
    }

    @Override
    protected void createDatabase( SQLiteDatabase db ) {

        String createPlanetTable = "CREATE TABLE Planet( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "keyId TEXT," +
                "pathIndex TEXT," +
                "name TEXT," +
                "address TEXT," +
                "balance TEXT," +
                "coinType TEXT," +
                "coinName TEXT," +
                "symbol TEXT," +
                "hide TEXT," +
                "decimals TEXT" +
                ")";

        String createERC20Table = "CREATE TABLE ERC20( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "keyId TEXT," +
                "hide TEXT," +
                "balance TEXT," +
                "name TEXT," +
                "symbol TEXT," +
                "decimals TEXT," +
                "img_path TEXT," +
                "contract TEXT" +
                ")";

        String keyPairTable = "CREATE TABLE KeyPair( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "keyId TEXT," +
                "value TEXT," +
                "master TEXT" +
                ")";

        String SearchTable = "CREATE TABLE Search( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "keyId TEXT," +
                "name TEXT," +
                "address TEXT," +
                "symbol TEXT," +
                "date INTEGER DEFAULT 0"+
                ")";

        db.execSQL( createPlanetTable );
        db.execSQL( createERC20Table );
        db.execSQL( keyPairTable );
        db.execSQL( SearchTable );

    }

    @Override
    protected void updateDatabase( SQLiteDatabase db, int oldVersion, int newVersion ) {


    }

}