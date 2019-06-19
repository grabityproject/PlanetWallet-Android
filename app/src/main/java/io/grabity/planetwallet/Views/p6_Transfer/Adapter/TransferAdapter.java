package io.grabity.planetwallet.Views.p6_Transfer.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.PlanetView;

public class TransferAdapter extends AdvanceArrayAdapter< Planet > {

    public TransferAdapter( Context context, ArrayList< Planet > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new NameSearchItem( View.inflate( getContext( ), R.layout.item_transfer_name_search, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, Planet item, int position ) {

        ( ( NameSearchItem ) viewMapper ).planet.setData( item.getAddress( ) );
        ( ( NameSearchItem ) viewMapper ).textName.setText( item.getName( ) );
        ( ( NameSearchItem ) viewMapper ).textNameAddress.setText( Utils.addressReduction( item.getAddress( ) ) );

    }


    class NameSearchItem extends ViewMapper {

        PlanetView planet;
        TextView textName;
        TextView textNameAddress;

        public NameSearchItem( View itemView ) {
            super( itemView );
            planet = findViewById( R.id.planet_item_transfer_name_search_planetview );
            textName = findViewById( R.id.text_item_transfer_name_search_name );
            textNameAddress = findViewById( R.id.text_item_transfer_name_search_address );
        }
    }

}
