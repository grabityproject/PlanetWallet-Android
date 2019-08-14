package io.grabity.planetwallet.Views.p5_Token.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.OnInsideItemClickListener;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.StretchImageView;

public class TokenAdapter extends AdvanceArrayAdapter< ERC20 > {

    private OnInsideItemClickListener onInsideItemClickListener;

    public TokenAdapter( Context context, ArrayList< ERC20 > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new EthItem( View.inflate( getContext( ), R.layout.item_token_add, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, ERC20 item, int position ) {

        ImageLoader.getInstance( ).displayImage( "http://test.planetwallet.io" + item.getImg_path( ), ( ( EthItem ) viewMapper ).icon );
        ( ( EthItem ) viewMapper ).textTokenSymbol.setText( item.getSymbol( ) );
        ( ( EthItem ) viewMapper ).textTokenName.setText( item.getName( ) );

        if ( item.isCheck( ) ) {
            ( ( EthItem ) viewMapper ).btnTokenRefresh.setImageResource( R.drawable.image_checkbox_on );
            ( ( EthItem ) viewMapper ).btnTokenRefresh.setBorderColor( Color.parseColor( "#ff0050" ) );
        } else {
            if ( getTheme( ) ) {
                ( ( EthItem ) viewMapper ).btnTokenRefresh.setImageDrawable( new ColorDrawable( Color.parseColor( "#FFFFFF" ) ) );
                ( ( EthItem ) viewMapper ).btnTokenRefresh.setBorderColor( Color.parseColor( "#ededed" ) );
            } else {
                ( ( EthItem ) viewMapper ).btnTokenRefresh.setImageDrawable( new ColorDrawable( Color.parseColor( "#111117" ) ) );
                ( ( EthItem ) viewMapper ).btnTokenRefresh.setBorderColor( Color.parseColor( "#5C5964" ) );
            }
        }

        ( ( EthItem ) viewMapper ).clickView.setOnClickListener( v -> {
            getObjects( ).get( position ).setCheck( !getObjects( ).get( position ).isCheck( ) );
            if ( getObjects( ).get( position ).isCheck( ) ) {
                ( ( EthItem ) viewMapper ).btnTokenRefresh.setImageResource( R.drawable.image_checkbox_on );
                ( ( EthItem ) viewMapper ).btnTokenRefresh.setBorderColor( Color.parseColor( "#ff0050" ) );
            } else {
                if ( getTheme( ) ) {
                    ( ( EthItem ) viewMapper ).btnTokenRefresh.setImageDrawable( new ColorDrawable( Color.parseColor( "#FFFFFF" ) ) );
                    ( ( EthItem ) viewMapper ).btnTokenRefresh.setBorderColor( Color.parseColor( "#ededed" ) );
                } else {
                    ( ( EthItem ) viewMapper ).btnTokenRefresh.setImageDrawable( new ColorDrawable( Color.parseColor( "#111117" ) ) );
                    ( ( EthItem ) viewMapper ).btnTokenRefresh.setBorderColor( Color.parseColor( "#5C5964" ) );
                }
            }
            onInsideItemClickListener.onInsideItemClick( item, position );
        } );
    }


    public void setOnInsideItemClickListener( OnInsideItemClickListener onInsideItemClickListener ) {
        this.onInsideItemClickListener = onInsideItemClickListener;
    }


    public class EthItem extends ViewMapper {
        StretchImageView icon;
        CircleImageView btnTokenRefresh;
        TextView textTokenSymbol;
        TextView textTokenName;
        View clickView;

        public EthItem( View itemView ) {
            super( itemView );

            icon = findViewById( R.id.image_item_token_add_icon );
            textTokenSymbol = findViewById( R.id.text_item_token_add_token_symbol );
            textTokenName = findViewById( R.id.text_item_token_add_token_name );
            clickView = findViewById( R.id.view_item_token_add_clickbtn );
            btnTokenRefresh = findViewById( R.id.btn_item_token_add_refresh );
        }
    }

}
