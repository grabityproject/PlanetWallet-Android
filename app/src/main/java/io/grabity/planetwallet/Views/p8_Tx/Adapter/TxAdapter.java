package io.grabity.planetwallet.Views.p8_Tx.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Tx;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.StretchImageView;

public class TxAdapter extends AdvanceArrayAdapter< Tx > {


    public TxAdapter( Context context, ArrayList< Tx > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new TxItem( View.inflate( getContext( ), R.layout.item_tx_list, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, Tx item, int position ) {

        ( ( TxItem ) viewMapper ).planetView.setData( item.getAmount( ).contains( "-" ) ? item.getFromAddress( ) : item.getToAddress( ) );
        ( ( TxItem ) viewMapper ).textAddress.setText( item.getAmount( ).contains( "-" ) ? item.getFromAddress( ) : item.getToAddress( ) );
        ( ( TxItem ) viewMapper ).textBalance.setText( String.format( "%s " + item.getSymbol( ), item.getAmount( ) ) );
        ( ( TxItem ) viewMapper ).textDate.setText( item.getDate( ) );

        ( ( TxItem ) viewMapper ).imageIcon.setImageResource( item.getAmount( ).contains( "-" ) ?
                R.drawable.image_btc_increase : R.drawable.image_btc_discrease );

    }

    class TxItem extends ViewMapper {

        PlanetView planetView;
        TextView textAddress;
        TextView textBalance;
        TextView textDate;
        StretchImageView imageIcon;

        public TxItem( View itemView ) {
            super( itemView );

            planetView = findViewById( R.id.planet_item_tx_list_planetview );
            textAddress = findViewById( R.id.text_item_tx_list_address );
            textBalance = findViewById( R.id.text_item_tx_list_balance );
            textDate = findViewById( R.id.text_item_tx_list_date );
            imageIcon = findViewById( R.id.image_item_tx_list_arrow );
        }
    }
}
