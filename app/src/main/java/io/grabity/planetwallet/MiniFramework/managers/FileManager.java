package io.grabity.planetwallet.MiniFramework.managers;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by. JcobPark on 2018. 08. 29
 */

public class FileManager {

    static String getApplicationName( Context context ) {
        ApplicationInfo applicationInfo = context.getApplicationInfo( );
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString( ) : context.getString( stringId );
    }

    public static String getAppDirectory( Context context ) {
        String appName = getApplicationName( context );
        return Environment.getExternalStorageDirectory( ).getAbsolutePath( ) + "/" + appName;
    }

    public static File makeFile( Context context, String fileName, String fileContents ) {
        String appName = getApplicationName( context );
        String directoryPath = Environment.getExternalStorageDirectory( ).getAbsolutePath( ) + "/" + appName + "/";
        makeDirectory( directoryPath );
        try {
//            new FileOutputStream( new File( directoryPath + fileName ) )
            FileOutputStream fOut = new FileOutputStream( new File( directoryPath + fileName ) );//context.openFileOutput( directoryPath + fileName , Context.MODE_PRIVATE );
            OutputStreamWriter osw = new OutputStreamWriter( fOut );
            osw.write( fileContents );
            // save and close
            osw.flush( );
            osw.close( );

            return new File( directoryPath + fileName );

        } catch ( FileNotFoundException e ) {
            e.printStackTrace( );
        } catch ( IOException e ) {
            e.printStackTrace( );
        }
        return null;
    }

    @SuppressLint( "NewApi" )
    public static String[] getRealPathFromURI( final Context context, final Uri uri ) {

        String appName = getApplicationName( context );

        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if ( isKitKat && DocumentsContract.isDocumentUri( context, uri ) ) {

            Log.e( "FileManager", "DocumentProvider" );

            // ExternalStorageProvider
            if ( isExternalStorageDocument( uri ) ) {
                final String docId = DocumentsContract.getDocumentId( uri );
                final String[] split = docId.split( ":" );
                final String type = split[ 0 ];

                if ( "primary".equalsIgnoreCase( type ) ) {
                    return new String[]{ Environment.getExternalStorageDirectory( ) + "/" + split[ 1 ] };
                }
            }
            // DownloadsProvider
            else if ( isDownloadsDocument( uri ) ) {

                Log.e( "FileManager", "DownloadsDocument" );

                final String id = DocumentsContract.getDocumentId( uri );
                if ( !TextUtils.isEmpty( id ) ) {
                    try {
                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse( "content://downloads/public_downloads" ), Long.valueOf( id ) );
                        return new String[]{ getDataColumn( context, contentUri, null, null ) };
                    } catch ( NumberFormatException e ) {
                        Log.e( "FileManager", e.getMessage( ) );
                        final String[] split = id.split( ":" );
                        final String type = split[ 0 ];
                        if ( "raw".equalsIgnoreCase( type ) ) {
                            return new String[]{ split[ 1 ] };
                        } else {
                            return null;
                        }
                    }
                }
            }
            // MediaProvider
            else if ( isMediaDocument( uri ) ) {

                Log.e( "FileManager", "MediaProvider" );

                final String docId = DocumentsContract.getDocumentId( uri );
                final String[] split = docId.split( ":" );
                final String type = split[ 0 ];
                Uri contentUri = null;
                if ( "image".equals( type ) ) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ( "video".equals( type ) ) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ( "audio".equals( type ) ) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{ split[ 1 ] };

                Log.e( "FileManager", "contentUri : " + contentUri );

                return new String[]{ getDataColumn( context, contentUri, selection, selectionArgs ) };
            }
            // GoogldDrive
            else if ( isGoogleDocsUri( uri ) ) {
                Log.e( "FileManager", "isGoogleDocsUri" );
                Cursor cursor = context.getContentResolver( )
                        .query( uri, null, null, null, null, null );

                if ( cursor != null && cursor.moveToFirst( ) ) {
                    String displayName = cursor.getString(
                            cursor.getColumnIndex( OpenableColumns.DISPLAY_NAME ) );
                    int sizeIndex = cursor.getColumnIndex( OpenableColumns.SIZE );
                    String size = null;
                    if ( !cursor.isNull( sizeIndex ) ) {
                        size = cursor.getString( sizeIndex );
                    } else {
                        size = "Unknown";
                    }
                    String fileName = Environment.getExternalStorageDirectory( ).getAbsolutePath( ) + "/" + appName + "/" + displayName;
                    File file = new File( fileName );
                    if ( file.exists( ) && file.length( ) == parseLong( size ) ) {
                        return new String[]{ fileName };
                    } else {
                        return new String[]{ fileName, displayName, size };
                    }
                }
                if ( cursor != null )
                    cursor.close( );

            } else if ( "com.microsoft.skydrive.content.StorageAccessProvider".equals( uri.getAuthority( ) ) ) {
                Log.e( "FileManager", "isOneDrive" );
                Cursor cursor = context.getContentResolver( )
                        .query( uri, null, null, null, null, null );
                if ( cursor != null && cursor.moveToFirst( ) ) {
                    String displayName = cursor.getString(
                            cursor.getColumnIndex( OpenableColumns.DISPLAY_NAME ) );
                    int sizeIndex = cursor.getColumnIndex( OpenableColumns.SIZE );
                    String size = null;
                    if ( !cursor.isNull( sizeIndex ) ) {
                        size = cursor.getString( sizeIndex );
                    } else {
                        size = "Unknown";
                    }
                    String fileName = Environment.getExternalStorageDirectory( ).getAbsolutePath( ) + "/" + appName + "/" + displayName;
                    File file = new File( fileName );
                    if ( file.exists( ) && file.length( ) == parseLong( size ) ) {
                        return new String[]{ fileName };
                    } else {
                        return new String[]{ fileName, displayName, size };
                    }
                }
                if ( cursor != null )
                    cursor.close( );
            }
        }

