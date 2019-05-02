package io.grabity.planetwallet.Widgets;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
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
 * Created by. JcobPark on 2018. 08. 29
 */

public class ToolBar extends RelativeLayout implements View.OnClickListener {


    private TextView textTitle;
    private StretchHorizontalImageView imageIcon;

    private LinearLayout groupLeft;
    private LinearLayout groupRight;
    private ArrayList< ButtonItem > buttonItems;

    private OnToolBarClickListener onToolBarClickListener;

    private String title;
    private int titleColor;
    private int titleIcon;

    public ToolBar( Context context, AttributeSet attrs ) {
        this( context, attrs, 0 );
    }

    public ToolBar( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.ToolBar, defStyleAttr, 0 );
        this.title = a.getString( R.styleable.ToolBar_toolbarTitle );
        this.titleColor = a.getColor( R.styleable.ToolBar_toolbarTitleColor, Color.parseColor( "#FFFFFF" ) );
        this.titleIcon = a.getResourceId( R.styleable.ToolBar_toolbarTitleIcon, -1 );
        viewInit( );
    }

    private void viewInit( ) {

        {
            if ( !isInEditMode( ) )
                setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, getResources( ).getDimensionPixelSize( R.dimen.toolbarHeight ) ) );

        }

        {
            LinearLayout wrapperTitle = new LinearLayout( getContext( ) );
            LayoutParams params = new LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT );
            params.addRule( CENTER_IN_PARENT );
            wrapperTitle.setLayoutParams( params );
            wrapperTitle.setOrientation( LinearLayout.HORIZONTAL );
            addView( wrapperTitle );
            {
                imageIcon = new StretchHorizontalImageView( getContext( ) );
                imageIcon.setLayoutParams( new LinearLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, getResources( ).getDimensionPixelSize( R.dimen.toolbarHeight ) ) );
                imageIcon.setPadding( ( int ) DPToPX( 12 ), ( int ) DPToPX( 12 ), 0, ( int ) DPToPX( 12 ) );
                if ( titleIcon != -1 )
                    imageIcon.setImageResource( titleIcon );
                wrapperTitle.addView( imageIcon );
            }

            {
                textTitle = new TextView( getContext( ) );
                textTitle.setLayoutParams( new LinearLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT ) );
                textTitle.setGravity( Gravity.CENTER );
                textTitle.setTextColor( titleColor );
                textTitle.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 16 );
                if ( titleIcon != -1 )
                    textTitle.setPadding( 0, 0, ( int ) DPToPX( 12 ), 0 );
