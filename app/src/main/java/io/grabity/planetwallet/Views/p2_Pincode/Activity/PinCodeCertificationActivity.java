package io.grabity.planetwallet.Views.p2_Pincode.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.managers.BioMetricManager;
import io.grabity.planetwallet.MiniFramework.utils.CornerRound;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyValueStore;
import io.grabity.planetwallet.MiniFramework.wallet.store.PlanetStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p3_Wallet.Activity.PlanetGenerateActivity;
import io.grabity.planetwallet.Views.p4_Main.Activity.MainActivity;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.PlanetWalletViews.PWView;
import io.grabity.planetwallet.Widgets.ToolBar;


public class PinCodeCertificationActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, BioMetricManager.OnBioAuthListener {

    private ViewMapper viewMapper;

    private ArrayList< CircleImageView > indicatorViews;
    private ArrayList< FontTextView > numberButtons;
    private ArrayList< FontTextView > alphabetButtons;

    private ArrayList< String > inputKeyList;

    private long backPressedTime = 0;
    private char[] pinCode;

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

        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.viewTitleMargin.setVisibility( Utils.getScrennHeight( this ) <= 1920 ? View.GONE : View.VISIBLE );
        viewMapper.btnDeleteNumber.setOnClickListener( this );
        viewMapper.btnDeleteAlphabet.setOnClickListener( this );
        viewMapper.btnReset.setOnClickListener( this );

        indicatorViews = Utils.getAllViewsFromParentView( viewMapper.groupIndicators, CircleImageView.class );
        numberButtons = Utils.getAllViewsFromParentView( viewMapper.groupNumberPad, FontTextView.class );
        alphabetButtons = Utils.getAllViewsFromParentView( viewMapper.groupAlphabetPad, FontTextView.class );

        Collections.shuffle( numberButtons );
        Collections.shuffle( alphabetButtons );

        if ( getRequestCode( ) == C.requestCode.SETTING_CHANGE_PINCODE ||
                getRequestCode( ) == C.requestCode.TRANSFER ||
                getRequestCode( ) == C.requestCode.PLANET_PRIVATEKEY_EXPORT ||
                getRequestCode( ) == C.requestCode.PLANET_MNEMONIC_EXPORT ||
                getRequestCode( ) == C.requestCode.BIO_METRIC ) {
            viewMapper.toolBar.addLeftButton( ToolBar.ButtonItem( !getCurrentTheme( ) ? R.drawable.image_toolbar_close_gray : R.drawable.image_toolbar_close_blue ).setTag( C.tag.TOOLBAR_CLOSE ) );
        }

