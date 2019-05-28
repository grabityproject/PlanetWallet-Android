package io.grabity.planetwallet.Views.p7_Setting.Activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p7_Setting.Activity.Account.AccountActivity;
import io.grabity.planetwallet.Views.p7_Setting.Activity.Board.BoardActivity;
import io.grabity.planetwallet.Views.p7_Setting.Activity.Planet.PlanetManagementActivity;
import io.grabity.planetwallet.Views.p7_Setting.Activity.Setting.DetailSettingActivity;
import io.grabity.planetwallet.Widgets.ToolBar;


public class SettingActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener{

    private ViewMapper viewMapper;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_setting, true );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );

//        setTheme( true );
    }

    @Override
    protected void onResume ( ) {
        super.onResume( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );


        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.btnPlanet.setOnClickListener( this );
        viewMapper.btnAccount.setOnClickListener( this );
        viewMapper.btnSetting.setOnClickListener( this );
        viewMapper.btnAnnouncements.setOnClickListener( this );
        viewMapper.btnFaq.setOnClickListener( this );
        viewMapper.btnThemeWhite.setOnClickListener( this );
        viewMapper.btnThemeBlack.setOnClickListener( this );



//
//        PLog.e( "toolbar btn items : " + viewMapper.toolBar.getButtonItems().get( 0 ).getResource() );
//        PLog.e( "getDr : " + getResources().getIdentifier( "image_toolbar_close_blue","drawable","io.grabity.planetwallet" ) );
//
//        if ( viewMapper.toolBar.getButtonItems().get( 0 ).getResource() == R.drawable.image_toolbar_close_blue ){
//            PLog.e( " 같습니다!!!! " );
//        }

    }


    @Override
    protected void setData( ) {
        super.setData( );
        viewMapper.name.setText( viewMapper.name.getText( ) + "choi3950" );
    }

    @Override
    public void onClick ( View v ) {
        super.onClick( v );
        if( v == viewMapper.btnPlanet ){
            sendAction( PlanetManagementActivity.class );
        }else if( v == viewMapper.btnAccount ){
            sendAction( AccountActivity.class );
        }else if( v == viewMapper.btnSetting ){
            sendAction( DetailSettingActivity.class );
        }else if( v == viewMapper.btnAnnouncements ){
            sendAction( BoardActivity.class , Utils.createStringBundle( "board", "announcements" ) );
        }else if( v == viewMapper.btnFaq ){
            sendAction( BoardActivity.class , Utils.createStringBundle( "board", "faq" ));
        }else if( v == viewMapper.btnThemeBlack ){

            getPlanetWalletApplication( ).setTheme( false );
            setTheme( false );

            Toast.makeText( this , "블랙테마" , Toast.LENGTH_SHORT).show( );
        }else if( v == viewMapper.btnThemeWhite ){
            getPlanetWalletApplication( ).setTheme( true );
            setTheme( true );
            Toast.makeText( this , "화이트테마" , Toast.LENGTH_SHORT).show( );
        }

    }

    @Override
    public void onToolBarClick ( Object tag, View view ) {
        if( Utils.equals( tag , C.tag.TOOLBAR_CLOSE ) ) {
            super.onBackPressed( );
        }
    }

    public class ViewMapper {

        ToolBar toolBar;
        TextView name;

        ViewGroup btnPlanet;
        ViewGroup btnAccount;
        ViewGroup btnSetting;
        ViewGroup btnAnnouncements;
        ViewGroup btnFaq;
        ViewGroup howtouse; // 보류

        View btnThemeWhite;
        View btnThemeBlack;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            name = findViewById( R.id.text_setting_name );
            btnPlanet = findViewById( R.id.group_setting_planet );
            btnAccount = findViewById( R.id.group_setting_account );
            btnSetting = findViewById( R.id.group_setting_setting );
            btnAnnouncements = findViewById( R.id.group_setting_announcements );
            btnFaq = findViewById( R.id.group_setting_faq );

            btnThemeBlack = findViewById( R.id.btn_setting_theme_black );
            btnThemeWhite = findViewById( R.id.btn_setting_theme_white );

        }

    }
}