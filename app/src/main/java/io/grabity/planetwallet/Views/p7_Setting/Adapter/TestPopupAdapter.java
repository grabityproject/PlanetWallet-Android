package io.grabity.planetwallet.Views.p7_Setting.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.AdvanceArrayAdapter;

public class TestPopupAdapter extends AdvanceArrayAdapter< String > {

    public TestPopupAdapter( Context context, ArrayList< String > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new TestItem( View.inflate( getContext( ), R.layout.item_test, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, String item, int position ) {
        ( ( TestItem ) viewMapper ).textName.setText( item );
    }

    class TestItem extends ViewMapper {

        TextView textName;
        TextView textCoin;

        public TestItem( View itemView ) {
            super( itemView );
            textName = findViewById( R.id.text_name );
            textCoin = findViewById( R.id.text_coin );

        }
    }

}
