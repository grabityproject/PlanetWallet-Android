package io.grabity.planetwallet.Views.p6_Transfer.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.Transfer;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TransferAmountActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;
    private ArrayList< FontTextView > amountButtons;
    private ArrayList< String > amount;

    private StringBuffer amountBuffer;

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

        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        amountButtons = Utils.getAllViewsFromParentView( viewMapper.groupInputAmount, FontTextView.class );
        viewMapper.btnAmonutDelete.setOnClickListener( this );
        viewMapper.btnSubmit.setOnClickListener( this );
        viewMapper.btnSubmit.setEnabled( false );

        for ( int i = 0; i < amountButtons.size( ); i++ ) {
            amountButtons.get( i ).setOnClickListener( this );
        }
        amount = new ArrayList<>( );
        amountBuffer = new StringBuffer( );

        //default
        amount.add( "0" );

    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.PLANET ) == null || getSerialize( C.bundleKey.TRANSFER ) == null ) {
            finish( );
        } else {

            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            transfer = ( Transfer ) getSerialize( C.bundleKey.TRANSFER );

            if ( CoinType.BTC.getCoinType( ).equals( planet.getCoinType( ) ) ) {
                viewMapper.textBalance.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), planet.getBalance( ) ) );
            } else if ( CoinType.ETH.getCoinType( ).equals( planet.getCoinType( ) ) ) {
                if ( getSerialize( C.bundleKey.ERC20 ) != null ) {
                    erc20 = ( ERC20 ) getSerialize( C.bundleKey.ERC20 );
                    viewMapper.textBalance.setText( String.format( "%s " + erc20.getName( ), erc20.getBalance( ) ) );
                } else {
                    viewMapper.textBalance.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), planet.getBalance( ) ) );
                }
            }
            toolBarSetView( );
        }
    }

    void toolBarSetView( ) {
        viewMapper.groupPlanet.setVisibility( Utils.equals( transfer.getChoice( ), C.transferChoice.PLANET_NAME ) ? View.VISIBLE : View.GONE );
        viewMapper.textAddress.setVisibility( Utils.equals( transfer.getChoice( ), C.transferChoice.ADDRESS ) ? View.VISIBLE : View.GONE );
        if ( Utils.equals( transfer.getChoice( ), C.transferChoice.PLANET_NAME ) ) {
            viewMapper.planetView.setData( transfer.getToAddress( ) );
//            viewMapper.textPlanetName.setText( transfer.getToName( ) );
            viewMapper.textPlanetName.setText( Utils.planetNameForm( transfer.getToName( ) ) );
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
        } else if ( v == viewMapper.btnAmonutDelete ) {
            if ( amount.size( ) > 0 ) {
                amount.remove( amount.size( ) - 1 );

                if ( amount.size( ) == 0 ) {
                    amount.add( "0" );
                }
            }
            setAmount( );
        } else if ( v instanceof FontTextView ) {

            //Todo 임시 자릿수 제한 10자리
            if ( amount.size() >= 10 ) return;

            if ( ( ( FontTextView ) v ).getText( ).equals( "." ) ) {
                if ( !amount.toString( ).contains( "." ) ) {
                    amount.add( ( ( FontTextView ) v ).getText( ).toString( ) );
                }
            } else if ( ( ( FontTextView ) v ).getText( ).equals( "0" ) ) {
                if ( amount.toString( ).contains( "." ) || !amount.get( 0 ).equals( "0" ) ) {
                    amount.add( ( ( FontTextView ) v ).getText( ).toString( ) );
                }
            } else {
                if ( amount.size( ) == 1 && amount.get( 0 ).equals( "0" ) ) {
                    amount.set( 0, ( ( FontTextView ) v ).getText( ).toString( ) );
                } else {
                    amount.add( ( ( FontTextView ) v ).getText( ).toString( ) );
                }
            }
            setAmount( );
        }
    }

    void setAmount( ) {
        amountBuffer.setLength( 0 );
        for ( int i = 0; i < amount.size( ); i++ ) {
            amountBuffer.append( amount.get( i ) );
        }
        viewMapper.textAmount.setText( amount.size( ) == 1 && amount.get( 0 ).equals( "0" ) ? "0" : amountBuffer.toString( ) );
        viewMapper.textAmountUSD.setText( amount.size( ) == 1 && amount.get( 0 ).equals( "0" ) ? "0 USD" : String.format( "%s USD", String.valueOf( Float.valueOf( amountBuffer.toString( ) ) / 2f ) ) );
        viewMapper.btnSubmit.setEnabled( btnEnable( ) );
    }

    private boolean btnEnable( ) {
        if ( amount.size( ) == 1 && amount.get( 0 ).equals( "0" ) ) {
            viewMapper.texterror.setVisibility( View.GONE );
            viewMapper.textAmountUSD.setVisibility( View.VISIBLE );
            return false;
        } else {
            if ( amount.get( amount.size( ) - 1 ).equals( "." ) ) {
                return false;
            }

            if ( CoinType.BTC.getCoinType( ).equals( planet.getCoinType( ) ) ) {
                return balanceCheck( planet.getBalance( ), viewMapper.textAmount.getText( ).toString( ) );
            } else if ( CoinType.ETH.getCoinType( ).equals( planet.getCoinType( ) ) ) {

                if ( getSerialize( C.bundleKey.ERC20 ) != null ) {
                    return balanceCheck( erc20.getBalance( ), viewMapper.textAmount.getText( ).toString( ) );
                } else {
                    return balanceCheck( planet.getBalance( ), viewMapper.textAmount.getText( ).toString( ) );
                }

            }
            return false;
        }
    }

    //Todo 수수료 값 추가
    private boolean balanceCheck( String myBalance, String toBalance ) {
        if ( myBalance == null || toBalance == null ) return false;
        BigDecimal mB = new BigDecimal( myBalance );
        BigDecimal tB = new BigDecimal( toBalance );
        viewMapper.texterror.setVisibility( mB.compareTo( tB ) > 0 ? View.GONE : View.VISIBLE );
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
