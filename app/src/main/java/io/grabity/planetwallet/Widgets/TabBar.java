package io.grabity.planetwallet.Widgets;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.annotation.Px;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;

/**
 * Created by. JcobPark on 2018. 08. 29
 */

public class TabBar extends HorizontalScrollView implements View.OnClickListener, ViewPager.OnPageChangeListener, Themeable {

    private RelativeLayout wrapper;
    private LinearLayout container;
    private View indicator;

    private ArrayList< ButtonItem > buttonItems;

    private boolean equalizeDivision = true;
    private ViewPager viewPager;

    private int currentPosition = 0;

    private int indicatorColor;

    private OnTabBarItemClickListener onTabBarItemClickListener;

    private int paddingLeft = 0;

    private boolean tabBarTheme;

    public TabBar( Context context, AttributeSet attrs ) {
        this( context, attrs, 0 );
    }

    public TabBar( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );

        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.TabBar, defStyleAttr, 0 );
        indicatorColor = a.getColor( R.styleable.TabBar_tabBarIndicatorColor, Color.WHITE );

        a.recycle( );
        viewInit( );
    }

    private void viewInit( ) {

        {
            if ( !isInEditMode( ) )
                setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, getResources( ).getDimensionPixelSize( R.dimen.tabbarHeight ) ) );
        }

        {
            wrapper = new RelativeLayout( getContext( ) );
            addView( wrapper, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT ) );
        }

        {
            container = new LinearLayout( getContext( ) );
            wrapper.addView( container, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT ) );
        }

        if ( !isInEditMode( ) ) {

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( 0, getResources( ).getDimensionPixelSize( R.dimen.tabbarIndicatorHeight ) );
//            params.addRule( RelativeLayout.ALIGN_PARENT_BOTTOM );
            indicator = new View( getContext( ) );
            indicator.setBackgroundColor( indicatorColor );
            wrapper.addView( indicator, params );

        }

    }

    @Override
    public void onClick( View view ) {
        if ( buttonItems != null ) {
            for ( int i = 0; i < buttonItems.size( ); i++ ) {
                if ( Utils.equals( buttonItems.get( i ).getView( ), view ) ) {
                    currentPosition = i;
                    if ( onTabBarItemClickListener != null ) {
                        onTabBarItemClickListener.onTabBarItemClick( buttonItems.get( i ) );
                    }
                    if ( this.viewPager == null ) {
                        indicator.setX( view.getX( ) );
                    } else {
                        this.viewPager.setCurrentItem( i );
                    }
                }
            }
        }
    }


    public ArrayList< ButtonItem > getItems( ) {
        return buttonItems;
    }

    public void setItems( ButtonItem... items ) {
        if ( buttonItems == null ) buttonItems = new ArrayList<>( );
        else buttonItems.clear( );
        container.removeAllViews( );

        if ( equalizeDivision ) {

            this.getViewTreeObserver( ).addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener( ) {
                @Override
                public void onGlobalLayout( ) {
                    container.getLayoutParams( ).width = TabBar.this.getWidth( );
                    container.requestLayout( );
                    indicator.getLayoutParams( ).width = ( int ) ( ( float ) TabBar.this.getWidth( ) / ( float ) buttonItems.size( ) );
                    indicator.requestLayout( );
                    TabBar.this.getViewTreeObserver( ).removeOnGlobalLayoutListener( this );
                }
            } );

        }

        boolean textOnly = true;
        for ( ButtonItem item : items ) {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( 0, ViewGroup.LayoutParams.MATCH_PARENT, 1 );
            RelativeLayout relativeLayout = new RelativeLayout( getContext( ) );
            relativeLayout.setLayoutParams( params );

            if ( item.getResource( ) > 0 && item.getText( ) != null ) {
                textOnly = false;
                {
                    StretchImageView imageView = new StretchImageView( getContext( ) );
                    RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams( ( int ) Utils.dpToPx( getContext( ), 40 ), ViewGroup.LayoutParams.WRAP_CONTENT );
                    params1.addRule( RelativeLayout.CENTER_IN_PARENT );
                    imageView.setLayoutParams( params1 );
                    imageView.setPadding( 0, 0, 0, ( int ) Utils.dpToPx( getContext( ), 12 ) );
                    relativeLayout.addView( imageView );
                    imageView.setImageResource( item.getResource( ) );
                }


                {
                    TextView textView = new TextView( getContext( ) );
                    RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
                    params1.addRule( RelativeLayout.ALIGN_PARENT_BOTTOM );
                    textView.setText( item.getText( ) );
                    textView.setLayoutParams( params1 );
                    textView.setGravity( Gravity.CENTER );
                    textView.setTextSize( TypedValue.COMPLEX_UNIT_DIP, item.getTextSize( ) );
                    textView.setPadding( 0, 0, 0, ( int ) Utils.dpToPx( getContext( ), 8 ) );
                    textView.setTextColor( item.getTextColor( ) );
                    relativeLayout.addView( textView );
                }
                relativeLayout.setOnClickListener( this );
                item.setView( relativeLayout );
                container.addView( relativeLayout, params );
                buttonItems.add( item );
            } else if ( item.getText( ) != null && item.getResource( ) == -1 ) {

                TextView textView = new TextView( getContext( ) );
                textView.setText( item.getText( ) );
                textView.setGravity( Gravity.CENTER );
                textView.setTextSize( TypedValue.COMPLEX_UNIT_DIP, item.getTextSize( ) );
                textView.setTextColor( item.getTextColor( ) );
                textView.setOnClickListener( this );
                textView.setPadding( ( int ) DPToPX( 8 ), 0, ( int ) DPToPX( 8 ), 0 );
                textView.setTypeface( Typeface.createFromAsset( getContext( ).getAssets( ), "fonts/WorkSans-Bold.otf" ), Typeface.BOLD );
                item.setView( textView );
                container.addView( textView, new LinearLayout.LayoutParams( 0, ViewGroup.LayoutParams.MATCH_PARENT, 1 ) );
                buttonItems.add( item );

            }

        }

        if ( textOnly ) {
            if ( indicator != null ) {
                ( ( RelativeLayout.LayoutParams ) indicator.getLayoutParams( ) ).addRule( RelativeLayout.ALIGN_PARENT_BOTTOM );
            }
        }

        container.setWeightSum( buttonItems.size( ) );

        if ( buttonItems.size( ) > 0 ) {
            if ( indicator != null ) {
//                indicator.getLayoutParams( ).width = getTextViewWidth( ( TextView ) buttonItems.get( 0 ).getView( ) );
                indicator.getLayoutParams( ).width = Utils.getScreenWidth( getContext( ) ) / buttonItems.size( );
                indicator.requestLayout( );
            }
        }


    }

    public void setEqualizeDivision( boolean equalizeDivision ) {
        this.equalizeDivision = equalizeDivision;
        if ( buttonItems != null ) {
            if ( this.equalizeDivision ) {
                this.getViewTreeObserver( ).addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener( ) {
                    @Override
                    public void onGlobalLayout( ) {
                        container.getLayoutParams( ).width = TabBar.this.getWidth( );
                        container.requestLayout( );
                        indicator.getLayoutParams( ).width = ( int ) ( ( float ) TabBar.this.getWidth( ) / ( float ) buttonItems.size( ) );
                        indicator.requestLayout( );
                        TabBar.this.getViewTreeObserver( ).removeOnGlobalLayoutListener( this );
                    }
                } );

                container.getLayoutParams( ).width = TabBar.this.getWidth( );
                container.requestLayout( );
                indicator.getLayoutParams( ).width = ( int ) ( ( float ) TabBar.this.getWidth( ) / ( float ) buttonItems.size( ) );
                indicator.requestLayout( );
                indicator.setX( buttonItems.get( currentPosition ).getView( ).getX( ) );

            } else {

                container.setLayoutParams( new RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT ) );
                container.setWeightSum( -1.0f );
                container.requestLayout( );

                this.getViewTreeObserver( ).addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener( ) {
                    @Override
                    public void onGlobalLayout( ) {
                        TabBar.this.getViewTreeObserver( ).removeOnGlobalLayoutListener( this );
                        int i = 0;
                        for ( ButtonItem item : buttonItems ) {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT );
                            if ( i == 0 )
                                params.leftMargin = paddingLeft;
                            item.getView( ).setLayoutParams( params );
                            item.getView( ).setMinimumWidth( TabBar.this.getWidth( ) / buttonItems.size( ) );
                            item.getView( ).requestLayout( );
                            i++;
                        }

                    }
                } );
                indicator.getLayoutParams( ).width = getTextViewWidth( ( TextView ) buttonItems.get( currentPosition ).getView( ) );
                indicator.requestLayout( );
                indicator.setX( paddingLeft + buttonItems.get( currentPosition ).getView( ).getX( ) );

            }
        }
    }

    public void setViewPager( ViewPager viewPager ) {
        this.viewPager = viewPager;
        this.viewPager.addOnPageChangeListener( this );
    }

    public void setIndicatorColor( int color ) {
        if ( indicator != null ) {
            indicator.setBackgroundColor( color );
        }
    }

    public OnTabBarItemClickListener getOnTabBarItemClickListener( ) {
        return onTabBarItemClickListener;
    }

    public void setOnTabBarItemClickListener( OnTabBarItemClickListener onTabBarItemClickListener ) {
        this.onTabBarItemClickListener = onTabBarItemClickListener;
    }

    @Override
    public void onPageScrolled( int position, float positionOffset, int positionOffsetPixels ) {
        if ( equalizeDivision ) {
            indicator.setX( ( ( float ) container.getWidth( ) / ( float ) buttonItems.size( ) ) * ( position + positionOffset ) );
        } else {
            if ( position < buttonItems.size( ) )
                indicator.setX( paddingLeft + buttonItems.get( position ).getView( ).getX( ) + buttonItems.get( position ).getView( ).getWidth( ) * positionOffset );
        }

    }

    @Override
    public void onPageSelected( int position ) {
        currentPosition = position;

        for ( int i = 0; i < getItems( ).size( ); i++ ) {
            if ( !getItems( ).get( i ).getView( ).getClass( ).equals( TextView.class ) ) return;
            if ( !tabBarTheme ) {
                ( ( TextView ) getItems( ).get( i ).getView( ) ).setTextColor( Color.parseColor( i == position ? "#FFFFFF" : "#5C5964" ) );
            } else {
                ( ( TextView ) getItems( ).get( i ).getView( ) ).setTextColor( Color.parseColor( i == position ? "#000000" : "#aaaaaa" ) );
            }
        }

    }

    @Override
    public void onPageScrollStateChanged( int state ) {

    }

    @Override
    public void setPadding( @Px int left, @Px int top, @Px int right, @Px int bottom ) {
        paddingLeft = left;
    }

    public static class ButtonItem {

        private int resource = -1;
        private String text;
        private int textColor = Color.BLACK;
        private Object tag;
        private int textSize = 10;

        private View view;

        public ButtonItem( String text ) {
            this.text = text;
        }

        public ButtonItem( int resource ) {
            this.resource = resource;
        }

        public ButtonItem( int resource, String text ) {
            this.resource = resource;
            this.text = text;
        }

        public ButtonItem( ) {
        }

        public String getText( ) {
            return text;
        }

        public ButtonItem setText( String text ) {
            this.text = text;
            return this;
        }

        public int getResource( ) {
            return resource;
        }

        public ButtonItem setResource( int resource ) {
            this.resource = resource;
            return this;
        }

        public Object getTag( ) {
            return tag;
        }

        public ButtonItem setTag( Object tag ) {
            this.tag = tag;
            return this;
        }

        public int getTextColor( ) {
            return textColor;
        }

        public ButtonItem setTextColor( int textColor ) {
            this.textColor = textColor;
            return this;

        }

        public int getTextSize( ) {
            return textSize;
        }

        public ButtonItem setTextSize( int textSize ) {
            this.textSize = textSize;
            return this;
        }

        public View getView( ) {
            return view;
        }

        public void setView( View view ) {
            this.view = view;
        }


    }

    public float DPToPX( int dp ) {
        Resources resources = getContext( ).getResources( );
        DisplayMetrics metrics = resources.getDisplayMetrics( );
        float px = dp * ( metrics.densityDpi / 160f );
        return px;
    }

    public int getTextViewWidth( TextView textView ) {

        // Base honeycomb over version
        WindowManager wm =
                ( WindowManager ) textView.getContext( ).getSystemService( Context.WINDOW_SERVICE );
        Display display = wm.getDefaultDisplay( );

        int deviceWidth;

        Point size = new Point( );
        display.getSize( size );
        deviceWidth = size.x;

        int widthMeasureSpec = MeasureSpec.makeMeasureSpec( deviceWidth, MeasureSpec.AT_MOST );
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec( 0, MeasureSpec.UNSPECIFIED );
        textView.measure( widthMeasureSpec, heightMeasureSpec );
        return textView.getMeasuredWidth( );
    }

    public interface OnTabBarItemClickListener {
        void onTabBarItemClick( ButtonItem item );
    }

    @Override
    public void setTheme( boolean theme ) {
        if ( !theme ) { //Dark
            tabBarTheme = false;
            setIndicatorColor( Color.argb( 255, 255, 255, 255 ) );

            if ( getItems( ) != null ) {
                for ( int i = 0; i < getItems( ).size( ); i++ ) {
                    ( ( TextView ) getItems( ).get( i ).getView( ) ).setTextColor( Color.parseColor( "#5C5964" ) );
                }
                ( ( TextView ) getItems( ).get( 0 ).getView( ) ).setTextColor( Color.WHITE );
            }
        } else { // white
            tabBarTheme = true;
            setIndicatorColor( Color.argb( 255, 0, 0, 0 ) );

            if ( getItems( ) != null ) {
                for ( int i = 0; i < getItems( ).size( ); i++ ) {
                    ( ( TextView ) getItems( ).get( i ).getView( ) ).setTextColor( Color.parseColor( "#aaaaaa" ) );
                }
                ( ( TextView ) getItems( ).get( 0 ).getView( ) ).setTextColor( Color.BLACK );
            }
        }


    }

}
