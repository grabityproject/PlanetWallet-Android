package io.grabity.planetwallet.Views.p7_Setting.Activity.Planet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Views.p7_Setting.Adapter.PlanetManagementAdapter;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.OnListSideViewClick;
import io.grabity.planetwallet.Widgets.ToolBar;

public class PlanetManagementActivity extends PlanetWalletActivity implements AdvanceRecyclerView.OnItemClickListener, ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;
    private ArrayList< Planet > items;
    private PlanetManagementAdapter adapter;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_planet_management );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );

        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( R.drawable.image_toolbar_back_blue ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setRightButton( new ToolBar.ButtonItem( R.drawable.image_toolbar_add_blue ).setTag( C.tag.TOOLBAR_ADD ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.listView.setOnItemClickListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        /**
         * 임시작업
         */
        items = new ArrayList<>( );
        items.add( new Planet( "111111", "ETH", "choiss" ) );
        items.add( new Planet( "232323", "BTC", "chois221s" ) );
        items.add( new Planet( "4342341", "ETH", "choi23ss" ) );
        items.add( new Planet( "34524", "BTC", "choi26ss" ) );
        items.add( new Planet( "111111", "ETH", "choiss" ) );
        items.add( new Planet( "232323", "BTC", "chois221s" ) );
        items.add( new Planet( "4342341", "ETH", "choi23ss" ) );
        items.add( new Planet( "34524", "BTC", "choi26ss" ) );

        adapter = new PlanetManagementAdapter( getApplicationContext( ), items );
        viewMapper.listView.setAdapter( adapter );
    }

    @Override
    public void onToolBarClick( Object tag, View view, int direction, int index ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            finish( );
        } else if ( Utils.equals( tag, C.tag.TOOLBAR_ADD ) ) {
            Toast.makeText( this, "ADD버튼클릭", Toast.LENGTH_SHORT ).show( );
        }
    }

    @Override
    public void onItemClick( AdvanceRecyclerView recyclerView, View view, int position ) {
        sendAction( DetailPlanetActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, items.get( position ) ) );
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