        // MediaStore (and general)
        else if ( "content".equalsIgnoreCase( uri.getScheme( ) ) ) {

            Log.e( "FileManager", "MediaStore" );
            // Return the remote address
            if ( isGooglePhotosUri( uri ) ) {
                Log.e( "FileManager", "isGooglePhotosUri" );
                return new String[]{ uri.getLastPathSegment( ) };
            } else if ( "com.microsoft.skydrive.content.external".equals( uri.getAuthority( ) ) || "com.google.android.apps.docs.storage.legacy".equals( uri.getAuthority( ) ) ) {

                Log.e( "FileManager", "MediaStore One or Google" );
                Cursor cursor = context.getContentResolver( )
                        .query( uri, null, null, null, null, null );

                if ( cursor != null && cursor.moveToFirst( ) ) {
                    String displayName = cursor.getString(
                            cursor.getColumnIndex( OpenableColumns.DISPLAY_NAME ) );
                    int sizeIndex = cursor.getColumnIndex( OpenableColumns.SIZE );
                    String size = null;
                    if ( !cursor.isNull( sizeIndex ) ) {
                        size = cursor.getString( sizeIndex );
                    } else {
                        size = "Unknown";
                    }
                    String fileName = Environment.getExternalStorageDirectory( ).getAbsolutePath( ) + "/" + appName + "/" + displayName;
                    File file = new File( fileName );
                    if ( file.exists( ) && file.length( ) == parseLong( size ) ) {
                        return new String[]{ fileName };
                    } else {
                        return new String[]{ fileName, displayName, size };
                    }
                }
                if ( cursor != null )
                    cursor.close( );

            } else {
                Cursor cursor = context.getContentResolver( )
                        .query( uri, null, null, null, null, null );

                if ( cursor != null && cursor.moveToFirst( ) ) {
                    String displayName = cursor.getString(
                            cursor.getColumnIndex( OpenableColumns.DISPLAY_NAME ) );
                    int sizeIndex = cursor.getColumnIndex( OpenableColumns.SIZE );
                    String size = null;
                    if ( !cursor.isNull( sizeIndex ) ) {
                        size = cursor.getString( sizeIndex );
                    } else {
                        size = "Unknown";
                    }
                    String fileName = Environment.getExternalStorageDirectory( ).getAbsolutePath( ) + "/" + appName + "/" + displayName;
                    File file = new File( fileName );
                    if ( file.exists( ) && file.length( ) == parseLong( size ) ) {
                        return new String[]{ fileName };
                    } else {
                        return new String[]{ fileName, displayName, size };
                    }
                }
                if ( cursor != null )
                    cursor.close( );
            }


        }
        // File
        else if ( "file".equalsIgnoreCase( uri.getScheme( ) ) ) {
            return new String[]{ uri.getPath( ) };
        }

        return null;
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */

