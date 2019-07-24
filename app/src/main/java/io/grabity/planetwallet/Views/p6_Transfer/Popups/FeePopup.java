package io.grabity.planetwallet.Views.p6_Transfer.Popups;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;

import io.grabity.planetwallet.Common.components.AbsPopupView.AbsSlideUpView;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.RoundRelativeLayout;

public class FeePopup extends AbsSlideUpView implements View.OnTouchListener {

    private ViewMapper viewMapper;
    private ArrayList< FontTextView > feeButtons;
    private ArrayList< String > price;
    private ArrayList< String > limit;
    private StringBuffer priceBuffer;
    private StringBuffer limitBuffer;

    private OnFeePopupSaveClickListener onFeePopupSaveClickListener;

    private float contentHeight = -1.0f;
    private float defaultY = -1.0f;
    private boolean isMove = false;

    private boolean isAbleMoving = false;

    private String fee;
    private String coinType;

    private float defaultFeePopupTop = -1.0f;
    private int statusBar = -1;


    public FeePopup( Context context ) {
        super( context );
    }

    public static FeePopup newInstance( Context context ) {
        FeePopup feePopup = new FeePopup( context );
        return feePopup;
    }

    @Override
    protected View contentView( ) {
        return View.inflate( getContext( ), R.layout.popup_fee, null );
    }

    @Override
    public void onCreateView( ) {

        contentHeight = Utils.dpToPx( getContext( ), 580 );
        statusBar = Utils.getDeviceStatusBarHeight( getActivity( ) );

        getBackground( ).setBackgroundColor( Color.TRANSPARENT );
        getContentView( ).setOnTouchListener( this );


        viewMapper = new ViewMapper( );


        viewMapper.groupGasPrice.setOnClickListener( this );
        viewMapper.groupGasLimit.setOnClickListener( this );
        viewMapper.btnCancel.setOnClickListener( this );
        viewMapper.btnSave.setOnClickListener( this );
        viewMapper.btnFeeDelete.setOnClickListener( this );

        viewMapper.btnHeader.setOnTouchListener( this );

        viewMapper.groupGasPrice.setFocusableInTouchMode( true );
        viewMapper.groupGasPrice.requestFocus( );

        feeButtons = Utils.getAllViewsFromParentView( viewMapper.groupInputFee, FontTextView.class );

        setData( );

    }

    @Override
    public void setData( ) {
        super.setData( );

        for ( int i = 0; i < feeButtons.size( ); i++ ) {
            feeButtons.get( i ).setOnClickListener( this );
        }
        price = new ArrayList<>( );
        limit = new ArrayList<>( );
        priceBuffer = new StringBuffer( );
        limitBuffer = new StringBuffer( );

        setList( price, viewMapper.textGasPrice );
        setList( limit, viewMapper.textGasLimit );

        viewMapper.textGasFee.setText( getFee( ) );
        viewMapper.textCoinType.setText( getCoinType( ) );

    }


