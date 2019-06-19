package io.grabity.planetwallet.Views.p6_Transfer.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import io.grabity.planetwallet.Common.components.PlanetWalletActivity;

public class TransferConfimrActivity extends PlanetWalletActivity {

    //Todo
    private ViewMapper viewMapper;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
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
