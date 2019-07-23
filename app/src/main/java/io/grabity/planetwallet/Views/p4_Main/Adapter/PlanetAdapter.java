package io.grabity.planetwallet.Views.p4_Main.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.PlanetView;

public class PlanetAdapter extends AdvanceArrayAdapter< Planet > {

    public static final int FOOTER_POSTION = -1;

    public PlanetAdapter( Context context, ArrayList< Planet > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new PlanetsItem( View.inflate( getContext( ), R.layout.item_main_planets, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, Planet item, int position ) {
        ( ( PlanetsItem ) viewMapper ).planetView.setData( item.getAddress( ) );
        ( ( PlanetsItem ) viewMapper ).textType.setText( CoinType.of( item.getCoinType( ) ).name( ) );
        ( ( PlanetsItem ) viewMapper ).textName.setText( item.getName( ) );
    }

    @Override
    protected ViewMapper onFooterView( ViewMapper viewMapper ) {
        viewMapper.itemView.setOnClickListener( v -> {
            if( getOnItemClickListener() != null ){
                try{
                    getOnItemClickListener().onItemClick( ( AdvanceRecyclerView ) viewMapper.getView().getParent(), null, -1 );
                }catch ( ClassCastException e ){
                    // Do not disturb
                }
            }
        } );
        return viewMapper;
    }

    class PlanetsItem extends ViewMapper {

        PlanetView planetView;
        TextView textType;
        TextView textName;

        public PlanetsItem( View itemView ) {
            super( itemView );

            planetView = findViewById( R.id.planetview_item_main_planets );
            textType = findViewById( R.id.text_item_main_planets_type );
            textName = findViewById( R.id.text_item_main_planets_name );

        }
    }
}
