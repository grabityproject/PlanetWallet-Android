package io.grabity.planetwallet.Views.p4_Main.Activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.BlurBuilder;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p4_Main.Adapter.TestAdapter;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.StretchImageView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class MainActivity extends PlanetWalletActivity implements AdvanceArrayAdapter.OnAttachViewListener, AdvanceRecyclerView.OnScrollListener {

    private ViewMapper viewMapper;

    private ArrayList< String > items;
    private TestAdapter adapter;


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.recyclerView.setOnAttachViewListener( this );
        viewMapper.recyclerView.setOnScrollListener( this );
        viewMapper.recyclerView.addHeaderView( R.layout.header_main );
        viewMapper.recyclerView.addFooterView( R.layout.header_main );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        items = new ArrayList<>( );
        items.add( "1" );
        items.add( "2" );
        items.add( "3" );
        items.add( "4" );
        items.add( "5" );
        items.add( "6" );
        items.add( "7" );
        items.add( "8" );
        items.add( "9" );
        items.add( "10" );
        viewMapper.recyclerView.setAdapter( adapter = new TestAdapter( this, items ) );

        viewMapper.recyclerView.getViewTreeObserver( ).addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener( ) {
            @Override
            public void onGlobalLayout( ) {
                viewMapper.recyclerView.getViewTreeObserver( ).removeOnGlobalLayoutListener( this );
                viewMapper.blurView.setImageBitmap( BlurBuilder.blur( MainActivity.this, viewMapper.recyclerView.getScreenshot( ), 0.25f, 25 ) );
                viewMapper.blurView.setColorFilter( Color.parseColor( "#111117" ), PorterDuff.Mode.SCREEN );
                viewMapper.blurView.setY( ( ( View ) viewMapper.blurView.getParent( ) ).getHeight( ) - viewMapper.recyclerView.getHeight( ) );
            }
        } );

    }


    @Override
    public void onScrolled( RecyclerView recyclerView, int dx, int dy, float scrollX, float scrollY ) {
        viewMapper.blurView.setY( ( ( View ) viewMapper.blurView.getParent( ) ).getHeight( ) - viewMapper.recyclerView.getHeight( ) - scrollY );
    }

    @Override
    public void onAttachView( int resId, int position, View view ) {
        if ( resId == R.layout.header_main && position == 0 ) {
            PlanetView planetView = view.findViewById( R.id.icon_planet );
            planetView.setData( "가즈아" );
        }
    }

    class ViewMapper {

        ToolBar toolBar;
        AdvanceRecyclerView recyclerView;
        StretchImageView blurView;

        public ViewMapper( ) {

            toolBar = findViewById( R.id.toolBar );
            recyclerView = findViewById( R.id.listView );
            blurView = findViewById( R.id.image_blur );
        }
    }
}