        setTitleMessage( true );
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
        setBioMetric( );
    }

    private void setBioMetric( ) {
        if ( getRequestCode( ) != C.requestCode.BIO_METRIC &&
                Utils.equals( Utils.getPreferenceData( this, C.pref.BIO_METRIC, String.valueOf( false ) ), String.valueOf( true ) ) ) {

            if ( getRequestCode( ) == C.requestCode.SETTING_CHANGE_PINCODE ) return;

            if ( !BioMetricManager.getInstance( ).isFingerPrintCheck( this ) ) {
                Utils.setPreferenceData( this, C.pref.BIO_METRIC, String.valueOf( false ) );
                BioMetricManager.getInstance( ).removeKey( );
            } else {
                BioMetricManager.getInstance( ).setOnBioAuthListener( this ).startAuth( this );
            }
        }
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

        } else {

            if ( v.getTag( ) != null ) {

                inputKeyList.add( String.valueOf( v.getTag( ) ) );
                setTitleMessage( true );

                if ( inputKeyList.size( ) == indicatorViews.size( ) ) {

                    char[] insertValue = Utils.join( inputKeyList ).toCharArray( );

                    if ( Utils.equals( Utils.sha256( Utils.join( inputKeyList ) ), KeyValueStore.getInstance( ).getValue( C.pref.PASSWORD, insertValue ) ) ) {

                        getPlanetWalletApplication( ).setPINCODE( insertValue );
                        pinCode = insertValue;
                        switchAction( );

                    } else {
                        inputKeyList.clear( );
                        setTitleMessage( false );
                    }

                }

            }
            setIndicatorView( );
        }
    }

    private void setTitleMessage( boolean isPassword ) {
        if ( isPassword ) {

            viewMapper.textTitle.setText( localized( R.string.pincode_certification_verification_code_title ) );
            viewMapper.textSubtitle.setText( localized( R.string.pincode_certification_sub_title ) );

            viewMapper.textTitle.setTextColor( getCurrentTheme( ) ? Color.parseColor( "#000000" ) : Color.parseColor( "#FFFFFF" ) );
            viewMapper.textSubtitle.setTextColor( getCurrentTheme( ) ? Color.parseColor( "#aaaaaa" ) : Color.parseColor( "#5C5964" ) );

        } else {

            viewMapper.textTitle.setText( localized( R.string.pincode_certification_code_incorrect_title ) );
            viewMapper.textSubtitle.setText( localized( R.string.pincode_certification_sub_title_error ) );

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

        if ( pinCode != null ) {
            switch ( getRequestCode( ) ) {
                case C.requestCode.PLANET_MNEMONIC_EXPORT:
                case C.requestCode.PLANET_PRIVATEKEY_EXPORT: {

                    Intent intent = new Intent( );
                    intent.putExtra( C.bundleKey.PINCODE, pinCode );
                    setResult( RESULT_OK, intent );

                    break;
                }


                case C.requestCode.SETTING_CHANGE_PINCODE: {

                    Bundle bundle = new Bundle( );
                    bundle.putCharArray( C.bundleKey.PINCODE, pinCode );
                    setTransition( Transition.SLIDE_UP );
                    sendAction( C.requestCode.SETTING_CHANGE_PINCODE, PinCodeRegistrationActivity.class, bundle );

                    return;
                }


                case C.requestCode.PINCODE_IS_NULL:
                case C.requestCode.TRANSFER: {
                    setResult( RESULT_OK );
                    break;
                }


                case C.requestCode.BIO_METRIC: {

                    if ( Utils.equals( Utils.getPreferenceData( this, C.pref.BIO_METRIC, String.valueOf( false ) ), String.valueOf( false ) ) ) {
                        Utils.setPreferenceData( this, C.pref.BIO_METRIC, String.valueOf( true ) );
                        BioMetricManager.getInstance( ).generateSecretKey( );
                        BioMetricManager.getInstance( ).saveKey( pinCode );
                    } else {
                        Utils.setPreferenceData( this, C.pref.BIO_METRIC, String.valueOf( false ) );
                        BioMetricManager.getInstance( ).removeKey( );
                    }
                    setResult( RESULT_OK );

                    break;
                }

                default: {

                    if ( PlanetStore.getInstance( ).getPlanetList( ).size( ) == 0 ) {

                        Bundle bundle = new Bundle( );
                        bundle.putCharArray( C.bundleKey.PINCODE, pinCode );
                        sendAction( PlanetGenerateActivity.class, bundle );

                    } else {

                        sendAction( MainActivity.class );

                    }

                }
                break;
            }
            finish( );
        }
    }

    @Override
    public void onBackPressed( ) {
        if ( inputKeyList.size( ) == 0 ) {
            if ( getRequestCode( ) < 0 ) {
                finish( );
            } else {
                if ( getRequestCode( ) == C.requestCode.PINCODE_IS_NULL ) {
                    if ( System.currentTimeMillis( ) > backPressedTime + 2000 ) {
                        backPressedTime = System.currentTimeMillis( );
                        Toast.makeText( this, localized( R.string.main_back_pressed_finish_title ), Toast.LENGTH_SHORT ).show( );
                    } else {
                        this.finishAffinity( );
                        System.runFinalization( );
                        System.exit( 0 );
                    }
                } else {
                    super.onBackPressed( );
                }

            }

        } else {
            inputKeyList.remove( inputKeyList.size( ) - 1 );
            setIndicatorView( );
        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.SETTING_CHANGE_PINCODE && resultCode == RESULT_OK ) {
            setResult( RESULT_OK );
        } else if ( requestCode == C.requestCode.SETTING_CHANGE_PINCODE && resultCode == RESULT_CANCELED ) {
            setResult( RESULT_CANCELED );
        }
        super.onBackPressed( );
    }

    @Override
    public void onBioAuth( boolean isResult, char[] data ) {
        if ( isResult ) {
            getPlanetWalletApplication( ).setPINCODE( data );
            pinCode = data;
            switchAction( );
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