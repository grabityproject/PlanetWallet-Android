package io.grabity.planetwallet.Views.p3_Wallet.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletFragment;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.managers.BitCoinManager;
import io.grabity.planetwallet.MiniFramework.wallet.managers.EthereumManager;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Views.p3_Wallet.Activity.WalletImportActivity;
import io.grabity.planetwallet.Widgets.RoundEditText;

public class MnemonicImportFragment extends PlanetWalletFragment implements View.OnClickListener, TextWatcher {

    private ViewMapper viewMapper;
    private WalletImportActivity walletImportActivity;

    public MnemonicImportFragment( ) {
    }

    public static MnemonicImportFragment newInstance( ) {
        MnemonicImportFragment fragment = new MnemonicImportFragment( );
        return fragment;
    }

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.fragment_mnemonic_import );

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

        viewMapper.etMnemonic.addTextChangedListener( this );

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

            if ( getPlanetWalletActivity( ).getPlanetWalletApplication( ).getPINCODE( ) != null ) {


                if ( getPlanetWalletActivity( ).getInt( C.bundleKey.COINTYPE, -1 ) == CoinType.BTC.getCoinType( ) ) {

                    try {
                        walletImportActivity.setPlanet( BitCoinManager.getInstance( ).importMnemonic( viewMapper.etMnemonic.getText( ).toString( ), viewMapper.etPassword.getText( ).toString( ), getPlanetWalletActivity( ).getPlanetWalletApplication( ).getPINCODE( ) ) );
                    } catch ( Exception e ) {
                        PLog.e( "Fail" );
                    }

                } else if ( getPlanetWalletActivity( ).getInt( C.bundleKey.COINTYPE, -1 ) == CoinType.ETH.getCoinType( ) ) {
                    try {
                        walletImportActivity.setPlanet( EthereumManager.getInstance( ).importMnemonic( viewMapper.etMnemonic.getText( ).toString( ), viewMapper.etPassword.getText( ).toString( ), getPlanetWalletActivity( ).getPlanetWalletApplication( ).getPINCODE( ) ) );
                    } catch ( Exception e ) {
                        PLog.e( "Fail" );
                    }
                }

            } else {
                getPlanetWalletActivity( ).sendAction( C.requestCode.PINCODE_IS_NULL, PinCodeCertificationActivity.class );
            }

        }

    }

    @Override
    protected void onUpdateTheme( boolean theme ) {
        super.onUpdateTheme( theme );
        if ( theme ) {
            viewMapper.etMnemonic.setTextColor( Color.parseColor( "#000000" ) );
            viewMapper.etMnemonic.setHintTextColor( Color.parseColor( "#aaaaaa" ) );
            viewMapper.etMnemonic.setBackgroundColor( Color.parseColor( "#FCFCFC" ) );
        } else {
            viewMapper.etMnemonic.setTextColor( Color.parseColor( "#FFFFFF" ) );
            viewMapper.etMnemonic.setHintTextColor( Color.parseColor( "#5C5964" ) );
            viewMapper.etMnemonic.setBackgroundColor( Color.parseColor( "#111117" ) );
        }
    }


    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

    }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count ) {
        if ( viewMapper.etMnemonic.getText( ) == null ) return;
        viewMapper.btnSubmit.setEnabled( viewMapper.etMnemonic.getText( ).toString( ).trim( ).length( ) == 0 ? false : true );
    }

    @Override
    public void afterTextChanged( Editable s ) {

    }


    public class ViewMapper {

        RoundEditText etPassword;
        RoundEditText etMnemonic;
        View passwordInvisible;
        View passwordVisible;
        View btnSubmit;

        public ViewMapper( ) {
            etPassword = findViewById( R.id.et_mnemonic_import_password );
            etMnemonic = findViewById( R.id.et_mnemonic_import_mnemonic );
            passwordInvisible = findViewById( R.id.btn_mnemonicImport_password_invisible );
            passwordVisible = findViewById( R.id.btn_mnemonic_import_password_visible );
            btnSubmit = findViewById( R.id.btn_submit );
        }
    }
}