    public static String getDataColumn( Context context, Uri uri,
                                        String selection, String[] selectionArgs ) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver( ).query( uri, projection,
                    selection, selectionArgs, null );
            if ( cursor != null && cursor.moveToFirst( ) ) {
                final int index = cursor.getColumnIndexOrThrow( column );
                return cursor.getString( index );
            }
        } finally {
            if ( cursor != null )
                cursor.close( );
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument( Uri uri ) {
        return "com.android.externalstorage.documents".equals( uri
                .getAuthority( ) );
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument( Uri uri ) {
        return "com.android.providers.downloads.documents".equals( uri
                .getAuthority( ) );
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument( Uri uri ) {
        return "com.android.providers.media.documents".equals( uri
                .getAuthority( ) );
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri( Uri uri ) {
        return "com.google.android.apps.photos.content".equals( uri
                .getAuthority( ) );
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Docs.
     */
    public static boolean isGoogleDocsUri( Uri uri ) {
        return "com.google.android.apps.docs.storage".equals( uri
                .getAuthority( ) );
    }

    private void copyInputStreamToFile( InputStream in, File file ) {
        OutputStream out = null;

        try {
            out = new FileOutputStream( file );
            byte[] buf = new byte[ 1024 ];
            int len;
            while ( ( len = in.read( buf ) ) > 0 ) {
                out.write( buf, 0, len );
            }
        } catch ( Exception e ) {
            e.printStackTrace( );
        } finally {
            // Ensure that the InputStreams are closed even if there's an exception.
            try {
                if ( out != null ) {
                    out.close( );
                }

                // If you want to close the "in" InputStream yourself then remove this
                // from here but ensure that you close it yourself eventually.
                in.close( );
            } catch ( IOException e ) {
                e.printStackTrace( );
            }
        }
    }

    @SuppressLint( "NewApi" )
    public static String getRealPathFromURI_API11to18( Context context, Uri contentUri ) {
        String[] proj = { MediaStore.Images.Media.DATA };
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null );
        Cursor cursor = cursorLoader.loadInBackground( );

        if ( cursor != null ) {
            int column_index =
                    cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DATA );
            cursor.moveToFirst( );
            result = cursor.getString( column_index );
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11( Context context, Uri contentUri ) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( contentUri, proj, null, null, null );
        int column_index
                = cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DATA );
        cursor.moveToFirst( );
        return cursor.getString( column_index );
    }

    public static String getMD5EncryptedString( String encTarget ) {
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance( "MD5" );
        } catch ( NoSuchAlgorithmException e ) {
            System.out.println( "Exception while encrypting to md5" );
            e.printStackTrace( );
        } // Encryption algorithm
        mdEnc.update( encTarget.getBytes( ), 0, encTarget.length( ) );
        String md5 = new BigInteger( 1, mdEnc.digest( ) ).toString( 16 );
        while ( md5.length( ) < 32 ) {
            md5 = "0" + md5;
        }
        return md5;
    }


    public static class CloudDownloader {

        OnCloudDownloadListener onCloudDownloadListener;
        String appName;

        public CloudDownloader( OnCloudDownloadListener onCloudDownloadListener ) {
            this.onCloudDownloadListener = onCloudDownloadListener;
        }

        public void action( Context context, Uri uri, String outputPath ) {
            appName = getApplicationName( context );
            new CloudDownloadTask( context, uri, outputPath ).execute( );
        }

        protected class CloudDownloadTask extends AsyncTask< Void, Void, String[] > {

            Context context;
            Uri uri;
            String outputPath;

            public CloudDownloadTask( Context context, Uri uri, String outputPath ) {
                this.context = context;
                this.uri = uri;
                this.outputPath = outputPath;
            }

            @Override
            protected void onPreExecute( ) {
                super.onPreExecute( );
            }

            @Override
            protected String[] doInBackground( Void... arg0 ) {
                Log.e( "FileManager", "CloudDownloader doing!" );
                makeDirectory( Environment.getExternalStorageDirectory( ).getAbsolutePath( ) + "/" + appName + "/" );
                File file = new File( outputPath );

                OutputStream out = null;
                InputStream in = null;
                try {
                    in = context.getContentResolver( ).openInputStream( uri );
                    try {
                        out = new FileOutputStream( file );
                        byte[] buf = new byte[ 1024 ];
                        int len;
                        long buffLen = 0;
                        while ( ( len = in.read( buf ) ) > 0 ) {
                            out.write( buf, 0, len );
                            buffLen += len;
                        }
                        return new String[]{ outputPath };
                    } catch ( Exception e ) {
                        e.printStackTrace( );
                    } finally {
                        try {
                            if ( out != null ) {
                                out.close( );
                            }
                            in.close( );
                        } catch ( IOException e ) {
                            e.printStackTrace( );
                        }
                    }
                } catch ( FileNotFoundException e ) {
                }
                return new String[]{ };
            }

            @Override
            protected void onProgressUpdate( Void... values ) {
                super.onProgressUpdate( values );
            }

            @Override
            protected void onPostExecute( String[] result ) {

                if ( result.length > 0 ) {
                    if ( onCloudDownloadListener != null ) {
                        onCloudDownloadListener.onDownload( true, outputPath );
                    }
                } else {
                    if ( onCloudDownloadListener != null ) {
                        onCloudDownloadListener.onDownload( false, outputPath );
                    }
                }
            }
        }

        public interface OnCloudDownloadListener {
            void onDownload( boolean success, String filePath );
        }

    }

    public static File makeDirectory( String dir_path ) {
        File dir = new File( dir_path );
        if ( !dir.exists( ) ) {
            dir.mkdirs( );
        }
        return dir;
    }

    public static long parseLong( String string ) {
        try {
            return Long.parseLong( string );
        } catch ( NumberFormatException | NullPointerException e ) {
            return 0;
        }
    }


}
