package io.grabity.planetwallet.Views.p3_Wallet.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.Common.components.PlanetWalletFragment;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p3_Wallet.Activity.PlanetNameActivity;
import io.grabity.planetwallet.Views.p3_Wallet.Activity.WalletImportActivity;
import io.grabity.planetwallet.Widgets.RoundEditText;

public class JSONImportFragment extends PlanetWalletFragment implements View.OnClickListener, TextWatcher {

    private ViewMapper viewMapper;
    private WalletImportActivity walletImportActivity;

    public JSONImportFragment( ) {
    }

    public static JSONImportFragment newInstance( ) {
        JSONImportFragment fragment = new JSONImportFragment( );
        return fragment;
    }

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.fragment_json_import );

        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.passwordVisible.setOnClickListener( this );
        viewMapper.passwordInvisible.setOnClickListener( this );

        viewMapper.btnSubmit.setEnabled( false );
        viewMapper.btnSubmit.setOnClickListener( this );

        viewMapper.etJson.addTextChangedListener( this );
    }

    @Override
    public void setData( ) {
        super.setData( );
        walletImportActivity = ( WalletImportActivity ) getPlanetWalletActivity( );
    }

    @Override
    public void onClick( View v ) {
        if ( v == viewMapper.passwordInvisible || v == viewMapper.passwordVisible ) {
            viewMapper.passwordInvisible.setVisibility( viewMapper.passwordInvisible.getVisibility( ) == View.GONE ? View.VISIBLE : View.GONE );
            viewMapper.passwordVisible.setVisibility( viewMapper.passwordVisible.getVisibility( ) == View.GONE ? View.VISIBLE : View.GONE );
            viewMapper.etPassword.setInputType( viewMapper.passwordInvisible.getVisibility( ) == View.GONE ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
            viewMapper.etPassword.setSelection( viewMapper.etPassword.length( ) );
        } else if ( v == viewMapper.btnSubmit ) {
            walletImportActivity.setTransition( PlanetWalletActivity.Transition.SLIDE_UP );
            walletImportActivity.sendAction( C.requestCode.PLANET_ADD, PlanetNameActivity.class );
        }
    }

    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

    }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count ) {
        if ( viewMapper.etJson.getText( ) == null ) return;
        viewMapper.btnSubmit.setEnabled( viewMapper.etJson.getText( ).toString( ).trim( ).length( ) != 0 );
    }

    @Override
    public void afterTextChanged( Editable s ) {

    }

    public class ViewMapper {

        RoundEditText etPassword;
        RoundEditText etJson;
        View passwordInvisible;
        View passwordVisible;
        View btnSubmit;

        public ViewMapper( ) {
            etPassword = findViewById( R.id.et_json_import_password );
            etJson = findViewById( R.id.et_json_import_json );
            passwordInvisible = findViewById( R.id.btn_json_import_password_invisible );
            passwordVisible = findViewById( R.id.btn_json_import_password_visible );
            btnSubmit = findViewById( R.id.btn_submit );
        }
    }
}
