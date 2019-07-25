package io.grabity.planetwallet.Widgets.ListPopupView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import io.grabity.planetwallet.Common.components.AbsPopupView.AbsSlideUpView;
import io.grabity.planetwallet.Common.components.AbsPopupView.PopupView;
import io.grabity.planetwallet.MiniFramework.utils.CornerRound;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.Themeable;

public class ListPopup extends AbsSlideUpView implements AdvanceRecyclerView.OnScrollListener, AdvanceRecyclerView.OnEndItemListener, AdvanceRecyclerView.OnItemClickListener {

    private ViewMapper viewMapper;
    private AdvanceArrayAdapter adapter;
    private OnListPopupItemClickListener onListPopupItemClickListener;
    private boolean showShadow = false;

    private boolean theme = false;

    private ListPopup( Context context ) {
        super( context );
    }

    public static ListPopup newInstance( Context context ) {
        ListPopup popup = new ListPopup( context );
        return popup;
    }

    public static ListPopup newInstance( Context context, boolean theme ) {
        ListPopup popup = new ListPopup( context );
        popup.theme = theme;
        return popup;
    }

    @Override
    protected View contentView( ) {
        return View.inflate( getContext( ), R.layout.popup_list, null );
    }

    @Override
    public void onCreateView( ) {
        viewMapper = new ViewMapper( );

        viewMapper.groupPopupList.setOnClickListener( this );
        viewMapper.recyclerView.setOverScrollMode( View.OVER_SCROLL_NEVER );
        viewMapper.recyclerView.addOnScrollListener( this );
        viewMapper.recyclerView.setOnEndItemListener( this );
        viewMapper.recyclerView.setOnItemClickListener( this );

        if ( showShadow ) {
            viewMapper.viewBottomShadow.setVisibility( View.VISIBLE );
        }

        if ( adapter != null ) {
            viewMapper.recyclerView.setAdapter( adapter );
        }

        findViewAndSetTheme( viewMapper.groupPopupList, theme );

        float dp = Utils.dpToPx( getContext( ), 8 );
        CornerRound.radius( viewMapper.groupPopupList, dp, dp, dp, dp, dp, dp, dp, dp );
    }


    protected void findViewAndSetTheme( final View v, boolean theme ) {
        try {
            if ( v instanceof ViewGroup ) {
                ViewGroup vg = ( ViewGroup ) v;
                if ( v instanceof Themeable ) {
                    ( ( Themeable ) v ).setTheme( theme );
                }
                for ( int i = 0; i < vg.getChildCount( ); i++ ) {
                    View child = vg.getChildAt( i );
                    findViewAndSetTheme( child, theme );
                }
            } else if ( v instanceof Themeable ) {
                ( ( Themeable ) v ).setTheme( theme );
            }

        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }


    public ListPopup setAdapter( AdvanceArrayAdapter adapter ) {
        this.adapter = adapter;
        return this;
    }

    public ListPopup setOnListPopupItemClickListener( OnListPopupItemClickListener onListPopupItemClickListener ) {
        this.onListPopupItemClickListener = onListPopupItemClickListener;
        return this;
    }

    public ListPopup setShowShadow( boolean showShadow ) {
        this.showShadow = showShadow;
        return this;
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
    }

    @Override
    public void onScrolled( RecyclerView recyclerView, int dx, int dy, float scrollX, float scrollY ) {
        if ( showShadow ) {
            if ( viewMapper != null ) {
                if ( viewMapper.viewTopShadow != null ) {
                    if ( scrollY < 10 ) {
                        viewMapper.viewTopShadow.setVisibility( View.GONE );
                    } else {
                        viewMapper.viewTopShadow.setVisibility( View.VISIBLE );
                    }
                }
                if ( viewMapper.viewBottomShadow != null ) {
                    viewMapper.viewBottomShadow.setVisibility( View.VISIBLE );
                }
            }
        }
    }

    @Override
    public void onLoadMore( int page, int totalItemsCount, RecyclerView view ) {
        if ( showShadow ) {
            if ( viewMapper != null ) {
                if ( viewMapper.viewBottomShadow != null ) {
                    viewMapper.viewBottomShadow.setVisibility( View.GONE );
                }
            }
        }
    }

    @Override
    public void onItemClick( AdvanceRecyclerView recyclerView, View view, int position ) {
        if ( onListPopupItemClickListener != null ) {
            onListPopupItemClickListener.onListPopupItemClick( this, view, position );
        }
    }

    public interface OnListPopupItemClickListener {
        void onListPopupItemClick( PopupView popup, View view, int position );
    }

    class ViewMapper {

        View groupPopupList;
        AdvanceRecyclerView recyclerView;

        View viewTopShadow;
        View viewBottomShadow;

        public ViewMapper( ) {
            groupPopupList = findViewById( R.id.group_popup_list );
            recyclerView = findViewById( R.id.list_popup_list );

            viewTopShadow = findViewById( R.id.view_list_popup_top_shadow );
            viewBottomShadow = findViewById( R.id.view_list_popup_bottom_shadow );


        }

    }
}
