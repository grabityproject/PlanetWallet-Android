package io.grabity.planetwallet.Views.p5_Token.Adapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Eth;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.OnListSideViewClick;
import io.grabity.planetwallet.Widgets.StretchImageView;

public class TokenAdapter extends AdvanceArrayAdapter< Eth > {

    OnListSideViewClick onListSideViewClick;

    public TokenAdapter ( Context context, ArrayList< Eth > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping ( int position ) {
        return new EthItem( View.inflate( getContext( ) , R.layout.item_token_add , null ) );
    }

    @Override
    public void bindData ( ViewMapper viewMapper, Eth item, int position ) {


        ( ( EthItem ) viewMapper ).icon.setImageResource( item.getIcon( ) );
        ( ( EthItem ) viewMapper ).name.setText( item.getName( ) == null ? "" : item.getName( ) );

        if( item.isCheck() ){
            ( ( EthItem ) viewMapper).btnTokenAddORremove.setImageResource( R.drawable.image_checkbox_on );
            ( ( EthItem ) viewMapper).btnTokenAddORremove.setBorderColor( Color.parseColor( "#ff0050") );
        }else{
            ( ( EthItem ) viewMapper).btnTokenAddORremove.setImageDrawable( new ColorDrawable( Color.parseColor( "#111117" ) ) );
            ( ( EthItem ) viewMapper).btnTokenAddORremove.setBorderColor( Color.parseColor( "#5C5964") );
        }

        ( ( EthItem ) viewMapper).clickView.setOnClickListener( v -> {
            item.setCheck( !item.isCheck() );
            onListSideViewClick.onListSideViewClick( position , item.isCheck( ) );
        } );
    }


    public void setOnListSideViewClick ( OnListSideViewClick onListSideViewClick ){
        this.onListSideViewClick = onListSideViewClick;
    }


    public class EthItem extends ViewMapper {
        StretchImageView icon;
        CircleImageView btnTokenAddORremove;
        TextView name;
        View clickView;

        public EthItem ( View itemView ) {
            super( itemView );

            icon = findViewById( R.id.image_item_tokenadd_icon );
            name = findViewById( R.id.text_item_tokenadd_tokenname );
            clickView = findViewById( R.id.view_item_tokenadd_clickbtn );
            btnTokenAddORremove = findViewById( R.id.btn_item_tokenadd_tokenadd_removebtn );
        }
    }
}
