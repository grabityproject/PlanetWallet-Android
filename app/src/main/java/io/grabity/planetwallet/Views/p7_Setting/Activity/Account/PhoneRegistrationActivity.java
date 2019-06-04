package io.grabity.planetwallet.Views.p7_Setting.Activity.Account;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.AbsPopupView.PopupView;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.PhoneCode;
import io.grabity.planetwallet.Views.p7_Setting.Adapter.PopupPhoneCodeAdapter;
import io.grabity.planetwallet.Widgets.ListPopupView.ListPopup;
import io.grabity.planetwallet.Widgets.RoundEditText;
import io.grabity.planetwallet.Widgets.TimerView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class PhoneRegistrationActivity extends PlanetWalletActivity implements TextWatcher, ToolBar.OnToolBarClickListener, ListPopup.OnListPopupItemClickListener {

    private ViewMapper viewMapper;

    private PopupPhoneCodeAdapter adapter;
    private ArrayList< PhoneCode > items;

    //Todo Server 응답후 타이머

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_phone_registration );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
        setStatusColor( Color.BLACK );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.etPhone.addTextChangedListener( this );
        viewMapper.etCode.addTextChangedListener( this );
        viewMapper.btnSend.setOnClickListener( this );
        viewMapper.btnDialCode.setOnClickListener( this );
        viewMapper.btnSubmit.setOnClickListener( this );

        viewMapper.btnSend.setEnabled( false );
        viewMapper.btnSubmit.setEnabled( false );
    }

    @Override
    protected void setData( ) {
        super.setData( );

        items = new ArrayList<>( );
        items.add( new PhoneCode( "Korea", "+ 82" ) );
        items.add( new PhoneCode( "China", "+ 86" ) );
        items.add( new PhoneCode( "Japan", "+ 81" ) );
        items.add( new PhoneCode( "New Zealand", "+ 64" ) );
        items.add( new PhoneCode( "Hongkong", "+ 852" ) );
        items.add( new PhoneCode( "Hawaii", "+ 1808" ) );
        items.add( new PhoneCode( "Grenada", "+ 1809" ) );
        items.add( new PhoneCode( "Denmark", "+ 45" ) );
        items.add( new PhoneCode( "China", "+ 86" ) );
        items.add( new PhoneCode( "Japan", "+ 81" ) );
        items.add( new PhoneCode( "New Zealand", "+ 64" ) );
        items.add( new PhoneCode( "Hongkong", "+ 852" ) );
        items.add( new PhoneCode( "Hawaii", "+ 1808" ) );
        items.add( new PhoneCode( "Grenada", "+ 1809" ) );
        items.add( new PhoneCode( "Denmark", "+ 45" ) );
        items.add( new PhoneCode( "China", "+ 86" ) );
        items.add( new PhoneCode( "Japan", "+ 81" ) );
        items.add( new PhoneCode( "New Zealand", "+ 64" ) );
        items.add( new PhoneCode( "Hongkong", "+ 852" ) );
        items.add( new PhoneCode( "Hawaii", "+ 1808" ) );
        items.add( new PhoneCode( "Grenada", "+ 1809" ) );
        items.add( new PhoneCode( "Denmark", "+ 45" ) );
        items.add( new PhoneCode( "China", "+ 86" ) );

        adapter = new PopupPhoneCodeAdapter( this, items );

    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );

        if ( v == viewMapper.btnDialCode ) {
            Utils.hideKeyboard( this, getCurrentFocus( ) );
            ListPopup.newInstance( this )
                    .setAdapter( adapter )
                    .setOnListPopupItemClickListener( this )
                    .setShowShadow( true )
                    .show( );
        } else if ( v == viewMapper.btnSend ) {
            viewMapper.groupPhoneCode.setVisibility( View.VISIBLE );
            viewMapper.textTime.Start( 180000 );
        } else if ( v == viewMapper.btnSubmit ) {
            //Todo 인증후 번호변경으로 변경
            if ( viewMapper.textTime.isCertification( ) ) {
                viewMapper.textTime.Stop( );
                Utils.hideKeyboard( this, getCurrentFocus( ) );
                super.onBackPressed( );
            } else {
                Toast.makeText( this, "인증시간초과", Toast.LENGTH_SHORT ).show( );
            }
        }
    }

    @Override
    public void onListPopupItemClick( PopupView popup, View view, int position ) {
        viewMapper.btnDialCode.setText( items.get( position ).getCountryCode( ) );
        super.onBackPressed( );
    }

    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

    }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count ) {
        viewMapper.btnSend.setEnabled( viewMapper.etPhone.getText( ).toString( ).trim( ).length( ) >= 10 ? true : false );
        viewMapper.btnSubmit.setEnabled( viewMapper.etCode.getText( ).toString( ).trim( ).length( ) == 0 ? false : true );
    }

    @Override
    public void afterTextChanged( Editable s ) {

    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_CLOSE ) ) {
            Utils.hideKeyboard( this, getCurrentFocus( ) );
            super.onBackPressed( );
        }
    }

    public class ViewMapper {

        ViewGroup groupPhoneCode;
        View btnSend;
        View btnSubmit;
        ToolBar toolBar;
        RoundEditText etPhone;
        RoundEditText etCode;
        TimerView textTime;

        TextView btnDialCode;

        public ViewMapper( ) {

            groupPhoneCode = findViewById( R.id.group_phone_registration_code );
            toolBar = findViewById( R.id.toolBar );

            etPhone = findViewById( R.id.et_phone_registration_phone );
            btnSend = findViewById( R.id.btn_phone_registration_send );
            btnDialCode = findViewById( R.id.btn_phone_registration_dial_code );

            etCode = findViewById( R.id.et_phone_registration_code );

            textTime = findViewById( R.id.text_phone_registration_time );

            btnSubmit = findViewById( R.id.btn_submit );
        }
    }


}
