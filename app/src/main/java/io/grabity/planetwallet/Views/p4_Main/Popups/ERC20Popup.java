package io.grabity.planetwallet.Views.p4_Main.Popups;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import io.grabity.planetwallet.Common.components.AbsPopupView.AbsSlideUpView;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Widgets.BarcodeView;

public class ERC20Popup extends AbsSlideUpView implements View.OnTouchListener {

    public static ERC20Popup instance = null;

    private ViewMapper viewMapper;

    private Planet planet;
    private ERC20 erc20;

    public static final int COPY = 0;
    public static final int TRANSFER = 1;

    private OnERC20PopupClickListener onERC20PopupClickListener;

    private float contentHeight = -1.0f;
    private float defaultTop = -1.0f;
    private float defaultY = -1.0f;
    private boolean isMove = false;



    public static ERC20Popup newInstance( Context context, Planet planet, ERC20 erc20 ) {
        return instance = new ERC20Popup( context, planet, erc20 );
//        return new ERC20Popup( context, planet, erc20 );
    }

    public static ERC20Popup newInstance( Context context ) {
        return instance = new ERC20Popup( context,null,null );
//        return new ERC20Popup( context, null, null );
    }

    public static ERC20Popup getInstance( ) {
        return instance;
    }

    public static void setInstance( ERC20Popup instance ) {
        ERC20Popup.instance = instance;
    }

    public ERC20Popup( Context context, Planet planet, ERC20 erc20 ) {
        super( context );
        this.planet = planet;
        this.erc20 = erc20;


    }

    @Override
    protected View contentView( ) {
        return View.inflate( getContext( ), R.layout.popup_erc20, null );
    }


    @Override
    public void onCreateView( ) {

        contentHeight = Utils.dpToPx( getContext( ), 580 );
        defaultTop = getScreenHeight( getContext( ) ) - contentHeight;

        getBackground( ).setBackgroundColor( Color.TRANSPARENT );
        getContentView( ).setOnTouchListener( this );

        viewMapper = new ViewMapper( );

        viewMapper.textERC20Name.setText( erc20.getName( ) );
        viewMapper.textPlanetName.setText( planet.getName( ) );
        viewMapper.textAddress.setText( planet.getAddress( ) );
        viewMapper.barcodeView.setData( planet.getAddress( ) );

        viewMapper.btnCopy.setOnClickListener( this );
        viewMapper.btnTransfer.setOnClickListener( this );
        viewMapper.btnClose.setOnClickListener( this );

    }

    @Override
    public void onClick( View v ) {
        if ( v == viewMapper.btnCopy ) {
            if ( onERC20PopupClickListener != null ) {
                onERC20PopupClickListener.onERC20PopupClick( planet, erc20, COPY );
            }
        } else if ( v == viewMapper.btnTransfer ) {
            if ( onERC20PopupClickListener != null ) {
                onERC20PopupClickListener.onERC20PopupClick( planet, erc20, TRANSFER );
            }
        } else if ( v == viewMapper.btnClose ) {
            getActivity( ).onBackPressed( );
        }
    }

    @Override
    public boolean onTouch( View v, MotionEvent event ) {
        if ( !isMove ) {
            if ( event.getAction( ) == MotionEvent.ACTION_DOWN ) {

                defaultY = event.getRawY( );

            } else if ( event.getAction( ) == MotionEvent.ACTION_MOVE ) {
                if ( ( event.getRawY( ) - defaultY ) >= 0 ) {
                    viewMapper.groupPopup.setTop( ( int ) ( event.getRawY( ) + defaultTop - defaultY ) );
                }
            } else if ( event.getAction( ) == MotionEvent.ACTION_UP || event.getAction( ) == MotionEvent.ACTION_CANCEL ) {
                if ( contentHeight * 1 / 4 < ( event.getRawY( ) - defaultY ) ) {
                    getActivity( ).onBackPressed( );
                } else {
                    ObjectAnimator animator = ObjectAnimator.ofInt( viewMapper.groupPopup, "top", ( int ) defaultTop );
                    animator.setDuration( 200 );
                    animator.addListener( new Animator.AnimatorListener( ) {
                        @Override
                        public void onAnimationStart( Animator animation ) {
                            isMove = true;
                        }

                        @Override
                        public void onAnimationEnd( Animator animation ) {
                            isMove = false;
                        }

                        @Override
                        public void onAnimationCancel( Animator animation ) {
                            isMove = false;
                        }

                        @Override
                        public void onAnimationRepeat( Animator animation ) {

                        }
                    } );
                    animator.start( );

                }
            }

        }
        return true;
    }

    @Override
    public void onBackPressed( ) {
        super.onBackPressed( );
        instance = null;
    }

    class ViewMapper {

        View groupPopup;

        BarcodeView barcodeView;

        TextView textERC20Name;
        TextView textPlanetName;
        TextView textAddress;

        View btnCopy;
        View btnTransfer;
        View btnClose;


        public ViewMapper( ) {

            groupPopup = findViewById( R.id.group_popup_erc20 );

            barcodeView = findViewById( R.id.barcode_popup_erc20 );

            textERC20Name = findViewById( R.id.text_popup_erc20_name );
            textPlanetName = findViewById( R.id.text_popup_erc20_planet_name );
            textAddress = findViewById( R.id.text_popup_erc20_planet_address );

            btnCopy = findViewById( R.id.btn_popup_erc20_copy );
            btnTransfer = findViewById( R.id.btn_popup_erc20_transfer );
            btnClose = findViewById( R.id.btn_popup_erc20_close );


        }
    }

    public Planet getPlanet( ) {
        return planet;
    }

    public ERC20Popup setPlanet( Planet planet ) {
        this.planet = planet;
        return this;
    }

    public ERC20 getErc20( ) {
        return erc20;
    }

    public ERC20Popup setErc20( ERC20 erc20 ) {
        this.erc20 = erc20;
        return this;
    }

    public OnERC20PopupClickListener getOnERC20PopupClickListener( ) {
        return onERC20PopupClickListener;
    }

    public ERC20Popup setOnERC20PopupClickListener( OnERC20PopupClickListener onERC20PopupClickListener ) {
        this.onERC20PopupClickListener = onERC20PopupClickListener;
        return this;
    }

    public interface OnERC20PopupClickListener {
        void onERC20PopupClick( Planet planet, ERC20 erc20, int button );
    }

}
