package io.grabity.planetwallet.Views.p7_Setting.Activity.Board;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Board;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.Views.p7_Setting.Adapter.AnnounceAdapter;
import io.grabity.planetwallet.Views.p7_Setting.Adapter.FAQAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class BoardActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, AdvanceRecyclerView.OnItemClickListener {

    private ViewMapper viewMapper;
    private ArrayList< Board > items;
    private Board board;


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_board );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.listView.setOnItemClickListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.BOARD ) == null ) {
            onBackPressed( );
        } else {
            board = ( Board ) getSerialize( C.bundleKey.BOARD );
            if ( board.getType( ) == null ) {
                onBackPressed( );
            } else {
                viewMapper.toolBar.setTitle( board.getType( ) );
                if ( Utils.equals( board.getType( ), localized( R.string.setting_faq_title ) ) ) {
                    new Get( this ).action( Route.URL( "board", "faq", "list" ), 0, 0, null );
                } else {
                    new Get( this ).action( Route.URL( "board", "notice", "list" ), 0, 0, null );
                }
            }
        }
    }

    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {
        super.onReceive( error, requestCode, resultCode, statusCode, result );
        if ( !error ) {
            ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Board.class );
            if ( returnVO.isSuccess( ) ) {
                items = ( ArrayList< Board > ) returnVO.getResult( );
                if ( Utils.equals( board.getType( ), localized( R.string.setting_faq_title ) ) ) {
                    viewMapper.listView.setAdapter( new FAQAdapter( getApplicationContext( ), items ) );
                } else {
                    viewMapper.listView.setAdapter( new AnnounceAdapter( getApplicationContext( ), items ) );
                }
            }
        }
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            onBackPressed( );
        }
    }

    @Override
    public void onItemClick( AdvanceRecyclerView recyclerView, View view, int position ) {
        items.get( position ).setType( board.getType( ) );
        sendAction( DetailBoardActivity.class, Utils.createSerializableBundle( C.bundleKey.BOARD, items.get( position ) ) );
    }

    public class ViewMapper {

        ToolBar toolBar;
        AdvanceRecyclerView listView;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            listView = findViewById( R.id.listView );
        }
    }
}
