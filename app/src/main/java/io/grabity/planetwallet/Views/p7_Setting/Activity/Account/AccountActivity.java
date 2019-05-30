package io.grabity.planetwallet.Views.p7_Setting.Activity.Account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.ToolBar;

public class AccountActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    ViewMapper viewMapper;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_account );

        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );


    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.btnNickName.setOnClickListener( this );
        viewMapper.btnPhone.setOnClickListener( this );
        viewMapper.btnEmail.setOnClickListener( this );
        viewMapper.btnConnectManager.setOnClickListener( this );

    }

    @Override
    protected void setData( ) {
        super.setData( );
    }

    //ToDo Data Setting
    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnNickName ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( C.requestCode.ACCOUNT_RENAME, NicknameRegistrationActivity.class, Utils.createStringBundle( "name", viewMapper.nickName.getText( ).toString( ) ) );
        } else if ( v == viewMapper.btnEmail ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( C.requestCode.ACCOUNT_REMAIL, EmailRegistrationActivity.class, Utils.createStringBundle( "mail", viewMapper.email.getText( ).toString( ) ) );
        } else if ( v == viewMapper.btnPhone ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( C.requestCode.ACCOUNT_REPHONE_NUMBER, PhoneRegistrationActivity.class, Utils.createStringBundle( "phone", viewMapper.phone.getText( ).toString( ) ) );
        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );

    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            finish( );
        }
    }

    public class ViewMapper {
        ToolBar toolBar;
        ViewGroup btnNickName;
        ViewGroup btnEmail;
        ViewGroup btnPhone;
        ViewGroup btnConnectManager;
        TextView nickName;
        TextView email;
        TextView phone;

        public ViewMapper( ) {

            toolBar = findViewById( R.id.toolBar );

            btnNickName = findViewById( R.id.group_account_nickname );
            btnEmail = findViewById( R.id.group_account_email );
            btnPhone = findViewById( R.id.group_account_phone );
            btnConnectManager = findViewById( R.id.group_account_connect_manager );

            nickName = findViewById( R.id.text_account_nickname );
            email = findViewById( R.id.text_account_email );
            phone = findViewById( R.id.text_account_phone );

        }
    }
}
