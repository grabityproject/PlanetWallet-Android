package io.grabity.planetwallet.Views.p3_Wallet.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Coin;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.StretchImageView;

public class PopupWalletAddAdapter extends AdvanceArrayAdapter< Coin > {

    public PopupWalletAddAdapter( Context context, ArrayList< Coin > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new WalletAddItem( View.inflate( getContext( ), R.layout.item_popup_wallet_add, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, Coin item, int position ) {
        ( ( WalletAddItem ) viewMapper ).icon.setImageResource( item.getIcon( ) );
        ( ( WalletAddItem ) viewMapper ).name.setText( item.getCoin( ) == null ? "" : item.getCoin( ) );
    }


    class WalletAddItem extends ViewMapper {

        StretchImageView icon;
        TextView name;

        public WalletAddItem( View itemView ) {
            super( itemView );
            icon = findViewById( R.id.image_item_popup_wallet_add_icon );
            name = findViewById( R.id.text_item_popup_wallet_add_coin_name );
        }
    }

}
