package io.grabity.planetwallet.Views.p4_Main.Components;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.Common.components.ViewComponent;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Views.p5_Token.Activity.TokenAddActivity;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.RoundRelativeLayout;

public class FooterViewComponent extends ViewComponent {

    private ViewMapper viewMapper;
    private AdvanceRecyclerView recyclerView;
    private Planet planet;

    public FooterViewComponent( PlanetWalletActivity activity, AdvanceRecyclerView recyclerView, View view ) {
        super( activity );
        setView( view );
        this.recyclerView = recyclerView;
        viewMapper = new ViewMapper( );
        viewInit( );
    }

    public void viewInit( ) {
        viewMapper.btnAddToken.setOnClickListener( this );
    }

    public void setPlanet( Planet planet ) {
        this.planet = planet;

        viewMapper.groupAddToken.setVisibility( planet.getCoinType( ).equals( CoinType.ETH.getCoinType( ) ) ? View.VISIBLE : View.GONE );
        if ( planet.getCoinType( ).equals( CoinType.BTC.getCoinType( ) ) ) {
            viewMapper.groupMessage.setVisibility( Objects.requireNonNull( recyclerView.getAdapter( ) ).getItemCount( ) <= 2 ? View.VISIBLE : View.GONE );
        }

        viewMapper.btnAddToken.setBorder_color_normal( Color.parseColor( getActivity( ).getCurrentTheme( ) ? "#EDEDED" : "#1E1E28" ) );
        viewMapper.btnAddToken.setBorder_color_normal( Color.parseColor( getActivity( ).getCurrentTheme( ) ? "#EDEDED" : "#1E1E28" ) );

    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnAddToken ) {
            if ( planet != null ) {
                getActivity( )
                        .setTransition( PlanetWalletActivity.Transition.SLIDE_SIDE )
                        .sendAction(
                                C.requestCode.MAIN_TOKEN_ADD,
                                TokenAddActivity.class,
                                Utils.createSerializableBundle( C.bundleKey.PLANET, planet )
                        );
            }
        }
    }

    class ViewMapper {

        RoundRelativeLayout btnAddToken;

        ViewGroup groupAddToken;
        ViewGroup groupMessage;

        ViewMapper( ) {
            btnAddToken = findViewById( R.id.btn_footer_main_manage_token );
            groupAddToken = findViewById( R.id.group_footer_main_manage_token );
            groupMessage = findViewById( R.id.group_footer_main_bit_message );
        }
    }

}
