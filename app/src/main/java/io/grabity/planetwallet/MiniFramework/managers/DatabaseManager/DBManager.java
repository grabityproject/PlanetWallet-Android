package io.grabity.planetwallet.MiniFramework.managers.DatabaseManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

import io.grabity.planetwallet.MiniFramework.utils.Utils;

/**
 * Created by Administrator on 2015-03-11.
 */
public abstract class DBManager {

    private final static String TAG = "DBManager";

    private OpenHelper openHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    DBManager( Context context ) {
        this.context = context;
        this.openHelper = new OpenHelper( context, getDatabaseName( ), null, getDatabaseVersion( ) );
        this.sqLiteDatabase = this.openHelper.getWritableDatabase( );
    }

    abstract protected String getDatabaseName( );

    abstract protected int getDatabaseVersion( );

    abstract protected void createDatabase( SQLiteDatabase db );

    abstract protected void updateDatabase( SQLiteDatabase db, int oldVersion, int newVersion );

    private void onOpen( SQLiteDatabase db ) {

    }

    private void onDowngrade( SQLiteDatabase db, int oldVersion, int newVersion ) {

    }

    public SQLiteDatabase getDatbase( ) {
        return sqLiteDatabase;
    }

    public < T > ArrayList< T > select( Class< T > type ) {
        return select( type, type.getSimpleName( ), null, null );
    }

    public < T > ArrayList< T > select( Class< T > type, String table ) {
        return select( type, table, null, null );
    }

    public < T > ArrayList< T > select( Class< T > type, String table, String[] columns ) {
        return select( type, table, null, columns );
    }

    public < T > ArrayList< T > select( Class< T > type, String table, String condition, String[] columns ) {
        ArrayList< T > result = new ArrayList< T >( );
        String selector = "*";
        condition = ( condition == null || Utils.equals( condition, "" ) ) ? "" : ( "WHERE " + condition.replace( "WHERE ", "" ) );
        if ( columns != null && columns.length != 0 ) {
            selector = "";
            for ( String column : columns ) {
                selector += column + ",";
            }
            selector = selector.substring( 0, selector.length( ) - 1 );
        }

        try {
            Cursor cursor = sqLiteDatabase.rawQuery( String.format( "SELECT %s FROM %s %s", selector, table, condition, Locale.US ), null );
            while ( cursor.moveToNext( ) ) {
                T item = type.newInstance( );
                Field[] fields = type.getDeclaredFields( );
                Method[] methods = type.getDeclaredMethods( );


                for ( Field field : fields ) {
                    for ( Method method : methods ) {
                        if ( ( method.getName( ).startsWith( "set" ) ) && ( method.getName( ).length( ) == ( field.getName( ).length( ) + 3 ) ) ) {
                            if ( method.getName( ).toLowerCase( ).endsWith( field.getName( ).toLowerCase( ) ) ) {
                                try {
                                    int index = cursor.getColumnIndexOrThrow( field.getName( ) );
                                    method.setAccessible( true );
                                    if ( field.getType( ).getSimpleName( ).toLowerCase( ).endsWith( "integer" ) )
                                        method.invoke( item, cursor.getInt( index ) );
                                    else if ( field.getType( ).getSimpleName( ).toLowerCase( ).endsWith( "long" ) )
                                        method.invoke( item, cursor.getLong( index ) );
                                    else if ( field.getType( ).getSimpleName( ).toLowerCase( ).endsWith( "string" ) )
                                        method.invoke( item, cursor.getString( index ) );
                                    else if ( field.getType( ).getSimpleName( ).toLowerCase( ).endsWith( "double" ) )
                                        method.invoke( item, cursor.getDouble( index ) );
                                    else if ( field.getType( ).getSimpleName( ).toLowerCase( ).endsWith( "float" ) )
                                        method.invoke( item, cursor.getFloat( index ) );

                                } catch ( IllegalAccessException | InvocationTargetException | IllegalArgumentException e ) {
//                                    System.err.println( e.getMessage( ) );
                                    // Do not disturb
                                }
                            }
                        }
                    }
                }
                result.add( item );
            }
        } catch ( IllegalAccessException e ) {
            e.printStackTrace( );
        } catch ( InstantiationException e ) {
            e.printStackTrace( );
        }
        return result;
    }


    public boolean insertData( Object object ) {
        return insertData( object.getClass( ).getSimpleName( ), object );
    }


