package io.grabity.planetwallet.Views.p7_Setting.Activity.Account;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p4_Main.Activity.MainActivity;
import io.grabity.planetwallet.Widgets.RoundEditText;
import io.grabity.planetwallet.Widgets.TimerView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class PhoneRegistrationActivity extends PlanetWalletActivity implements TextWatcher, ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;

    //Todo Server 응답후 타이머

    @Override
    protected void onCreate ( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_phone_registration );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
        setStatusColor( Color.BLACK );
    }

    @Override
    protected void viewInit ( ) {
        super.viewInit( );
        viewMapper.toolBar.addLeftButton( new ToolBar.ButtonItem( R.drawable.image_toolbar_close_blue ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.etPhone.addTextChangedListener( this );
        viewMapper.etCode.addTextChangedListener( this );
        viewMapper.btnSend.setOnClickListener( this );
        viewMapper.btnDialCode.setOnClickListener( this );
        viewMapper.btnSubmit.setOnClickListener( this );

        viewMapper.btnSend.setEnabled( false );
    }

    @Override
    protected void setData ( ) {
        super.setData( );

    }

    @Override
    public void onClick ( View v ) {
        super.onClick( v );

        if( v == viewMapper.btnSend ) {
            viewMapper.groupPhoneCode.setVisibility( View.VISIBLE );
            viewMapper.textTime.Start( 180000 );
        } else if( v == viewMapper.btnSubmit ) {

            if( viewMapper.textTime.isCertification( ) ){
                viewMapper.textTime.Stop( );
                finish( );
            }else{
                Toast.makeText( this , "인증시간초과", Toast.LENGTH_SHORT).show( );
            }

        }
    }

    @Override
    public void beforeTextChanged ( CharSequence s, int start, int count, int after ) {

    }

    @Override
    public void onTextChanged ( CharSequence s, int start, int before, int count ) {
        viewMapper.btnSend.setEnabled( viewMapper.etPhone.getText( ).toString( ).trim( ).length( ) >= 10 ? true : false );
        viewMapper.btnSubmit.setEnabled( viewMapper.etCode.getText( ).toString( ).trim( ).length() == 0 ? false : true );
    }

    @Override
    public void afterTextChanged ( Editable s ) {

    }

    @Override
    public void onToolBarClick ( Object tag, View view, int direction, int index ) {
        if( Utils.equals( tag, C.tag.TOOLBAR_CLOSE ) ){
            super.onBackPressed( );
        }
    }

    public class ViewMapper {

        ViewGroup groupPhoneCode;
        View btnSend;
        View btnDialCode;
        View btnSubmit;
        ToolBar toolBar;
        RoundEditText etPhone;
        RoundEditText etCode;
        TimerView textTime;

        public ViewMapper ( ) {

            groupPhoneCode = findViewById( R.id.group_phone_registration_code );
            toolBar = findViewById( R.id.toolBar );

            etPhone = findViewById( R.id.et_phone_registration_phone  );
            btnSend = findViewById( R.id.btn_phone_registration_send );
            btnDialCode = findViewById( R.id.btn_phone_registration_dial_code );

            etCode = findViewById( R.id.et_phone_registration_code );

            textTime = findViewById( R.id.text_phone_registration_time );

            btnSubmit = findViewById( R.id.btn_submit );
        }
    }


}
