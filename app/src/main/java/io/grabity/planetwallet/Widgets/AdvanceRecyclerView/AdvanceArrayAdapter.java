package io.grabity.planetwallet.Widgets.AdvanceRecyclerView;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.managers.FontManager;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.Themeable;

/**
 * Created by. JcobPark on 2018. 08. 29
 */

public abstract class AdvanceArrayAdapter< T > extends RecyclerView.Adapter< AdvanceArrayAdapter< T >.ViewMapper > {

    private boolean theme = false;

    private Context context;
    private ArrayList< T > objects;

    private ArrayList< Integer > headerViews;
    private ArrayList< Integer > footerViews;

    private AdvanceRecyclerView.OnItemClickListener onItemClickListener;
    private AdvanceRecyclerView.OnItemLongClickListener onItemLongClickListener;

    private OnAttachViewListener onAttachViewListener;

    public AdvanceArrayAdapter( Context context, ArrayList< T > objects ) {
        this.context = context;
        this.objects = objects;
        headerViews = new ArrayList<>( );
        footerViews = new ArrayList<>( );
    }

    public ArrayList< T > getObjects( ) {
        return objects;
    }

    public void setObjects( ArrayList< T > objects ) {
        this.objects = objects;
    }

    public Context getContext( ) {
        return context;
    }

    protected ViewMapper onHeaderView( ViewMapper viewMapper ) {
        return viewMapper;
    }

    protected ViewMapper onFooterView( ViewMapper viewMapper ) {
        return viewMapper;
    }

    public abstract ViewMapper viewMapping( int position );

    public abstract void bindData( ViewMapper viewMapper, T item, int position );

    @Override
    public ViewMapper onCreateViewHolder( ViewGroup parent, int viewType ) {
        if ( viewType <= -2000 ) {
            return new ViewMapper( View.inflate( getContext( ), footerViews.get( viewType * -1 - 2000 ), null ), footerViews.get( viewType * -1 - 2000 ) );
        } else if ( viewType <= -1000 ) {
            return new ViewMapper( View.inflate( getContext( ), headerViews.get( viewType * -1 - 1000 ), null ), headerViews.get( viewType * -1 - 1000 ) );
        } else {
            return viewMapping( viewType );
        }
    }

    @Override
    public void onBindViewHolder( final ViewMapper viewMapper, int position ) {
        try {
            findViewAndSetTheme( viewMapper.getView( ), getTheme( ) );
            overrideFonts( viewMapper.getView( ) );
        } catch ( NullPointerException e ) {
            e.printStackTrace( );
        }
        if ( position - headerViews.size( ) >= 0 && position - headerViews.size( ) < objects.size( ) ) {
            bindData( viewMapper, objects.get( position - headerViews.size( ) ), position );
        } else if ( 0 <= position && position < headerViews.size( ) ) {
            if ( onAttachViewListener != null )
                onAttachViewListener.onAttachView( viewMapper.getResId( ), position, viewMapper.itemView );
            onHeaderView( viewMapper );
        } else if ( objects.size( ) + headerViews.size( ) <= position && position < objects.size( ) + headerViews.size( ) + footerViews.size( ) ) {
            if ( onAttachViewListener != null )
                onAttachViewListener.onAttachView( viewMapper.getResId( ), position, viewMapper.itemView );
            onFooterView( viewMapper );
        }
    }

    @Override
    public int getItemCount( ) {
        if ( objects == null ) {
            return 0;
        }
        return objects.size( ) + headerViews.size( ) + footerViews.size( );
    }

    @Override
    public int getItemViewType( int position ) {
        if ( headerViews.size( ) > 0 && headerViews.size( ) > position ) {
            return -1000 - ( position );
        }
        if ( footerViews.size( ) > 0 && position >= objects.size( ) + headerViews.size( ) ) {
            return -2000 - ( position - ( objects.size( ) + headerViews.size( ) ) );
        } else {
            return position - headerViews.size( );
        }
    }

    public void addHeaderView( int view ) {
        if ( headerViews == null ) headerViews = new ArrayList<>( );
        headerViews.add( view );
    }

    public void removeHeaderView( int resId ) {
        if ( headerViews != null ) {
            if ( headerViews.indexOf( resId ) >= 0 ) {
                headerViews.remove( resId );
            }
        }
    }

