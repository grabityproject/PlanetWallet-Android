package io.grabity.planetwallet.Widgets;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;

public class SlideDrawerLayout extends ViewGroup {

    public static class Position {
        public final static int TOP = 0;
        public final static int LEFT = 1;
        public final static int RIGHT = 2;
        public final static int BOTTOM = 3;
    }

    private SparseArray< Trigger > triggers;

    private boolean isGettingTriggerLocation = false;
    private boolean inSideSlide = false;

    private int[] thisLocations = new int[ 2 ];

    private boolean isOpen = false;
    private boolean onTrigger = false;

    private boolean isGoToOutside = false;

    private float beforeTouchPositionX;
    private float beforeTouchPositionY;

    private float inSideTouchPositionX;
    private float inSideTouchPositionY;

    private int currentMovingPosition = -1;

    private ArrayList< OnSlideDrawerListener > onSlideDrawerListeners = new ArrayList<>( );

    private ArrayList< View > bypassAreaViews;
    private ArrayList< View > doNotEventViews;

    public SlideDrawerLayout( @NonNull Context context ) {
        super( context );
        setUp( );

    }

    public SlideDrawerLayout( @NonNull Context context, @Nullable AttributeSet attrs ) {
        super( context, attrs );
        setUp( );
    }

    public SlideDrawerLayout( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        setUp( );
    }

    void setUp( ) {

        triggers = new SparseArray<>( );
        triggers.put( Position.TOP, new Trigger( Position.TOP, null ) );
        triggers.put( Position.LEFT, new Trigger( Position.LEFT, null ) );
        triggers.put( Position.RIGHT, new Trigger( Position.RIGHT, null ) );
        triggers.put( Position.BOTTOM, new Trigger( Position.BOTTOM, null ) );

    }

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
        final int widthMode = MeasureSpec.getMode( widthMeasureSpec );
        final int widthSize = MeasureSpec.getSize( widthMeasureSpec );
        final int heightMode = MeasureSpec.getMode( heightMeasureSpec );
        final int heightSize = MeasureSpec.getSize( heightMeasureSpec );

        if ( widthMode != MeasureSpec.EXACTLY ) {
            throw new IllegalStateException( "Width must have an exact value or MATCH_PARENT" );
        } else if ( heightMode != MeasureSpec.EXACTLY ) {
            throw new IllegalStateException( "Height must have an exact value or MATCH_PARENT" );
        }

        final int count = getChildCount( );

