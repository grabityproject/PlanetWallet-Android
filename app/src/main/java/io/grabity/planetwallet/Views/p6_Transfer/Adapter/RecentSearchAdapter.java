package io.grabity.planetwallet.Views.p6_Transfer.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.OnInsideItemClickListener;
import io.grabity.planetwallet.Widgets.PlanetView;

public class RecentSearchAdapter extends AdvanceArrayAdapter< Planet > {

    private OnInsideItemClickListener onInsideItemClickListener;

    public RecentSearchAdapter( Context context, ArrayList< Planet > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new RecentSearchItem( View.inflate( getContext( ), R.layout.item_transfer_recent_search, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, Planet item, int position ) {

        ( ( RecentSearchItem ) viewMapper ).textRecent.setVisibility( position == 0 ? View.VISIBLE : View.GONE );
        ( ( RecentSearchItem ) viewMapper ).planetView.setData( item.getAddress( ) );
        ( ( RecentSearchItem ) viewMapper ).textName.setText( item.getName( ) );
        ( ( RecentSearchItem ) viewMapper ).textAddress.setText( Utils.addressReduction( item.getAddress( ) ) );

        (( RecentSearchItem) viewMapper).btnDelete.setOnClickListener( v -> onInsideItemClickListener.onInsideItemClick( item, position ) );

    }

    public void setOnInsideItemClickListener ( OnInsideItemClickListener onInsideItemClickListener ) {
        this.onInsideItemClickListener = onInsideItemClickListener;
    }

    class RecentSearchItem extends ViewMapper {

        PlanetView planetView;
        TextView textName;
        TextView textAddress;
        TextView textRecent;
        View btnDelete;

        public RecentSearchItem( View itemView ) {
            super( itemView );
            planetView = findViewById( R.id.planet_item_transfer_recent_search_planetview );
            textName = findViewById( R.id.text_item_transfer_recent_search_name );
            textAddress = findViewById( R.id.text_item_transfer_recent_search_address );
            textRecent = findViewById( R.id.text_item_transfer_recent_search_title );
            btnDelete = findViewById( R.id.image_item_transfer_recent_delete );
        }
    }
}
