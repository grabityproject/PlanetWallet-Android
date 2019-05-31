package io.grabity.planetwallet.Views.p5_Token.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.ETH;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.OnInsideItemClickListener;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.StretchImageView;

public class TokenAdapter extends AdvanceArrayAdapter< ETH > {

    private OnInsideItemClickListener onInsideItemClickListener;

    public TokenAdapter( Context context, ArrayList< ETH > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new EthItem( View.inflate( getContext( ), R.layout.item_token_add, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, ETH item, int position ) {


        ( ( EthItem ) viewMapper ).icon.setImageResource( item.getIconRes( ) );
        ( ( EthItem ) viewMapper ).name.setText( item.getName( ) );

        if ( item.isCheck( ) ) {
            ( ( EthItem ) viewMapper ).btnTokenAddORremove.setImageResource( R.drawable.image_checkbox_on );
            ( ( EthItem ) viewMapper ).btnTokenAddORremove.setBorderColor( Color.parseColor( "#ff0050" ) );
        } else {
            if ( getTheme( ) ) {
                ( ( EthItem ) viewMapper ).btnTokenAddORremove.setImageDrawable( new ColorDrawable( Color.parseColor( "#FFFFFF" ) ) );
                ( ( EthItem ) viewMapper ).btnTokenAddORremove.setBorderColor( Color.parseColor( "#ededed" ) );
            } else {
                ( ( EthItem ) viewMapper ).btnTokenAddORremove.setImageDrawable( new ColorDrawable( Color.parseColor( "#111117" ) ) );
                ( ( EthItem ) viewMapper ).btnTokenAddORremove.setBorderColor( Color.parseColor( "#5C5964" ) );
            }
        }

        ( ( EthItem ) viewMapper ).clickView.setOnClickListener( v -> {
            getObjects( ).get( position ).setCheck( !getObjects( ).get( position ).isCheck( ) );
            if ( getObjects( ).get( position ).isCheck( ) ) {
                ( ( EthItem ) viewMapper ).btnTokenAddORremove.setImageResource( R.drawable.image_checkbox_on );
                ( ( EthItem ) viewMapper ).btnTokenAddORremove.setBorderColor( Color.parseColor( "#ff0050" ) );
            } else {
                if ( getTheme( ) ) {
                    ( ( EthItem ) viewMapper ).btnTokenAddORremove.setImageDrawable( new ColorDrawable( Color.parseColor( "#FFFFFF" ) ) );
                    ( ( EthItem ) viewMapper ).btnTokenAddORremove.setBorderColor( Color.parseColor( "#ededed" ) );
                } else {
                    ( ( EthItem ) viewMapper ).btnTokenAddORremove.setImageDrawable( new ColorDrawable( Color.parseColor( "#111117" ) ) );
                    ( ( EthItem ) viewMapper ).btnTokenAddORremove.setBorderColor( Color.parseColor( "#5C5964" ) );
                }
            }

            onInsideItemClickListener.onInsideItemClick( position );
        } );
    }


    public void setOnInsideItemClickListener( OnInsideItemClickListener onInsideItemClickListener ) {
        this.onInsideItemClickListener = onInsideItemClickListener;
    }


    public class EthItem extends ViewMapper {
        StretchImageView icon;
        CircleImageView btnTokenAddORremove;
        TextView name;
        View clickView;

        public EthItem( View itemView ) {
            super( itemView );

            icon = findViewById( R.id.image_item_tokenadd_icon );
            name = findViewById( R.id.text_item_tokenadd_tokenname );
            clickView = findViewById( R.id.view_item_tokenadd_clickbtn );
            btnTokenAddORremove = findViewById( R.id.btn_item_tokenadd_tokenadd_removebtn );
        }
    }

}
