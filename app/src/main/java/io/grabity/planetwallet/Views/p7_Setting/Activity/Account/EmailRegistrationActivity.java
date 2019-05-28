package io.grabity.planetwallet.Views.p7_Setting.Activity.Account;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.RoundEditText;
import io.grabity.planetwallet.Widgets.ToolBar;

public class EmailRegistrationActivity extends PlanetWalletActivity implements TextWatcher, ToolBar.OnToolBarClickListener {

    ViewMapper viewMapper;

    @Override
    protected void onCreate ( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_email_registration );

        viewMapper = new ViewMapper( );
        viewInit();
        setData();

    }

    @Override
    protected void viewInit ( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.etEmail.addTextChangedListener( this );
        viewMapper.btnClear.setOnClickListener( this );
        viewMapper.btnSubmit.setOnClickListener( this );
    }

    @Override
    protected void setData ( ) {
        super.setData( );
        viewMapper.etEmail.setText( getString( "mail" ) );
        viewMapper.etEmail.setSelection( viewMapper.etEmail.length( ) );
    }

    @Override
    public void onClick ( View v ) {
        super.onClick( v );
        if( v == viewMapper.btnClear ){
            viewMapper.etEmail.setText( "" );
        }else if( v == viewMapper.btnSubmit ){
            if( viewMapper.etEmail.getText( ) == null ) return;
            Utils.hideKeyboard( this, getCurrentFocus( ) );
            setResult( RESULT_OK , new Intent(  ).putExtra( "mail" , viewMapper.etEmail.getText( ).toString( ) ) );
            super.onBackPressed( );
            finish( );
        }
    }

    @Override
    public void beforeTextChanged ( CharSequence s, int start, int count, int after ) {

    }

    @Override
    public void onTextChanged ( CharSequence s, int start, int before, int count ) {
        if( viewMapper.etEmail.getText( ).toString( ).trim( ).length() != 0 ){
            viewMapper.btnSubmit.setEnabled( Utils.isValidEmail( viewMapper.etEmail.getText( ).toString( ) ) ? true : false );
        }
        viewMapper.btnClear.setVisibility( viewMapper.etEmail.getText().length() == 0 ? View.GONE : View.VISIBLE );
    }

    @Override
    public void afterTextChanged ( Editable s ) {

    }

    @Override
    public void onToolBarClick ( Object tag, View view ) {
        if( Utils.equals( tag , C.tag.TOOLBAR_CLOSE ) ){
            super.onBackPressed( );
            finish( );
        }
    }


    public class ViewMapper {

        ToolBar toolBar;

        EditText etEmail;
        TextView textMessage;
        View btnClear;

        View btnSubmit;

        public ViewMapper ( ) {
            toolBar = findViewById( R.id.toolBar );

            etEmail = findViewById( R.id.et_email_registration_email );
            textMessage = findViewById( R.id.text_email_registration_message );
            btnClear = findViewById( R.id.btn_email_registration_clear );
            btnSubmit = findViewById( R.id.btn_submit );
        }
    }
}
