package io.grabity.planetwallet.Views.p7_Setting.Activity.Planet;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.networktask.Post;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.signer.Signer;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyPairStore;
import io.grabity.planetwallet.MiniFramework.wallet.store.PlanetStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.ErrorResult;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.Widgets.RoundEditText;
import io.grabity.planetwallet.Widgets.ToolBar;

public class RenamePlanetActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, TextWatcher {

    private ViewMapper viewMapper;
    private Planet planet;
    private String planetReName;
    private int cursor;

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

            Planet request = new Planet( );
            request.setPlanet( viewMapper.etName.getText( ).toString( ) );
            request.setSignature(
                    Signer.getInstance( ).sign( viewMapper.etName.getText( ).toString( ),
                            planet.getPrivateKey( KeyPairStore.getInstance( ), getPlanetWalletApplication( ).getPINCODE( ) ) ) );
            request.setAddress( planet.getAddress( ) );

            new Post( this ).action( Route.URL( "planet", CoinType.of( planet.getCoinType( ) ).name( ) ), 0, 0, request );

        } else if ( v == viewMapper.btnNameClear ) {

            viewMapper.etName.setText( "" );

        }
    }

    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {
        super.onReceive( error, requestCode, resultCode, statusCode, result );

        if ( !error ) {
            if ( statusCode == 200 ) {
                if ( requestCode == 0 ) {
                    ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Planet.class );
                    if ( returnVO.isSuccess( ) ) {

                        Planet planet = ( Planet ) returnVO.getResult( );
                        planet.setKeyId( this.planet.getKeyId( ) );
                        PlanetStore.getInstance( ).update( planet );

                        setResult( RESULT_OK );
                        super.onBackPressed( );

                    } else {
                        PLog.e( returnVO.getResult( ).getClass( ) + result );
                    }
                }
            } else {
                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, ErrorResult.class );
                ErrorResult errorResult = ( ErrorResult ) returnVO.getResult( );
                viewMapper.textError.setText( errorResult.getErrorMsg( ) );
            }
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
        if ( Objects.requireNonNull( viewMapper.etName.getText( ) ).length( ) != 0 ) {
            planetReName = viewMapper.etName.getText( ).toString( );
            cursor = viewMapper.etName.getSelectionStart( );
        } else {
            planetReName = "";
        }
    }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count ) {
//        viewMapper.btnSubmit.setEnabled( viewMapper.etName.getText( ).toString( ).trim( ).length( ) == 0 ? false : true );
        if ( viewMapper.etName.getText( ) == null ) return;
        viewMapper.btnSubmit.setEnabled( viewMapper.etName.getText( ).toString( ).trim( ).length( ) != 0 );
        viewMapper.btnNameClear.setVisibility( viewMapper.etName.getText( ).toString( ).trim( ).length( ) == 0 ? View.GONE : View.VISIBLE );
    }

    @Override
    public void afterTextChanged( Editable s ) {
        if ( Objects.requireNonNull( viewMapper.etName.getText( ) ).length( ) != 0 ) {
            if ( !Utils.isPlanetName( viewMapper.etName.getText( ).toString( ) ) ) {
                viewMapper.etName.setText( planetReName );
                try {
                    viewMapper.etName.setSelection( cursor <= 0 ? cursor : cursor - 1 );
                } catch ( Exception e ) {
                    viewMapper.etName.setSelection( viewMapper.etName.getText( ).toString( ).length( ) );
                }

            }
        }

    }

    public class ViewMapper {
        ToolBar toolBar;
        RoundEditText etName;
        View btnNameClear;
        View btnSubmit;

        TextView textError;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            etName = findViewById( R.id.et_rename_planet_name );
            btnNameClear = findViewById( R.id.btn_rename_planet_clear );
            btnSubmit = findViewById( R.id.btn_submit );

            textError = findViewById( R.id.text_error );
        }
    }
}
