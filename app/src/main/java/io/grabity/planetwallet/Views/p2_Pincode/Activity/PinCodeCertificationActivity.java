package io.grabity.planetwallet.Views.p2_Pincode.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.biometric.BioMetricManager;
import io.grabity.planetwallet.MiniFramework.utils.CornerRound;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyValueStore;
import io.grabity.planetwallet.MiniFramework.wallet.store.PlanetStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p3_Wallet.Activity.PlanetGenerateActivity;
import io.grabity.planetwallet.Views.p4_Main.Activity.MainActivity;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.RoundRelativeLayout;
import io.grabity.planetwallet.Widgets.ToolBar;


public class PinCodeCertificationActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, BioMetricManager.OnBioResultListener {

    private ViewMapper viewMapper;
    private ArrayList< RoundRelativeLayout > passwordViews;
    private ArrayList< FontTextView > numberButtons;
    private ArrayList< FontTextView > alphabetButtons;

    private ArrayList< String > keyList;
    private String strKeyList;
    private long backPressedTime = 0;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pincode_certification );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );

        BioMetricManager.Init( this, this );

        if ( Utils.getScrennHeight( this ) <= 1920 ) {
            viewMapper.passwordTitle.getViewTreeObserver( ).addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener( ) {
                @Override
                public void onGlobalLayout( ) {
                    viewMapper.passwordTitle.getViewTreeObserver( ).removeOnGlobalLayoutListener( this );
                    viewMapper.groupPasswordView.setPadding( ( int ) Utils.dpToPx( PinCodeCertificationActivity.this, 42 ), ( int ) Utils.dpToPx( PinCodeCertificationActivity.this, 42 ), ( int ) Utils.dpToPx( PinCodeCertificationActivity.this, 42 ), ( int ) Utils.dpToPx( PinCodeCertificationActivity.this, 42 ) );
                    viewMapper.passwordTitle.setPadding( 0, 0, 0, 0 );
                    viewMapper.groupPasswordView.requestLayout( );
                    viewMapper.passwordTitle.requestLayout( );

                }
            } );
        }


        viewMapper.btnDeleteNumber.setOnClickListener( this );
        viewMapper.btnDeleteAlphabet.setOnClickListener( this );

        passwordViews = Utils.getAllViewsFromParentView( viewMapper.inputPassword, RoundRelativeLayout.class );
        numberButtons = Utils.getAllViewsFromParentView( viewMapper.inputNumber, FontTextView.class );
        alphabetButtons = Utils.getAllViewsFromParentView( viewMapper.inputAlphabet, FontTextView.class );

        Collections.shuffle( numberButtons );
        Collections.shuffle( alphabetButtons );

        if ( getRequestCode( ) == C.requestCode.SETTING_CHANGE_PINCODE || getRequestCode( ) == C.requestCode.TRANSFER || getRequestCode( ) == C.requestCode.PLANET_PRIVATEKEY_EXPORT || getRequestCode( ) == C.requestCode.PLANET_MNEMONIC_EXPORT || getRequestCode( ) == C.requestCode.BIO_METRIC ) {
            viewMapper.toolBar.addLeftButton( new ToolBar.ButtonItem( !getCurrentTheme( ) ? R.drawable.image_toolbar_close_gray : R.drawable.image_toolbar_close_blue ).setTag( C.tag.TOOLBAR_CLOSE ) );
            viewMapper.toolBar.setOnToolBarClickListener( this );
        }

        if ( getRequestCode( ) != C.requestCode.BIO_METRIC && Utils.equals( Utils.getPreferenceData( this, C.pref.BIO_METRIC, String.valueOf( false ) ), String.valueOf( true ) ) ) {
            BioMetricManager.getInstance( ).startAuth( );
        }
        //Todo 지문인식이 적용된 후에 핀코드 변경을 누르면 어떻게 처리할까요

    }

    @Override
    public void onBioResult( boolean isResult, Object data ) {
        PLog.e( "onBioResult : " + isResult );
        if ( isResult ) {
            sendAction( MainActivity.class );
            finish( );
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
        keyList = new ArrayList<>( );

        setPasswordView( );
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
            if ( keyList.size( ) > 0 ) {
                keyList.remove( keyList.size( ) - 1 );
                setPasswordView( );
            }
        } else {
            String tag = String.valueOf( v.getTag( ) );
            if ( v.getTag( ) != null ) {
                keyList.add( tag );
                setPasswordMessage( true );

                if ( keyList.size( ) == 5 ) {
                    StringBuilder stringBuffer = new StringBuilder( );
                    for ( int i = 0; i < keyList.size( ); i++ ) {
                        stringBuffer.append( keyList.get( i ) );
                    }
                    strKeyList = stringBuffer.toString( );

                    if ( Utils.equals( Utils.sha256( strKeyList ), KeyValueStore.getInstance( ).getValue( C.pref.PASSWORD, strKeyList.toCharArray( ) ) ) ) {

                        getPlanetWalletApplication( ).setPINCODE( strKeyList.toCharArray( ) );

                        if ( getRequestCode( ) == C.requestCode.PLANET_MNEMONIC_EXPORT || getRequestCode( ) == C.requestCode.PLANET_PRIVATEKEY_EXPORT ) {

                            Intent intent = new Intent( );
                            intent.putExtra( C.bundleKey.PINCODE, strKeyList.toCharArray( ) );
                            setResult( RESULT_OK, intent );

                        } else if ( getRequestCode( ) == C.requestCode.SETTING_CHANGE_PINCODE ) {

                            Bundle bundle = new Bundle( );
                            bundle.putCharArray( C.bundleKey.PINCODE, strKeyList.toCharArray( ) );
                            setTransition( Transition.SLIDE_UP );
                            sendAction( C.requestCode.SETTING_CHANGE_PINCODE, PinCodeRegistrationActivity.class, bundle );
                            return;

                        } else if ( getRequestCode( ) == C.requestCode.PINCODE_IS_NULL ) {

                            setResult( RESULT_OK );

                        } else if ( getRequestCode( ) == C.requestCode.TRANSFER ) {

                            setResult( RESULT_OK );

                        } else if ( getRequestCode( ) == C.requestCode.BIO_METRIC ) {
                            if ( Utils.equals( Utils.getPreferenceData( this, C.pref.BIO_METRIC, String.valueOf( false ) ), String.valueOf( false ) ) ) {
                                Utils.setPreferenceData( this, C.pref.BIO_METRIC, String.valueOf( true ) );
                            } else {
                                Utils.setPreferenceData( this, C.pref.BIO_METRIC, String.valueOf( false ) );
                            }
                            setResult( RESULT_OK );
                        } else {

                            if ( PlanetStore.getInstance( ).getPlanetList( ).size( ) == 0 ) {

                                Bundle bundle = new Bundle( );
                                bundle.putCharArray( C.bundleKey.PINCODE, strKeyList.toCharArray( ) );
                                sendAction( PlanetGenerateActivity.class, bundle );

                            } else {

                                sendAction( MainActivity.class );

                            }

                        }
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

    void setPasswordMessage( boolean isPassword ) {
        viewMapper.passwordTitle.setText( isPassword ? localized( R.string.pincode_certification_verification_code_title ) : localized( R.string.pincode_certification_code_incorrect_title ) );
        viewMapper.passwordSubtitle.setText( isPassword ? localized( R.string.pincode_certification_sub_title ) : localized( R.string.pincode_certification_sub_title_error ) );

        if ( !getPlanetWalletApplication( ).getCurrentTheme( ) ) {
            viewMapper.passwordTitle.setTextColor( isPassword ? Color.parseColor( "#FFFFFF" ) : Color.parseColor( "#FF0050" ) );
        } else {
            viewMapper.passwordTitle.setTextColor( isPassword ? Color.parseColor( "#000000" ) : Color.parseColor( "#FF0050" ) );
        }
        viewMapper.passwordSubtitle.setTextColor( isPassword ? Color.parseColor( !getCurrentTheme( ) ? "#5C5964" : "#aaaaaa" ) : Color.parseColor( "#FF0050" ) );
    }

    void setPasswordView( ) {
        for ( int i = 0; i < passwordViews.size( ); i++ ) {
            LinearLayout.LayoutParams params;
            if ( i < keyList.size( ) ) {
                params = new LinearLayout.LayoutParams( ( int ) Utils.dpToPx( this, 16 ), ( int ) Utils.dpToPx( this, 16 ) );
            } else {
                params = new LinearLayout.LayoutParams( ( int ) Utils.dpToPx( this, 16 ), ( int ) Utils.dpToPx( this, 2 ) );

                passwordViews.get( i ).setBackground_color_normal( !getPlanetWalletApplication( ).getCurrentTheme( ) ? Color.parseColor( "#5C5964" ) : Color.parseColor( "#BCBDD5" ) );
                viewMapper.decorationViewHeight.setBackgroundColor( !getPlanetWalletApplication( ).getCurrentTheme( ) ? Color.parseColor( "#5C5964" ) : Color.parseColor( "#BCBDD5" ) );
                viewMapper.decorationViewWidth.setBackgroundColor( !getPlanetWalletApplication( ).getCurrentTheme( ) ? Color.parseColor( "#5C5964" ) : Color.parseColor( "#BCBDD5" ) );
            }
            params.rightMargin = ( int ) Utils.dpToPx( this, 12 );
            passwordViews.get( i ).setLayoutParams( params );

            if ( !getPlanetWalletApplication( ).getCurrentTheme( ) ) {
                passwordViews.get( i ).setBackground_color_normal( i < keyList.size( ) ? Color.parseColor( "#FFFFFF" ) : Color.parseColor( "#5C5964" ) );
            } else {
                passwordViews.get( i ).setBackground_color_normal( i < keyList.size( ) ? Color.parseColor( "#000000" ) : Color.parseColor( "#BCBDD5" ) );
            }

            viewMapper.inputNumber.setVisibility( keyList.size( ) <= 3 ? View.VISIBLE : View.GONE );
            viewMapper.inputAlphabet.setVisibility( keyList.size( ) >= 4 ? View.VISIBLE : View.GONE );

            float dp = Utils.dpToPx( this, 2 );
            CornerRound.radius( viewMapper.decorationViewHeight, dp, dp, dp, dp, dp, dp, dp, dp );
            CornerRound.radius( viewMapper.decorationViewWidth, dp, dp, dp, dp, dp, dp, dp, dp );
        }
    }

    @Override
    public void onBackPressed( ) {
        if ( keyList.size( ) == 0 ) {
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
            keyList.remove( keyList.size( ) - 1 );
            setPasswordView( );
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


    public class ViewMapper {

        ToolBar toolBar;

        ViewGroup groupPasswordView;
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

            toolBar = findViewById( R.id.toolBar );

            groupPasswordView = findViewById( R.id.group_pincode_certification_inputpassword_wrapper );

            inputPassword = findViewById( R.id.group_pincode_certification_inputpassword );
            inputNumber = findViewById( R.id.group_pincode_certification_inputnumber );
            inputAlphabet = findViewById( R.id.group_pincode_certification_inputalphabet );

            btnDeleteNumber = findViewById( R.id.group_pincode_certification_numberdelete );
            btnDeleteAlphabet = findViewById( R.id.btn_pincode_certification_alphabetdelete );

            passwordTitle = findViewById( R.id.text_pincode_certification_verificationcode );
            passwordSubtitle = findViewById( R.id.text_pincode_certification_specification );

            decorationViewHeight = findViewById( R.id.view_pincode_certification_plus_height );
            decorationViewWidth = findViewById( R.id.view_pincode_certification_plus_width );

        }

    }
}