package io.grabity.planetwallet.Views.p4_Main.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Coin;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.StretchImageView;

public class CoinAdapter extends AdvanceArrayAdapter< Coin > {

    public CoinAdapter( Context context, ArrayList< Coin > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        if ( getObjects( ).get( position ).getCoin( ).equals( "ETH" ) ){
            return new ETHItems( View.inflate( getContext( ), R.layout.item_main_eth, null ) );
        } else{
            return new BTCItems( View.inflate( getContext( ), R.layout.item_main_btc, null ) );
        }
    }

    @Override
    public void bindData( ViewMapper viewMapper, Coin item, int position ) {

        if ( item.getCoin( ).equals( "ETH" ) ){
            ( ( ETHItems ) viewMapper ).ethIcon.setImageResource( item.getIcon( ) );
            ( ( ETHItems ) viewMapper ).ethName.setText( item.getCoinName( ) == null ? "ETH" : item.getCoinName( ) );
            ( ( ETHItems ) viewMapper ).ethBalance.setText( item.getBalance( ) == null ? "" : item.getBalance( ) );
            ( ( ETHItems ) viewMapper ).ethCurrency.setText( item.getCurrency( ) == null ? "USD" : item.getCurrency( ) );
        } else {
            ( ( BTCItems ) viewMapper ).btcName.setText( item.getWalletName( ) == null ? "" : item.getWalletName( ) );
            ( ( BTCItems ) viewMapper ).btcTime.setText( item.getTransferTime( ) == null ? "" : item.getTransferTime( ) );
            ( ( BTCItems ) viewMapper ).btcBalance.setText( item.getTransferTime( ) == null ? "" : item.getBalance( ) );
            ( ( BTCItems ) viewMapper ).btcIcon.setImageResource( item.getIcon( ) );
        }

    }

    class ETHItems extends ViewMapper {

        StretchImageView ethIcon;
        TextView ethName;
        TextView ethBalance;
        TextView ethCurrency;


        public ETHItems( View itemView ) {
            super( itemView );
            ethIcon = findViewById( R.id.image_item_main_eth_icon );
            ethName = findViewById( R.id.text_item_main_eth_name );
            ethBalance = findViewById( R.id.text_item_main_eth_balance );
            ethCurrency = findViewById( R.id.text_item_main_eth_currency );
        }
    }

    class BTCItems extends ViewMapper {

        StretchImageView btcIcon;
        TextView btcName;
        TextView btcBalance;
        TextView btcTime;

        public BTCItems( View itemView ) {
            super( itemView );
            btcIcon = findViewById( R.id.image_item_main_btc_arrow );
            btcName = findViewById( R.id.text_item_main_btc_name );
            btcTime = findViewById( R.id.text_item_main_btc_time );
            btcBalance = findViewById( R.id.text_item_main_btc_balance );
        }
    }


}
