package io.grabity.planetwallet.Views.p7_Setting.Activity.Board;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Board;
import io.grabity.planetwallet.Widgets.ToolBar;

public class ServiceActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;
    private Board board;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_service );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );

        viewMapper.toolBar.setLeftButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.btnWallet.setOnClickListener( this );
        viewMapper.btnPlanet.setOnClickListener( this );
        viewMapper.btnUniverse.setOnClickListener( this );
        viewMapper.btnSecurity.setOnClickListener( this );
        viewMapper.btnTransfer.setOnClickListener( this );
        viewMapper.btnContactUs.setOnClickListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );

        board = new Board( );
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            onBackPressed( );
        }
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnWallet ) {
            board.setType( C.boardCategory.CATEGORY_WALLET );
            sendAction( BoardActivity.class, Utils.createSerializableBundle( C.bundleKey.BOARD, board ) );

        } else if ( v == viewMapper.btnPlanet ) {
            board.setType( C.boardCategory.CATEGORY_PLANET );
            sendAction( BoardActivity.class, Utils.createSerializableBundle( C.bundleKey.BOARD, board ) );

        } else if ( v == viewMapper.btnUniverse ) {
            board.setType( C.boardCategory.CATEGORY_UNIVERSE );
            sendAction( BoardActivity.class, Utils.createSerializableBundle( C.bundleKey.BOARD, board ) );

        } else if ( v == viewMapper.btnSecurity ) {
            board.setType( C.boardCategory.CATEGORY_SECURITY );
            sendAction( BoardActivity.class, Utils.createSerializableBundle( C.bundleKey.BOARD, board ) );

        } else if ( v == viewMapper.btnTransfer ) {
            board.setType( C.boardCategory.CATEGORY_TRANSFER );
            sendAction( BoardActivity.class, Utils.createSerializableBundle( C.bundleKey.BOARD, board ) );

        } else if ( v == viewMapper.btnContactUs ) {

            Intent emailSelectorIntent = new Intent( Intent.ACTION_SENDTO );
            emailSelectorIntent.setData( Uri.parse( "mailto:" ) );

            final Intent emailIntent = new Intent( Intent.ACTION_SEND );
            emailIntent.putExtra( Intent.EXTRA_EMAIL, new String[]{ "planetwallet.io@gmail.com" } );
            emailIntent.addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );
            emailIntent.addFlags( Intent.FLAG_GRANT_WRITE_URI_PERMISSION );
            emailIntent.setSelector( emailSelectorIntent );

            if ( emailIntent.resolveActivity( getPackageManager( ) ) != null )
                startActivity( emailIntent );


        }
    }

    public class ViewMapper {

        ToolBar toolBar;

        View btnWallet;
        View btnPlanet;
        View btnUniverse;
        View btnSecurity;
        View btnTransfer;
        View btnContactUs;

        public ViewMapper( ) {

            toolBar = findViewById( R.id.toolBar );
            btnWallet = findViewById( R.id.group_service_wallet );
            btnPlanet = findViewById( R.id.group_service_planet );
            btnUniverse = findViewById( R.id.group_service_universe );
            btnSecurity = findViewById( R.id.group_service_security );
            btnTransfer = findViewById( R.id.group_service_transfer );
            btnContactUs = findViewById( R.id.group_service_contact_us );


        }
    }
}
