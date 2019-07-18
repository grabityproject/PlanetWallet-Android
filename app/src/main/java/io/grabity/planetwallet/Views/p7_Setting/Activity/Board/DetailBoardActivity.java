package io.grabity.planetwallet.Views.p7_Setting.Activity.Board;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Board;
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

        webSettings = viewMapper.webView.getSettings( );
        webSettings.setJavaScriptEnabled( true );
        viewMapper.webView.setWebChromeClient( new WebChromeClient( ) );
        viewMapper.webView.setWebViewClient( new WebViewClient( ) );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.BOARD ) == null ) {
            onBackPressed( );
        } else {
            Board board = ( Board ) getSerialize( C.bundleKey.BOARD );
            String parameter = !getCurrentTheme( ) ? "?theme=white" : "?theme=black";
            viewMapper.toolBar.setTitle( board.getType( ) );
            viewMapper.title.setText( board.getSubject( ) );
            viewMapper.webView.clearCache( true );
            viewMapper.webView.clearHistory( );
            if ( Utils.equals( board.getType( ), localized( R.string.setting_faq_title ) ) ) {
                ( ( RelativeLayout.LayoutParams ) viewMapper.groupBoard.getLayoutParams( ) ).height = ( int ) Utils.dpToPx( this, 60 );
                viewMapper.groupBoard.requestLayout( );
                viewMapper.webView.loadUrl( Route.URL( "board", "faq", board.getId( ) ) + parameter );
            } else {
                viewMapper.time.setVisibility( View.VISIBLE );
                viewMapper.time.setText( board.getCreated_at( ) );
                viewMapper.webView.loadUrl( Route.URL( "board", "notice", board.getId( ) ) + parameter );
            }
        }
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            onBackPressed( );
        }
    }

    public class ViewMapper {

        ViewGroup groupBoard;

        ToolBar toolBar;
        TextView title;
        TextView time;
        WebView webView;

        public ViewMapper( ) {
            groupBoard = findViewById( R.id.group_detail_board );
            toolBar = findViewById( R.id.toolBar );
            title = findViewById( R.id.text_detail_board_title );
            time = findViewById( R.id.text_detail_board_create_time );
            webView = findViewById( R.id.webView );
        }
    }
}
