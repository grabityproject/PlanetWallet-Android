package io.grabity.planetwallet.Views.p3_Wallet.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.StretchImageView;

public class PopupWalletAddAdapter extends AdvanceArrayAdapter< Planet > {

    public PopupWalletAddAdapter( Context context, ArrayList< Planet > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new WalletAddItem( View.inflate( getContext( ), R.layout.item_popup_wallet_add, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, Planet item, int position ) {
        ( ( WalletAddItem ) viewMapper ).imageIcon.setImageResource( item.getIconRes( ) );
        ( ( WalletAddItem ) viewMapper ).textName.setText( String.format( "%s Universe", CoinType.of( item.getCoinType( ) ).name( ) ) );
    }

    class WalletAddItem extends ViewMapper {

        StretchImageView imageIcon;
        TextView textName;

        public WalletAddItem( View itemView ) {
            super( itemView );
            imageIcon = findViewById( R.id.image_item_popup_wallet_add_icon );
            textName = findViewById( R.id.text_item_popup_wallet_add_coin_name );
        }
    }
}
