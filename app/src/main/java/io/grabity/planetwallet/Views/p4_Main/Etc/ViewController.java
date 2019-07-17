package io.grabity.planetwallet.Views.p4_Main.Etc;

import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.airbnb.lottie.LottieDrawable;

import io.grabity.planetwallet.MiniFramework.utils.BlurBuilder;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.Views.p4_Main.Activity.MainActivity;
import io.grabity.planetwallet.Views.p4_Main.Activity.MainActivity.HeaderViewMapper;
import io.grabity.planetwallet.Views.p4_Main.Activity.MainActivity.ViewMapper;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.OverScrollWrapper.OverScrollWrapper;
import io.grabity.planetwallet.Widgets.SlideDrawerLayout;

public class ViewController implements AdvanceRecyclerView.OnScrollListener, SlideDrawerLayout.OnSlideDrawerListener, ViewTreeObserver.OnGlobalLayoutListener, OverScrollWrapper.OnRefreshListener {

    MainActivity activity;
    ViewMapper viewMapper;
    HeaderViewMapper headerViewMapper;

    float scrollY = 0.0f;
    float backgroundTopMargin = 0.0f;

    boolean loaderStart = false;

    public ViewController( MainActivity activity, ViewMapper viewMapper ) {
        this.activity = activity;
        this.viewMapper = viewMapper;
        viewMapper.slideDrawer.setOnSlideDrawerListener( this );
        viewMapper.listMain.addOnScrollListener( this );

        viewMapper.slideDrawer.setTrigger( SlideDrawerLayout.Position.BOTTOM, viewMapper.viewTrigger, false );
        viewMapper.slideDrawer.getTrigger( SlideDrawerLayout.Position.BOTTOM ).setOffset( -Utils.dpToPx( activity, 100 ) );
        viewMapper.overScrollWrapper.addOnRefreshListener( this );

        viewMapper.lottiePullToRefresh.setAnimation( "lottie/loader_loading.json" );
        viewMapper.lottiePullToRefresh.setProgress( 0.0f );

        viewMapper.listMain.getViewTreeObserver( ).addOnGlobalLayoutListener( this );
    }


    @Override
    public void onSlide( int position, float percent, float x, float y ) {
        if ( position == SlideDrawerLayout.Position.BOTTOM ) {
            float blurTop = viewMapper.slideDrawer.getHeight( ) - viewMapper.groupBlur.getHeight( ) / 2.0f;
            float movePoint = ( ( ( viewMapper.slideDrawer.getHeight( ) - viewMapper.groupBlur.getHeight( ) / 2.0f - Utils.dpToPx( activity, 20 ) ) - y ) / ( ( float ) viewMapper.groupBlur.getHeight( ) / 2.0f ) );

            viewMapper.groupBottom.setClickable( movePoint != 0 );

            viewMapper.textNotice.setAlpha( 1.0f - movePoint );
            viewMapper.groupBlur.setAlpha( 1.0f - movePoint );

            viewMapper.groupBottom.setAlpha( movePoint * 1.2f );

            if ( ( y - ( viewMapper.slideDrawer.getHeight( ) - viewMapper.groupBlur.getHeight( ) / 2.0f ) + Utils.dpToPx( activity, 20 ) ) > 0 ) {

                viewMapper.textNotice.setY( viewMapper.slideDrawer.getHeight( ) - viewMapper.groupBlur.getHeight( ) / 2.0f - viewMapper.textNotice.getHeight( ) );
                viewMapper.groupBlur.setY( blurTop );
            } else {

//                viewMapper.textNotice.setY( y - viewMapper.textNotice.getHeight( ) );
                viewMapper.textNotice.setY( y - viewMapper.textNotice.getHeight( ) + Utils.dpToPx( activity, 20 ) );
                viewMapper.groupBlur.setY( y + Utils.dpToPx( activity, 20 ) );

                viewMapper.imageBlurView.setY(
                        ( ( View ) viewMapper.imageBlurView.getParent( ) ).getHeight( ) -
                                viewMapper.listMain.getHeight( ) -
                                ( scrollY > 0 ? scrollY : 0 ) - viewMapper.groupBlur.getHeight( ) / 2.0f +
                                ( ( viewMapper.slideDrawer.getHeight( ) - viewMapper.groupBlur.getHeight( ) / 2.0f ) - y ) );

            }

        }
    }

