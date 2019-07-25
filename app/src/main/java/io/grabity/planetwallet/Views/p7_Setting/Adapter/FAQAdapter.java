package io.grabity.planetwallet.Views.p7_Setting.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Board;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;

public class FAQAdapter extends AdvanceArrayAdapter< Board > {

    public FAQAdapter( Context context, ArrayList< Board > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new BoardItem( View.inflate( getContext( ), R.layout.item_board, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, Board item, int position ) {
        ( ( BoardItem ) viewMapper ).title.setText( item.getSubject( ) );
    }

    class BoardItem extends ViewMapper {

        TextView title;

        public BoardItem( View itemView ) {
            super( itemView );
            title = findViewById( R.id.text_item_board_title );
        }
    }


}
