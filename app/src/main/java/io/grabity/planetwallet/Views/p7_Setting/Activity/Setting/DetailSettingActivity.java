package io.grabity.planetwallet.Views.p7_Setting.Activity.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.AbsPopupView.PopupView;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Views.p7_Setting.Adapter.PopupCurrencyAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.ListPopupView.ListPopup;
import io.grabity.planetwallet.Widgets.ToggleButton;
import io.grabity.planetwallet.Widgets.ToolBar;

public class DetailSettingActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, ToggleButton.OnToggleListener, ListPopup.OnListPopupItemClickListener {

    private ViewMapper viewMapper;

    private AdvanceArrayAdapter adapter;
    private ArrayList< String > items;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail_setting );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.toggleButton.setOnToggleListener( this );
        viewMapper.btnPinCode.setOnClickListener( this );
        viewMapper.btnCurrency.setOnClickListener( this );


    }

    @Override
    protected void setData( ) {
        super.setData( );

        items = new ArrayList<>( );
        items.add( "한국어" );
        items.add( "English" );
        items.add( "中國語" );

        adapter = new PopupCurrencyAdapter( this, items );


    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            finish( );
        }
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnPinCode ) {
            sendAction( C.requestCode.SETTING_CHANGE_PINCODE, PinCodeCertificationActivity.class, Utils.createIntBundle( C.bundleKey.PINCODE, PinCodeCertificationActivity.CHANGE ) );
        } else if ( v == viewMapper.btnCurrency ) {
            //Todo popup list

            ListPopup.newInstance( this )
                    .setAdapter( adapter )
                    .setOnListPopupItemClickListener( this )
                    .show( );
        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.SETTING_CHANGE_PINCODE && resultCode == RESULT_OK ) {
            Toast.makeText( this, "PinCode Change Success", Toast.LENGTH_SHORT ).show( );
        }
    }

    @Override
    public void onToggle( ToggleButton toggleButton, boolean isOn ) {

    }

    @Override
    public void onListPopupItemClick( PopupView popup, View view, int position ) {
        viewMapper.textCurrency.setText( items.get( position ) );
        super.onBackPressed( );

    }

    public class ViewMapper {
        ToolBar toolBar;
        ToggleButton toggleButton;
        ViewGroup btnPinCode;
        ViewGroup btnCurrency;
        TextView textCurrency;


        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            toggleButton = findViewById( R.id.toggleBtn );
            btnPinCode = findViewById( R.id.group_detail_setting_change_pincode );
            btnCurrency = findViewById( R.id.group_detail_setting_change_currency );
            textCurrency = findViewById( R.id.text_detail_setting_currency );
        }
    }
}
