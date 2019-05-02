package io.grabity.planetwallet.MiniFramework.networktask;

public interface NetworkInterface {
    void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result );
}
