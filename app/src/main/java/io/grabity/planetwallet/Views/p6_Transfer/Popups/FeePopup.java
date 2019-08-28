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
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.Tx;
import io.grabity.planetwallet.Widgets.CustomToast;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.RoundButton.RoundButton;

public class FeePopup extends AbsSlideUpView implements View.OnTouchListener {

    private float contentHeight = -1.0f;
    private float defaultY = -1.0f;
    private boolean isMove = false;
    private boolean isAbleMoving = false;

    private float defaultFeePopupTop = -1.0f;
    private int statusBar = -1;


    private ViewMapper viewMapper;
    private ArrayList< FontTextView > numberButtons;

    private Planet planet;
    private MainItem mainItem;
    private Tx tx;

    private StringBuilder stringBuilder = new StringBuilder( );

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

    public FeePopup setPlanet( Planet planet ) {
        this.planet = planet;
        return this;
    }

    public FeePopup setMainItem( MainItem mainItem ) {
        this.mainItem = mainItem;
        return this;
    }

    public FeePopup setTx( Tx tx ) {
        this.tx = tx;
        return this;
    }

    @Override
    public void setData( ) {
        super.setData( );
        viewMapper.btnGasPrice.setText( "20" );
        viewMapper.btnGasLimit.setText( "21000" );

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

            if ( new BigDecimal( viewMapper.btnGasLimit.getText( ).toString( ) ).compareTo( new BigDecimal( "21000" ) ) < 0 ) {
                CustomToast.makeText( getActivity( ), "21000 이하로 안됨" ).show( );
            } else {
                focusBtn( true );
            }

        } else if ( v == viewMapper.btnGasLimit && currentFocus( ) == viewMapper.btnGasPrice ) {

            if ( Utils.equals( viewMapper.btnGasPrice.getText( ).toString( ), "0" ) ) {
                CustomToast.makeText( getActivity( ), "gas는 0 안됨" ).show( );
            } else {
                focusBtn( true );
            }

            focusBtn( false );

        } else if ( v == viewMapper.btnCancel ) {

            getActivity( ).onBackPressed( );

        } else if ( v == viewMapper.btnSave ) {

            if ( validValues( ) ) {
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

            String gasPrice = viewMapper.btnGasPrice.isSelected( ) ? stringBuilder.toString( ) : viewMapper.btnGasPrice.getText( ).toString( );
            String gasLimit = viewMapper.btnGasLimit.isSelected( ) ? stringBuilder.toString( ) : viewMapper.btnGasLimit.getText( ).toString( );

            BigDecimal amount = new BigDecimal( tx.getAmount( ) );
            BigDecimal balance = new BigDecimal( mainItem.getBalance( ) );
            BigDecimal fee = new BigDecimal( gasPrice ).multiply( new BigDecimal( "1000000000" ) ).multiply( new BigDecimal( gasLimit ) );

            if ( balance.subtract( amount.add( fee ) ).signum( ) <= 0 ) {
                CustomToast.makeText( getActivity( ), "gas비가 너무 높아" ).show( );
                stringBuilder.deleteCharAt( stringBuilder.length( ) - 1 );
            } else {
                currentFocus( ).setText( stringBuilder );
                setFeeText( );
            }

        }
    }

    private boolean validValues( ) {
        String gasPrice = viewMapper.btnGasPrice.isSelected( ) ? stringBuilder.toString( ) : viewMapper.btnGasPrice.getText( ).toString( );
        String gasLimit = viewMapper.btnGasLimit.isSelected( ) ? stringBuilder.toString( ) : viewMapper.btnGasLimit.getText( ).toString( );

        BigDecimal amount = new BigDecimal( tx.getAmount( ) );
        BigDecimal balance = new BigDecimal( mainItem.getBalance( ) );
        BigDecimal fee = new BigDecimal( gasPrice ).multiply( new BigDecimal( "1000000000" ) ).multiply( new BigDecimal( gasLimit ) );

        if ( new BigDecimal( gasLimit ).compareTo( new BigDecimal( "21000" ) ) < 0 ) {
            CustomToast.makeText( getActivity( ), "21000 이하로 안됨" ).show( );
            return false;
        }

        if ( currentFocus( ) == viewMapper.btnGasPrice && Utils.equals( gasPrice, "0" ) ) {
            CustomToast.makeText( getActivity( ), "gas는 0 안됨" ).show( );
            return false;
        }

        if ( balance.subtract( amount.add( fee ) ).signum( ) <= 0 ) {
            CustomToast.makeText( getActivity( ), "gas비가 너무 높아" ).show( );
            return false;
        }

        return true;
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
        String fee = new BigDecimal( viewMapper.btnGasPrice.getText( ).toString( ) ).multiply( new BigDecimal( "1000000000" ) ).multiply( new BigDecimal( viewMapper.btnGasLimit.getText( ).toString( ) ) ).toPlainString( );
        viewMapper.textGasFee.setText( String.format( Locale.US, "%s %s", Utils.ofZeroClear( Utils.toMaxUnit( CoinType.of( CoinType.of( mainItem.getCoinType( ) ).getParent( ) ), fee ) ), CoinType.of( mainItem.getCoinType( ) ).getParent( ) ) );
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
}
