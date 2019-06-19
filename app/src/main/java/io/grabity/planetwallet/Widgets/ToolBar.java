package io.grabity.planetwallet.Widgets;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;


/**
 * Change ToolBar 2019. 05. 28
 */


public class ToolBar extends RelativeLayout implements View.OnClickListener, Themeable {

    private int defaultTheme;

    private View backgroundView;
    private TextView textTitle;
    private OnToolBarClickListener onToolBarClickListener;

    private String title;

    private int bottomWidth;
    private int bottomColor;
    private int titleColor;

    private View line;

    private ArrayList< ButtonItem > items;

    private StretchImageView imageViewLeft;
    private StretchImageView imageViewRight;

    private Drawable darkLeftDrawable;
    private Drawable whiteLeftDrawable;

    private Drawable darkRightDrawable;
    private Drawable whiteRightDrawable;

    private Drawable imageLeftDrawable;
    private Drawable imageRightDrawable;

    public ToolBar( Context context, AttributeSet attrs ) {
        this( context, attrs, 0 );
    }

    public ToolBar( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.ToolBar, defStyleAttr, 0 );
        this.title = a.getString( R.styleable.ToolBar_toolbarTitle );
        this.bottomWidth = a.getDimensionPixelSize( R.styleable.ToolBar_toolbarBottomWidth, 0 );
        this.defaultTheme = a.getInt( R.styleable.ToolBar_defaultTheme, 0 );

        this.bottomColor = Color.TRANSPARENT;
        this.titleColor = Color.BLACK;

        this.darkLeftDrawable = a.getDrawable( R.styleable.ToolBar_blackThemeLeftSrc );
        this.whiteLeftDrawable = a.getDrawable( R.styleable.ToolBar_whiteThemeLeftSrc );

        this.darkRightDrawable = a.getDrawable( R.styleable.ToolBar_blackThemeRightSrc );
        this.whiteRightDrawable = a.getDrawable( R.styleable.ToolBar_whiteThemeRightSrc );

        if ( defaultTheme >= 0 ) {
            setTheme( false );
        }

