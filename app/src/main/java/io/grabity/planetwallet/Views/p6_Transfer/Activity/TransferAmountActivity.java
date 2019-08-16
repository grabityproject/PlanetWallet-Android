package io.grabity.planetwallet.Views.p6_Transfer.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.VO.Transfer;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TransferAmountActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;
    private ArrayList< FontTextView > amountButtons;
    private ArrayList< String > amount;
    private String inputAmount;


    private Planet planet;
    private Transfer transfer;
    private ERC20 erc20;


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_transfer_amount );
        viewMapper = new ViewMapper( );
        C.transferClass.transferAmountActivity = this;
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
        if ( getSerialize( C.bundleKey.PLANET ) == null || getSerialize( C.bundleKey.TRANSFER ) == null ) {
            finish( );
        } else {

            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            transfer = ( Transfer ) getSerialize( C.bundleKey.TRANSFER );


            if ( CoinType.ETH.getCoinType( ).equals( planet.getCoinType( ) ) && getSerialize( C.bundleKey.ERC20 ) != null ) {
                erc20 = ( ERC20 ) getSerialize( C.bundleKey.ERC20 );
                viewMapper.textBalance.setText( String.format( "%s " + erc20.getName( ), Utils.balanceReduction( Utils.moveLeftPoint( erc20.getBalance( ), 18 ) ) ) );
            } else {
                viewMapper.textBalance.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), Utils.balanceReduction( Utils.moveLeftPoint( planet.getBalance( ), 18 ) ) ) );
            }

            getBalance( );
            toolBarSetView( );
        }
    }


    //Todo 일단 ETH만 적용

    private void getBalance( ) {
        if ( CoinType.ETH.getCoinType( ).equals( planet.getCoinType( ) ) ) {
            if ( getSerialize( C.bundleKey.ERC20 ) != null ) { //ERC20
                new Get( this ).setDeviceKey( C.DEVICE_KEY )
                        .action( Route.URL( "balance", erc20.getSymbol( ), planet.getName( ) ), 0, 1, null );
            } else { // ETH
                new Get( this ).setDeviceKey( C.DEVICE_KEY )
                        .action( Route.URL( "balance", CoinType.ETH.name( ), planet.getName( ) ), 0, 0, null );
            }
        } else if ( CoinType.BTC.getCoinType( ).equals( planet.getCoinType( ) ) ) { //BTC

        }
    }

    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {
        super.onReceive( error, requestCode, resultCode, statusCode, result );


        if ( !error ) {
            if ( requestCode == 0 ) {
                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, resultCode == 0 ? Planet.class : ERC20.class );
                if ( returnVO.isSuccess( ) ) {
                    if ( resultCode == 0 ) {
                        Planet p = ( Planet ) returnVO.getResult( );
                        planet.setBalance( p.getBalance( ) );
                        viewMapper.textBalance.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), Utils.balanceReduction( Utils.moveLeftPoint( planet.getBalance( ), 18 ) ) ) );
                    } else if ( resultCode == 1 ) {
                        ERC20 e = ( ERC20 ) returnVO.getResult( );
                        erc20.setBalance( e.getBalance( ) );
                        viewMapper.textBalance.setText( String.format( "%s " + erc20.getName( ), Utils.balanceReduction( Utils.moveLeftPoint( erc20.getBalance( ), 18 ) ) ) );
                    }
                }
            }
        } else {
            super.onBackPressed( );
        }

    }

    private void toolBarSetView( ) {
        viewMapper.groupPlanet.setVisibility( Utils.equals( transfer.getChoice( ), C.transferChoice.PLANET_NAME ) ? View.VISIBLE : View.GONE );
        viewMapper.textAddress.setVisibility( Utils.equals( transfer.getChoice( ), C.transferChoice.ADDRESS ) ? View.VISIBLE : View.GONE );
        if ( Utils.equals( transfer.getChoice( ), C.transferChoice.PLANET_NAME ) ) {
            viewMapper.planetView.setData( transfer.getToAddress( ) );
            viewMapper.textPlanetName.setText( transfer.getToName( ) );
        } else if ( Utils.equals( transfer.getChoice( ), C.transferChoice.ADDRESS ) ) {
            viewMapper.textAddress.setText( Utils.addressReduction( transfer.getToAddress( ) ) );
        }
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnSubmit ) {
            Bundle bundle = new Bundle( );
            if ( CoinType.BTC.getCoinType( ).equals( planet.getCoinType( ) ) ) {
                bundle.putSerializable( C.bundleKey.PLANET, planet );

            } else if ( CoinType.ETH.getCoinType( ).equals( planet.getCoinType( ) ) ) {

                if ( getSerialize( C.bundleKey.ERC20 ) != null ) {
                    bundle.putSerializable( C.bundleKey.PLANET, planet );
                    bundle.putSerializable( C.bundleKey.ERC20, erc20 );
                } else {
                    bundle.putSerializable( C.bundleKey.PLANET, planet );
                }
            }
            //balance setting
            transfer.setToBalance( viewMapper.textAmount.getText( ).toString( ) );
            bundle.putSerializable( C.bundleKey.TRANSFER, transfer );
            setTransition( Transition.SLIDE_SIDE );
            sendAction( TransferConfirmActivity.class, bundle );
        } else if ( v == viewMapper.btnAmountDelete ) {
            if ( amount.size( ) > 0 ) {
                amount.remove( amount.size( ) - 1 );

                if ( amount.size( ) == 0 ) {
                    amount.add( "0" );
                }
            }
            setAmount( );
        } else if ( v instanceof FontTextView ) {

            //Todo 임시 자릿수 제한 10자리
            if ( amount.size( ) >= 10 ) return;

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

        //Todo 언어별로 원화변경 및 계산
        viewMapper.textAmountUSD.setText( amount.size( ) == 1 && amount.get( 0 ).equals( "0" ) ? "0 USD" : String.format( "%s USD", String.valueOf( Float.valueOf( inputAmount ) / 2f ) ) );
        viewMapper.btnSubmit.setEnabled( btnEnable( ) );
    }

    private boolean btnEnable( ) {
        if ( amount.size( ) == 1 && amount.get( 0 ).equals( "0" ) ) {
            viewMapper.textError.setVisibility( View.GONE );
            viewMapper.textAmountUSD.setVisibility( View.VISIBLE );
            return false;
        } else {
            if ( amount.get( amount.size( ) - 1 ).equals( "." ) ) {
                return false;
            }

            if ( CoinType.BTC.getCoinType( ).equals( planet.getCoinType( ) ) ) {
                return balanceCheck( planet.getBalance( ), viewMapper.textAmount.getText( ).toString( ) );
            } else if ( CoinType.ETH.getCoinType( ).equals( planet.getCoinType( ) ) ) {

                return balanceCheck( getSerialize( C.bundleKey.ERC20 ) != null ? erc20.getBalance( ) : planet.getBalance( ), viewMapper.textAmount.getText( ).toString( ) );

            }
            return false;
        }
    }

    //Todo 수수료 값 추가
    private boolean balanceCheck( String myBalance, String toBalance ) {
        if ( myBalance == null || toBalance == null ) return false;
        BigDecimal mB = new BigDecimal( myBalance );
        BigDecimal tB = new BigDecimal( toBalance );
        viewMapper.textError.setVisibility( mB.compareTo( tB ) > 0 ? View.GONE : View.VISIBLE );
        viewMapper.textAmountUSD.setVisibility( mB.compareTo( tB ) > 0 ? View.VISIBLE : View.GONE );
        if ( mB.compareTo( tB ) > 0 ) return true;
        return false;
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
        TextView textError;

        ViewGroup groupInputAmount;
        View btnAmountDelete;
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
            textError = findViewById( R.id.text_transfer_amount_error_message );
            groupInputAmount = findViewById( R.id.group_transfer_amount_input_amount );
            btnAmountDelete = findViewById( R.id.group_transfer_amount_delete );
            btnSubmit = findViewById( R.id.btn_submit );

        }
    }
}
