package io.grabity.planetwallet.Views.p7_Setting.Activity.Planet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Widgets.RoundEditText;
import io.grabity.planetwallet.Widgets.ToolBar;

public class RenamePlanetActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, TextWatcher {

    private ViewMapper viewMapper;
    private Planet planet;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_rename_planet );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.etName.addTextChangedListener( this );
        viewMapper.btnSubmit.setOnClickListener( this );
        viewMapper.btnNameClear.setOnClickListener( this );

        viewMapper.btnSubmit.setEnabled( false );

    }

    @Override
    protected void setData( ) {
        super.setData( );
        planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
        viewMapper.etName.setText( planet.getName( ) );
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnSubmit ) {
            if ( viewMapper.etName.getText( ) == null ) return;
            Utils.hideKeyboard( this, getCurrentFocus( ) );
            planet.setName( viewMapper.etName.getText( ).toString( ) );
            setResult( RESULT_OK, new Intent( ).putExtra( C.bundleKey.PLANET, planet ) );
            super.onBackPressed( );

        } else if ( v == viewMapper.btnNameClear ) {
            viewMapper.etName.setText( "" );
        }
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_CLOSE ) ) {
            Utils.hideKeyboard( this, getCurrentFocus( ) );
            super.onBackPressed( );

        }
    }

    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

    }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count ) {
        viewMapper.btnSubmit.setEnabled( viewMapper.etName.getText( ).toString( ).trim( ).length( ) == 0 ? false : true );
        viewMapper.btnNameClear.setVisibility( viewMapper.etName.getText( ).toString( ).trim( ).length( ) == 0 ? View.GONE : View.VISIBLE );
    }

    @Override
    public void afterTextChanged( Editable s ) {

    }

    public class ViewMapper {
        ToolBar toolBar;
        RoundEditText etName;
        View btnNameClear;
        View btnSubmit;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            etName = findViewById( R.id.et_rename_planet_name );
            btnNameClear = findViewById( R.id.btn_rename_planet_clear );
            btnSubmit = findViewById( R.id.btn_submit );
        }
    }
}
