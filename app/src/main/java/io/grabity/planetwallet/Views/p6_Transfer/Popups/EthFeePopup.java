package io.grabity.planetwallet.Views.p6_Transfer.Popups;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import io.grabity.planetwallet.Common.components.AbsPopupView.AbsSlideUpView;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.CustomToast;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.RoundButton.RoundButton;

public class EthFeePopup extends AbsSlideUpView implements View.OnTouchListener {

    private final static BigDecimal GWEI = new BigDecimal( "1000000000" );
    private final static String ETH_DEFAULT_GAS_LIMIT = "21000";
    private final static String ETH_DEFAULT_GAS_GWEI = "20";

    private final static String ERC20_DEFAULT_GAS_LIMIT = "100000";
    private final static String ERC20_DEFAULT_GAS_GWEI = "10";


    private float contentHeight = -1.0f;
    private float defaultY = -1.0f;
    private boolean isMove = false;
    private boolean isAbleMoving = false;

    private float defaultFeePopupTop = -1.0f;
    private int statusBar = -1;


    private ViewMapper viewMapper;
    private ArrayList< FontTextView > numberButtons;

    private StringBuilder stringBuilder = new StringBuilder( );

    private OnEthFeePopupListener onEthFeePopupListener;

    private CoinType coinType;
    private BigDecimal ethBalance;
    private BigDecimal ethAmount;

    public EthFeePopup( Context context ) {
        super( context );
    }

    public static EthFeePopup newInstance( Context context ) {
        EthFeePopup ethFeePopup = new EthFeePopup( context );
        return ethFeePopup;
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

        viewMapper.btnGasPrice.setOnClickListener( this );
        viewMapper.btnGasLimit.setOnClickListener( this );

        viewMapper.btnCancel.setOnClickListener( this );
        viewMapper.btnSave.setOnClickListener( this );

        numberButtons = Utils.getAllViewsFromParentView( viewMapper.groupInputFee, FontTextView.class );
        for ( int i = 0; i < numberButtons.size( ); i++ ) {
            numberButtons.get( i ).setOnClickListener( this );
        }
        viewMapper.btnDelete.setOnClickListener( this );
        viewMapper.btnHeader.setOnTouchListener( this );

        setData( );
    }


    @Override
    public void setData( ) {
        super.setData( );

        if ( coinType == CoinType.ETH ) {
            viewMapper.btnGasPrice.setText( ETH_DEFAULT_GAS_GWEI );
            viewMapper.btnGasLimit.setText( ETH_DEFAULT_GAS_LIMIT );
        } else if ( coinType == CoinType.ERC20 ) {
            viewMapper.btnGasPrice.setText( ERC20_DEFAULT_GAS_GWEI );
            viewMapper.btnGasLimit.setText( ERC20_DEFAULT_GAS_LIMIT );
        }


        focusBtn( true );
        setFeeText( );
    }

