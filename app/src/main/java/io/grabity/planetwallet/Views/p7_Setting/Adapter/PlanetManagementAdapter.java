package io.grabity.planetwallet.Views.p7_Setting.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.PlanetView;

public class PlanetManagementAdapter extends AdvanceArrayAdapter< Planet > {

    public PlanetManagementAdapter( Context context, ArrayList< Planet > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new PlanetItem( View.inflate( getContext( ), R.layout.item_planet_management, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, Planet item, int position ) {

        if ( Utils.equals( item.getHide( ), "Y" ) ) viewMapper.getView( ).setAlpha( 0.5f );
        ( ( PlanetItem ) viewMapper ).planetView.setData( item.getAddress( ) );
        ( ( PlanetItem ) viewMapper ).textType.setText( CoinType.of( item.getCoinType( ) ).name( ) );
        ( ( PlanetItem ) viewMapper ).textName.setText( item.getName( ) );
        ( ( PlanetItem ) viewMapper ).textAddress.setText( Utils.addressReduction( item.getAddress( ) ) );

    }


    class PlanetItem extends ViewMapper {

        PlanetView planetView;
        TextView textType;
        TextView textName;
        TextView textAddress;

        public PlanetItem( View itemView ) {
            super( itemView );
            planetView = findViewById( R.id.planetview_item_detailplanet );
            textType = findViewById( R.id.text_item_detailplanet_currencyname );
            textName = findViewById( R.id.text_item_detailplanet_walletname );
            textAddress = findViewById( R.id.text_item_detailplanet_address );
        }
    }
}
