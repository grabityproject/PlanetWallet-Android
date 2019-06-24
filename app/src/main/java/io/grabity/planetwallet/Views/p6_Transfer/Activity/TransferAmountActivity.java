package io.grabity.planetwallet.Views.p6_Transfer.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TransferAmountActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;
    private ArrayList< FontTextView > amountButtons;
    private ArrayList< String > amount;

    private StringBuffer amountBuffer;


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_transfer_amount );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );

        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        amountButtons = Utils.getAllViewsFromParentView( viewMapper.groupInputAmount, FontTextView.class );
        viewMapper.btnAmonutDelete.setOnClickListener( this );
        viewMapper.btnSubmit.setOnClickListener( this );
        viewMapper.btnSubmit.setEnabled( false );

        //Todo 임시
        if ( getString( "address" ) != null ) {
            viewMapper.textAddress.setVisibility( View.VISIBLE );
            viewMapper.textAddress.setText( Utils.addressReduction( getString( "address" ) ) );
        } else if ( getString( "planet" ) != null ) {
            viewMapper.groupPlanet.setVisibility( View.VISIBLE );
            viewMapper.planetView.setData( getString( "planet" ) );
            viewMapper.textPlanetName.setText( "Choi_3950" );
        }
    }

    @Override
    protected void setData( ) {
        super.setData( );

        for ( int i = 0; i < amountButtons.size( ); i++ ) {
            amountButtons.get( i ).setOnClickListener( this );
        }
        amount = new ArrayList<>( );
        amountBuffer = new StringBuffer( );

    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnSubmit ) {
            sendAction( TransferConfirmActivity.class );
        } else if ( v == viewMapper.btnAmonutDelete ) {
            if ( amount.size( ) > 0 ) {
                amount.remove( amount.size( ) - 1 );

//                if ( amount.get( amount.size( ) - 1 ).equals( "." ) ) {
//                    //지우고 다음 자리가 .일 경우 .도 제거
//                    amount.remove( amount.size( ) - 1 );
//                }
                setAmount( );
            }
        } else if ( v instanceof FontTextView ) {

            if ( ( ( FontTextView ) v ).getText( ).equals( "." ) ) {
                if ( !amount.toString( ).contains( "." ) && amount.size( ) > 0 ) {
                    amount.add( ( ( FontTextView ) v ).getText( ).toString( ) );
                    setAmount( );
                }
            } else {
                amount.add( ( ( FontTextView ) v ).getText( ).toString( ) );
                setAmount( );

            }
        }
    }

    void setAmount( ) {
        amountBuffer.setLength( 0 );
        for ( int i = 0; i < amount.size( ); i++ ) {
            amountBuffer.append( amount.get( i ) );
        }
        viewMapper.textAmount.setText( amount.size( ) == 0 ? "0" : amountBuffer.toString( ) );
        viewMapper.textAmountUSD.setText( amount.size( ) == 0 ? "0 USD" : String.format( "%s USD", String.valueOf( Float.valueOf( amountBuffer.toString( ) ) / 2f ) ) );
        viewMapper.btnSubmit.setEnabled( amount.size( ) != 0 );
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            super.onBackPressed( );
        }
    }

    public class ViewMapper {

        ToolBar toolBar;

        ViewGroup groupPlanet;
        PlanetView planetView;
        TextView textPlanetName;

        TextView textAddress;
        TextView textBalance;
        TextView textAmount;
        TextView textAmountUSD;
        TextView texterror;

        ViewGroup groupInputAmount;
        View btnAmonutDelete;
        View btnSubmit;


        public ViewMapper( ) {

            toolBar = findViewById( R.id.toolBar );
            groupPlanet = findViewById( R.id.group_transfer_amount_planet );
            planetView = findViewById( R.id.planet_transfer_amount_planetview );
            textPlanetName = findViewById( R.id.text_transfer_amount_planet_name );
            textAddress = findViewById( R.id.text_transfer_amount_address );
            textBalance = findViewById( R.id.text_transfer_amount_balance );
            textAmount = findViewById( R.id.text_transfer_amount_amount );
            textAmountUSD = findViewById( R.id.text_transfer_amount_amount_usd );
            texterror = findViewById( R.id.text_transfer_amount_error_message );
            groupInputAmount = findViewById( R.id.group_transfer_amount_input_amount );
            btnAmonutDelete = findViewById( R.id.group_transfer_amount_delete );
            btnSubmit = findViewById( R.id.btn_submit );

        }
    }
}
