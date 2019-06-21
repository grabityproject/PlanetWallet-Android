package io.grabity.planetwallet.Views.p7_Setting.Activity.Planet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.store.PlanetStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Views.p3_Wallet.Activity.WalletAddActivity;
import io.grabity.planetwallet.Views.p7_Setting.Adapter.PlanetManagementAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class PlanetManagementActivity extends PlanetWalletActivity implements AdvanceRecyclerView.OnItemClickListener, ToolBar.OnToolBarClickListener {


    private ViewMapper viewMapper;
    private ArrayList< Planet > items;
    private PlanetManagementAdapter adapter;

    private Planet planet;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_planet_management );
        viewMapper = new ViewMapper( );
        viewInit( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setRightButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_ADD ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.listView.setOnItemClickListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.PLANET ) != null ) {
            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            setUpList( );
        } else {
            finish( );
        }
    }

    @Override
    protected void onResume( ) {
        super.onResume( );
        setData( );
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            super.onBackPressed( );
        } else if ( Utils.equals( tag, C.tag.TOOLBAR_ADD ) ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( C.requestCode.PLANET_ADD, WalletAddActivity.class );
        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.PLANET_ADD && resultCode == RESULT_OK ) {
            Toast.makeText( this, "Planet Add", Toast.LENGTH_SHORT ).show( );
        }
    }

    @Override
    public void onItemClick( AdvanceRecyclerView recyclerView, View view, int position ) {
        sendAction( DetailPlanetActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, items.get( position ) ) );
    }

    private void setUpList( ) {
        items = PlanetStore.getInstance( ).getPlanetList( );
        int removeIndex = -1;
        for ( int i = 0; i < items.size( ); i++ ) {
            if ( Utils.equals( items.get( i ).getKeyId( ), planet.getKeyId( ) ) ) {
                removeIndex = i;
            }
        }
        if ( removeIndex >= 0 ) {
            items.remove( removeIndex );
        }
        adapter = new PlanetManagementAdapter( this, items );
        viewMapper.listView.setAdapter( adapter );
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
