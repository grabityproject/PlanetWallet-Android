package io.grabity.planetwallet.Views.p7_Setting.Activity.Setting;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.managers.BioMetricManager;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Widgets.CustomToast;
import io.grabity.planetwallet.Widgets.ToggleButton;
import io.grabity.planetwallet.Widgets.ToolBar;

public class SecurityActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

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
        viewMapper.toolBar.setLeftButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.btnPinCode.setOnClickListener( this );
        viewMapper.btnBioAuth.setOnClickListener( this );

        if ( !BioMetricManager.getInstance( ).isHardwareCheck( this ) ) {
            viewMapper.groupBio.setVisibility( View.GONE );
        }
    }

    @Override
    protected void setData( ) {
        super.setData( );
        setBioToggleView( false );
    }

    @Override
    protected void onResume( ) {
        super.onResume( );
        Utils.postDelayed( ( ) -> setBioToggleView( true ), 400 );
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
        if ( v == viewMapper.btnBioAuth ) {

            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ) {
                requestPermissions( C.requestCode.BIO_METRIC, Manifest.permission.USE_BIOMETRIC );
            } else {
                requestPermissions( C.requestCode.BIO_METRIC, Manifest.permission.USE_FINGERPRINT );
            }

        } else if ( v == viewMapper.btnPinCode ) {
            setTransition( Transition.SLIDE_UP );
            sendAction(
                    C.requestCode.SETTING_CHANGE_PINCODE,
                    PinCodeCertificationActivity.class );
        }
    }

    @Override
    protected void neverNotAllowed( int code, String permission ) {
        super.neverNotAllowed( code, permission );
        CustomToast.makeText( this, localized( R.string.security_bio_permission_never_not_allowed_title ) ).show( );
    }

    @Override
    protected void permissionNotAllowed( int code, String permission ) {
        super.permissionNotAllowed( code, permission );
        CustomToast.makeText( this, localized( R.string.security_bio_permission_not_allowed_title ) ).show( );
    }

    @Override
    protected void permissionsAllowed( int code ) {
        super.permissionsAllowed( code );
        if ( BioMetricManager.getInstance( ).isFingerPrintCheck( this ) ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( C.requestCode.BIO_METRIC, PinCodeCertificationActivity.class );
        } else {
            CustomToast.makeText( this, localized( R.string.security_bio_not_registered_fingerprint ) ).show( );
        }
    }

    protected void setBioToggleView( boolean animate ) {
        if ( BioMetricManager.getInstance( ).isFingerPrintCheck( this ) ) {
            viewMapper.toggleBioAuth.setOn( Utils.equals( Utils.getPreferenceData( this, C.pref.BIO_METRIC, String.valueOf( false ) ), String.valueOf( true ) ), animate );
        } else {
            viewMapper.toggleBioAuth.setOn( false, animate );
        }
    }

    public class ViewMapper {

        ToolBar toolBar;

        View btnPinCode;

        ViewGroup groupBio;
        View btnBioAuth;
        ToggleButton toggleBioAuth;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );

            btnPinCode = findViewById( R.id.group_security_change_pincode );

            groupBio = findViewById( R.id.group_security_bio );
            btnBioAuth = findViewById( R.id.btn_security_bio );
            toggleBioAuth = findViewById( R.id.toggle_security_bio_auth );

        }
    }
}
