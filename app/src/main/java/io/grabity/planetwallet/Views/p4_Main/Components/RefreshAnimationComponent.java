package io.grabity.planetwallet.Views.p4_Main.Components;

import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.Common.components.ViewComponent;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.OverScrollWrapper.OverScrollWrapper;

public class RefreshAnimationComponent extends ViewComponent implements AdvanceRecyclerView.OnScrollListener, OverScrollWrapper.OnRefreshListener {

    private ViewMapper viewMapper;

    private OverScrollWrapper overScrollWrapper;

    boolean loaderStart = false;

    public RefreshAnimationComponent( PlanetWalletActivity activity, OverScrollWrapper overScrollWrapper, AdvanceRecyclerView recyclerView ) {
        super( activity );
        viewMapper = new ViewMapper( );
        this.overScrollWrapper = overScrollWrapper;
        overScrollWrapper.addOnRefreshListener( this );
        recyclerView.addOnScrollListener( this );
        viewInit( );
    }

    @Override
    public void viewInit( ) {
        super.viewInit( );
        viewMapper.lottiePullToRefresh.setAnimation( "lottie/loader_loading.json" );
        viewMapper.lottiePullToRefresh.setRepeatCount( 0 );
    }

    @Override
    public void onScrolled( RecyclerView recyclerView, int dx, int dy, float scrollX, float scrollY ) {

        if ( !overScrollWrapper.isRefreshing( ) ) {
            if ( loaderStart ) {
                viewMapper.lottiePullToRefresh.setAnimation( "lottie/loader_loading.json" );
                viewMapper.lottiePullToRefresh.setRepeatCount( 0 );
                loaderStart = false;
            }
            float refreshPercent = ( -scrollY ) / Utils.dpToPx( getActivity( ), 80 );
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

    @Override
    public void onRefresh( ) {
        viewMapper.lottiePullToRefresh.setAnimation( "lottie/loader_start.json" );
        viewMapper.lottiePullToRefresh.setRepeatCount( LottieDrawable.INFINITE );
        viewMapper.lottiePullToRefresh.setRepeatMode( LottieDrawable.RESTART );
        viewMapper.lottiePullToRefresh.playAnimation( );
        loaderStart = true;
    }

    class ViewMapper {

        LottieAnimationView lottiePullToRefresh;

        ViewMapper( ) {
            lottiePullToRefresh = findViewById( R.id.lottie_main_pull_to_refresh );
        }

    }
}
