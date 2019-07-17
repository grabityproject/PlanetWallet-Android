package io.grabity.planetwallet.Views.p2_Pincode.Activity;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.CornerRound;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyPairStore;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyValueStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.RoundRelativeLayout;
import io.grabity.planetwallet.Widgets.ToolBar;


public class PinCodeRegistrationActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;
    private ArrayList< RoundRelativeLayout > passwordViews;
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

        if ( Utils.getScrennHeight( this ) <= 1920 ) {
            viewMapper.passwordTitle.getViewTreeObserver( ).addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener( ) {
                @Override
                public void onGlobalLayout( ) {
                    viewMapper.passwordTitle.getViewTreeObserver( ).removeOnGlobalLayoutListener( this );
                    viewMapper.groupPasswordView.setPadding( ( int ) Utils.dpToPx( PinCodeRegistrationActivity.this, 42 ), ( int ) Utils.dpToPx( PinCodeRegistrationActivity.this, 42 ), ( int ) Utils.dpToPx( PinCodeRegistrationActivity.this, 42 ), ( int ) Utils.dpToPx( PinCodeRegistrationActivity.this, 42 ) );
                    viewMapper.passwordTitle.setPadding( 0, 0, 0, 0 );
                    viewMapper.groupPasswordView.requestLayout( );
                    viewMapper.passwordTitle.requestLayout( );

                }
            } );
        }

        viewMapper.btnDeleteNumber.setOnClickListener( this );
        viewMapper.btnDeleteAlphabet.setOnClickListener( this );
        viewMapper.btnReset.setOnClickListener( this );

        passwordViews = Utils.getAllViewsFromParentView( viewMapper.inputPassword, RoundRelativeLayout.class );
        numberButtons = Utils.getAllViewsFromParentView( viewMapper.inputNumber, FontTextView.class );
        alphabetButtons = Utils.getAllViewsFromParentView( viewMapper.inputAlphabet, FontTextView.class );


        Collections.shuffle( numberButtons );
        Collections.shuffle( alphabetButtons );

        if ( getRequestCode( ) == C.requestCode.SETTING_CHANGE_PINCODE ) {
            viewMapper.toolBar.addLeftButton( new ToolBar.ButtonItem( !getCurrentTheme( ) ? R.drawable.image_toolbar_close_gray : R.drawable.image_toolbar_close_blue ).setTag( C.tag.TOOLBAR_CLOSE ) );
            viewMapper.toolBar.setOnToolBarClickListener( this );
            setPasswordMessage( true, 2 );
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
        } else if ( v == viewMapper.btnReset ) {
            //pincode 초기화
            strKeyList = null;
            checkKeyList = null;
            keyList.clear( );
            setPasswordView( );
            if ( getRequestCode() == C.requestCode.SETTING_CHANGE_PINCODE ){
                setPasswordMessage( true, 2 );
            } else{
                setPasswordMessage( true, 0 );
            }

            viewMapper.btnReset.setVisibility( View.GONE );
            //============

        } else {
            String tag = String.valueOf( v.getTag( ) );
            if ( v.getTag( ) != null ) {
                keyList.add( tag );
                if ( getRequestCode( ) == C.requestCode.SETTING_CHANGE_PINCODE ) {
                    setPasswordMessage( true, checkKeyList != null ? 1 : 2 );
                } else {
                    setPasswordMessage( true, checkKeyList != null ? 1 : 0 );
                }

                if ( keyList.size( ) == 5 && strKeyList == null ) {
                    StringBuilder stringBuffer = new StringBuilder( );
                    for ( int i = 0; i < keyList.size( ); i++ ) {
                        stringBuffer.append( keyList.get( i ) );
                    }
                    strKeyList = stringBuffer.toString( );
                    checkKeyList = stringBuffer.toString( );
                    keyList.clear( );

                    setPasswordMessage( true, 1 );

                    //pincode 초기화기능 추가
                    viewMapper.btnReset.setVisibility( View.VISIBLE );
                    //===================

                } else if ( keyList.size( ) == 5 && checkKeyList != null ) {

                    StringBuilder stringBuffer = new StringBuilder( );
                    for ( int i = 0; i < keyList.size( ); i++ ) {
                        stringBuffer.append( keyList.get( i ) );
                    }
                    strKeyList = stringBuffer.toString( );

                    if ( strKeyList.equals( checkKeyList ) ) {

                        if ( getRequestCode( ) == C.requestCode.SETTING_CHANGE_PINCODE ) {

                            char[] beforePinCode = getIntent( ).getCharArrayExtra( C.bundleKey.PINCODE );
                            //Todo DataBase 전체 교체

                            String value = Utils.sha256( strKeyList );
                            KeyPairStore.getInstance( ).changePWDBKeyPairs( beforePinCode, checkKeyList.toCharArray( ) );

                            KeyValueStore.getInstance( ).setValue( C.pref.PASSWORD, value, checkKeyList.toCharArray( ) );
                            getPlanetWalletApplication( ).setPINCODE( checkKeyList.toCharArray( ) );

                            setResult( RESULT_OK );
                            super.onBackPressed( );

                        } else {

                            String value = Utils.sha256( strKeyList );
                            KeyValueStore.getInstance( ).setValue( C.pref.PASSWORD, value, checkKeyList.toCharArray( ) );
                            getPlanetWalletApplication( ).setPINCODE( checkKeyList.toCharArray( ) );

                            setTransition( Transition.NO_ANIMATION );
                            sendAction( PinCodeCertificationActivity.class );
                            finish( );
                        }

                    } else {
                        keyList.clear( );
                        setPasswordMessage( false, -1 );
                    }

                }
                setPasswordView( );

            }
        }
    }

    /**
     * @param isPassword 패스워드가 맞는지 체크여부
     * @param flag       -1: 틀렸을경우
     *                   0: 비밀번호를 등록하세요.
     *                   1: 한번 더 입력해주세요.
     *                   2: 새 비밀번호를 입력해주세요.
     */
    void setPasswordMessage( boolean isPassword, int flag ) {
        if ( isPassword ) {
            if ( flag == 0 ) {
                viewMapper.passwordTitle.setText( localized( R.string.pincode_registration_registration_code_title ) );
            } else if ( flag == 1 ) {
                viewMapper.passwordTitle.setText( localized( R.string.pincode_registration_one_more_registration_title ) );
            } else if ( flag == 2 ) {
                viewMapper.passwordTitle.setText( localized( R.string.pincode_registration_change_pin_code_title ) );
            }
            viewMapper.passwordSubtitle.setText( localized( R.string.pincode_registration_sub_title ) );
        } else {
            viewMapper.passwordTitle.setText( localized( R.string.pincode_registration_code_incorrect_title ) );
            viewMapper.passwordSubtitle.setText( localized( R.string.pincode_registration_sub_title_error ) );
        }

        if ( !getPlanetWalletApplication( ).getCurrentTheme( ) ) {
            viewMapper.passwordTitle.setTextColor( isPassword ? Color.parseColor( "#FFFFFF" ) : Color.parseColor( "#FF0050" ) );
        } else {
            viewMapper.passwordTitle.setTextColor( isPassword ? Color.parseColor( "#000000" ) : Color.parseColor( "#FF0050" ) );
        }
        viewMapper.passwordSubtitle.setTextColor( isPassword ? Color.parseColor( "#5C5964" ) : Color.parseColor( "#FF0050" ) );
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
                super.onBackPressed( );
            }

        } else {
            keyList.remove( keyList.size( ) - 1 );
            setPasswordView( );
        }
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

        //Reset Test
        View btnReset;

        public ViewMapper( ) {

            toolBar = findViewById( R.id.toolBar );

            groupPasswordView = findViewById( R.id.group_pincode_registration_inputpassword_wrapper );

            inputPassword = findViewById( R.id.group_pincode_registration_inputpassword );
            inputNumber = findViewById( R.id.group_pincode_registration_inputnumber );
            inputAlphabet = findViewById( R.id.group_pincode_registration_inputalphabet );

            btnDeleteNumber = findViewById( R.id.btn_pincode_registration_numberdelete );
            btnDeleteAlphabet = findViewById( R.id.btn_pincode_registration_alphabetdelete );

            passwordTitle = findViewById( R.id.text_pincode_registration_verificationcode );
            passwordSubtitle = findViewById( R.id.text_pincode_registration_specification );

            decorationViewHeight = findViewById( R.id.view_pincode_registration_plus_height );
            decorationViewWidth = findViewById( R.id.view_pincode_registration_plus_width );

            btnReset = findViewById( R.id.btn_pin_reset );


        }

    }
}