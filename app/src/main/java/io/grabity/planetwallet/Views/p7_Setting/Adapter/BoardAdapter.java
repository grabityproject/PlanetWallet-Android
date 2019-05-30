package io.grabity.planetwallet.Views.p7_Setting.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Board;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;

public class BoardAdapter extends AdvanceArrayAdapter< Board > {

    public BoardAdapter( Context context, ArrayList< Board > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new BoardItem( View.inflate( getContext( ), R.layout.item_board, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, Board item, int position ) {
        ( ( BoardItem ) viewMapper ).title.setText( item.getTitle( ) == null ? "" : item.getTitle( ) );
        ( ( BoardItem ) viewMapper ).date.setVisibility( item.getDate( ) == null ? View.GONE : View.VISIBLE );
        ( ( BoardItem ) viewMapper ).date.setText( item.getDate( ) == null ? "" : item.getDate( ) );
    }

    class BoardItem extends ViewMapper {

        TextView title;
        TextView date;


        public BoardItem( View itemView ) {
            super( itemView );
            title = findViewById( R.id.text_item_board_title );
            date = findViewById( R.id.text_item_board_date );
        }
    }


}
