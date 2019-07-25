package io.grabity.planetwallet.Views.p7_Setting.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Board;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;

public class AnnounceAdapter extends AdvanceArrayAdapter< Board > {

    public AnnounceAdapter( Context context, ArrayList< Board > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new BoardItem( View.inflate( getContext( ), R.layout.item_board, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, Board item, int position ) {
        ( ( BoardItem ) viewMapper ).textTitle.setText( item.getSubject( ) );
        ( ( BoardItem ) viewMapper ).textDate.setVisibility( View.VISIBLE );
        ( ( BoardItem ) viewMapper ).textDate.setText( item.getCreated_at( ) );
    }

    class BoardItem extends ViewMapper {

        TextView textTitle;
        TextView textDate;

        public BoardItem( View itemView ) {
            super( itemView );
            textTitle = findViewById( R.id.text_item_board_title );
            textDate = findViewById( R.id.text_item_board_date );
        }
    }


}
