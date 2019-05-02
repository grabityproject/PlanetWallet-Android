package io.grabity.planetwallet.Views.p5_Token.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import io.grabity.planetwallet.Common.components.PlanetWalletFragment;
import io.grabity.planetwallet.R;

public class TokenListFragment extends PlanetWalletFragment {

    public TokenListFragment( ) {
    }

    public static TokenListFragment newInstance( ) {
        TokenListFragment fragment = new TokenListFragment( );
        return fragment;
    }

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.fragment_token_list );
    }

}
