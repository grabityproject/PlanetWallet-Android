package io.grabity.planetwallet.Views.p5_Token.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import io.grabity.planetwallet.Common.components.PlanetWalletFragment;
import io.grabity.planetwallet.R;

public class CustomTokenFragment extends PlanetWalletFragment {

    public CustomTokenFragment( ) {
    }

    public static CustomTokenFragment newInstance( ) {
        CustomTokenFragment fragment = new CustomTokenFragment( );
        return fragment;
    }

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.fragment_custom_token );
    }

}