    @Override
    public void onScrolled( RecyclerView recyclerView, int dx, int dy, float scrollX, float scrollY ) {
        this.scrollY = scrollY;
        viewMapper.imageBlurView.setY(
                ( ( View ) viewMapper.imageBlurView.getParent( ) ).getHeight( ) -
                        viewMapper.listMain.getHeight( ) - ( scrollY > 0 ? scrollY : 0 ) - viewMapper.groupBlur.getHeight( ) / 2.0f );

        if ( headerViewMapper != null ) {

            float start = ( headerViewMapper.groupHeaderPlanet.getTop( ) + headerViewMapper.groupHeaderPlanet.getHeight( ) / 3.0f - viewMapper.toolBar.getHeight( ) );
            float end = ( headerViewMapper.groupHeaderPlanet.getTop( ) + headerViewMapper.groupHeaderPlanet.getHeight( ) - viewMapper.toolBar.getHeight( ) );
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
            viewMapper.groupBackground.setTop( ( int ) -scrollY );
            viewMapper.groupBackground.setScaleX( 1.0f );
            viewMapper.groupBackground.setScaleY( 1.0f );

        } else {

            float scale = ( float ) ( 1.0 - scrollY / ( Utils.getScreenWidth( activity ) / 2.0f ) * 0.5f );
            viewMapper.groupBackground.setTop( 0 );
            viewMapper.groupBackground.setScaleX( scale );
            viewMapper.groupBackground.setScaleY( scale );
        }

        if ( !viewMapper.overScrollWrapper.isRefreshing( ) ) {
            if ( loaderStart ) {
                viewMapper.lottiePullToRefresh.setAnimation( "lottie/loader_loading.json" );
                viewMapper.lottiePullToRefresh.setRepeatCount( 0 );
                loaderStart = false;
            }
            float refreshPercent = ( -scrollY ) / Utils.dpToPx( activity, 80 );
            if ( 0 <= refreshPercent ) {
                if ( refreshPercent < 1.0f ) {
                    viewMapper.lottiePullToRefresh.setAlpha( refreshPercent );
                }
                if ( refreshPercent > 1.0f ) {
                    refreshPercent -= 1.0f;
                }
                viewMapper.lottiePullToRefresh.setProgress( refreshPercent );
            }
        }
    }

    public void setHeaderViewMapper( HeaderViewMapper headerViewMapper ) {
        this.headerViewMapper = headerViewMapper;
        viewMapper.planetBackground.getLayoutParams( ).width = headerViewMapper.groupHeaderPlanet.getWidth( );
    }

    @Override
    public void onGlobalLayout( ) {
        viewMapper.listMain.getViewTreeObserver( ).removeOnGlobalLayoutListener( this );


        updateBlurView( activity.getCurrentTheme( ) );

        viewMapper.textNotice.setY( viewMapper.groupBlur.getY( ) + viewMapper.groupBlur.getHeight( ) - viewMapper.textNotice.getHeight( ) );

        viewMapper.groupBlur.setY( viewMapper.groupBlur.getY( ) + viewMapper.groupBlur.getHeight( ) );
        viewMapper.groupBlur.getLayoutParams( ).height = viewMapper.groupBlur.getLayoutParams( ).height * 2;
        viewMapper.groupBlur.requestLayout( );
        viewMapper.imageBlurView.setY( ( ( View ) viewMapper.imageBlurView.getParent( ) ).getHeight( ) - viewMapper.listMain.getHeight( ) );

        float backgroundSize = ( Utils.getScreenWidth( activity ) * 410.0f / 375.0f );
        backgroundTopMargin = ( ( Utils.getScreenWidth( activity ) - Utils.dpToPx( activity, 120 - 30 ) - Utils.getScreenWidth( activity ) * 170.0f / 375.0f ) );
        viewMapper.planetBackground.getLayoutParams( ).width = ( int ) backgroundSize;
        viewMapper.planetBackground.getLayoutParams( ).height = ( int ) backgroundSize;
        viewMapper.shadowBackground.getLayoutParams( ).width = ( int ) backgroundSize;
        viewMapper.shadowBackground.getLayoutParams( ).height = ( int ) backgroundSize;
        ( ( ViewGroup.MarginLayoutParams ) viewMapper.planetBackground.getLayoutParams( ) ).topMargin = ( int ) -backgroundTopMargin;
        ( ( ViewGroup.MarginLayoutParams ) viewMapper.shadowBackground.getLayoutParams( ) ).topMargin = ( int ) -backgroundTopMargin;
        viewMapper.planetBackground.requestLayout( );
        viewMapper.shadowBackground.requestLayout( );

    }

    public void updateBlurView( boolean theme ) {
        if ( viewMapper.listMain.getAdapter( ) != null ) {
            try {
                viewMapper.imageBlurView.setImageBitmap( BlurBuilder.blur( activity, viewMapper.listMain.getScreenshot( Color.parseColor( theme ? "#FFFFFF" : "#111117" ) ), 0.25f, 25 ) );
            } catch ( IllegalArgumentException e ) {
                PLog.e( "image width or height is 0" );
            }
        }
    }

    @Override
    public void onRefresh( ) {
        viewMapper.lottiePullToRefresh.setAnimation( "lottie/loader_start.json" );
        viewMapper.lottiePullToRefresh.setRepeatCount( LottieDrawable.INFINITE );
        viewMapper.lottiePullToRefresh.setRepeatMode( LottieDrawable.RESTART );
        viewMapper.lottiePullToRefresh.playAnimation( );
        loaderStart = true;
    }

}