//                textTitle.setTypeface( Typeface.create( Typeface.SANS_SERIF , Typeface.BOLD ) );
                textTitle.setText( title );
                wrapperTitle.addView( textTitle );
            }

        }

        {
            groupLeft = new LinearLayout( getContext( ) );
            groupLeft.setGravity( Gravity.LEFT );
            groupLeft.setOrientation( LinearLayout.HORIZONTAL );
            addView( groupLeft, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT ) );
        }

        {
            groupRight = new LinearLayout( getContext( ) );
            groupRight.setGravity( Gravity.RIGHT );
            groupRight.setOrientation( LinearLayout.HORIZONTAL );
            groupRight.setLayoutDirection( LAYOUT_DIRECTION_RTL );
            addView( groupRight, new LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT ) );
            ( ( LayoutParams ) groupRight.getLayoutParams( ) ).addRule( ALIGN_PARENT_RIGHT );
        }


        buttonItems = new ArrayList<>( );
    }

    public void addLeftButton( ButtonItem resource ) {

        if ( groupLeft != null && resource != null ) {

            // Both
            if ( resource.getText( ) != null && resource.getResource( ) > 0 ) {

                // Do not disturb!!

            } else if ( resource.getResource( ) > 0 ) {

                // Image
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( getResources( ).getDimensionPixelSize( R.dimen.toolbarHeight ), getResources( ).getDimensionPixelSize( R.dimen.toolbarHeight ) );
                StretchImageView imageView = new StretchImageView( getContext( ) );
                imageView.setImageResource( resource.getResource( ) );
                imageView.setOnClickListener( this );
                imageView.setPadding( ( int ) DPToPX( 12 ), ( int ) DPToPX( 12 ), ( int ) DPToPX( 12 ), ( int ) DPToPX( 12 ) );
                resource.setView( imageView );
                groupLeft.addView( imageView, params );

            } else if ( resource.getText( ) != null ) {

                // Text
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT );
                TextView textView = new TextView( getContext( ) );
                textView.setText( resource.getText( ) );
                textView.setGravity( Gravity.CENTER );
                textView.setTextSize( TypedValue.COMPLEX_UNIT_DIP, resource.getTextSize( ) );
                textView.setTextColor( resource.getTextColor( ) );
                textView.setOnClickListener( this );
                resource.setView( textView );
                groupLeft.addView( textView, params );

            }

            if ( buttonItems != null ) {
                buttonItems.add( resource );
            }
        }

    }

    public void removeLeftButton( int index ) {
        if ( groupLeft != null ) {
            if ( groupLeft.getChildCount( ) > 0 && groupLeft.getChildCount( ) > index && index >= 0 ) {
                buttonItems.remove( groupLeft.getChildAt( index ) );
                groupLeft.removeViewAt( index );
            }
        }
    }

    public void setLeftButtons( ButtonItem... items ) {
        if ( items != null ) {
            removeLeftButtons( );
            for ( ButtonItem item : items ) {
                addLeftButton( item );
            }
        }
    }

    public void setLeftButton( ButtonItem item ) {
        if ( item != null ) {
            removeLeftButtons( );
            addLeftButton( item );
        }
    }

    public void removeLeftButtons( ) {
        if ( groupLeft != null ) {
            for ( int i = 0; i < groupLeft.getChildCount( ); i++ ) {
                buttonItems.remove( groupLeft.getChildAt( i ) );
            }
            groupLeft.removeAllViews( );
        }
    }

    // Right buttons

    public void addRightButton( ButtonItem resource ) {

        if ( groupRight != null && resource != null ) {

            // Both
            if ( resource.getText( ) != null && resource.getResource( ) > 0 ) {

                // Do not disturb!!

            } else if ( resource.getResource( ) > 0 ) {

                // Image
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( getResources( ).getDimensionPixelSize( R.dimen.toolbarHeight ), getResources( ).getDimensionPixelSize( R.dimen.toolbarHeight ) );
                StretchImageView imageView = new StretchImageView( getContext( ) );
                imageView.setOnClickListener( this );
                imageView.setPadding( ( int ) DPToPX( 12 ), ( int ) DPToPX( 12 ), ( int ) DPToPX( 12 ), ( int ) DPToPX( 12 ) );
                resource.setView( imageView );
                imageView.setImageResource( resource.getResource( ) );
                groupRight.addView( imageView, params );

            } else if ( resource.getText( ) != null ) {

                // Text
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT );
                TextView textView = new TextView( getContext( ) );
                textView.setText( resource.getText( ) );
                textView.setGravity( Gravity.CENTER );
                textView.setTextSize( TypedValue.COMPLEX_UNIT_DIP, resource.getTextSize( ) );
                textView.setTextColor( resource.getTextColor( ) );
                textView.setOnClickListener( this );
                resource.setView( textView );
                groupRight.addView( textView, params );

            }

            if ( buttonItems != null ) {
                buttonItems.add( resource );
            }
        }

    }

    public void removeRightButton( int index ) {
        if ( groupRight != null ) {
            if ( groupRight.getChildCount( ) > 0 && groupRight.getChildCount( ) > index && index >= 0 ) {
                buttonItems.remove( groupRight.getChildAt( index ) );
                groupRight.removeViewAt( index );
            }
        }
    }

    public void setRightButtons( ButtonItem... items ) {
        if ( items != null ) {
            removeRightButtons( );
            for ( ButtonItem item : items ) {
                addRightButton( item );
            }
        }
    }

    public void setRightButton( ButtonItem item ) {
        if ( item != null ) {
            removeRightButtons( );
            addRightButton( item );
        }
    }

    public void removeRightButtons( ) {
        if ( groupRight != null ) {
            for ( int i = 0; i < groupRight.getChildCount( ); i++ ) {
                buttonItems.remove( groupRight.getChildAt( i ) );
            }
            groupRight.removeAllViews( );
        }
    }

    @Override
    public void onClick( View view ) {
        if ( buttonItems != null ) {

            for ( int i = 0; i < buttonItems.size( ); i++ ) {
                if ( Utils.equals( buttonItems.get( i ).getView( ), view ) ) {
                    if ( onToolBarClickListener != null ) {

                        int direction = -1;
                        int position = -1;

                        for ( int j = 0; j < groupLeft.getChildCount( ); j++ ) {

                            if ( Utils.equals( groupLeft.getChildAt( j ), view ) ) {
                                direction = 0;
                                position = j;
                            }

                        }

                        for ( int j = 0; j < groupRight.getChildCount( ); j++ ) {

                            if ( Utils.equals( groupRight.getChildAt( j ), view ) ) {
                                direction = 1;
                                position = j;
                            }
                        }

                        onToolBarClickListener.onToolBarClick( buttonItems.get( i ).getTag( ), view, direction, position );
                    }
                }
            }

        }
    }

    public void setVisibilityRightButtons( int visible ) {
        groupRight.setVisibility( visible );
    }

    public String getTitle( ) {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
        if ( textTitle != null ) textTitle.setText( title );
    }

    public int getTitleColor( ) {
        return titleColor;
    }

    public void setTitleColor( int titleColor ) {
        this.titleColor = titleColor;
        if ( textTitle != null ) textTitle.setTextColor( titleColor );
    }

    public ArrayList< ButtonItem > getButtonItems( ) {
        return buttonItems;
    }

    public OnToolBarClickListener getOnToolBarClickListener( ) {
        return onToolBarClickListener;
    }

    public void setOnToolBarClickListener( OnToolBarClickListener onToolBarClickListener ) {
        this.onToolBarClickListener = onToolBarClickListener;
    }

    public static class ButtonItem {

        private int resource = -1;
        private String text;
        private int textColor = Color.BLACK;
        private Object tag;
        private int textSize = 16;

        private View view;

        public ButtonItem( String text ) {
            this.text = text;
        }

        public ButtonItem( int resource ) {
            this.resource = resource;
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

    public interface OnToolBarClickListener {
        void onToolBarClick( Object tag, View view, int direction, int index );
    }

    public float DPToPX( int dp ) {
        Resources resources = getContext( ).getResources( );
        DisplayMetrics metrics = resources.getDisplayMetrics( );
        float px = dp * ( metrics.densityDpi / 160f );
        return px;
    }

}
