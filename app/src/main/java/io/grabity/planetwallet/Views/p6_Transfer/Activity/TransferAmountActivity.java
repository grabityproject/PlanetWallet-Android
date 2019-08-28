package io.grabity.planetwallet.Views.p6_Transfer.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.VO.Tx;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TransferAmountActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;
    private ArrayList< FontTextView > amountButtons;
    private ArrayList< String > amount;
    private String inputAmount;


    private Planet planet;
    private MainItem mainItem;
    private Tx tx;


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        getPlanetWalletApplication( ).recordActvityStack( this );
        setContentView( R.layout.activity_transfer_amount );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );

        if ( Utils.getScrennHeight( this ) <= 1920 ) {
            Utils.setPadding( viewMapper.textBalance, 0, 24, 0, 0 );
            Utils.setPadding( viewMapper.textAmount, 0, 20, 0, 0 );
            Utils.addBottomMargin( viewMapper.groupInputAmount, 20 );
        }


        viewMapper.toolBar.setLeftButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        amountButtons = Utils.getAllViewsFromParentView( viewMapper.groupInputAmount, FontTextView.class );
        viewMapper.btnAmountDelete.setOnClickListener( this );
        viewMapper.btnSubmit.setOnClickListener( this );
        viewMapper.btnSubmit.setEnabled( false );

        for ( int i = 0; i < amountButtons.size( ); i++ ) {
            amountButtons.get( i ).setOnClickListener( this );
        }
        amount = new ArrayList<>( );

        //default
        amount.add( "0" );
        setAmount( );

    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.PLANET ) == null || getSerialize( C.bundleKey.MAIN_ITEM ) == null || getSerialize( C.bundleKey.TX ) == null ) {
            finish( );
        } else {

            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            mainItem = ( MainItem ) getSerialize( C.bundleKey.MAIN_ITEM );
            tx = ( Tx ) getSerialize( C.bundleKey.TX );

            viewMapper.textBalance.setText( String.format( Locale.US, "%s %s", Utils.ofZeroClear( Utils.toMaxUnit( mainItem, mainItem.getBalance( ) ) ), mainItem.getSymbol( ) ) );

            if ( tx.getTo_planet( ) == null ) { // Address
                viewMapper.planetView.setVisibility( View.GONE );
                viewMapper.textName.setText( Utils.addressReduction( tx.getTo( ) ) );
            } else { // Planet
                viewMapper.planetView.setVisibility( View.VISIBLE );
                viewMapper.planetView.setData( tx.getTo( ) );
                viewMapper.textName.setText( tx.getTo_planet( ) );
            }

            getBalance( );
        }
    }


    private void getBalance( ) {
        new Get( this ).setDeviceKey( C.DEVICE_KEY )
                .action( Route.URL( "balance", mainItem.getSymbol( ), planet.getAddress( ) ), 0, 0, null );
    }

    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {
        super.onReceive( error, requestCode, resultCode, statusCode, result );

        if ( !error ) {
            if ( requestCode == 0 ) {
                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, MainItem.class );
                if ( returnVO.isSuccess( ) ) {
                    MainItem balance = ( MainItem ) returnVO.getResult( );
                    this.mainItem.setBalance( balance.getBalance( ) );
                    viewMapper.textBalance.setText( String.format( Locale.US, "%s %s", Utils.ofZeroClear( Utils.toMaxUnit( mainItem, mainItem.getBalance( ) ) ), mainItem.getSymbol( ) ) );
                }
            }
        } else {
            super.onBackPressed( );
        }

    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnSubmit ) {

            tx.setAmount( Utils.toMinUnit( mainItem, viewMapper.textAmount.getText( ).toString( ) ) );

            setTransition( Transition.SLIDE_SIDE );
            sendAction( TransferConfirmActivity.class,
                    Utils.mergeBundles(
                            Utils.createSerializableBundle( C.bundleKey.PLANET, planet ),
                            Utils.createSerializableBundle( C.bundleKey.MAIN_ITEM, mainItem ),
                            Utils.createSerializableBundle( C.bundleKey.TX, tx ) ) );


        } else if ( v == viewMapper.btnAmountDelete ) {
            if ( amount.size( ) > 0 ) {
                amount.remove( amount.size( ) - 1 );

                if ( amount.size( ) == 0 ) {
                    amount.add( "0" );
                }
            }
            setAmount( );
        } else if ( v instanceof FontTextView ) {

            inputAmountCheck( ( ( FontTextView ) v ).getText( ).toString( ) );
            setAmount( );

        }
    }

    void inputAmountCheck( String s ) {
        switch ( s ) {
            case ".":
                if ( !amount.toString( ).contains( "." ) ) amount.add( s );
                break;
            case "0":
                if ( amount.toString( ).contains( "." ) || !amount.get( 0 ).equals( "0" ) )
                    amount.add( s );
                break;
            default:
                if ( amount.size( ) == 1 && amount.get( 0 ).equals( "0" ) ) {
                    amount.set( 0, s );
                } else {
                    amount.add( s );
                }
                break;
        }
    }

    void setAmount( ) {
        inputAmount = Utils.join( amount );

        viewMapper.textAmount.setText( amount.size( ) == 1 && amount.get( 0 ).equals( "0" ) ? "0" : inputAmount );
        viewMapper.textAmountUSD.setText( amount.size( ) == 1 && amount.get( 0 ).equals( "0" ) ? "0 USD" : String.format( "%s USD", String.valueOf( Float.valueOf( inputAmount ) / 2f ) ) );

        viewMapper.btnSubmit.setEnabled( btnEnable( ) );
    }

    private boolean btnEnable( ) {
        if ( amount.size( ) == 1 && amount.get( 0 ).equals( "0" ) ) {
            viewMapper.textError.setVisibility( View.GONE );
            viewMapper.textAmountUSD.setVisibility( View.VISIBLE );
            return false;
        } else {
            if ( amount.get( amount.size( ) - 1 ).equals( "." ) ) return false;
            return balanceCheck( );
        }
    }

    private boolean balanceCheck( ) {

        BigDecimal balance = new BigDecimal( mainItem.getBalance( ) );
        BigDecimal amount = new BigDecimal( Utils.toMinUnit( mainItem, viewMapper.textAmount.getText( ).toString( ) ) );

        int compare = amount.compareTo( balance );
        viewMapper.textError.setVisibility( compare < 0 ? View.GONE : View.VISIBLE );
        viewMapper.textAmountUSD.setVisibility( compare < 0 ? View.VISIBLE : View.GONE );

        return compare < 0;
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
        TextView textName;

        TextView textBalance;
        TextView textAmount;
        TextView textAmountUSD;
        TextView textError;

        ViewGroup groupInputAmount;
        View btnAmountDelete;
        View btnSubmit;


        public ViewMapper( ) {

            toolBar = findViewById( R.id.toolBar );
            groupPlanet = findViewById( R.id.group_transfer_amount_planet );
            planetView = findViewById( R.id.planet_transfer_amount_planetview );
            textName = findViewById( R.id.text_transfer_amount_planet_name );

            textBalance = findViewById( R.id.text_transfer_amount_balance );
            textAmount = findViewById( R.id.text_transfer_amount_amount );
            textAmountUSD = findViewById( R.id.text_transfer_amount_amount_usd );
            textError = findViewById( R.id.text_transfer_amount_error_message );
            groupInputAmount = findViewById( R.id.group_transfer_amount_input_amount );
            btnAmountDelete = findViewById( R.id.group_transfer_amount_delete );
            btnSubmit = findViewById( R.id.btn_submit );

        }
    }
}