    public void addFooterView( int view ) {
        if ( footerViews == null ) footerViews = new ArrayList<>( );
        footerViews.add( view );
    }

    public void setLayoutManager( final RecyclerView.LayoutManager layoutManager ) {
        if ( layoutManager instanceof GridLayoutManager && headerViews.size( ) > 0 ) {
            ( ( GridLayoutManager ) layoutManager ).setSpanSizeLookup( new GridLayoutManager.SpanSizeLookup( ) {
                @Override
                public int getSpanSize( int position ) {
                    if ( headerViews.size( ) > 0 && headerViews.size( ) > position ) {
                        return ( ( GridLayoutManager ) layoutManager ).getSpanCount( );
                    }
                    if ( footerViews.size( ) > 0 && position >= objects.size( ) + headerViews.size( ) ) {
                        return ( ( GridLayoutManager ) layoutManager ).getSpanCount( );
                    }
                    return 1;
                }
            } );
        }
    }

    public AdvanceRecyclerView.OnItemClickListener getOnItemClickListener( ) {
        return onItemClickListener;
    }

    public void setOnItemClickListener( AdvanceRecyclerView.OnItemClickListener onItemClickListener ) {
        this.onItemClickListener = onItemClickListener;
    }

    public AdvanceRecyclerView.OnItemLongClickListener getOnItemLongClickListener( ) {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener( AdvanceRecyclerView.OnItemLongClickListener onItemLongClickListener ) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public class ViewMapper extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private int resId = -1;

        public < V extends View > V findViewById( int resId ) {
            return itemView.findViewById( resId );
        }

        public ViewMapper( View itemView, int resId ) {
            this( itemView );
            this.resId = resId;
        }

        public ViewMapper( View itemView ) {
            super( itemView );
            itemView.setOnClickListener( this );
            itemView.setOnLongClickListener( this );
        }

        @Override
        public void onClick( View view ) {
            if ( onItemClickListener != null && getAdapterPosition( ) - headerViews.size( ) >= 0 && getAdapterPosition( ) - headerViews.size( ) < objects.size( ) )
                onItemClickListener.onItemClick( ( AdvanceRecyclerView ) view.getParent( ), view, getAdapterPosition( ) - headerViews.size( ) );
        }

        @Override
        public boolean onLongClick( View view ) {
            if ( onItemLongClickListener != null && getAdapterPosition( ) - headerViews.size( ) >= 0 && getAdapterPosition( ) - headerViews.size( ) < objects.size( ) )
                return onItemLongClickListener.onItemLongClick( ( AdvanceRecyclerView ) view.getParent( ), view, getAdapterPosition( ) - headerViews.size( ) );
            else
                return false;
        }

        public int getResId( ) {
            return resId;
        }

        public View getView( ) {
            return itemView;
        }


    }

    public ArrayList< Integer > getHeaderViews( ) {
        return headerViews;
    }

    public ArrayList< Integer > getFooterViews( ) {
        return footerViews;
    }

    public void setOnAttachViewListener( OnAttachViewListener onAttachViewListener ) {
        this.onAttachViewListener = onAttachViewListener;
    }

    public interface OnAttachViewListener {
        void onAttachView( int resId, int position, View view );
    }

    public void setTheme( boolean theme ) {
        this.theme = theme;
        PLog.e( "adapter theme : " + theme );
    }

    public boolean getTheme( ) {
        return theme;
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

    protected void overrideFonts( final View v ) {
        try {
            if ( v instanceof ViewGroup ) {
                ViewGroup vg = ( ViewGroup ) v;
                for ( int i = 0; i < vg.getChildCount( ); i++ ) {
                    View child = vg.getChildAt( i );
                    overrideFonts( child );
                }
            } else if ( v instanceof FontTextView ) {
                ( ( TextView ) v ).setTypeface( FontManager.getInstance( ).getFont( ( ( FontTextView ) v ).getFontStyle( ) ) );
            } else if ( v instanceof TextView ) {
                ( ( TextView ) v ).setTypeface( FontManager.getInstance( ).getFont( ( ( TextView ) v ).getTypeface( ).getStyle( ) ) );
            }
        } catch ( Exception e ) {

        }
    }
}
