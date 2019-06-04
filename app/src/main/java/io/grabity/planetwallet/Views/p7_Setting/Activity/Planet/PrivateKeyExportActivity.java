package io.grabity.planetwallet.Views.p7_Setting.Activity.Planet;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.RoundEditText;
import io.grabity.planetwallet.Widgets.RoundRelativeLayout;
import io.grabity.planetwallet.Widgets.ToolBar;

public class PrivateKeyExportActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    ViewMapper viewMapper;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_privatekey_export );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );

        //Data setting
        viewMapper.etPrivateKey.setHint( "3QSfDbpgf1sysw73Hm9CDBHoVe54MVujQ73253434QSfDbpgf1sysw73Hm9CDBHoVe54MVujQ7" );
        viewMapper.groupPrivateKey.setBackground_color_normal( Color.parseColor( getCurrentTheme( ) ? "#1E1E28" : "#F5F5F5" ) );
        viewMapper.groupPrivateKey.setBorder_color_normal( Color.parseColor( getCurrentTheme( ) ? "#1E1E28" : "#EDEDED" ) );
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_CLOSE ) ) {
            super.onBackPressed( );
        }
    }


    public class ViewMapper {
        ToolBar toolBar;
        EditText etPrivateKey;
        RoundRelativeLayout groupPrivateKey;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            etPrivateKey = findViewById( R.id.et_privatekey_export_privatekey );
            groupPrivateKey = findViewById( R.id.group_privatekey_export_privatekey );
        }
    }
}
