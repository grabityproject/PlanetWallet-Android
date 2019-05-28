package io.grabity.planetwallet.Views.p2_Pincode.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p3_Wallet.Activity.WalletAddActivity;
import io.grabity.planetwallet.Widgets.DotView;
import io.grabity.planetwallet.Widgets.RoundButton.RoundButton;


public class PinCodeRegistrationActivity extends PlanetWalletActivity {

    private ViewMapper viewMapper;
    private ArrayList< DotView > passwordViews;
    private ArrayList< TextView > numberButtons;
    private ArrayList< TextView > alphabetButtons;

    private ArrayList< String > keyList;
    private String strKeyList;


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pincode_registration );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );

    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );

        viewMapper.btnDeleteNumber.setOnClickListener( this );
        viewMapper.btnDeleteAlphabet.setOnClickListener( this );

        passwordViews = Utils.getAllViewsFromParentView( viewMapper.inputPassword, DotView.class );
        numberButtons = Utils.getAllViewsFromParentView( viewMapper.inputNumber, TextView.class );
        alphabetButtons = Utils.getAllViewsFromParentView( viewMapper.inputAlphabet, TextView.class );
        Collections.shuffle( numberButtons );
        Collections.shuffle( alphabetButtons );

    }

    @Override
    protected void setData( ) {
        super.setData( );
        for ( int i = 0; i < numberButtons.size( ); i++ ) {
            numberButtons.get( i ).setText( String.valueOf( i ) );
            numberButtons.get( i ).setTag( String.valueOf( i ) );
            numberButtons.get( i ).setOnClickListener( this );
        }
        for ( int i = 0; i < alphabetButtons.size( ); i++ ) {
            alphabetButtons.get( i ).setText( Character.toString( ( char ) ( i + 65 ) ) );
            alphabetButtons.get( i ).setTag( Character.toString( ( char ) ( i + 65 ) ) );
            alphabetButtons.get( i ).setOnClickListener( this );
        }
        keyList = new ArrayList<>( );

    }


    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnDeleteNumber || v == viewMapper.btnDeleteAlphabet ) {
            if ( keyList.size( ) > 0 ) {
                keyList.remove( keyList.size( ) - 1 );
                setPasswordView( );
            }
        } else {
            String tag = String.valueOf( v.getTag( ) );
            if ( tag != null ) {
                keyList.add( tag );
                setPasswordMessage( true );

                if ( keyList.size( ) == 5 ){
                    //Todo 임시작업
                    StringBuffer stringBuffer = new StringBuffer( );
                    for ( int i = 0; i < keyList.size( ); i++ ) {
                        stringBuffer.append( keyList.get( i ) );
                    }
                    strKeyList = stringBuffer.toString( );

                    if ( Utils.getPreferenceData( this , C.pref.PASSWORD ).length( ) == 0 ) {
                        Utils.setPreferenceData( this, C.pref.PASSWORD, strKeyList );
                        strKeyList = null;
                        keyList.clear( );
                    }else{
                        if ( Utils.equals( Utils.getPreferenceData( this , C.pref.PASSWORD ), strKeyList ) ){
                            sendAction( WalletAddActivity.class );
                            finish( );
                        }else{
                            keyList.clear( );
                            setPasswordMessage( false );
                        }
                    }
                }
                setPasswordView( );
            }
        }
    }

    void setPasswordView( ) {
        for ( int i = 0; i < passwordViews.size( ); i++ ) {
            LinearLayout.LayoutParams params;
            if ( i < keyList.size( ) ) {
                params = new LinearLayout.LayoutParams( ( int ) Utils.dpToPx( this, 14 ), ( int ) Utils.dpToPx( this, 14 ) );
            } else {
                params = new LinearLayout.LayoutParams( ( int ) Utils.dpToPx( this, 14 ), ( int ) Utils.dpToPx( this, 2 ) );
            }
            params.rightMargin = ( int ) Utils.dpToPx( this, 12 );
            passwordViews.get( i ).setLayoutParams( params );
            passwordViews.get( i ).setDotColor( i < keyList.size( ) ? Color.parseColor( "#FFFFFF" ) : Color.parseColor( "#5C5964" ) );
            viewMapper.inputNumber.setVisibility( keyList.size( ) <= 3 ? View.VISIBLE : View.GONE );
            viewMapper.inputAlphabet.setVisibility( keyList.size( ) >= 4 ? View.VISIBLE : View.GONE );
        }
    }

    void setPasswordMessage( boolean check ) {
        viewMapper.passwordTitle.setText( check ? "Verification Code" : "Code incorrect" );
        viewMapper.passwordSubtitle.setText( check ? "Enter the 4 digit + alphabet" : "Please check your code" );
        viewMapper.passwordTitle.setTextColor( check ? Color.parseColor( "#FFFFFF" ) : Color.parseColor( "#FF0050" ) );
        viewMapper.passwordSubtitle.setTextColor( check ? Color.parseColor( "#5C5964" ) : Color.parseColor( "#FF0050" ) );
    }

    @Override
    public void onBackPressed( ) {
        if ( keyList.size( ) == 0 ) {
            super.onBackPressed( );
        } else {
            keyList.remove( keyList.size( ) - 1 );
            setPasswordView( );
        }
    }

    public class ViewMapper {

        ViewGroup inputPassword;
        ViewGroup inputNumber;
        ViewGroup inputAlphabet;

        View btnDeleteNumber;
        View btnDeleteAlphabet;

        TextView passwordTitle;
        TextView passwordSubtitle;

        public ViewMapper( ) {

            inputPassword = findViewById( R.id.group_pincode_registration_inputpassword );
            inputNumber = findViewById( R.id.group_pincode_registration_inputnumber );
            inputAlphabet = findViewById( R.id.group_pincode_registration_inputalphabet );

            btnDeleteNumber = findViewById( R.id.btn_pincode_registration_numberdelete );
            btnDeleteAlphabet = findViewById( R.id.btn_pincode_registration_alphabetdelete );

            passwordTitle = findViewById( R.id.text_pincode_registration_verificationcode );
            passwordSubtitle = findViewById( R.id.text_pincode_registration_specification );


        }

    }
}