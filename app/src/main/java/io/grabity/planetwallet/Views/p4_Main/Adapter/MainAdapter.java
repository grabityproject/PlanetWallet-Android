package io.grabity.planetwallet.Views.p4_Main.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.BTC;
import io.grabity.planetwallet.VO.MainItems.CoinType;
import io.grabity.planetwallet.VO.MainItems.ETH;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.StretchImageView;

public class MainAdapter extends AdvanceArrayAdapter< MainItem > {

    public MainAdapter( Context context, ArrayList< MainItem > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        if ( getObjects( ).get( position ).getCoinType( ).equals( CoinType.BTC ) ) {
            return new BTCItem( View.inflate( getContext( ), R.layout.item_main_btc, null ) );
        } else if ( getObjects( ).get( position ).getCoinType( ).equals( CoinType.ETH ) || getObjects( ).get( position ).getCoinType( ).equals( CoinType.ERC20 ) ) {
            return new ETHItem( View.inflate( getContext( ), R.layout.item_main_eth, null ) );
        } else {
            return null;
        }
    }

    @Override
    public void bindData( ViewMapper viewMapper, MainItem item, int position ) {

        if ( item.getCoinType( ).equals( CoinType.BTC ) ) {

            BTC btc = ( BTC ) item;
            ( ( BTCItem ) viewMapper ).imageIcon.setImageResource(
                    btc.getBalance( ) != null && btc.getBalance( ).contains( "-" ) ?
                            R.drawable.image_btc_increase : R.drawable.image_btc_discrease );
            ( ( BTCItem ) viewMapper ).textName.setText( btc.getPlanetName( ) );
            ( ( BTCItem ) viewMapper ).textBalance.setText( btc.getBalance( ).replace( "-", "" ) );
            ( ( BTCItem ) viewMapper ).textTime.setText( btc.getDate( ) );

        } else if ( item.getCoinType( ).equals( CoinType.ETH ) || item.getCoinType( ).equals( CoinType.ERC20 ) ) {

            ETH eth = ( ETH ) item;
            ( ( ETHItem ) viewMapper ).imageIcon.setImageResource( eth.getIconRes( ) );
            ( ( ETHItem ) viewMapper ).textName.setText( eth.getName( ) );
            ( ( ETHItem ) viewMapper ).textBalance.setText( eth.getBalance( ) );
            ( ( ETHItem ) viewMapper ).textPrice.setText( eth.getPrice( ) );

        }

    }

    class ETHItem extends ViewMapper {

        StretchImageView imageIcon;
        TextView textName;
        TextView textBalance;
        TextView textPrice;


        public ETHItem( View itemView ) {
            super( itemView );
            imageIcon = findViewById( R.id.image_item_main_eth_icon );
            textName = findViewById( R.id.text_item_main_eth_name );
            textBalance = findViewById( R.id.text_item_main_eth_balance );
            textPrice = findViewById( R.id.text_item_main_eth_currency );
        }
    }

    class BTCItem extends ViewMapper {

        StretchImageView imageIcon;
        TextView textName;
        TextView textBalance;
        TextView textTime;

        public BTCItem( View itemView ) {
            super( itemView );
            imageIcon = findViewById( R.id.image_item_main_btc_arrow );
            textName = findViewById( R.id.text_item_main_btc_name );
            textTime = findViewById( R.id.text_item_main_btc_time );
            textBalance = findViewById( R.id.text_item_main_btc_balance );
        }
    }


}
