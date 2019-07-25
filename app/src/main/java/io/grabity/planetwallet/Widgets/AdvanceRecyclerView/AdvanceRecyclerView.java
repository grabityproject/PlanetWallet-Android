package io.grabity.planetwallet.Widgets.AdvanceRecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.LruCache;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.Widgets.Themeable;

/**
 * Created by. JcobPark on 2018. 08. 29
 */

public class AdvanceRecyclerView extends RecyclerView implements Themeable {

    private boolean theme = false;

    public static final int VERTICAL = 0x0000;
    public static final int HORIZONTAL = 0x0001;
    public static final int GRID = 0x0100;
    public static final int STAGGERED = 0x1000;

    public ArrayList< Integer > headerViews;
    public ArrayList< Integer > footerViews;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnDetectEndScrollListener onDetectEndScrollListener;
    private OnEndItemListener onEndItemListener;
    private AdvanceArrayAdapter.OnAttachViewListener onAttachViewListener;

    private float scrollX = 0;
    private float scrollY = 0;

    public AdvanceRecyclerView( Context context ) {
        super( context );
        viewInit( );
    }

    public AdvanceRecyclerView( Context context, @Nullable AttributeSet attrs ) {
        super( context, attrs );
        viewInit( );
    }

    public AdvanceRecyclerView( Context context, @Nullable AttributeSet attrs, int defStyle ) {
        super( context, attrs, defStyle );
        viewInit( );
    }

    private void viewInit( ) {
        setLayoutManager( new LinearLayoutManager( getContext( ), LinearLayoutManager.VERTICAL, false ) );
        onScrollListeners = new ArrayList<>( );
    }

    public void setLayout( int layout ) {
        setLayout( layout, 3 );
    }


    public void setLayout( int layout, int spanCount ) {
        switch ( layout ) {
            case VERTICAL:
                setLayoutManager( new LinearLayoutManager( getContext( ), LinearLayoutManager.VERTICAL, false ) );
                break;
            case HORIZONTAL:
                setLayoutManager( new LinearLayoutManager( getContext( ), LinearLayoutManager.HORIZONTAL, false ) );
                break;
            case GRID:
                setLayoutManager( new GridLayoutManager( getContext( ), spanCount, GridLayoutManager.VERTICAL, false ) );
                break;
            case GRID | HORIZONTAL:
                setLayoutManager( new GridLayoutManager( getContext( ), spanCount, GridLayoutManager.HORIZONTAL, false ) );
                break;
            case STAGGERED:
                setLayoutManager( new StaggeredGridLayoutManager( spanCount, LinearLayoutManager.VERTICAL ) );
                break;
            case STAGGERED | HORIZONTAL:
                setLayoutManager( new StaggeredGridLayoutManager( spanCount, LinearLayoutManager.HORIZONTAL ) );
                break;
        }

        if ( getAdapter( ) != null && getAdapter( ) instanceof AdvanceArrayAdapter ) {
            ( ( AdvanceArrayAdapter ) getAdapter( ) ).setLayoutManager( getLayoutManager( ) );
        }
    }

    @Override
    public void setAdapter( Adapter adapter ) {
        if ( adapter instanceof AdvanceArrayAdapter ) {
            if ( headerViews != null && headerViews.size( ) > 0 ) {
                for ( Integer i : headerViews ) {
                    if ( i != null )
                        ( ( AdvanceArrayAdapter ) adapter ).addHeaderView( i );
                }
            }
            if ( footerViews != null && footerViews.size( ) > 0 ) {

                for ( Integer i : footerViews ) {
                    if ( i != null )
                        ( ( AdvanceArrayAdapter ) adapter ).addFooterView( i );
                }
            }
            if ( this.onAttachViewListener != null ) {
                ( ( AdvanceArrayAdapter ) adapter ).setOnAttachViewListener( onAttachViewListener );
            }

            ( ( AdvanceArrayAdapter ) adapter ).setLayoutManager( getLayoutManager( ) );

            ( ( AdvanceArrayAdapter ) adapter ).setOnItemClickListener( onItemClickListener );

            ( ( AdvanceArrayAdapter ) adapter ).setOnItemLongClickListener( onItemLongClickListener );

            ( ( AdvanceArrayAdapter ) adapter ).setTheme( theme );

        }
        scrollX = 0;
        scrollY = 0;
        super.setAdapter( adapter );
    }