    public boolean insertData( String table, Object object ) {
        if ( table == null || object == null ) return false;
        Field[] fields = object.getClass( ).getDeclaredFields( );
        Method[] methods = object.getClass( ).getDeclaredMethods( );
        ContentValues contentValues = new ContentValues( );
        for ( int i = 0; i < methods.length; i++ ) {
            try {
                Method method = methods[ i ];
                String methodName = method.getName( );

                if ( methodName.length( ) > 3 && methodName.substring( 0, 3 ).equals( "get" ) ) {
                    for ( Field field : fields ) {
                        if ( methodName.substring( 3 ).toLowerCase( ).equals( field.getName( ).toLowerCase( ) ) ) {
                            Object item = method.invoke( object );
                            if ( item != null ) {
                                contentValues.put( field.getName( ), String.valueOf( item ) );
                            }
                            break;
                        }
                    }
                } else if ( methodName.length( ) > 2 && methodName.substring( 0, 2 ).equals( "is" ) ) {
                    for ( Field field : fields ) {
                        if ( methodName.substring( 2 ).toLowerCase( ).equals( field.getName( ).toLowerCase( ) ) ) {
                            Object item = method.invoke( object );
                            if ( item != null ) {
                                contentValues.put( field.getName( ), String.valueOf( item ) );
                            }
                            break;
                        }
                    }
                }

            } catch ( InvocationTargetException | IllegalAccessException e ) {
                // Do not disturb
            }
        }
        return sqLiteDatabase.insert( table, null, contentValues ) > 0;
    }

    public void updateData( Object object, String condition ) {
        updateData( object.getClass( ).getSimpleName( ), object, condition );
    }

    public void updateData( String table, Object object, String condition ) {
        condition = condition == null ? "" : ( "WHERE " + condition.replace( "WHERE ", "" ) );
        if ( table == null || object == null ) return;

        Field[] fields = object.getClass( ).getDeclaredFields( );
        Method[] methods = object.getClass( ).getDeclaredMethods( );
        StringBuilder valueSet = new StringBuilder( );
        for ( int i = 0; i < methods.length; i++ ) {
            try {
                Method method = methods[ i ];
                String methodName = method.getName( );

                if ( methodName.length( ) > 3 && methodName.substring( 0, 3 ).equals( "get" ) ) {
                    for ( Field field : fields ) {
                        if ( methodName.substring( 3 ).toLowerCase( ).equals( field.getName( ).toLowerCase( ) ) ) {
                            Object item = method.invoke( object );
                            if ( item != null ) {
                                valueSet.append( String.format( ", %s='%s'", field.getName( ), String.valueOf( item ) ) );
                            }
                            break;
                        }
                    }
                } else if ( methodName.length( ) > 2 && methodName.substring( 0, 2 ).equals( "is" ) ) {
                    for ( Field field : fields ) {
                        if ( methodName.substring( 2 ).toLowerCase( ).equals( field.getName( ).toLowerCase( ) ) ) {
                            Object item = method.invoke( object );
                            if ( item != null ) {
                                valueSet.append( String.format( ", %s='%s'", field.getName( ), String.valueOf( item ) ) );
                            }
                            break;
                        }
                    }
                }

            } catch ( InvocationTargetException | IllegalAccessException e ) {
                // Do not disturb
                e.printStackTrace( );
            }
        }

        if ( valueSet.length( ) > 2 ) {
            valueSet.delete( 0, 2 );
        }
        Log.e( TAG, String.format( "UPDATE %s SET %s %s", table, valueSet.toString( ), condition ) );
        sqLiteDatabase.execSQL( String.format( "UPDATE %s SET %s %s", table, valueSet.toString( ), condition ) );
    }

    public void deleteData( Object object ) {
        deleteData( object.getClass( ).getSimpleName( ), object );
    }

    //add
    public void deleteData( Object object, String condition ) {
        deleteData( object.getClass( ).getSimpleName( ), object, condition );
    }

