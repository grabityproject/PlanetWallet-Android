package io.grabity.planetwallet.Widgets.ListPopupView;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;

import io.grabity.planetwallet.Common.components.AbsPopupView.AbsSlideUpView;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.RoundRelativeLayout;

public class FeePopup extends AbsSlideUpView {

    private ViewMapper viewMapper;
    private ArrayList< FontTextView > feeButtons;
    private ArrayList< String > price;
    private ArrayList< String > limit;
    private StringBuffer priceBuffer;
    private StringBuffer limitBuffer;

    private OnFeePopupSaveClickListener onFeePopupSaveClickListener;


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

        viewMapper = new ViewMapper( );

        viewMapper.groupFeePopup.setOnClickListener( this );

        viewMapper.groupGasPrice.setOnClickListener( this );
        viewMapper.groupGasLimit.setOnClickListener( this );
        viewMapper.btnCancel.setOnClickListener( this );
        viewMapper.btnSave.setOnClickListener( this );
        viewMapper.btnFeeDelete.setOnClickListener( this );
        viewMapper.btnHeader.setOnClickListener( this );

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

        setFocusDataSet( price, viewMapper.textGasPrice );
        setFocusDataSet( limit, viewMapper.textGasLimit );
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.groupFeePopup ) {

        } else if ( v == viewMapper.btnHeader ) {
            dismiss( );
        } else if ( v == viewMapper.groupGasPrice ) {
            setFocusViewSetting( viewMapper.groupGasPrice, viewMapper.textGasPrice, viewMapper.groupGasLimit, viewMapper.textGasLimit );
            setFocusDataSet( price, viewMapper.textGasPrice );
        } else if ( v == viewMapper.groupGasLimit ) {
            setFocusViewSetting( viewMapper.groupGasLimit, viewMapper.textGasLimit, viewMapper.groupGasPrice, viewMapper.textGasPrice );
            setFocusDataSet( limit, viewMapper.textGasLimit );
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
            dismiss( );
        } else if ( v == viewMapper.btnSave ) {
            if ( onFeePopupSaveClickListener != null ) {
                onFeePopupSaveClickListener.onFeePopupSaveClick( viewMapper.textGasFee.getText( ).toString( ) );
            }
        } else {
            if ( v instanceof FontTextView ){
                if ( getActivity( ).getCurrentFocus( ) == viewMapper.groupGasPrice ) {
                    price.add( ( ( FontTextView ) v ).getText( ).toString( ) );
                    setPriceORLimit( priceBuffer, price, viewMapper.textGasPrice );
                } else if ( getActivity( ).getCurrentFocus( ) == viewMapper.groupGasLimit ) {
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
    void setFocusViewSetting( RoundRelativeLayout gv, TextView t, RoundRelativeLayout gv1, TextView t1 ) {
        gv.setBorder_color_normal( Color.parseColor( "#000000" ) );
        gv1.setBorder_color_normal( Color.parseColor( "#EDEDED" ) );

        t.setTextColor( Color.parseColor( "#000000" ) );
        t1.setTextColor( Color.parseColor( "#aaaaaa" ) );

        gv.setFocusableInTouchMode( true );
        gv.requestFocus( );
        gv1.setFocusableInTouchMode( false );
    }

    void setFocusDataSet( ArrayList< String > list, TextView v ) {
        list.clear( );
        for ( int i = 0; i < v.getText( ).length( ); i++ ) {
            list.add( String.valueOf( v.getText( ).charAt( i ) ) );
        }
    }

    void setPriceORLimit( StringBuffer b, ArrayList< String > list, TextView v ) {
        b.setLength( 0 );
        for ( int i = 0; i < list.size( ); i++ ) {
            b.append( list.get( i ) );
        }
        v.setText( b.toString( ) );

        setFee( );
    }

    void setFee( ) {
        if ( viewMapper.textGasLimit.getText( ).length( ) != 0 && viewMapper.textGasPrice.getText( ).length( ) != 0 ) {
            BigDecimal price = new BigDecimal( viewMapper.textGasPrice.getText( ).toString( ) ).movePointLeft( 9 );
            BigDecimal limit = new BigDecimal( viewMapper.textGasLimit.getText( ).toString( ) );
            viewMapper.textGasFee.setText( String.valueOf( limit.multiply( price ).stripTrailingZeros( ) ) );
        }
    }

    public FeePopup setOnFeePopupSaveClickListener( OnFeePopupSaveClickListener onFeePopupSaveClickListener ) {
        this.onFeePopupSaveClickListener = onFeePopupSaveClickListener;
        return this;
    }

    public interface OnFeePopupSaveClickListener {
        void onFeePopupSaveClick( String fee );
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
