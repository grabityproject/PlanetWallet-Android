package io.grabity.planetwallet.Views.Test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TestActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener{

        public ViewMapper viewMapper;

        @Override
        protected void onCreate( @Nullable Bundle savedInstanceState ) {
            super.onCreate( savedInstanceState );
            setContentView( R.layout.testlayout );

//            viewMapper = new ViewMapper( );

//            viewInit();
//            setData();

        }

        @Override
        protected void viewInit( ) {
            super.viewInit( );

    //        viewMapper.themeToolBar.addLeftButton( new ThemeToolBar.ButtonItem( R.drawable.icon_eth ).setTag( C.tag.TOOLBAR_ADD ) );
    //        viewMapper.themeToolBar.addRightButton( new ThemeToolBar.ButtonItem( R.drawable.icon_gbt ).setTag( C.tag.TOOLBAR_BACK ) );
//            viewMapper.themeToolBar.addLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_ADD ) );
//            viewMapper.themeToolBar.addRightButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
//            viewMapper.themeToolBar.setOnToolBarClickListener( this );

            viewMapper.button.setOnClickListener( this );
            viewMapper.button1.setOnClickListener( this  );

//            viewMapper.button.setOnClickListener( new View.OnClickListener( ) {
//                @Override
//                public void onClick( View v ) {
//                    PLog.e( " 블랙 " );
//                    getPlanetWalletApplication( ).setTheme( false );
//                    setTheme( false );
//                }
//            } );
//
//            viewMapper.button1.setOnClickListener( new View.OnClickListener( ) {
//                @Override
//                public void onClick( View v ) {
//                    PLog.e( " 화이트 " );
//                    getPlanetWalletApplication( ).setTheme( true );
//                    setTheme( true );
//                }
//            } );
        }


    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.button ) {
            Toast.makeText( this , "블랙버튼" , Toast.LENGTH_SHORT).show();
            getPlanetWalletApplication( ).setTheme( false );
            setTheme( false );
        } else if ( v == viewMapper.button1 ) {
            Toast.makeText( this , "화이트" , Toast.LENGTH_SHORT).show();
            getPlanetWalletApplication( ).setTheme( true );
            setTheme( true );
        }
    }

    @Override
        public void onToolBarClick( Object tag, View view ) {
            if ( Utils.equals( tag , C.tag.TOOLBAR_ADD ) ){
                Toast.makeText( this , "테스트 툴바 왼쪽 클릭 " , Toast.LENGTH_SHORT).show();
            } else if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ){
                Toast.makeText( this , "테스트 툴바 오른쪽 클릭 " , Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void setData( ) {
            super.setData( );
        }

        public class ViewMapper{
//            ToolBar themeToolBar;
            Button button;
            Button button1;
            public ViewMapper( ) {

                button = findViewById( R.id.btn );
                button1 = findViewById( R.id.btn1 );

//                themeToolBar = findViewById( R.id.themetoolbar );

            }
        }
}
