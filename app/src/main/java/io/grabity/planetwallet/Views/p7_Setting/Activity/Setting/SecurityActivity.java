package io.grabity.planetwallet.Views.p7_Setting.Activity.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.biometric.BioMetricManager;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Widgets.ToggleButton;
import io.grabity.planetwallet.Widgets.ToolBar;

public class SecurityActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, ToggleButton.OnToggleListener {

    private ViewMapper viewMapper;


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
        viewMapper.btnToggle.setOnClickListener( this );

        BioMetricManager.Init( this );

        if ( !BioMetricManager.getInstance( ).isHardwareCheck( ) ) {
            viewMapper.groupBio.setVisibility( View.GONE );
        }

    }

    @Override
    protected void setData( ) {
        super.setData( );
    }

    @Override
    protected void onResume( ) {
        super.onResume( );
            viewMapper.toggle.setOn( Utils.equals( Utils.getPreferenceData( this,C.pref.BIO_METRIC , String.valueOf( false ) ),  String.valueOf( true ) ) );
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
        if ( v == viewMapper.btnToggle ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( C.requestCode.BIO_METRIC, PinCodeCertificationActivity.class );

        } else if ( v == viewMapper.btnPinCode ) {
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
        ViewGroup groupBio;

        View btnToggle;


        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            toggle = findViewById( R.id.toggle );
            btnPinCode = findViewById( R.id.group_security_change_pincode );
            btnToggle = findViewById( R.id.btn_security_sub_toggle );

            groupBio = findViewById( R.id.group_security_bio );
        }
    }
}
