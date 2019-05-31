package io.grabity.planetwallet.Views.p7_Setting.Activity.Board;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.ToolBar;

public class DetailBoardActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;
    private WebSettings webSettings;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail_board );

        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @SuppressLint( "SetJavaScriptEnabled" )
    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.toolBar.setTitle( getString( "title" ) );

        webSettings = viewMapper.webView.getSettings( );
        webSettings.setJavaScriptEnabled( true );
        viewMapper.webView.setWebChromeClient( new WebChromeClient( ) );
        viewMapper.webView.setWebViewClient( new WebViewClient( ) );

        viewMapper.webView.loadUrl( "http://www.naver.com" );

    }

    @Override
    protected void setData( ) {
        super.setData( );
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            finish( );
        }
    }

    public class ViewMapper {
        ToolBar toolBar;
        TextView title;
        TextView time;
        WebView webView;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            title = findViewById( R.id.text_detail_board_title );
            time = findViewById( R.id.text_detail_board_create_time );
            webView = findViewById( R.id.webView );
        }
    }
}
