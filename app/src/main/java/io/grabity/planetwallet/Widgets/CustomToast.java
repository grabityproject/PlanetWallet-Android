package io.grabity.planetwallet.Widgets;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import io.grabity.planetwallet.R;

public class CustomToast extends View {

    private String s;

    public CustomToast( Context context ) {
        super( context );
    }

    public static CustomToast newInstance( Context context ) {
        CustomToast toast = new CustomToast( context );
        return toast;
    }

    public static CustomToast makeText( Context context, String text ) {
        CustomToast toast = new CustomToast( context );
        toast.setText( text );
        return toast;
    }

    public CustomToast setText( String s ) {
        this.s = s;
        return this;
    }

    public CustomToast show( ) {
        LayoutInflater inflater = ( LayoutInflater ) getContext( ).getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate( R.layout.toast, findViewById( R.id.group_toast ) );
        TextView textView = view.findViewById( R.id.text_toast_message );
        textView.setText( s != null ? s : "" );
        Toast toast = new Toast( getContext( ) );
        toast.setDuration( Toast.LENGTH_SHORT );
        toast.setView( view );
        toast.setGravity( Gravity.CENTER, 0, 0 );
        toast.show( );
        return this;
    }


}