    @Override
    public void onBackPressed( ) {
        super.onBackPressed( );

    }


    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnGasPrice && currentFocus( ) == viewMapper.btnGasLimit ) {

            if ( tooLowGasLimit( ) ) {
                CustomToast.makeText( getActivity( ), "21000 이하로 안됨" ).show( );
            } else {
                focusBtn( true );
            }

        } else if ( v == viewMapper.btnGasLimit && currentFocus( ) == viewMapper.btnGasPrice ) {

            if ( isZeroGasPrice( ) ) {
                CustomToast.makeText( getActivity( ), "gas는 0 안됨" ).show( );
            } else {
                focusBtn( false );
            }

        } else if ( v == viewMapper.btnCancel ) {

            getActivity( ).onBackPressed( );

        } else if ( v == viewMapper.btnSave ) {

            if ( validValues( ) ) {
                Objects.requireNonNull( onEthFeePopupListener ).onFeePopupSaveClick( this, new BigDecimal( viewMapper.btnGasPrice.getText( ).toString( ) ).multiply( GWEI ).toPlainString( ), viewMapper.btnGasLimit.getText( ).toString( ) );
                getActivity( ).onBackPressed( );
            }

        } else if ( v == viewMapper.btnDelete ) {

            if ( stringBuilder.length( ) > 0 )
                stringBuilder.deleteCharAt( stringBuilder.length( ) - 1 );

            if ( stringBuilder.length( ) == 0 )
                stringBuilder.append( 0 );

            currentFocus( ).setText( stringBuilder );
            setFeeText( );

        } else if ( Objects.requireNonNull( numberButtons ).contains( v ) && Objects.requireNonNull( v ).getClass( ).equals( FontTextView.class ) ) {

            FontTextView numberButton = ( FontTextView ) v;
            if ( stringBuilder.length( ) == 1 && stringBuilder.charAt( 0 ) == '0' ) {
                stringBuilder.deleteCharAt( 0 );
            }
            stringBuilder.append( numberButton.getText( ).toString( ) );

            if ( tooLargeFee( ) ) {
                CustomToast.makeText( getActivity( ), "gas비가 너무 높아" ).show( );
                stringBuilder.deleteCharAt( stringBuilder.length( ) - 1 );
            } else {
                currentFocus( ).setText( stringBuilder );
                setFeeText( );
            }

        }
    }

    private boolean tooLargeFee( ) {

        String gasPrice = viewMapper.btnGasPrice.isSelected( ) ? stringBuilder.toString( ) : viewMapper.btnGasPrice.getText( ).toString( );
        String gasLimit = viewMapper.btnGasLimit.isSelected( ) ? stringBuilder.toString( ) : viewMapper.btnGasLimit.getText( ).toString( );

        BigDecimal fee = new BigDecimal( gasPrice ).multiply( new BigDecimal( gasLimit ) ).multiply( GWEI );

        return ethBalance.subtract( ethAmount ).subtract( fee ).signum( ) <= 0;
    }

    private boolean isZeroGasPrice( ) {
        return Utils.equals( viewMapper.btnGasPrice.getText( ).toString( ), "0" );
    }

    private boolean tooLowGasLimit( ) {
        return new BigDecimal( viewMapper.btnGasLimit.getText( ).toString( ) ).compareTo( new BigDecimal( "21000" ) ) < 0;
    }

    private boolean validValues( ) {
        return !( tooLargeFee( ) || tooLowGasLimit( ) || isZeroGasPrice( ) );
    }

    private void focusBtn( boolean isGasPrice ) {

        RoundButton focus = isGasPrice ? viewMapper.btnGasPrice : viewMapper.btnGasLimit;
        RoundButton blur = isGasPrice ? viewMapper.btnGasLimit : viewMapper.btnGasPrice;

        focus.setSelected( true );
        blur.setSelected( false );

        focus.setBorderColorNormal( Color.parseColor( "#000000" ) );
        focus.setTextColorNormal( Color.parseColor( "#000000" ) );
        focus.setBorderColorHighlight( Color.parseColor( "#000000" ) );
        focus.setTextColorHighlight( Color.parseColor( "#000000" ) );
        focus.setSuperTextColor( Color.parseColor( "#000000" ) );

        blur.setBorderColorNormal( Color.parseColor( "#EDEDED" ) );
        blur.setTextColorNormal( Color.parseColor( "#AAAAAA" ) );
        blur.setBorderColorHighlight( Color.parseColor( "#EDEDED" ) );
        blur.setTextColorHighlight( Color.parseColor( "#AAAAAA" ) );
        blur.setSuperTextColor( Color.parseColor( "#AAAAAA" ) );

        focus.invalidate( );
        blur.invalidate( );

        stringBuilder = new StringBuilder( focus.getText( ) );
    }

    private RoundButton currentFocus( ) {
        return viewMapper.btnGasPrice.isSelected( ) ? viewMapper.btnGasPrice : viewMapper.btnGasLimit;
    }

    private void setFeeText( ) {
        String fee = new BigDecimal( viewMapper.btnGasPrice.getText( ).toString( ) ).multiply( GWEI ).multiply( new BigDecimal( viewMapper.btnGasLimit.getText( ).toString( ) ) ).toPlainString( );
        viewMapper.textGasFee.setText( String.format( Locale.US, "%s %s", Utils.ofZeroClear( Utils.toMaxUnit( CoinType.ETH, fee ) ), CoinType.ETH.name( ) ) );
    }

    public EthFeePopup setEthBalance( String ethBalance ) {
        if ( ethBalance == null ) {
            this.ethBalance = new BigDecimal( 0 );
        } else {
            this.ethBalance = new BigDecimal( ethBalance );
        }
        return this;
    }

    public EthFeePopup setEthAmount( String ethAmount ) {
        if ( ethAmount == null ) {
            this.ethAmount = new BigDecimal( 0 );
        } else {
            this.ethAmount = new BigDecimal( ethAmount );
        }
        return this;
    }

    public EthFeePopup setCoinType( CoinType coinType ) {
        this.coinType = coinType;
        return this;
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

    public EthFeePopup setOnEthFeePopupListener( OnEthFeePopupListener onEthFeePopupListener ) {
        this.onEthFeePopupListener = onEthFeePopupListener;
        return this;
    }

    class ViewMapper {

        ViewGroup groupFeePopup;

        RoundButton btnGasPrice;
        RoundButton btnGasLimit;
        TextView textGasFee;

        View btnCancel;
        View btnSave;

        ViewGroup groupInputFee;
        View btnDelete;
        View btnHeader;

        public ViewMapper( ) {

            groupFeePopup = findViewById( R.id.group_popup_fee_bottom );

            btnGasPrice = findViewById( R.id.btn_popup_fee_gas_price );
            btnGasLimit = findViewById( R.id.btn_popup_fee_gas_limit );
            textGasFee = findViewById( R.id.text_poup_fee_estimate_fee );

            btnCancel = findViewById( R.id.btn_popup_fee_bottom_cancel );
            btnSave = findViewById( R.id.btn_popup_fee_bottom_save );

            groupInputFee = findViewById( R.id.group_popup_fee_bottom_input_fee );
            btnDelete = findViewById( R.id.group_popup_fee_bottom_delete );
            btnHeader = findViewById( R.id.btn_popup_fee_header );
        }
    }

    public interface OnEthFeePopupListener {
        void onFeePopupSaveClick( EthFeePopup ethFeePopup, String gasPriceWei, String gasLimit );
    }
}
