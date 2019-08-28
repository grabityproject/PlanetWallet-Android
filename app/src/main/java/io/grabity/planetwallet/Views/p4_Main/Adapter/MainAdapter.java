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
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.StretchImageView;

public class MainAdapter extends AdvanceArrayAdapter< MainItem > {

    public MainAdapter( Context context, ArrayList< MainItem > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new ETHItem( View.inflate( getContext( ), R.layout.item_main_eth, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, MainItem item, int position ) {

        ( ( ETHItem ) viewMapper ).textName.setText( item.getSymbol( ) );
        ( ( ETHItem ) viewMapper ).textBalance.setText( Utils.balanceReduction( Utils.toMaxUnit( item, item.getBalance( ) ) ) );
        ( ( ETHItem ) viewMapper ).textPrice.setText( String.format( "%s USD", item.getBalance( ) ) );

        if ( Utils.equals( CoinType.ETH.getCoinType( ), item.getCoinType( ) ) ) {

            ( ( ETHItem ) viewMapper ).imageIcon.setImageResource( R.drawable.icon_eth );

        } else if ( Utils.equals( CoinType.ERC20.getCoinType( ), item.getCoinType( ) ) ) {

            ImageLoader.getInstance( ).displayImage( Route.URL( item.getImg_path( ) ), ( ( ETHItem ) viewMapper ).imageIcon );

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

}
