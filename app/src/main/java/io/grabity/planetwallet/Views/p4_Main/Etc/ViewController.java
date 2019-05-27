package io.grabity.planetwallet.Views.p4_Main.Etc;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import io.grabity.planetwallet.MiniFramework.utils.BlurBuilder;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.Views.p4_Main.Activity.MainActivity;
import io.grabity.planetwallet.Views.p4_Main.Activity.MainActivity.ViewMapper;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.SlideDrawerLayout;

public class ViewController implements AdvanceRecyclerView.OnScrollListener, SlideDrawerLayout.OnSlideDrawerListener {

    MainActivity activity;
    ViewMapper viewMapper;

    float scrollY = 0.0f;


    public ViewController( MainActivity activity, ViewMapper viewMapper ) {
        this.activity = activity;
        this.viewMapper = viewMapper;
        viewMapper.slideDrawer.setOnSlideDrawerListener( this );
        viewMapper.listView.addOnScrollListener( this );
        viewMapper.slideDrawer.setTrigger( SlideDrawerLayout.Position.BOTTOM, viewMapper.viewTrigger, false );

        viewMapper.slideDrawer.getTrigger( SlideDrawerLayout.Position.BOTTOM ).setOffset( -Utils.dpToPx( activity, 80 ) );


        viewMapper.listView.getViewTreeObserver( ).addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener( ) {
            @Override
            public void onGlobalLayout( ) {
                viewMapper.listView.getViewTreeObserver( ).removeOnGlobalLayoutListener( this );

//                Bitmap bitMap = BlurBuilder.blur( activity, viewMapper.listView.getScreenshot( ), 0.25f, 25 );
                Bitmap bitMap = viewMapper.listView.getScreenshot( );
                viewMapper.blurView.setImageBitmap( bitMap );
                viewMapper.blurView.setAlpha( 0.4f );

                viewMapper.imageTest.setImageBitmap( bitMap );
                viewMapper.blurView.setColorFilter( Color.parseColor( "#111117" ), PorterDuff.Mode.SCREEN );

                viewMapper.blurView.setY( ( ( View ) viewMapper.blurView.getParent( ) ).getHeight( ) - viewMapper.listView.getHeight( ) - viewMapper.groupBlur.getHeight( ) );

                viewMapper.textNotice.setY( viewMapper.groupBlur.getY( ) + viewMapper.groupBlur.getHeight( ) - viewMapper.textNotice.getHeight( ) );
                viewMapper.groupBlur.setY( viewMapper.groupBlur.getY( ) + viewMapper.groupBlur.getHeight( ) );
                viewMapper.groupBlur.getLayoutParams( ).height = viewMapper.groupBlur.getLayoutParams( ).height * 2;
                viewMapper.groupBlur.requestLayout( );

            }
        } );
    }

    @Override
    public void onSlide( int position, float percent, float x, float y ) {
        if ( position == SlideDrawerLayout.Position.BOTTOM ) {
            float movePoint = ( ( ( viewMapper.slideDrawer.getHeight( ) - viewMapper.groupBlur.getHeight( ) / 2.0f ) - y ) / ( ( float ) viewMapper.groupBlur.getHeight( ) / 2.0f ) );
            viewMapper.textNotice.setAlpha( 1.0f - movePoint );
            viewMapper.groupBottom.setAlpha( movePoint * 1.2f );

            if ( y > viewMapper.slideDrawer.getHeight( ) - viewMapper.groupBlur.getHeight( ) / 2.0f ) {
                viewMapper.textNotice.setY( viewMapper.slideDrawer.getHeight( ) - viewMapper.groupBlur.getHeight( ) / 2.0f - viewMapper.textNotice.getHeight( ) );
                viewMapper.groupBlur.setY( viewMapper.slideDrawer.getHeight( ) - viewMapper.groupBlur.getHeight( ) / 2.0f );
            } else {
                viewMapper.textNotice.setY( y - viewMapper.textNotice.getHeight( ) );
                viewMapper.groupBlur.setY( y );
            }

            viewMapper.blurView.setY(
                    ( ( View ) viewMapper.blurView.getParent( ) ).getHeight( ) -
                            viewMapper.listView.getHeight( ) -
                            ( scrollY > 0 ? scrollY : 0 ) - viewMapper.groupBlur.getHeight( ) / 2.0f +
                            ( ( viewMapper.slideDrawer.getHeight( ) - viewMapper.groupBlur.getHeight( ) / 2.0f ) - y ) );
        }
    }

    @Override
    public void onScrolled( RecyclerView recyclerView, int dx, int dy, float scrollX, float scrollY ) {

        PLog.e( "scrollY : " + scrollY );

        this.scrollY = scrollY;
        viewMapper.blurView.setY(
                ( ( View ) viewMapper.blurView.getParent( ) ).getHeight( ) -
                        viewMapper.listView.getHeight( ) - ( scrollY > 0 ? scrollY : 0 ) - viewMapper.groupBlur.getHeight( ) / 2.0f );
    }
}
