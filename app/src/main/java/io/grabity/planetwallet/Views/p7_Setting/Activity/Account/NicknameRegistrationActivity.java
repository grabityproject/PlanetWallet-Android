package io.grabity.planetwallet.Views.p7_Setting.Activity.Account;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.RoundEditText;
import io.grabity.planetwallet.Widgets.ToolBar;

public class NicknameRegistrationActivity extends PlanetWalletActivity implements TextWatcher, ToolBar.OnToolBarClickListener {

    ViewMapper viewMapper;

    @Override
    protected void onCreate ( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_nickname_registration );

        viewMapper = new ViewMapper( );
        viewInit();
        setData();


    }

    @Override
    protected void viewInit ( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.etNickName.addTextChangedListener( this );
        viewMapper.btnNameClear.setOnClickListener( this );
        viewMapper.btnSubmit.setOnClickListener( this );
    }

    @Override
    protected void setData ( ) {
        super.setData( );
        viewMapper.etNickName.setText( getString( "name" ) );
        viewMapper.etNickName.setSelection( viewMapper.etNickName.length( ) );
    }



    @Override
    public void onClick ( View v ) {
        super.onClick( v );
        if( v == viewMapper.btnNameClear ){
            viewMapper.etNickName.setText( "" );
        }else if( v == viewMapper.btnSubmit ){

            if( viewMapper.etNickName.getText( ) == null ) return;
            Utils.hideKeyboard( this , getCurrentFocus( ) );
            setResult( RESULT_OK , new Intent(  ).putExtra( "name" , viewMapper.etNickName.getText( ).toString( ) ) );
            super.onBackPressed( );
            finish( );
        }
    }

//    @Override
//    public void setTheme ( boolean theme ) {
//        super.setTheme( theme );
//        setStatusColor( !theme );
//    }

    @Override
    public void beforeTextChanged ( CharSequence s, int start, int count, int after ) {

    }

    @Override
    public void onTextChanged ( CharSequence s, int start, int before, int count ) {
        viewMapper.btnSubmit.setEnabled( viewMapper.etNickName.getText( ).toString( ).trim( ).length( ) == 0 ? false : true );
        viewMapper.btnNameClear.setVisibility( viewMapper.etNickName.getText().toString( ).trim( ).length( ) == 0 ? View.GONE : View.VISIBLE );
    }

    @Override
    public void afterTextChanged ( Editable s ) {

    }


    @Override
    public void onToolBarClick ( Object tag, View view ) {
        if( Utils.equals( tag , C.tag.TOOLBAR_CLOSE) ){
            super.onBackPressed( );
            finish( );
        }
    }

    public class ViewMapper {

        ToolBar toolBar;
        RoundEditText etNickName;
        CircleImageView btnNameClear;
        View btnSubmit;

        public ViewMapper ( ) {
            toolBar = findViewById( R.id.toolBar );
            etNickName = findViewById( R.id.et_nickname_registration_nickname );
            btnNameClear = findViewById( R.id.btn_nickname_registration_clear );
            btnSubmit = findViewById( R.id.btn_submit );
        }
    }
}
