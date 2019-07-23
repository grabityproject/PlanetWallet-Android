package io.grabity.planetwallet.Views.p4_Main.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.BTC;
import io.grabity.planetwallet.VO.MainItems.ERC20;
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
        if ( getObjects( ).get( position ).getCoinType( ).equals( CoinType.BTC.getCoinType( ) ) ) {
            return new BTCItem( View.inflate( getContext( ), R.layout.item_main_btc, null ) );
        } else if ( Math.abs( getObjects( ).get( position ).getCoinType( ) ) == CoinType.ETH.getCoinType( ) ) {
            return new ETHItem( View.inflate( getContext( ), R.layout.item_main_eth, null ) );
        } else {
            return null;
        }
    }

    @Override
    public void bindData( ViewMapper viewMapper, MainItem item, int position ) {

        if ( Utils.equals( CoinType.BTC.getCoinType( ), item.getCoinType( ) ) ) {

            BTC btc = ( BTC ) item;
            ( ( BTCItem ) viewMapper ).imageIcon.setImageResource(
                    btc.getBalance( ) != null && btc.getBalance( ).contains( "-" ) ?
                            R.drawable.image_btc_increase : R.drawable.image_btc_discrease );
            ( ( BTCItem ) viewMapper ).textName.setText( btc.getPlanetName( ) );
            ( ( BTCItem ) viewMapper ).textBalance.setText( btc.getBalance( ).replace( "-", "" ) );
            ( ( BTCItem ) viewMapper ).textTime.setText( btc.getDate( ) );

        } else if ( Utils.equals( CoinType.ETH.getCoinType( ), item.getCoinType( ) ) ) {


            ETH eth = ( ETH ) item;
            ( ( ETHItem ) viewMapper ).textName.setText( CoinType.ETH.name( ) );
            ( ( ETHItem ) viewMapper ).textBalance.setText( eth.getBalance( ) );
            //todo 화폐단위 임시고정
            ( ( ETHItem ) viewMapper ).textPrice.setText( String.format( "%s USD", eth.getBalance( ) ) );

        } else if ( Utils.equals( CoinType.ERC20.getCoinType( ), item.getCoinType( ) ) ) {

            ERC20 erc20 = ( ERC20 ) item;
            ImageLoader.getInstance( ).displayImage( Route.URL( erc20.getImg_path( ) ), ( ( ETHItem ) viewMapper ).imageIcon );
            ( ( ETHItem ) viewMapper ).textName.setText( erc20.getName( ) );
            ( ( ETHItem ) viewMapper ).textBalance.setText( erc20.getBalance( ) );
            //todo 화폐단위 임시고정
            ( ( ETHItem ) viewMapper ).textPrice.setText( String.format( "%s USD", erc20.getBalance( ) ) );

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
