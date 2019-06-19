package io.grabity.planetwallet.Views.p6_Transfer.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.StretchImageView;

public class TransferAdapter extends AdvanceArrayAdapter< Planet > {

    public TransferAdapter( Context context, ArrayList< Planet > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new NameSearchItem( View.inflate( getContext( ), R.layout.item_transfer_name_search, null ) );
//        if ( !isChoieSearch( ) ) {
//            return new NameSearchItem( View.inflate( getContext( ), R.layout.item_transfer_name_search, null ) );
//        } else {
//            PLog.e( "viewMapping Address");
//            return new AddressSearchItem( View.inflate( getContext( ), R.layout.item_transfer_address_search, null ) );
//        }
    }

    @Override
    public void bindData( ViewMapper viewMapper, Planet item, int position ) {

        ( ( NameSearchItem ) viewMapper ).planet.setData( item.getAddress( ) );
        ( ( NameSearchItem ) viewMapper ).textName.setText( item.getName( ) );
        ( ( NameSearchItem ) viewMapper ).textNameAddress.setText( Utils.addressReduction( item.getAddress( ) ) );

//        if ( !isChoieSearch( ) ) {
//            ( ( NameSearchItem ) viewMapper ).planet.setData( item.getAddress( ) );
//            ( ( NameSearchItem ) viewMapper ).textName.setText( item.getName( ) );
//            ( ( NameSearchItem ) viewMapper ).textNameAddress.setText( Utils.addressReduction( item.getAddress( ) ) );
//
//        } else {
//            PLog.e( "bindData Address");
//            ( ( AddressSearchItem ) viewMapper ).textAddress.setText( item.getAddress( ) );
//        }
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

//    class AddressSearchItem extends ViewMapper {
//        StretchImageView icon;
//        TextView textAddress;
//
//        public AddressSearchItem( View itemView ) {
//            super( itemView );
//            icon = findViewById( R.id.image_item_transfer_address_search_icon );
//            textAddress = findViewById( R.id.text_item_transfer_address_search_address );
//        }
//    }
}
