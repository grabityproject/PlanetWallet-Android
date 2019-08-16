package io.grabity.planetwallet.Views.p4_Main.Components;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.Common.components.ViewComponent;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.CustomToast;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.ShadowView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class HeaderViewComponent extends ViewComponent implements AdvanceRecyclerView.OnScrollListener {

    private ViewMapper viewMapper;
    private Planet planet;
    private AdvanceRecyclerView recyclerView;

    public HeaderViewComponent( PlanetWalletActivity activity, AdvanceRecyclerView recyclerView, View view ) {
        super( activity );
        setView( view );
        this.recyclerView = recyclerView;
        viewMapper = new ViewMapper( );
        viewInit( );
    }

    @Override
    public void viewInit( ) {
        super.viewInit( );
        viewMapper.groupAddress.setOnClickListener( this );
        recyclerView.addOnScrollListener( this );
        setTheme( getActivity( ).getCurrentTheme( ) );

        float backgroundSize = ( Utils.getScreenWidth( getActivity( ) ) * 410.0f / 375.0f );
        float backgroundTopMargin = ( ( Utils.getScreenWidth( getActivity( ) ) - Utils.dpToPx( getActivity( ), 120 - 30 ) - Utils.getScreenWidth( getActivity( ) ) * 170.0f / 375.0f ) );

        viewMapper.planetBackground.getLayoutParams( ).width = ( int ) backgroundSize;
        viewMapper.planetBackground.getLayoutParams( ).height = ( int ) backgroundSize;
        viewMapper.shadowBackground.getLayoutParams( ).width = ( int ) backgroundSize;
        viewMapper.shadowBackground.getLayoutParams( ).height = ( int ) backgroundSize;
        ( ( ViewGroup.MarginLayoutParams ) viewMapper.planetBackground.getLayoutParams( ) ).topMargin = ( int ) -backgroundTopMargin;
        ( ( ViewGroup.MarginLayoutParams ) viewMapper.shadowBackground.getLayoutParams( ) ).topMargin = ( int ) -backgroundTopMargin;
        viewMapper.planetBackground.requestLayout( );
        viewMapper.shadowBackground.requestLayout( );

        viewMapper.shadowBackground.setX( ( Utils.getScreenWidth( getActivity( ) ) - backgroundSize ) / 2.0f );
        viewMapper.planetBackground.setX( ( Utils.getScreenWidth( getActivity( ) ) - backgroundSize ) / 2.0f );
    }

    public void setPlanet( Planet planet ) {
        this.planet = planet;
        viewMapper.planetBackground.setData( planet.getAddress( ) );
        viewMapper.planetView.setData( planet.getAddress( ) );
        viewMapper.textAddress.setText( Utils.addressReduction( planet.getAddress( ) ) );
        viewMapper.textName.setText( planet.getName( ) );
    }


    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.groupAddress ) {
            Utils.copyToClipboard( getActivity( ), planet.getAddress( ) );
            CustomToast.makeText( getActivity( ), getActivity( ).localized( R.string.main_copy_to_clipboard ) ).show( );
        }
    }

    public void setTheme( boolean theme ) {
        viewMapper.shadowBackground.setShadowColor(
                Color.parseColor( theme ? "#FFFFFF" : "#000000" ),
                Color.parseColor( theme ? "#C8FFFFFF" : "#AA000000" )
        );
    }

    @Override
    public void onScrolled( RecyclerView recyclerView, int dx, int dy, float scrollX, float scrollY ) {

        if ( viewMapper.groupHeaderPlanet != null ) {

            float start = ( viewMapper.groupHeaderPlanet.getTop( ) + viewMapper.groupHeaderPlanet.getHeight( ) / 3.0f - viewMapper.toolBar.getHeight( ) );
            float end = ( viewMapper.groupHeaderPlanet.getTop( ) + viewMapper.groupHeaderPlanet.getHeight( ) - viewMapper.toolBar.getHeight( ) );
            float moveRange = end - start;
            float current = scrollY - start > 0 ? scrollY - start : 0;
            float alpha = current / moveRange;

            if ( current > moveRange ) {
                viewMapper.toolBar.setBackgroundAlpha( 1.0f );
            } else if ( 0.0f <= alpha && alpha <= 1.0f ) {
                viewMapper.toolBar.setBackgroundAlpha( alpha );
            }

        }

        if ( scrollY > 0 ) {
            viewMapper.groupBackground.setY( ( int ) -scrollY );
            viewMapper.groupBackground.setScaleX( 1.0f );
            viewMapper.groupBackground.setScaleY( 1.0f );
        } else {
            float scale = ( float ) ( 1.0 - scrollY / ( Utils.getScreenWidth( getActivity( ) ) / 2.0f ) * 0.5f );
            viewMapper.groupBackground.setY( 0 );
            viewMapper.groupBackground.setScaleX( scale );
            viewMapper.groupBackground.setScaleY( scale );
        }


    }

    class ViewMapper {

        ToolBar toolBar;
        View groupBackground;

        View groupHeaderPlanet;
        PlanetView planetView;

        TextView textName;
        TextView textAddress;
        View btnCopy;

        ViewGroup groupAddress;
        PlanetView planetBackground;
        ShadowView shadowBackground;

        ViewMapper( ) {
            groupHeaderPlanet = findViewById( R.id.group_main_header_planet );
            groupAddress = findViewById( R.id.group_main_header_address );
            planetView = findViewById( R.id.planet_main_header );
            textName = findViewById( R.id.text_main_header_planet_name );
            textAddress = findViewById( R.id.text_main_header_planet_address );
            btnCopy = findViewById( R.id.btn_main_header_copy );


            toolBar = getActivity( ).findViewById( R.id.toolBar );
            groupBackground = getActivity( ).findViewById( R.id.group_main_background );
            planetBackground = getActivity( ).findViewById( R.id.planet_main_background );
            shadowBackground = getActivity( ).findViewById( R.id.shadow_main_background );

        }
    }

}