    public void addHeaderView( int resId ) {
        if ( getAdapter( ) != null && getAdapter( ) instanceof AdvanceArrayAdapter ) {
            ( ( AdvanceArrayAdapter ) getAdapter( ) ).addHeaderView( resId );
        } else {
            if ( headerViews == null ) headerViews = new ArrayList<>( );
            headerViews.add( resId );
        }
    }


    public void removeHeaderView( int resId ) {
        if ( getAdapter( ) != null && getAdapter( ) instanceof AdvanceArrayAdapter ) {
            ( ( AdvanceArrayAdapter ) getAdapter( ) ).removeHeaderView( resId );
        }
        if ( headerViews != null ) {
            headerViews.remove( resId );
        }
    }


    public void addFooterView( int resId ) {
        if ( getAdapter( ) != null && getAdapter( ) instanceof AdvanceArrayAdapter ) {
            ( ( AdvanceArrayAdapter ) getAdapter( ) ).addFooterView( resId );
        } else {
            if ( footerViews == null ) footerViews = new ArrayList<>( );
            footerViews.add( resId );
        }
    }


    public OnItemClickListener getOnItemClickListener( ) {
        return onItemClickListener;
    }

    public void setOnItemClickListener( OnItemClickListener onItemClickListener ) {
        this.onItemClickListener = onItemClickListener;
        if ( getAdapter( ) != null && getAdapter( ) instanceof AdvanceArrayAdapter ) {
            ( ( AdvanceArrayAdapter ) getAdapter( ) ).setOnItemClickListener( onItemClickListener );
        }
    }

    public OnItemLongClickListener getOnItemLongClickListener( ) {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener( OnItemLongClickListener onItemLongClickListener ) {
        this.onItemLongClickListener = onItemLongClickListener;
        if ( getAdapter( ) != null && getAdapter( ) instanceof AdvanceArrayAdapter ) {
            ( ( AdvanceArrayAdapter ) getAdapter( ) ).setOnItemLongClickListener( onItemLongClickListener );
        }
    }

    @Override
    public void setTheme( boolean theme ) {
        this.theme = theme;
        if ( getAdapter( ) != null && getAdapter( ) instanceof AdvanceArrayAdapter ) {
            ( ( AdvanceArrayAdapter ) getAdapter( ) ).setTheme( theme );
            getAdapter( ).notifyDataSetChanged( );
        }
    }

    public interface OnItemClickListener {
        void onItemClick( AdvanceRecyclerView recyclerView, View view, int position );
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick( AdvanceRecyclerView recyclerView, View view, int position );
    }

    public interface OnEndItemListener {
        void onLoadMore( int page, int totalItemsCount, RecyclerView view );
    }

    public OnEndItemListener getOnEndItemListener( ) {
        return onEndItemListener;
    }

    public void resetState( ) {
        if ( onDetectEndScrollListener != null ) {
            onDetectEndScrollListener.resetState( );
        }
    }

    public void setOnEndItemListener( OnEndItemListener onEndItemListener ) {
        this.onEndItemListener = onEndItemListener;
        if ( onDetectEndScrollListener != null ) {
            this.removeOnScrollListener( onDetectEndScrollListener );
        }

        if ( getLayoutManager( ) instanceof LinearLayoutManager ) {
            onDetectEndScrollListener = new OnDetectEndScrollListener( ( LinearLayoutManager ) getLayoutManager( ) );
        } else if ( getLayoutManager( ) instanceof GridLayoutManager ) {
            onDetectEndScrollListener = new OnDetectEndScrollListener( ( GridLayoutManager ) getLayoutManager( ) );
        } else if ( getLayoutManager( ) instanceof StaggeredGridLayoutManager ) {
            onDetectEndScrollListener = new OnDetectEndScrollListener( ( StaggeredGridLayoutManager ) getLayoutManager( ) );
        }

        addOnScrollListener( onDetectEndScrollListener );
    }

    private class OnDetectEndScrollListener extends EndlessRecyclerViewScrollListener {

        public OnDetectEndScrollListener( LinearLayoutManager layoutManager ) {
            super( layoutManager );
        }

        public OnDetectEndScrollListener( GridLayoutManager layoutManager ) {
            super( layoutManager );
        }

        public OnDetectEndScrollListener( StaggeredGridLayoutManager layoutManager ) {
            super( layoutManager );
        }

        @Override
        public void onLoadMore( int page, int totalItemsCount, RecyclerView view ) {
            if ( onEndItemListener != null )
                onEndItemListener.onLoadMore( page, totalItemsCount, view );
        }
    }


    private class OnDefScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled( RecyclerView recyclerView, int dx, int dy ) {
            super.onScrolled( recyclerView, dx, dy );
            scrollX += dx;
            scrollY += dy;
            if ( onScrollListeners != null ) {
                for ( OnScrollListener listener : onScrollListeners ) {
                    listener.onScrolled( recyclerView, dx, dy, scrollX, scrollY );
                }
            }
        }

    }

