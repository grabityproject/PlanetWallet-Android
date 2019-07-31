package io.grabity.planetwallet.Views.p3_Wallet.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Objects;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletFragment;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.managers.BitCoinManager;
import io.grabity.planetwallet.MiniFramework.wallet.managers.EthereumManager;
import io.grabity.planetwallet.MiniFramework.wallet.store.PlanetStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Views.p3_Wallet.Activity.WalletImportActivity;
import io.grabity.planetwallet.Widgets.RoundEditText;

public class PrivateKeyImportFragment extends PlanetWalletFragment implements View.OnClickListener, TextWatcher {

    private ViewMapper viewMapper;
    private WalletImportActivity walletImportActivity;

    public PrivateKeyImportFragment( ) {
    }

    public static PrivateKeyImportFragment newInstance( ) {
        PrivateKeyImportFragment fragment = new PrivateKeyImportFragment( );
        return fragment;
    }

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.fragment_privatekey_import );
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

        viewMapper.etPrivateKey.addTextChangedListener( this );
        viewMapper.etPrivateKey.requestFocus( );
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
            viewMapper.etPrivateKey.setInputType( viewMapper.passwordInvisible.getVisibility( ) == View.GONE ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );


            viewMapper.etPrivateKey.setSelection( viewMapper.etPrivateKey.length( ) );
        } else if ( v == viewMapper.btnSubmit ) {

            if ( C.PINCODE != null ) {
                if ( getPlanetWalletActivity( ).getInt( C.bundleKey.COINTYPE, -1 ) == CoinType.BTC.getCoinType( ) ) {

                    try {
                        Planet btcPlanet = BitCoinManager.getInstance( ).importPrivateKey( Objects.requireNonNull( viewMapper.etPrivateKey.getText( ) ).toString( ), C.PINCODE );

                        if ( PlanetStore.getInstance( ).getPlanet( btcPlanet.getKeyId( ) ) == null ) {
                            walletImportActivity.setPlanet( btcPlanet );
                        } else {
                            Toast.makeText( getActivity( ), getString( R.string.privatekey_import_exists_title ), Toast.LENGTH_SHORT ).show( );
                        }

                    } catch ( Exception e ) {
                        Toast.makeText( getActivity( ), getString( R.string.privatekey_import_not_match_title ), Toast.LENGTH_SHORT ).show( );
                    }

                } else if ( getPlanetWalletActivity( ).getInt( C.bundleKey.COINTYPE, -1 ) == CoinType.ETH.getCoinType( ) ) {

                    try {
                        Planet ethPlanet = EthereumManager.getInstance( ).importPrivateKey( Objects.requireNonNull( viewMapper.etPrivateKey.getText( ) ).toString( ), C.PINCODE );

                        if ( PlanetStore.getInstance( ).getPlanet( ethPlanet.getKeyId( ) ) == null ) {
                            walletImportActivity.setPlanet( ethPlanet );
                        } else {
                            Toast.makeText( getActivity( ), getString( R.string.privatekey_import_exists_title ), Toast.LENGTH_SHORT ).show( );
                        }

                    } catch ( Exception e ) {
                        Toast.makeText( getActivity( ), getString( R.string.privatekey_import_not_match_title ), Toast.LENGTH_SHORT ).show( );
                    }
                }

            } else {
                getPlanetWalletActivity( ).sendAction( C.requestCode.PINCODE_IS_NULL, PinCodeCertificationActivity.class );
            }
        }
    }


    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

    }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count ) {

        if ( viewMapper.etPrivateKey.getText( ) == null ) return;
        viewMapper.btnSubmit.setEnabled( viewMapper.etPrivateKey.getText( ).toString( ).trim( ).length( ) > 0 );
    }

    @Override
    public void afterTextChanged( Editable s ) {

    }

    public class ViewMapper {

        RoundEditText etPrivateKey;
        View passwordInvisible;
        View passwordVisible;
        View btnSubmit;

        public ViewMapper( ) {
            etPrivateKey = findViewById( R.id.et_privatekey_import_privatekey );
            passwordInvisible = findViewById( R.id.btn_privatekey_import_password_invisible );
            passwordVisible = findViewById( R.id.btn_privatekey_import_password_visible );
            btnSubmit = findViewById( R.id.btn_submit );
        }
    }
}
