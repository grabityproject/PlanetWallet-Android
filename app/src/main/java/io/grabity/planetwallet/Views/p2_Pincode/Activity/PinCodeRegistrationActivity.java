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
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.RoundButton.RoundButton;


public class PinCodeRegistrationActivity extends PlanetWalletActivity {

    private ViewMapper viewMapper;
    private ArrayList< DotView > passwordViews;
    private ArrayList< FontTextView > numberButtons;
    private ArrayList< FontTextView > alphabetButtons;

    private ArrayList< String > keyList;
    private String strKeyList;
    private String checkKeyList;


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
        numberButtons = Utils.getAllViewsFromParentView( viewMapper.inputNumber, FontTextView.class );
        alphabetButtons = Utils.getAllViewsFromParentView( viewMapper.inputAlphabet, FontTextView.class );
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

        setPasswordView( );

        viewMapper.passwordTitle.setText( "Verification Code" );
        viewMapper.passwordSubtitle.setText( "Enter the 4 digit + alphabet" );
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

                if ( keyList.size( ) == 5 && strKeyList == null ) {
                    //Todo 임시작업
                    StringBuffer stringBuffer = new StringBuffer( );
                    for ( int i = 0; i < keyList.size( ); i++ ) {
                        stringBuffer.append( keyList.get( i ) );
                    }
                    strKeyList = stringBuffer.toString( );
                    checkKeyList = stringBuffer.toString( );
                    keyList.clear( );
                } else if ( keyList.size( ) == 5 && checkKeyList != null ) {
                    StringBuffer stringBuffer = new StringBuffer( );
                    for ( int i = 0; i < keyList.size( ); i++ ) {
                        stringBuffer.append( keyList.get( i ) );
                    }
                    strKeyList = stringBuffer.toString( );

                    if ( strKeyList.equals( checkKeyList ) ) {
                        Utils.setPreferenceData( this, C.pref.PASSWORD, strKeyList );
                        setTransition( Transition.NO_ANIMATION );
                        sendAction( PinCodeCertificationActivity.class );
                        finish( );
                    } else {
                        keyList.clear( );
                        setPasswordMessage( false );
                    }

                }
                setPasswordView( );
            }
        }
    }

    void setPasswordMessage( boolean check ) {
        viewMapper.passwordTitle.setText( check ? "Verification Code" : "Code incorrect" );
        viewMapper.passwordSubtitle.setText( check ? "Enter the 4 digit + alphabet" : "Please check your code" );

        if ( !getPlanetWalletApplication( ).getCurrentTheme( ) ) {
            viewMapper.passwordTitle.setTextColor( check ? Color.parseColor( "#FFFFFF" ) : Color.parseColor( "#FF0050" ) );
        } else {
            viewMapper.passwordTitle.setTextColor( check ? Color.parseColor( "#000000" ) : Color.parseColor( "#FF0050" ) );
        }
        viewMapper.passwordSubtitle.setTextColor( check ? Color.parseColor( "#5C5964" ) : Color.parseColor( "#FF0050" ) );
    }

    void setPasswordView( ) {
        for ( int i = 0; i < passwordViews.size( ); i++ ) {
            LinearLayout.LayoutParams params;
            if ( i < keyList.size( ) ) {
                params = new LinearLayout.LayoutParams( ( int ) Utils.dpToPx( this, 14 ), ( int ) Utils.dpToPx( this, 14 ) );
            } else {
                params = new LinearLayout.LayoutParams( ( int ) Utils.dpToPx( this, 14 ), ( int ) Utils.dpToPx( this, 2 ) );

                passwordViews.get( i ).setDotColor( !getPlanetWalletApplication( ).getCurrentTheme( ) ? Color.parseColor( "#5C5964" ) : Color.parseColor( "#BCBDD5" ) );
                viewMapper.decorationViewHeight.setBackgroundColor( !getPlanetWalletApplication( ).getCurrentTheme( ) ? Color.parseColor( "#5C5964" ) : Color.parseColor( "#BCBDD5" ) );
                viewMapper.decorationViewWidth.setBackgroundColor( !getPlanetWalletApplication( ).getCurrentTheme( ) ? Color.parseColor( "#5C5964" ) : Color.parseColor( "#BCBDD5" ) );

            }
            params.rightMargin = ( int ) Utils.dpToPx( this, 12 );
            passwordViews.get( i ).setLayoutParams( params );

            if ( !getPlanetWalletApplication( ).getCurrentTheme( ) ) {
                passwordViews.get( i ).setDotColor( i < keyList.size( ) ? Color.parseColor( "#FFFFFF" ) : Color.parseColor( "#5C5964" ) );
            } else {
                passwordViews.get( i ).setDotColor( i < keyList.size( ) ? Color.parseColor( "#000000" ) : Color.parseColor( "#BCBDD5" ) );
            }

            viewMapper.inputNumber.setVisibility( keyList.size( ) <= 3 ? View.VISIBLE : View.GONE );
            viewMapper.inputAlphabet.setVisibility( keyList.size( ) >= 4 ? View.VISIBLE : View.GONE );
        }
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

        FontTextView passwordTitle;
        FontTextView passwordSubtitle;

        View decorationViewWidth;
        View decorationViewHeight;

        public ViewMapper( ) {

            inputPassword = findViewById( R.id.group_pincode_registration_inputpassword );
            inputNumber = findViewById( R.id.group_pincode_registration_inputnumber );
            inputAlphabet = findViewById( R.id.group_pincode_registration_inputalphabet );

            btnDeleteNumber = findViewById( R.id.btn_pincode_registration_numberdelete );
            btnDeleteAlphabet = findViewById( R.id.btn_pincode_registration_alphabetdelete );

            passwordTitle = findViewById( R.id.text_pincode_registration_verificationcode );
            passwordSubtitle = findViewById( R.id.text_pincode_registration_specification );

            decorationViewHeight = findViewById( R.id.view_pincode_registration_plus_height );
            decorationViewWidth = findViewById( R.id.view_pincode_registration_plus_width );


        }

    }
}