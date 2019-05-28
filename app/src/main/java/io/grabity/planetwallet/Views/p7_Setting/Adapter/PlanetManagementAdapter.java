package io.grabity.planetwallet.Views.p7_Setting.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.OnInsideItemClickListener;
import io.grabity.planetwallet.Widgets.PlanetView;

public class PlanetManagementAdapter extends AdvanceArrayAdapter< Planet > {

    OnInsideItemClickListener onInsideItemClickListener;

    public PlanetManagementAdapter( Context context, ArrayList< Planet > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new PlanetItem( View.inflate( getContext( ), R.layout.item_planet_management, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, Planet item, int position ) {
        ( ( PlanetItem ) viewMapper ).planetView.setData( item.getAddress( ) == null ? "" : item.getAddress( ) );
        ( ( PlanetItem ) viewMapper ).currency.setText( item.getCurrency( ) == null ? "ETH" : item.getCurrency( ) );
        ( ( PlanetItem ) viewMapper ).walletName.setText( item.getWalletName( ) == null ? "WalletName" : item.getWalletName( ) );
        ( ( PlanetItem ) viewMapper ).address.setText( item.getAddress( ) == null ? "address" : item.getAddress( ) );

//        ( ( PlanetItem ) viewMapper ).clickView.setOnClickListener( v -> {
//            item.setCheck( !item.isCheck( ) );
//            ( ( PlanetWalletActivity ) getContext( ) ).sendAction( DetailPlanetActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, item ) );
//        } );
    }

    public void setOnInsideItemClickListener( OnInsideItemClickListener onInsideItemClickListener ) {
        this.onInsideItemClickListener = onInsideItemClickListener;
    }


    class PlanetItem extends ViewMapper {

        PlanetView planetView;
        TextView currency;
        TextView walletName;
        TextView address;
        View clickView;


        public PlanetItem( View itemView ) {
            super( itemView );

            planetView = findViewById( R.id.planetview_item_detailplanet );
            currency = findViewById( R.id.text_item_detailplanet_currencyname );
            walletName = findViewById( R.id.text_item_detailplanet_walletname );
            address = findViewById( R.id.text_item_detailplanet_address );

        }
    }
}
