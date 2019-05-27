package io.grabity.planetwallet.Views.p7_Setting.Activity.Board;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Board;
import io.grabity.planetwallet.Views.p7_Setting.Adapter.BoardAdapter;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class BoardActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, AdvanceRecyclerView.OnItemClickListener{

    private ViewMapper viewMapper;
    private ArrayList< Board > items;
    private BoardAdapter adapter;


    @Override
    protected void onCreate ( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_board );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit ( ) {
        super.viewInit( );

        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( R.drawable.image_toolbar_back_blue ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setTitle( Utils.equals( getString( "board" ), "announcements" ) ? "announcements" : "faq" );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.listView.setOnItemClickListener( this );
    }

    @Override
    protected void setData ( ) {
        super.setData( );
        /**
         * 임시세팅
         */
        items = new ArrayList<>(  );
        if( viewMapper.toolBar.getTitle( ).equals( "announcements" ) ){
            items.add( new Board( "title1" , "2018-05-02" ) );
            items.add( new Board( "title2" , "2018-05-01" ) );
            items.add( new Board( "title3" , "2018-05-06" ) );
            items.add( new Board( "title1" , "2018-05-02" ) );
            items.add( new Board( "title2" , "2018-05-01" ) );
            items.add( new Board( "title3" , "2018-05-06" ) );
            items.add( new Board( "title1" , "2018-05-02" ) );
            items.add( new Board( "title2" , "2018-05-01" ) );
            items.add( new Board( "title3" , "2018-05-06" ) );
        }else{
            items.add( new Board( "title1" , null ) );
            items.add( new Board( "title2" , null ) );
            items.add( new Board( "title3" , null ) );
            items.add( new Board( "title1" , null ) );
            items.add( new Board( "title2" , null ) );
            items.add( new Board( "title3" , null ) );
            items.add( new Board( "title1" , null ) );
            items.add( new Board( "title2" , null ) );
            items.add( new Board( "title3" , null ) );
        }

        adapter = new BoardAdapter( getApplicationContext( ), items );
        viewMapper.listView.setAdapter( adapter );

    }

    @Override
    public void onToolBarClick ( Object tag, View view, int direction, int index ) {
        if( Utils.equals( tag , C.tag.TOOLBAR_BACK ) ){
            finish( );
        }
    }

    @Override
    public void onItemClick ( AdvanceRecyclerView recyclerView, View view, int position ) {
        sendAction( DetailBoardActivity.class );
    }

    public class ViewMapper {

        ToolBar toolBar;
        AdvanceRecyclerView listView;

        public ViewMapper ( ) {
            toolBar = findViewById( R.id.toolBar );
            listView = findViewById( R.id.listView );
        }
    }
}