        for ( int i = 0; i < count; i++ ) {
            final View child = getChildAt( i );
            final LayoutParams lp = ( LayoutParams ) child.getLayoutParams( );

            int childWidthSpec;
            if ( lp.width == LayoutParams.WRAP_CONTENT ) {
                childWidthSpec = MeasureSpec.makeMeasureSpec( widthSize, MeasureSpec.AT_MOST );
            } else if ( lp.width == LayoutParams.MATCH_PARENT ) {
                childWidthSpec = MeasureSpec.makeMeasureSpec( widthSize, MeasureSpec.EXACTLY );
            } else {
                childWidthSpec = MeasureSpec.makeMeasureSpec( lp.width, MeasureSpec.EXACTLY );
            }

            int childHeightSpec;
            if ( lp.height == LayoutParams.WRAP_CONTENT ) {
                childHeightSpec = MeasureSpec.makeMeasureSpec( heightSize, MeasureSpec.AT_MOST );
            } else if ( lp.height == LayoutParams.MATCH_PARENT ) {
                childHeightSpec = MeasureSpec.makeMeasureSpec( heightSize, MeasureSpec.EXACTLY );
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec( lp.height, MeasureSpec.EXACTLY );
            }

            child.measure( childWidthSpec, childHeightSpec );

        }
        setMeasuredDimension(
                getDefaultSize( getSuggestedMinimumWidth( ), widthMeasureSpec ),
                getDefaultSize( getSuggestedMinimumHeight( ), heightMeasureSpec )
        );
    }


    public static int getDefaultSize( int size, int measureSpec ) {
        int result = size;
        int specMode = MeasureSpec.getMode( measureSpec );
        int specSize = MeasureSpec.getSize( measureSpec );

        switch ( specMode ) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    protected void onLayout( boolean changed, int l, int t, int r, int b ) {
        final int paddingLeft = getPaddingLeft( );
        final int paddingTop = getPaddingTop( );

        final int childCount = getChildCount( );
        int yStart = paddingTop;


        triggers.get( Position.TOP ).setView( null );
        triggers.get( Position.LEFT ).setView( null );
        triggers.get( Position.RIGHT ).setView( null );
        triggers.get( Position.BOTTOM ).setView( null );

        for ( int i = 0; i < childCount; i++ ) {
            final View child = getChildAt( i );

            if ( child.getVisibility( ) == GONE ) {
                continue;
            }

            final LayoutParams lp = ( LayoutParams ) child.getLayoutParams( );
            int childHeight = child.getMeasuredHeight( );

            final int childTop = yStart;
            final int childBottom = childTop + childHeight;
            final int childLeft = paddingLeft;
            final int childRight = childLeft + child.getMeasuredWidth( );


            if ( lp.position == Position.TOP ) { // top

                if ( triggers.get( Position.TOP ).getView( ) == null ) {
                    triggers.get( Position.TOP ).setView( child );
                } else {
                    throw new IllegalStateException( "Top child only 1" );
                }

                child.layout( childLeft, -childHeight, childRight, 0 );
            } else if ( lp.position == Position.LEFT ) { // left

                if ( triggers.get( Position.LEFT ).getView( ) == null ) {
                    triggers.get( Position.LEFT ).setView( child );
                } else {
                    throw new IllegalStateException( "LEFT child only 1" );
                }

                child.layout( -child.getMeasuredWidth( ), childTop, 0, childBottom );
            } else if ( lp.position == Position.RIGHT ) { // right

                if ( triggers.get( Position.RIGHT ).getView( ) == null ) {
                    triggers.get( Position.RIGHT ).setView( child );
                } else {
                    throw new IllegalStateException( "RIGHT child only 1" );
                }

                child.layout( 0, childTop, getMeasuredWidth( ) + child.getMeasuredWidth( ), childBottom );
            } else if ( lp.position == Position.BOTTOM ) { // bottom

                if ( triggers.get( Position.BOTTOM ).getView( ) == null ) {
                    triggers.get( Position.BOTTOM ).setView( child );
                } else {
                    throw new IllegalStateException( "BOTTOM child only 1" );
                }

                child.layout( childLeft, getMeasuredHeight( ), childRight, getMeasuredHeight( ) + childHeight );
            } else {
                child.layout( childLeft, childTop, childRight, childBottom );
            }

        }
    }

    @Override
    public boolean onInterceptTouchEvent( MotionEvent ev ) {
        if ( !isGettingTriggerLocation ) {
            getLocationOnScreen( thisLocations );
            isGettingTriggerLocation = true;
        }

        beforeTouchPositionX = ( ev.getRawX( ) - thisLocations[ 0 ] );
        beforeTouchPositionY = ( ev.getRawY( ) - thisLocations[ 1 ] );

        inSideTouchPositionX = ( ev.getRawX( ) - thisLocations[ 0 ] );
        inSideTouchPositionY = ( ev.getRawY( ) - thisLocations[ 1 ] );

        if ( bypassAreaViews != null ) {
            for ( int i = 0; i < bypassAreaViews.size( ); i++ ) {
                int[] cood = new int[]{ -1, -1 };
                bypassAreaViews.get( i ).getLocationOnScreen( cood );
                if ( cood[ 0 ] != -1 && cood[ 1 ] != -1 ) {
                    if ( ( cood[ 0 ] < beforeTouchPositionX && beforeTouchPositionX < cood[ 0 ] + bypassAreaViews.get( i ).getWidth( ) )
                            && ( cood[ 1 ] < beforeTouchPositionY && beforeTouchPositionY < cood[ 1 ] + bypassAreaViews.get( i ).getHeight( ) ) ) {
                        return false;
                    }
                }
            }
        }

        if ( isOpen ) {

            if ( currentMovingPosition > -1 ) {

                if ( currentMovingPosition == Position.TOP ) {
                    return triggers.get( currentMovingPosition ).view.getHeight( ) - Utils.dpToPx( getContext( ), 48 ) < ( ev.getRawY( ) - thisLocations[ 1 ] );
                } else if ( currentMovingPosition == Position.BOTTOM ) {
                    return getHeight( ) - triggers.get( currentMovingPosition ).view.getHeight( ) + Utils.dpToPx( getContext( ), 48 ) > ( ev.getRawY( ) - thisLocations[ 1 ] );
                } else {
                    return false;
                }

            } else {
                return false;
            }

        } else {

            for ( int i = 0; i < triggers.size( ); i++ ) {
                onTrigger = isOnTriggerView( i, ev.getRawX( ) - thisLocations[ 0 ], ev.getRawY( ) - thisLocations[ 1 ] );
                if ( onTrigger ) {
                    currentMovingPosition = i;
                    break;
                }
            }

            if ( onTrigger ) isGoToOutside = false;
            return onTrigger;

        }
    }

    public void setCurrentMovingPosition( int currentMovingPosition ) {
        this.currentMovingPosition = currentMovingPosition;
    }

    @SuppressLint( "ClickableViewAccessibility" )
    @Override
    public boolean onTouchEvent( MotionEvent event ) {
        if ( currentMovingPosition == -1 ) return false;
        View getCurrentPositionView = triggers.get( currentMovingPosition ).view;

        if ( !isOpen ) {

            if ( currentMovingPosition > -1 ) {

                if ( event.getAction( ) == MotionEvent.ACTION_MOVE ) {

                    if ( !isGoToOutside && !isOnTriggerView( currentMovingPosition, event.getRawX( ) - thisLocations[ 0 ], event.getRawY( ) - thisLocations[ 1 ] ) ) {
                        isGoToOutside = true;
                    }

                    if ( currentMovingPosition == Position.TOP ) {

                        float moveY = -getCurrentPositionView.getHeight( ) + ( event.getRawY( ) - thisLocations[ 1 ] ) + triggers.get( Position.TOP ).getOffset( ) + ( triggers.get( currentMovingPosition ).isSticky( ) ? 0 : -beforeTouchPositionY );
                        if ( 0 > moveY ) {
                            getCurrentPositionView.setY( moveY );

                            {
                                onSlide(
                                        Position.TOP,
                                        ( moveY + getCurrentPositionView.getHeight( ) ) / ( getCurrentPositionView.getHeight( ) + triggers.get( Position.TOP ).getOffset( ) ),
                                        0,
                                        moveY );
                            }
                        }

                    } else if ( currentMovingPosition == Position.BOTTOM ) {

                        float moveY = ( event.getRawY( ) - thisLocations[ 1 ] ) + triggers.get( Position.BOTTOM ).getOffset( ) + ( triggers.get( Position.BOTTOM ).isSticky( ) ? 0 : ( getHeight( ) - beforeTouchPositionY ) );
                        if ( getHeight( ) - getCurrentPositionView.getHeight( ) < moveY ) {


                            if ( !inSideSlide ) {
                                inSideSlideCheck( event.getRawY( ) - thisLocations[ 1 ] );
                            }
                            getCurrentPositionView.setY( moveY );
                            {
                                onSlide( Position.BOTTOM,
                                        1.0f - ( moveY - ( getHeight( ) - getCurrentPositionView.getHeight( ) ) ) / ( ( float ) getCurrentPositionView.getHeight( ) + triggers.get( Position.BOTTOM ).getOffset( ) ),
                                        0,
                                        moveY );
                            }
                        }

                    }

                } else if ( event.getAction( ) == MotionEvent.ACTION_CANCEL || event.getAction( ) == MotionEvent.ACTION_UP ) {


                    if ( !isGoToOutside && isOnTriggerView( currentMovingPosition, event.getRawX( ) - thisLocations[ 0 ], event.getRawY( ) - thisLocations[ 1 ] ) ) {
                        slideAnimation( currentMovingPosition, false );
                        if ( triggers.get( currentMovingPosition ).getTrigger( ) != null ) {
                            triggers.get( currentMovingPosition ).getTrigger( ).performClick( );
                        }

                        if ( currentMovingPosition == Position.BOTTOM ) {
                            if ( !inSideSlide && isInSideTouchORClick( event.getRawX( ) - thisLocations[ 0 ], event.getRawY( ) - thisLocations[ 1 ] ) ) {
                                slideAnimation( currentMovingPosition, true );
                                onTrigger = false;
                            }
                            inSideSlide = false;
                        }


                    } else {
                        if ( currentMovingPosition == Position.TOP ) {
                            float moveY = ( event.getRawY( ) - thisLocations[ 1 ] ) + triggers.get( Position.TOP ).getOffset( ) + ( triggers.get( currentMovingPosition ).isSticky( ) ? 0 : -beforeTouchPositionY );
                            slideAnimation( Position.TOP, getCurrentPositionView.getHeight( ) * 2.0 / 5.0 < moveY );

                        } else if ( currentMovingPosition == Position.BOTTOM ) {
                            float moveY = ( event.getRawY( ) - thisLocations[ 1 ] ) + triggers.get( Position.BOTTOM ).getOffset( ) + ( triggers.get( Position.BOTTOM ).isSticky( ) ? 0 : ( getHeight( ) - beforeTouchPositionY ) );
                            slideAnimation( Position.BOTTOM, ( ( getHeight( ) - getCurrentPositionView.getHeight( ) * 2.0 / 5.0 ) > moveY ) );
                        }

                        onTrigger = false;

                    }

                }

            }
            return onTrigger;

        } else {

            if ( event.getAction( ) == MotionEvent.ACTION_MOVE ) {

                if ( currentMovingPosition == Position.TOP ) {

                    float moveY = -beforeTouchPositionY + ( event.getRawY( ) - thisLocations[ 1 ] );
                    if ( 0 >= moveY ) {
                        getCurrentPositionView.setY( moveY );
                        {
                            onSlide( Position.TOP,
                                    ( getCurrentPositionView.getHeight( ) + moveY ) / ( ( float ) getCurrentPositionView.getHeight( ) + triggers.get( Position.TOP ).getOffset( ) ),
                                    0,
                                    moveY );
                        }
                    }


                } else if ( currentMovingPosition == Position.BOTTOM ) {

                    float moveY = ( getHeight( ) - getCurrentPositionView.getHeight( ) ) + ( event.getRawY( ) - thisLocations[ 1 ] ) - beforeTouchPositionY;

                    if ( getHeight( ) - getCurrentPositionView.getHeight( ) <= moveY ) {
                        getCurrentPositionView.setY( moveY );
                        {
                            onSlide( Position.BOTTOM,
                                    1.0f - ( ( moveY - ( getHeight( ) - getCurrentPositionView.getHeight( ) ) ) / ( ( float ) getCurrentPositionView.getHeight( ) + triggers.get( Position.BOTTOM ).getOffset( ) ) ),
                                    0,
                                    moveY );
                        }
                    }

                }


            } else if ( event.getAction( ) == MotionEvent.ACTION_CANCEL || event.getAction( ) == MotionEvent.ACTION_UP ) {

                if ( currentMovingPosition == Position.TOP ) {

                    float movePoint = ( event.getRawY( ) - thisLocations[ 1 ] );
                    if ( getCurrentPositionView.getHeight( ) - Utils.dpToPx( getContext( ), 48 ) < movePoint && movePoint < getCurrentPositionView.getHeight( ) ) { //Close Area
                        slideAnimation( Position.TOP, false );
                    } else {
                        slideAnimation( Position.TOP, getCurrentPositionView.getHeight( ) * 4.0 / 5.0 < getCurrentPositionView.getHeight( ) + ( -beforeTouchPositionY + ( event.getRawY( ) - thisLocations[ 1 ] ) ) );
                    }


                } else if ( currentMovingPosition == Position.BOTTOM ) {

                    float movePoint = ( event.getRawY( ) - thisLocations[ 1 ] );
                    if ( getHeight( ) - getCurrentPositionView.getHeight( ) < movePoint && movePoint < getHeight( ) - getCurrentPositionView.getHeight( ) + Utils.dpToPx( getContext( ), 48 ) ) {
                        slideAnimation( Position.BOTTOM, false );
                    } else {
                        slideAnimation( Position.BOTTOM, getHeight( ) - getCurrentPositionView.getHeight( ) * 4.0 / 5.0 > ( event.getRawY( ) - thisLocations[ 1 ] ) + ( getHeight( ) - beforeTouchPositionY - getCurrentPositionView.getHeight( ) ) );
                    }
                }

            }

            return isOpen;
        }
    }


    public Trigger getTrigger( int position ) {
        if ( triggers.get( position ) != null ) {
            return triggers.get( position );
        } else {
            return null;
        }
    }

    public void setTrigger( int position, View trigger ) {
        this.setTrigger( position, trigger, true );
    }

    public void setTrigger( int position, View trigger, boolean sticky ) {
        if ( triggers.get( position ) != null ) {
            triggers.get( position ).setTrigger( trigger, sticky );
        }
    }

    protected boolean isOnTriggerView( int position, float x, float y ) {
        if ( triggers.get( position ) == null || triggers.get( position ).getTrigger( ) == null )
            return false;
        int triggerX = triggers.get( position ).getTriggerLocations( )[ 0 ];
        int triggerY = triggers.get( position ).getTriggerLocations( )[ 1 ];
        return
                ( triggerX <= x && x <= triggerX + triggers.get( position ).getTrigger( ).getWidth( ) )
                        &&
                        ( triggerY <= y && y <= triggerY + triggers.get( position ).getTrigger( ).getHeight( ) );
    }

    protected boolean isInSideTouchORClick( float x, float y ) {
        float clickDp = Utils.dpToPx( getContext( ), 16 );

        if ( doNotEventViews != null ) {
            if ( doNotEventViews.get( 0 ).getY( ) > 0 ) {
                return false;
            } else {
                return x + clickDp > inSideTouchPositionX && y + clickDp > inSideTouchPositionY;
            }
        } else {
            return x + clickDp > inSideTouchPositionX && y + clickDp > inSideTouchPositionY;
        }

//        if ( x + clickDp > inSideTouchPositionX && y + clickDp > inSideTouchPositionY ) {
//            return true; //클릭으로판단
//        } else {
//            return false; //터치로판단
//        }
    }

    protected boolean inSideSlideCheck( float y ) {
        float clickDp = Utils.dpToPx( getContext( ), 16 );
        if ( y + clickDp < inSideTouchPositionY ) {
            inSideSlide = true;
            return true;
        } else {
            return false;
        }
    }


    public void close( ) {
        if ( currentMovingPosition >= 0 )
            this.close( currentMovingPosition );
    }


    public void close( int position ) {
        if ( isOpen ) {
            slideAnimation( position, false );
            onTrigger = false;
            isOpen = false;
        }
    }

    public void open( int position ) {
        if ( !isOpen ) {
            setCurrentMovingPosition( position );
            slideAnimation( position, true );
            onTrigger = false;
            isOpen = true;

        }
    }

    public boolean isOpen( ) {
        return isOpen;
    }


    public void addOnSlideDrawerListener( OnSlideDrawerListener onSlideDrawerListener ) {
        if ( onSlideDrawerListeners == null ) onSlideDrawerListeners = new ArrayList<>( );
        onSlideDrawerListeners.add( onSlideDrawerListener );
    }


    private void slideAnimation( final int position, boolean open ) {
        View target = triggers.get( position ).getView( );
        isOpen = open;

        if ( target != null ) {
            ObjectAnimator animator = null;
            if ( position == Position.TOP ) {
                animator = ObjectAnimator.ofFloat( target, "y", open ? 0 : -target.getHeight( ) ).setDuration( 200 );
            } else if ( position == Position.LEFT ) {
                animator = ObjectAnimator.ofFloat( target, "x", open ? 0 : -target.getWidth( ) ).setDuration( 200 );
            } else if ( position == Position.RIGHT ) {
                animator = ObjectAnimator.ofFloat( target, "x", open ? getWidth( ) - target.getHeight( ) : getWidth( ) ).setDuration( 200 );
            } else if ( position == Position.BOTTOM ) {
                animator = ObjectAnimator.ofFloat( target, "y", open ? getHeight( ) - target.getHeight( ) : getHeight( ) + triggers.get( position ).getOffset( ) ).setDuration( 200 );
//                animator = ObjectAnimator.ofFloat( target, "y", open ? getHeight( ) - target.getHeight( ) : getHeight( ) ).setDuration( 200 );
            }

            if ( animator != null ) {
                animator.addUpdateListener( new ValueAnimator.AnimatorUpdateListener( ) {
                    @Override
                    public void onAnimationUpdate( ValueAnimator animation ) {
                        float value = ( Float ) animation.getAnimatedValue( );
                        {
                            if ( position == Position.TOP ) {
                                onSlide( Position.TOP,
                                        1.0f + ( value / triggers.get( Position.TOP ).getView( ).getHeight( ) ),
                                        0,
                                        value );
                            } else if ( position == Position.BOTTOM ) {

                                onSlide( Position.BOTTOM,
                                        1.0f - ( ( value - ( getHeight( ) - triggers.get( Position.BOTTOM ).getView( ).getHeight( ) ) ) / ( triggers.get( Position.BOTTOM ).getView( ).getHeight( ) + triggers.get( Position.BOTTOM ).getOffset( ) ) ),
                                        0,
                                        value );
                            }
                        }
                    }
                } );
                animator.start( );
            }
        }
    }

    private void onSlide( int position, float percent, float x, float y ) {
        if ( onSlideDrawerListeners != null ) {
            for ( OnSlideDrawerListener listener : onSlideDrawerListeners ) {
                listener.onSlide( position, percent, x, y );
            }
        }
    }

    public interface OnSlideDrawerListener {
        void onSlide( int position, float percent, float x, float y );
    }

    public class Trigger {

        int position;
        View trigger;
        View view;
        int[] triggerLocations = new int[ 2 ];

        float offset = 0.0f;

        boolean sticky = true;

        public Trigger( int position, View view ) {
            this.position = position;
            this.view = view;
        }

        public View getTrigger( ) {
            return trigger;
        }

        public void setTrigger( View trigger ) {
            this.trigger = trigger;
            getTriggerLocations( );
        }

        public void setTrigger( View trigger, boolean sticky ) {
            this.trigger = trigger;
            this.sticky = sticky;
            getTriggerLocations( );
        }

        public View getView( ) {
            return view;
        }

        public void setView( View view ) {
            this.view = view;
        }

        public boolean isSticky( ) {
            return sticky;
        }

        public float getOffset( ) {
            return offset;
        }

        public void setOffset( float offset ) {
            this.offset = offset;
        }

        public void setSticky( boolean sticky ) {
            this.sticky = sticky;
        }

        public int[] getTriggerLocations( ) {
            if ( trigger != null ) {
                if ( triggerLocations[ 0 ] == 0 && triggerLocations[ 1 ] == 0 )
                    trigger.getLocationOnScreen( triggerLocations );
            }
            return triggerLocations;
        }

    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams( ViewGroup.LayoutParams p ) {
        return new LayoutParams( p );
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams( ) {
        return new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT );
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams( AttributeSet attrs ) {
        return new SlideDrawerLayout.LayoutParams( getContext( ), attrs );
    }

    @Override
    protected boolean checkLayoutParams( ViewGroup.LayoutParams p ) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends MarginLayoutParams {

        public int position;

        public LayoutParams( Context c, AttributeSet attrs ) {
            super( c, attrs );
            TypedArray a = c.obtainStyledAttributes( attrs, R.styleable.SlideDrawerLayout );
            position = a.getInt( R.styleable.SlideDrawerLayout_position, -1 );
            a.recycle( );

        }

        public LayoutParams( int width, int height ) {
            super( width, height );
        }

        public LayoutParams( ViewGroup.LayoutParams source ) {
            super( source );
        }
    }

    public void addBypassArea( View view ) {
        if ( bypassAreaViews == null ) bypassAreaViews = new ArrayList<>( );
        bypassAreaViews.add( view );
    }

    public void addNotEventArea( View view ) {
        if ( doNotEventViews == null ) doNotEventViews = new ArrayList<>( );
        doNotEventViews.add( view );
    }

}