    @Override
    public boolean onTouch( View v, MotionEvent event ) {
        if ( !isMove ) {
            if ( event.getAction( ) == MotionEvent.ACTION_DOWN ) {
                defaultY = event.getRawY( );
                defaultFeePopupTop = viewMapper.groupFeePopup.getTop( );
                if ( defaultY - statusBar >= defaultFeePopupTop + Utils.dpToPx( getContext( ), 52 ) ) {
                    isAbleMoving = false;
                } else {
                    isAbleMoving = true;
                }

            } else if ( event.getAction( ) == MotionEvent.ACTION_MOVE && isAbleMoving ) {
                if ( ( event.getRawY( ) - defaultY ) >= 0 ) {
                    viewMapper.groupFeePopup.setTop( ( int ) ( event.getRawY( ) + defaultFeePopupTop - defaultY ) );
                }
            } else if ( ( event.getAction( ) == MotionEvent.ACTION_UP || event.getAction( ) == MotionEvent.ACTION_CANCEL ) && isAbleMoving ) {
                if ( v == viewMapper.btnHeader && viewMapper.groupFeePopup.getTop( ) == defaultFeePopupTop ) {
                    getActivity( ).onBackPressed( );
                } else if ( contentHeight * 1 / 4 < ( event.getRawY( ) - defaultY ) ) {
                    getActivity( ).onBackPressed( );
                } else {
                    ObjectAnimator animator = ObjectAnimator.ofInt( viewMapper.groupFeePopup, "top", ( int ) defaultFeePopupTop );
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

    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.groupGasPrice ) {
            setFocusViewSetting( viewMapper.groupGasPrice, viewMapper.textGasPrice, viewMapper.groupGasLimit, viewMapper.textGasLimit );
            setList( price, viewMapper.textGasPrice );
        } else if ( v == viewMapper.groupGasLimit ) {
            setFocusViewSetting( viewMapper.groupGasLimit, viewMapper.textGasLimit, viewMapper.groupGasPrice, viewMapper.textGasPrice );
            setList( limit, viewMapper.textGasLimit );
        } else if ( v == viewMapper.btnFeeDelete ) {
            if ( getActivity( ).getCurrentFocus( ) == viewMapper.groupGasPrice ) {
                if ( price.size( ) > 0 ) {
                    price.remove( price.size( ) - 1 );
                    setPriceORLimit( priceBuffer, price, viewMapper.textGasPrice );
                }
            } else if ( getActivity( ).getCurrentFocus( ) == viewMapper.groupGasLimit ) {
                if ( limit.size( ) > 0 ) {
                    limit.remove( limit.size( ) - 1 );
                    setPriceORLimit( limitBuffer, limit, viewMapper.textGasLimit );
                }
            }
        } else if ( v == viewMapper.btnCancel ) {
            getActivity( ).onBackPressed( );
        } else if ( v == viewMapper.btnSave ) {
            //gas limit, price check
            if ( viewMapper.textGasLimit.getText( ).length( ) == 0 || viewMapper.textGasPrice.getText( ).length( ) == 0 ) {
                Toast.makeText( getActivity( ), localized( R.string.fee_popup_not_spaces_title ), Toast.LENGTH_SHORT ).show( );

                //QA 이후 주석해제 -> 리밋이나 프라이스가 빈 경우 자동으로 기본값세팅
                if ( viewMapper.textGasPrice.getText( ).length( ) == 0 ) {
                    viewMapper.textGasPrice.setText( "1" );
                    setList( price, viewMapper.textGasPrice );
                    setPriceORLimit( priceBuffer, price, viewMapper.textGasPrice );
                }
                if ( viewMapper.textGasLimit.getText( ).length( ) == 0 ) {
                    viewMapper.textGasLimit.setText( "21000" );
                    setList( limit, viewMapper.textGasLimit );
                    setPriceORLimit( limitBuffer, limit, viewMapper.textGasLimit );
                }

                if ( getActivity( ).getCurrentFocus( ) == viewMapper.groupGasPrice ) {
                    setFocusViewSetting( viewMapper.groupGasPrice, viewMapper.textGasPrice, viewMapper.groupGasLimit, viewMapper.textGasLimit );
                } else if ( getActivity( ).getCurrentFocus( ) == viewMapper.groupGasLimit ) {
                    setFocusViewSetting( viewMapper.groupGasLimit, viewMapper.textGasLimit, viewMapper.groupGasPrice, viewMapper.textGasPrice );
                }


            } else {
                if ( Integer.valueOf( viewMapper.textGasLimit.getText( ).toString( ) ) < 21000 ) {
                    Toast.makeText( getActivity( ), localized( R.string.fee_popup_gas_limit_least_title ), Toast.LENGTH_SHORT ).show( );
                    viewMapper.textGasLimit.setText( "21000" );
                    setList( limit, viewMapper.textGasLimit );
                    setPriceORLimit( limitBuffer, limit, viewMapper.textGasLimit );
                    return;
                } else if ( Integer.valueOf( viewMapper.textGasPrice.getText( ).toString( ) ) < 1 ) {
                    Toast.makeText( getActivity( ), localized( R.string.fee_popup_gas_price_least_title ), Toast.LENGTH_SHORT ).show( );
                    viewMapper.textGasPrice.setText( "1" );
                    setList( price, viewMapper.textGasPrice );
                    setPriceORLimit( priceBuffer, price, viewMapper.textGasPrice );
                    return;
                }
                if ( onFeePopupSaveClickListener != null ) {
                    onFeePopupSaveClickListener.onFeePopupSaveClick( viewMapper.textGasFee.getText( ).toString( ) );
                }
            }

        } else {
            if ( v instanceof FontTextView ) {
                if ( getActivity( ).getCurrentFocus( ) == viewMapper.groupGasPrice ) {

                    //Todo 자릿수 제한 2
                    if ( price.size( ) >= 2 ) return;
                    price.add( ( ( FontTextView ) v ).getText( ).toString( ) );
                    setPriceORLimit( priceBuffer, price, viewMapper.textGasPrice );
                } else if ( getActivity( ).getCurrentFocus( ) == viewMapper.groupGasLimit ) {
                    //Todo limit 자릿수 제한 6
                    if ( limit.size( ) >= 6 ) return;
                    limit.add( ( ( FontTextView ) v ).getText( ).toString( ) );
                    setPriceORLimit( limitBuffer, limit, viewMapper.textGasLimit );
                }
            }
        }

    }

    /**
     * @param gv  포커스그룹뷰
     * @param t   포커스텍스트뷰
     * @param gv1 포커스해제그룹뷰
     * @param t1  포커스해제텍스트뷰
     */
    private void setFocusViewSetting( RoundRelativeLayout gv, TextView t, RoundRelativeLayout gv1, TextView t1 ) {
        gv.setBorder_color_normal( Color.parseColor( "#000000" ) );
        gv1.setBorder_color_normal( Color.parseColor( "#EDEDED" ) );

        t.setTextColor( Color.parseColor( "#000000" ) );
        t1.setTextColor( Color.parseColor( "#aaaaaa" ) );

        gv.setFocusableInTouchMode( true );
        gv.requestFocus( );
        gv1.setFocusableInTouchMode( false );
    }

    private void setList( ArrayList< String > list, TextView v ) {
        list.clear( );
        for ( int i = 0; i < v.getText( ).length( ); i++ ) {
            list.add( String.valueOf( v.getText( ).charAt( i ) ) );
        }
    }

    private void setPriceORLimit( StringBuffer b, ArrayList< String > list, TextView v ) {
        b.setLength( 0 );
        for ( int i = 0; i < list.size( ); i++ ) {
            b.append( list.get( i ) );
        }
        v.setText( b.toString( ) );


        feeCalculation( );
    }

    private void feeCalculation( ) {
        if ( viewMapper.textGasLimit.getText( ).length( ) != 0 && viewMapper.textGasPrice.getText( ).length( ) != 0 ) {
            BigDecimal price = new BigDecimal( viewMapper.textGasPrice.getText( ).toString( ) ).movePointLeft( 9 );
            BigDecimal limit = new BigDecimal( viewMapper.textGasLimit.getText( ).toString( ) );
            viewMapper.textGasFee.setText( String.valueOf( limit.multiply( price ).stripTrailingZeros( ) ) );
        } else {
            viewMapper.textGasFee.setText( "0.0" );
        }
    }

    public FeePopup setOnFeePopupSaveClickListener( OnFeePopupSaveClickListener onFeePopupSaveClickListener ) {
        this.onFeePopupSaveClickListener = onFeePopupSaveClickListener;
        return this;
    }

    public FeePopup setFee( String fee, String coinType ) {
        this.fee = fee;
        this.coinType = coinType;
        return this;
    }

    public interface OnFeePopupSaveClickListener {
        void onFeePopupSaveClick( String fee );
    }

    public String getFee( ) {
        if ( fee == null ) return "0";
        return fee;
    }

    public void setFee( String fee ) {
        this.fee = fee;
    }

    public String getCoinType( ) {
        if ( coinType == null ) return "ETH";
        return coinType;
    }

    public void setCoinType( String coinType ) {
        this.coinType = coinType;
    }

    class ViewMapper {


        RoundRelativeLayout groupGasPrice;
        RoundRelativeLayout groupGasLimit;

        ViewGroup groupInputFee;
        ViewGroup groupFeePopup;

        TextView textGasPrice;
        TextView textGasLimit;
        TextView textGasFee;
        TextView textCoinType;

        View btnCancel;
        View btnSave;
        View btnFeeDelete;
        View btnHeader;

        public ViewMapper( ) {

            groupFeePopup = findViewById( R.id.group_popup_fee_bottom );
            groupGasPrice = findViewById( R.id.group_popup_fee_bottom_gas_price );
            groupGasLimit = findViewById( R.id.group_popup_fee_bottom_gas_limit );
            groupInputFee = findViewById( R.id.group_popup_fee_bottom_input_fee );

            textGasPrice = findViewById( R.id.text_popup_fee_bottom_gas_price );
            textGasLimit = findViewById( R.id.text_popup_fee_bottom_gas_limit );
            textGasFee = findViewById( R.id.text_poup_fee_bottom_fee );
            textCoinType = findViewById( R.id.text_popup_fee_bottom_coin_type );

            btnCancel = findViewById( R.id.btn_popup_fee_bottom_cancel );
            btnSave = findViewById( R.id.btn_popup_fee_bottom_save );
            btnFeeDelete = findViewById( R.id.group_popup_fee_bottom_delete );
            btnHeader = findViewById( R.id.btn_popup_fee_header );
        }
    }
}
