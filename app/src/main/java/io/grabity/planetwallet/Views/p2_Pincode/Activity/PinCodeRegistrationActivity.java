package io.grabity.planetwallet.Views.p2_Pincode.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.managers.BioMetricManager;
import io.grabity.planetwallet.MiniFramework.utils.CornerRound;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyPairStore;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyValueStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.PlanetWalletViews.PWView;
import io.grabity.planetwallet.Widgets.ToolBar;


public class PinCodeRegistrationActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;

    private ArrayList< CircleImageView > indicatorViews;
    private ArrayList< FontTextView > numberButtons;
    private ArrayList< FontTextView > alphabetButtons;

    private ArrayList< String > inputKeyList;
    private String beforePinCode;

    private final static int INCORRECT_PIN = -1;
    private final static int REGISTRATION_PIN = 0;
    private final static int TRY_AGAIN = 1;
    private final static int NEW_PIN = 2;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pincode );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );

    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );

        setBarViewsCornerRadius( );

        viewMapper.viewTitleMargin.setVisibility( Utils.getScrennHeight( this ) <= 1920 ? View.GONE : View.VISIBLE );
        viewMapper.btnDeleteNumber.setOnClickListener( this );
        viewMapper.btnDeleteAlphabet.setOnClickListener( this );
        viewMapper.btnReset.setOnClickListener( this );

        indicatorViews = Utils.getAllViewsFromParentView( viewMapper.groupIndicators, CircleImageView.class );
        numberButtons = Utils.getAllViewsFromParentView( viewMapper.groupNumberPad, FontTextView.class );
        alphabetButtons = Utils.getAllViewsFromParentView( viewMapper.groupAlphabetPad, FontTextView.class );

        Collections.shuffle( numberButtons );
        Collections.shuffle( alphabetButtons );

        if ( getRequestCode( ) == C.requestCode.SETTING_CHANGE_PINCODE ) {
            viewMapper.toolBar.addLeftButton( ToolBar.ButtonItem( !getCurrentTheme( ) ? R.drawable.image_toolbar_close_gray : R.drawable.image_toolbar_close_blue ).setTag( C.tag.TOOLBAR_CLOSE ) );
            viewMapper.toolBar.setOnToolBarClickListener( this );
            setMessage( true, NEW_PIN );
        } else {
            setMessage( true, REGISTRATION_PIN );
        }
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
        inputKeyList = new ArrayList<>( );

        setIndicatorView( );
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_CLOSE ) ) {
            super.onBackPressed( );
        }
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnDeleteNumber || v == viewMapper.btnDeleteAlphabet ) {

            if ( inputKeyList.size( ) > 0 ) {
                inputKeyList.remove( inputKeyList.size( ) - 1 );

                setIndicatorView( );
            }

        } else if ( v == viewMapper.btnReset ) {

            viewMapper.btnReset.setVisibility( View.GONE );

            beforePinCode = null;
            inputKeyList.clear( );

            setIndicatorView( );
            setMessage( true, getRequestCode( ) == C.requestCode.SETTING_CHANGE_PINCODE ? NEW_PIN : REGISTRATION_PIN );

        } else {

            if ( v.getTag( ) != null ) {

                inputKeyList.add( String.valueOf( v.getTag( ) ) );
                setMessage( true, beforePinCode == null ? ( getRequestCode( ) == C.requestCode.SETTING_CHANGE_PINCODE ? NEW_PIN : REGISTRATION_PIN ) : TRY_AGAIN );

                if ( inputKeyList.size( ) == indicatorViews.size( ) ) {

                    if ( beforePinCode == null ) {

                        beforePinCode = Utils.join( inputKeyList );
                        inputKeyList.clear( );
                        viewMapper.btnReset.setVisibility( View.VISIBLE );
                        setMessage( true, TRY_AGAIN );

                    } else {

                        if ( Utils.equals( beforePinCode, Utils.join( inputKeyList ) ) ) {

                            switchAction( );

                        } else {

                            inputKeyList.clear( );
                            setMessage( false, INCORRECT_PIN );

                        }
                    }
                }
                setIndicatorView( );

            }
        }
    }

    private void setMessage( boolean isPassword, int messageId ) {
        if ( isPassword ) {

            switch ( messageId ) {
                case REGISTRATION_PIN:
                    viewMapper.textTitle.setText( localized( R.string.pincode_registration_registration_code_title ) );
                    break;
                case TRY_AGAIN:
                    viewMapper.textTitle.setText( localized( R.string.pincode_registration_one_more_registration_title ) );
                    break;
                case NEW_PIN:
                    viewMapper.textTitle.setText( localized( R.string.pincode_registration_change_pin_code_title ) );
                    break;
            }
            viewMapper.textSubtitle.setText( localized( R.string.pincode_registration_sub_title ) );

            viewMapper.textTitle.setTextColor( getCurrentTheme( ) ? Color.parseColor( "#000000" ) : Color.parseColor( "#FFFFFF" ) );
            viewMapper.textSubtitle.setTextColor( getCurrentTheme( ) ? Color.parseColor( "#aaaaaa" ) : Color.parseColor( "#5C5964" ) );

        } else {

            viewMapper.textTitle.setText( localized( R.string.pincode_registration_code_incorrect_title ) );
            viewMapper.textSubtitle.setText( localized( R.string.pincode_registration_sub_title_error ) );

            viewMapper.textTitle.setTextColor( Color.parseColor( "#FF0050" ) );
            viewMapper.textSubtitle.setTextColor( Color.parseColor( "#FF0050" ) );

        }
    }

    private void setIndicatorView( ) {
        for ( int i = 0; i < indicatorViews.size( ); i++ ) {
            if ( i < inputKeyList.size( ) ) {
                indicatorViews.get( i ).animate( ).alpha( 1.0f ).setDuration( 100 ).start( );
            } else {
                indicatorViews.get( i ).setAlpha( 0.0f );
            }
        }
        viewMapper.groupNumberPad.setVisibility( inputKeyList.size( ) < 4 ? View.VISIBLE : View.GONE );
        viewMapper.groupAlphabetPad.setVisibility( viewMapper.groupNumberPad.getVisibility( ) == View.VISIBLE ? View.GONE : View.VISIBLE );
    }

    private void setBarViewsCornerRadius( ) {
        ArrayList< PWView > barViews = Utils.getAllViewsFromParentView( viewMapper.groupIndicators, PWView.class );
        float dp = Utils.dpToPx( this, 2 );
        for ( PWView view : barViews ) {
            CornerRound.radius( view, dp, dp, dp, dp, dp, dp, dp, dp );
        }
    }

    private void switchAction( ) {

        if ( getRequestCode( ) == C.requestCode.SETTING_CHANGE_PINCODE ) {

            char[] beforeValue = getIntent( ).getCharArrayExtra( C.bundleKey.PINCODE );
            char[] newValue = Utils.join( inputKeyList ).toCharArray( );

            KeyPairStore.getInstance( ).changePWDBKeyPairs( beforeValue, newValue );
            KeyValueStore.getInstance( ).setValue( C.pref.PASSWORD, Utils.sha256( Utils.join( inputKeyList ) ), newValue );
            getPlanetWalletApplication( ).setPINCODE( newValue );

            BioMetricManager.getInstance().saveKey( newValue );

            setResult( RESULT_OK );
            super.onBackPressed( );

        } else {

            char[] newValue = Utils.join( inputKeyList ).toCharArray( );

            KeyValueStore.getInstance( ).setValue( C.pref.PASSWORD, Utils.sha256( Utils.join( inputKeyList ) ), newValue );
            getPlanetWalletApplication( ).setPINCODE( newValue );

            setTransition( Transition.NO_ANIMATION );
            sendAction( PinCodeCertificationActivity.class );
            finish( );
        }

    }

    @Override
    public void onBackPressed( ) {
        if ( inputKeyList.size( ) == 0 ) {
            if ( getRequestCode( ) < 0 ) {
                finish( );
            } else {
                super.onBackPressed( );
            }
        } else {
            inputKeyList.remove( inputKeyList.size( ) - 1 );
            setIndicatorView( );
        }
    }

    public class ViewMapper {

        ToolBar toolBar;

        View viewTitleMargin;

        ViewGroup groupIndicators;
        ViewGroup groupNumberPad;
        ViewGroup groupAlphabetPad;

        View btnDeleteNumber;
        View btnDeleteAlphabet;

        FontTextView textTitle;
        FontTextView textSubtitle;

        View btnReset;

        public ViewMapper( ) {

            toolBar = findViewById( R.id.toolBar );

            viewTitleMargin = findViewById( R.id.view_pin_code_title_margin );
            groupIndicators = findViewById( R.id.group_pin_code_indicator );

            groupNumberPad = findViewById( R.id.group_pin_code_number_pad );
            groupAlphabetPad = findViewById( R.id.group_pin_code_alphabet_pad );

            btnDeleteNumber = findViewById( R.id.btn_pincode_registration_numberdelete );
            btnDeleteAlphabet = findViewById( R.id.btn_pincode_registration_alphabetdelete );

            textTitle = findViewById( R.id.text_pin_code_title );
            textSubtitle = findViewById( R.id.text_pin_code_subtitle );

            btnReset = findViewById( R.id.btn_pin_code_reset );
        }

    }
}