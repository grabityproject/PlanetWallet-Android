package io.grabity.planetwallet.Views.p3_Wallet.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import io.grabity.planetwallet.Common.components.PlanetWalletFragment;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.RoundEditText;

public class PrivateKeyImportFragment extends PlanetWalletFragment implements View.OnClickListener, TextWatcher {

    private ViewMapper viewMapper;

    public PrivateKeyImportFragment ( ) {
    }

    public static PrivateKeyImportFragment newInstance( ){
        PrivateKeyImportFragment fragment = new PrivateKeyImportFragment( );
        return fragment;
    }

    @Override
    public void onCreate ( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.fragment_privatekey_import );
        viewMapper = new ViewMapper( );
        viewInit();
        setData();
    }

    @Override
    protected void viewInit ( ) {
        super.viewInit( );

        viewMapper.passwordVisible.setOnClickListener( this );
        viewMapper.passwordInvisible.setOnClickListener( this );

        viewMapper.btnSubmit.setEnabled( false );
        viewMapper.btnSubmit.setOnClickListener( this );

        viewMapper.etPrivateKey.addTextChangedListener( this );
    }

    @Override
    public void setData ( ) {
        super.setData( );
    }

    @Override
    public void onClick ( View v ) {
        if( v == viewMapper.passwordInvisible || v == viewMapper.passwordVisible ){
            viewMapper.passwordInvisible.setVisibility( viewMapper.passwordInvisible.getVisibility() == View.GONE ? View.VISIBLE : View.GONE );
            viewMapper.passwordVisible.setVisibility( viewMapper.passwordVisible.getVisibility() == View.GONE ? View.VISIBLE : View.GONE );
            viewMapper.etPrivateKey.setInputType( viewMapper.passwordInvisible.getVisibility() == View.GONE ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
            viewMapper.etPrivateKey.setSelection( viewMapper.etPrivateKey.length() );
        } else if( v == viewMapper.btnSubmit ) {
            Toast.makeText( getContext( ) , "PrivateKeyImport" , Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

    }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count ) {

        if ( viewMapper.etPrivateKey.getText( ) == null ) return;
        viewMapper.btnSubmit.setEnabled( viewMapper.etPrivateKey.getText( ).toString( ).trim( ).length( ) == 0 ? false : true );
    }

    @Override
    public void afterTextChanged( Editable s ) {

    }

    public class ViewMapper {

        RoundEditText etPrivateKey;
        View passwordInvisible;
        View passwordVisible;
        View btnSubmit;

        public ViewMapper ( ) {
            etPrivateKey = findViewById( R.id.et_privatekey_import_privatekey );
            passwordInvisible = findViewById( R.id.btn_privatekey_import_password_invisible );
            passwordVisible = findViewById( R.id.btn_privatekey_import_password_visible );
            btnSubmit = findViewById( R.id.btn_submit );
        }
    }
}
