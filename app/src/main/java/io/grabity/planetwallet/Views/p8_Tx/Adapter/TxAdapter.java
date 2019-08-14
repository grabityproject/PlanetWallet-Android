package io.grabity.planetwallet.Views.p8_Tx.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Tx;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.StretchImageView;

public class TxAdapter extends AdvanceArrayAdapter< Tx > {

    String planetAddress;

    public TxAdapter( Context context, ArrayList< Tx > objects ) {
        super( context, objects );
    }

    public TxAdapter( Context context, ArrayList< Tx > objects, String planetName ) {
        super( context, objects );
        this.planetAddress = planetName;
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new TxItem( View.inflate( getContext( ), R.layout.item_tx_list, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, Tx item, int position ) {

        if ( Utils.equals( item.getStatus( ), C.transferStatus.PENDING ) ) {
            ( ( TxItem ) viewMapper ).imageStatusIcon.setImageResource( !getTheme( ) ? R.drawable.image_tx_list_pending_gray : R.drawable.image_tx_list_pending_blue );
            ( ( TxItem ) viewMapper ).textStatus.setText( "pending" );
            ( ( TxItem ) viewMapper ).textBalance.setText( String.format( "-%s", Utils.moveLeftPoint( item.getAmount( ), 18 ) ) );
        } else if ( Utils.equals( item.getStatus( ), C.transferStatus.CONFIRMED ) ) {
            ( ( TxItem ) viewMapper ).imageStatusIcon.setImageResource( Utils.equals( planetAddress, item.getFrom( ) ) ? R.drawable.image_tx_list_sent : R.drawable.image_tx_list_received );
            ( ( TxItem ) viewMapper ).textStatus.setText( Utils.equals( planetAddress, item.getFrom( ) ) ? "Sent" : "Received" );
            ( ( TxItem ) viewMapper ).textBalance.setText( Utils.equals( planetAddress, item.getFrom( ) ) ? String.format( "-%s", Utils.moveLeftPoint( item.getAmount( ), 18 ) ) : String.format( "%s", Utils.moveLeftPoint( item.getAmount( ), 18 ) ) );
            if ( Utils.equals( planetAddress, item.getFrom( ) ) )
                ( ( TxItem ) viewMapper ).textBalance.setTextColor( Color.parseColor( "#00E291" ) );
        }
        ( ( TxItem ) viewMapper ).textSymbol.setText( item.getSymbol( ) );
        ( ( TxItem ) viewMapper ).textCurrency.setText( String.format( "%s USD ", Utils.moveLeftPoint( item.getAmount( ), 18 ) ) );

    }

    class TxItem extends ViewMapper {

        StretchImageView imageStatusIcon;
        TextView textStatus;
        TextView textBalance;
        TextView textSymbol;
        TextView textCurrency;


        public TxItem( View itemView ) {
            super( itemView );

            imageStatusIcon = findViewById( R.id.image_item_tx_list_icon );
            textStatus = findViewById( R.id.text_item_tx_list_status );
            textBalance = findViewById( R.id.text_item_tx_list_balance );
            textSymbol = findViewById( R.id.text_item_tx_list_symbol );
            textCurrency = findViewById( R.id.text_item_tx_list_currency );

        }
    }
}
