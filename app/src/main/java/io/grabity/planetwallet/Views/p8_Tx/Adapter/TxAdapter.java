package io.grabity.planetwallet.Views.p8_Tx.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Tx;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.StretchImageView;

public class TxAdapter extends AdvanceArrayAdapter< Tx > {

    //todo 임시
    String planetName;

    public TxAdapter( Context context, ArrayList< Tx > objects ) {
        super( context, objects );
    }

    public TxAdapter( Context context, ArrayList< Tx > objects, String planetName ) {
        super( context, objects );
        this.planetName = planetName;
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new TxItem( View.inflate( getContext( ), R.layout.item_tx_list, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, Tx item, int position ) {

        if ( item.getTo_planet( ) == null ) {
            ( ( TxItem ) viewMapper ).planetView.setVisibility( View.INVISIBLE );
        } else {
            ( ( TxItem ) viewMapper ).planetView.setVisibility( View.VISIBLE );
            ( ( TxItem ) viewMapper ).planetView.setData( Utils.equals( planetName, item.getTo_planet( ) ) ? item.getFrom( ) : item.getTo( ) );
        }
        ( ( TxItem ) viewMapper ).textAddress.setText( Utils.equals( planetName, item.getTo_planet( ) ) ? item.getFrom( ) : item.getTo( ) );
        ( ( TxItem ) viewMapper ).textBalance.setText( String.format( "%s " + item.getSymbol( ), new BigDecimal( item.getAmount( ) ).movePointLeft( 18 ).stripTrailingZeros( ).toString( ) ) );
        ( ( TxItem ) viewMapper ).textDate.setText( Utils.dateFormat( new Date( Long.parseLong( item.getCreated_at( ) ) ), "MMMM dd, HH:mm" ) );
        ( ( TxItem ) viewMapper ).imageIcon.setImageResource( Utils.equals( planetName, item.getTo_planet( ) ) ? R.drawable.image_btc_increase : R.drawable.image_btc_discrease );


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