    //add
    public void deleteData( String table, Object object, String condition ) {
        condition = condition == null ? "" : ( "WHERE " + condition.replace( "WHERE ", "" ) );
        if ( table == null || object == null ) return;

        Field[] fields = object.getClass( ).getDeclaredFields( );
        Method[] methods = object.getClass( ).getDeclaredMethods( );
        StringBuilder valueSet = new StringBuilder( );
        for ( int i = 0; i < methods.length; i++ ) {
            try {
                Method method = methods[ i ];
                String methodName = method.getName( );

                if ( methodName.length( ) > 3 && methodName.substring( 0, 3 ).equals( "get" ) ) {
                    for ( Field field : fields ) {
                        if ( methodName.substring( 3 ).toLowerCase( ).equals( field.getName( ).toLowerCase( ) ) ) {
                            Object item = method.invoke( object );
                            if ( item != null ) {
                                valueSet.append( String.format( ", %s='%s'", field.getName( ), String.valueOf( item ) ) );
                            }
                            break;
                        }
                    }
                } else if ( methodName.length( ) > 2 && methodName.substring( 0, 2 ).equals( "is" ) ) {
                    for ( Field field : fields ) {
                        if ( methodName.substring( 2 ).toLowerCase( ).equals( field.getName( ).toLowerCase( ) ) ) {
                            Object item = method.invoke( object );
                            if ( item != null ) {
                                valueSet.append( String.format( ", %s='%s'", field.getName( ), String.valueOf( item ) ) );
                            }
                            break;
                        }
                    }
                }

            } catch ( InvocationTargetException | IllegalAccessException e ) {
                // Do not disturb
                e.printStackTrace( );
            }
        }

        if ( valueSet.length( ) > 2 ) {
            valueSet.delete( 0, 2 );
        }

        Log.e( TAG, String.format( "DELETE FROM %s %s", table, condition ) );
        sqLiteDatabase.execSQL( String.format( "DELETE FROM %s %s", table, condition ) );
    }


    public void deleteData( String table, Object object ) {
        if ( table == null || object == null ) return;
        String condition = "";
        if ( object.getClass( ).equals( String.class ) ) {
            condition = String.valueOf( object ).replace( "WHERE ", "" );
        } else {
            Field[] fields = object.getClass( ).getDeclaredFields( );
            Method[] methods = object.getClass( ).getDeclaredMethods( );
            StringBuilder valueSet = new StringBuilder( );
            for ( int i = 0; i < methods.length; i++ ) {
                try {
                    Method method = methods[ i ];
                    String methodName = method.getName( );

                    if ( methodName.length( ) > 3 && methodName.substring( 0, 3 ).equals( "get" ) ) {
                        for ( Field field : fields ) {
                            if ( methodName.substring( 3 ).toLowerCase( ).equals( field.getName( ).toLowerCase( ) ) ) {
                                Object item = method.invoke( object );
                                if ( item != null ) {
                                    valueSet.append( String.format( " AND %s='%s'", field.getName( ), String.valueOf( item ) ) );
                                }
                                break;
                            }
                        }
                    } else if ( methodName.length( ) > 2 && methodName.substring( 0, 2 ).equals( "is" ) ) {
                        for ( Field field : fields ) {
                            if ( methodName.substring( 2 ).toLowerCase( ).equals( field.getName( ).toLowerCase( ) ) ) {
                                Object item = method.invoke( object );
                                if ( item != null ) {
                                    valueSet.append( String.format( " AND %s='%s'", field.getName( ), String.valueOf( item ) ) );
                                }
                                break;
                            }
                        }
                    }

                } catch ( InvocationTargetException | IllegalAccessException e ) {
                    // Do not disturb
                    e.printStackTrace( );
                }
            }

            if ( valueSet.length( ) > 4 ) {
                valueSet.delete( 0, 4 );
            }
            condition = valueSet.toString( );
        }

        Log.e( TAG, String.format( "DELETE FROM %s WHERE %s ", table, condition ) );
        sqLiteDatabase.execSQL( String.format( "DELETE FROM %s WHERE %s ", table, condition ) );
    }

    public Context getContext( ) {
        return context;
    }

    private class OpenHelper extends SQLiteOpenHelper {

        OpenHelper( Context context, String name, SQLiteDatabase.CursorFactory factory, int version ) {
            super( context, name, factory, version );
        }

        @Override
        public void onCreate( SQLiteDatabase db ) {
            createDatabase( db );
        }

        @Override
        public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
            updateDatabase( db, oldVersion, newVersion );
        }

        @Override
        public void onOpen( SQLiteDatabase db ) {
            super.onOpen( db );
        }

        @Override
        public void onDowngrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
            super.onDowngrade( db, oldVersion, newVersion );
        }

    }


}
