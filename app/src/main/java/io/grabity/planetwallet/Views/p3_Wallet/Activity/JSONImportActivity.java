package io.grabity.planetwallet.Views.p3_Wallet.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.R;


public class JSONImportActivity extends PlanetWalletActivity {

    private ViewMapper viewMapper;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_json_import );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
    }

    @Override
    protected void setData( ) {
        super.setData( );
    }

    public class ViewMapper {

        public ViewMapper( ) {

        }

    }
}