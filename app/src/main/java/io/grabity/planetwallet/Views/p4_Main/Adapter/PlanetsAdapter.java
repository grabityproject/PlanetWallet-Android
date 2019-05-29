package io.grabity.planetwallet.Views.p4_Main.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.PlanetView;

public class PlanetsAdapter extends AdvanceArrayAdapter< Planet > {


    public PlanetsAdapter( Context context, ArrayList< Planet > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new PlanetsItem( View.inflate( getContext( ), R.layout.item_main_planets, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, Planet item, int position ) {
        ( ( PlanetsItem ) viewMapper ).planetView.setData( item.getAddress( ) == null ? "" : item.getAddress( ) );
        ( ( PlanetsItem ) viewMapper ).currency.setText( item.getCurrency( ) == null ? "ETH" : item.getCurrency( )+" Universe" );
        ( ( PlanetsItem ) viewMapper ).walletName.setText( item.getWalletName( ) == null ? "WalletName" : item.getWalletName( ) );
    }


    class PlanetsItem extends ViewMapper {

        PlanetView planetView;
        TextView currency;
        TextView walletName;

        public PlanetsItem( View itemView ) {
            super( itemView );

            planetView = findViewById( R.id.planetview_item_main_planets );
            currency = findViewById( R.id.text_item_main_planets_currency );
            walletName = findViewById( R.id.text_item_main_planets_name );

        }
    }
}
