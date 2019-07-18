package io.grabity.planetwallet.Views.p7_Setting.Activity.Setting;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Widgets.ToggleButton;
import io.grabity.planetwallet.Widgets.ToolBar;

public class SecurityActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, ToggleButton.OnToggleListener {

    private ViewMapper viewMapper;

    private ArrayList< String > items;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_security );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.toggle.setOnToggleListener( this );
        viewMapper.btnPinCode.setOnClickListener( this );


    }

    @Override
    protected void setData( ) {
        super.setData( );

    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            super.onBackPressed( );
        }
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnPinCode ) {
            setTransition( Transition.SLIDE_UP );
            sendAction(
                    C.requestCode.SETTING_CHANGE_PINCODE,
                    PinCodeCertificationActivity.class );
        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.SETTING_CHANGE_PINCODE && resultCode == RESULT_OK ) {
            Toast.makeText( this, "PinCode Change Success", Toast.LENGTH_SHORT ).show( );
        } else if ( requestCode == C.requestCode.SETTING_CHANGE_PINCODE && resultCode == RESULT_CANCELED ) {
        }
    }

    @Override
    public void onToggle( ToggleButton toggleButton, boolean isOn ) {

    }


    public class ViewMapper {
        ToolBar toolBar;
        ToggleButton toggle;
        ViewGroup btnPinCode;


        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            toggle = findViewById( R.id.toggle );
            btnPinCode = findViewById( R.id.group_detail_setting_change_pincode );
        }
    }
}
