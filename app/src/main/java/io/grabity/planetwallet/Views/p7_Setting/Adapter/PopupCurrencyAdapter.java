package io.grabity.planetwallet.Views.p7_Setting.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;

public class PopupCurrencyAdapter extends AdvanceArrayAdapter< String > {


    public PopupCurrencyAdapter( Context context, ArrayList< String > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new CurrencyItem( View.inflate( getContext( ), R.layout.item_popup_currency, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, String item, int position ) {
        ( ( CurrencyItem ) viewMapper ).TextCurrencyName.setText( item == null ? "" : item );
    }


    class CurrencyItem extends ViewMapper {

        TextView TextCurrencyName;

        public CurrencyItem( View itemView ) {
            super( itemView );
            TextCurrencyName = findViewById( R.id.text_item_popup_currency );
        }
    }
}