        a.recycle( );
        viewInit( );
    }


    public void viewInit( ) {

        {
//            if ( !isInEditMode( ) )
//                setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, getResources( ).getDimensionPixelSize( R.dimen.toolbarHeight ) ) );
        }

        int height = ( int ) Utils.dpToPx( getContext( ), 68 );
        {
            backgroundView = new View( getContext( ) );
            RelativeLayout.LayoutParams params = new LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
            addView( backgroundView, params );
            backgroundView.setAlpha( 0f );
        }

        {
            RelativeLayout relativeLayout = new RelativeLayout( getContext( ) );
            RelativeLayout.LayoutParams params = new LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, height );
            params.addRule( CENTER_HORIZONTAL );
            params.addRule( ALIGN_PARENT_BOTTOM );
            addView( relativeLayout, params );

            textTitle = new TextView( getContext( ) );
            textTitle.setLayoutParams( params );
            textTitle.setGravity( Gravity.CENTER );
            textTitle.setTextColor( titleColor );
            textTitle.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 18 );
            textTitle.setTypeface( Typeface.createFromAsset( getContext( ).getAssets( ), "fonts/WorkSans-SemiBold.otf" ), Typeface.BOLD );
            textTitle.setText( title );
            textTitle.setBackgroundColor( Color.RED );
            textTitle.setAlpha( 0.3f );

            relativeLayout.addView( textTitle );
        }

        {
            line = new LinearLayout( getContext( ) );
            LayoutParams params = new LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, bottomWidth );
            params.addRule( ALIGN_PARENT_BOTTOM );
            line.setLayoutParams( params );
            line.setBackgroundColor( bottomColor );
            addView( line );
        }

        {  //Left
            LayoutParams params = new LayoutParams( getResources( ).getDimensionPixelSize( R.dimen.toolbarHeight ), getResources( ).getDimensionPixelSize( R.dimen.toolbarHeight ) );
            params.addRule( ALIGN_PARENT_LEFT );
            params.addRule( ALIGN_PARENT_BOTTOM );
            imageViewLeft = new StretchImageView( getContext( ) );
            imageViewLeft.setImageDrawable( getImageLeftDrawable( ) );
            imageViewLeft.setPadding( ( int ) DPToPX( 18 ), ( int ) DPToPX( 14 ), ( int ) DPToPX( 14 ), ( int ) DPToPX( 14 ) );
            imageViewLeft.setOnClickListener( this );

            addView( imageViewLeft, params );
        }

        { //Right
            LayoutParams params = new LayoutParams( getResources( ).getDimensionPixelSize( R.dimen.toolbarHeight ), getResources( ).getDimensionPixelSize( R.dimen.toolbarHeight ) );
            params.addRule( ALIGN_PARENT_RIGHT );
            params.addRule( ALIGN_PARENT_BOTTOM );
            imageViewRight = new StretchImageView( getContext( ) );
            imageViewRight.setImageDrawable( getImageRightDrawable( ) );
            imageViewRight.setPadding( ( int ) DPToPX( 14 ), ( int ) DPToPX( 14 ), ( int ) DPToPX( 18 ), ( int ) DPToPX( 14 ) );
            imageViewRight.setOnClickListener( this );

            addView( imageViewRight, params );
        }

        items = new ArrayList<>( );

    }

    public void setTitleColor( int titleColor ) {
        this.titleColor = titleColor;
        if ( textTitle != null ) textTitle.setTextColor( titleColor );
    }

    public void setBottomColor( int bottomColor ) {
        this.bottomColor = bottomColor;
        if ( line != null ) line.setBackgroundColor( bottomColor );

    }

    public String getTitle( ) {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
        if ( textTitle != null ) textTitle.setText( title );

    }

    public void setLeftButton( ButtonItem button ) {
        imageViewLeft.setOnClickListener( this );
        button.setView( imageViewLeft );
        items.add( button );
    }

    public void setRightButton( ButtonItem button ) {
        imageViewRight.setOnClickListener( this );
        button.setView( imageViewRight );
        items.add( button );
    }

    public void addLeftButton( ButtonItem button ) {

        if ( button.getResource( ) > 0 ) {
            imageViewLeft.setOnClickListener( this );
            imageViewLeft.setImageResource( button.getResource() );
            button.setView( imageViewLeft );
            items.add( button );
        }

    }

    public ArrayList< ButtonItem > getButtonItems( ) {
        return items;
    }

    public static class ButtonItem {

        private Object tag;
        private View view;
        private int resource = -1;

        public ButtonItem( ) {

        }

        public int getResource( ) {
            return resource;
        }

        public void setResource( int resource ) {
            this.resource = resource;
        }

        public ButtonItem( int resource ) {
            this.resource = resource;
        }

        public Object getTag( ) {
            return tag;
        }

        public ButtonItem setTag( Object tag ) {
            this.tag = tag;
            return this;
        }

        public View getView( ) {
            return view;
        }

        public void setView( View view ) {
            this.view = view;

        }
    }

    public void setOnToolBarClickListener( OnToolBarClickListener onToolBarClickListener ) {
        this.onToolBarClickListener = onToolBarClickListener;
    }

    public interface OnToolBarClickListener {
        void onToolBarClick( Object tag, View view );
    }


    @Override
    public void onClick( View v ) {

        if ( items != null ) {

            for ( int i = 0; i < items.size( ); i++ ) {

                if ( Utils.equals( items.get( i ).getView( ), v ) ) {

                    if ( onToolBarClickListener != null ) {

                        onToolBarClickListener.onToolBarClick( items.get( i ).getTag( ), v );
                    }
                }
            }
        }
    }

    public void setLeftDrawable( Drawable d ) {
        this.imageLeftDrawable = d;
        if ( imageViewLeft != null ) imageViewLeft.setImageDrawable( imageLeftDrawable );
    }

    public void setRightDrawable( Drawable d ) {
        this.imageRightDrawable = d;
        if ( imageViewRight != null ) imageViewRight.setImageDrawable( imageRightDrawable );
    }

    public Drawable getImageLeftDrawable( ) {
        return imageLeftDrawable;
    }

    public void setImageLeftDrawable( Drawable imageLeftDrawable ) {
        this.imageLeftDrawable = imageLeftDrawable;
    }

    public Drawable getImageRightDrawable( ) {
        return imageRightDrawable;
    }

    public void setImageRightDrawable( Drawable imageRightDrawable ) {
        this.imageRightDrawable = imageRightDrawable;
    }

    public void setBackgroundAlpha( float alpha ) {
        if ( backgroundView != null ) {
            backgroundView.setAlpha( alpha );
        }
    }

    @Override
    public void setTheme( boolean theme ) {

        if ( defaultTheme > 0 ) {
            theme = ( defaultTheme == 2 ) != theme;

            if ( !theme ) { //Dark
                setTitleColor( Color.WHITE );
                setBottomColor( Color.argb( 255, 30, 30, 40 ) );

                if ( darkLeftDrawable != null ) setLeftDrawable( darkLeftDrawable );
                if ( darkRightDrawable != null ) setRightDrawable( darkRightDrawable );

                if ( backgroundView != null )
                    backgroundView.setBackgroundColor( Color.BLACK );

            } else { // white
                setTitleColor( Color.BLACK );
                setBottomColor( Color.argb( 255, 237, 237, 237 ) );

                if ( whiteLeftDrawable != null ) setLeftDrawable( whiteLeftDrawable );
                if ( whiteRightDrawable != null ) setRightDrawable( whiteRightDrawable );

                if ( backgroundView != null )
                    backgroundView.setBackgroundColor( Color.WHITE );

            }
        }
    }

    public float DPToPX( int dp ) {
        Resources resources = getContext( ).getResources( );
        DisplayMetrics metrics = resources.getDisplayMetrics( );
        float px = dp * ( metrics.densityDpi / 160f );
        return px;
    }
}