    private OnDefScrollListener defScrollListener;

    private ArrayList< OnScrollListener > onScrollListeners;

    public ArrayList< OnScrollListener > getOnScrollListeners( ) {
        return onScrollListeners;
    }

    public OnScrollListener getOnScrollListener( int index ) {
        if ( index < 0 ) return null;
        try {
            return onScrollListeners.get( index );
        } catch ( ArrayIndexOutOfBoundsException e ) {
            return null;
        }
    }

    public void addOnScrollListener( OnScrollListener onScrollListener ) {
        if ( onScrollListeners != null ) {
            onScrollListeners.add( onScrollListener );
        }
        defScrollListener = new OnDefScrollListener( );
        addOnScrollListener( defScrollListener );
    }

    public interface OnScrollListener {
        void onScrolled( RecyclerView recyclerView, int dx, int dy, float scrollX, float scrollY );
    }

    public Bitmap getScreenshot( int backgroundColor ) {
        AdvanceArrayAdapter adapter = ( AdvanceArrayAdapter ) this.getAdapter( );
        adapter.screenshotFlag = true;
        if ( adapter == null ) return null;
        int size = adapter.getItemCount( );
        int height = 0;
        Paint paint = new Paint( );
        int iHeight = 0;
        final int maxMemory = ( int ) ( Runtime.getRuntime( ).maxMemory( ) / 1024 );

        final int cacheSize = maxMemory / 8;
        LruCache< String, Bitmap > bitmaCache = new LruCache<>( cacheSize );

        for ( int i = 0; i < size; i++ ) {
            ViewHolder holder = adapter.createViewHolder( this, adapter.getItemViewType( i ) );
            adapter.onBindViewHolder( ( AdvanceArrayAdapter.ViewMapper ) holder, i );
            holder.itemView.measure( MeasureSpec.makeMeasureSpec( this.getWidth( ), MeasureSpec.EXACTLY ),
                    MeasureSpec.makeMeasureSpec( 0, MeasureSpec.UNSPECIFIED ) );
            holder.itemView.layout( 0, 0, holder.itemView.getMeasuredWidth( ), holder.itemView.getMeasuredHeight( ) );
            holder.itemView.setDrawingCacheEnabled( true );
            holder.itemView.buildDrawingCache( );
            Bitmap drawingCache = holder.itemView.getDrawingCache( );
            if ( drawingCache != null ) {
                bitmaCache.put( String.valueOf( i ), drawingCache );
            }
            height += holder.itemView.getMeasuredHeight( );
        }

        height += Utils.dpToPx( getContext( ), 130 );

        if ( height < Utils.getScrennHeight( getContext( ) ) )
            height = Utils.getScrennHeight( getContext( ) );

        if ( height == 0 ) {
            height = 1;
        }

        float width = this.getMeasuredWidth( );

        if ( width == 0 ) {
            width = 1;
        }
        Bitmap bigBitmap = Bitmap.createBitmap( ( int ) width, height, Bitmap.Config.ARGB_8888 );
        Canvas bigCanvas = new Canvas( bigBitmap );

        if ( backgroundColor != Color.TRANSPARENT ) {
            Paint backgroundPaint = new Paint( );
            backgroundPaint.setColor( backgroundColor );
            bigCanvas.drawRect( 0, 0, width, height, backgroundPaint );
        }


        for ( int i = 0; i < size; i++ ) {
            Bitmap bitmap = bitmaCache.get( String.valueOf( i ) );
            if ( bitmap != null ) {
                bigCanvas.drawBitmap( bitmap, 0f, iHeight, paint );
                iHeight += bitmap.getHeight( );
                bitmap.recycle( );
            }
        }
        adapter.screenshotFlag = false;
        return bigBitmap;
    }

    public Bitmap getScreenshot( ) {
        return getScreenshot( Color.TRANSPARENT );
    }

    public void setOnAttachViewListener( AdvanceArrayAdapter.OnAttachViewListener onAttachViewListener ) {
        this.onAttachViewListener = onAttachViewListener;
    }

}
