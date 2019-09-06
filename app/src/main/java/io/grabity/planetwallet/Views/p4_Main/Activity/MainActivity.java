package io.grabity.planetwallet.Views.p4_Main.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.managers.SyncManager;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyValueStore;
import io.grabity.planetwallet.MiniFramework.wallet.store.MainItemStore;
import io.grabity.planetwallet.MiniFramework.wallet.store.PlanetStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.VO.Tx;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Views.p3_Wallet.Activity.WalletAddActivity;
import io.grabity.planetwallet.Views.p4_Main.Adapter.MainAdapter;
import io.grabity.planetwallet.Views.p4_Main.Components.BottomLauncherComponent;
import io.grabity.planetwallet.Views.p4_Main.Components.FooterViewComponent;
import io.grabity.planetwallet.Views.p4_Main.Components.HeaderViewComponent;
import io.grabity.planetwallet.Views.p4_Main.Components.RefreshAnimationComponent;
import io.grabity.planetwallet.Views.p4_Main.Components.TopLauncherComponent;
import io.grabity.planetwallet.Views.p4_Main.Etc.NodeService;
import io.grabity.planetwallet.Views.p7_Setting.Activity.Planet.MnemonicExportActivity;
import io.grabity.planetwallet.Views.p7_Setting.Activity.SettingActivity;
import io.grabity.planetwallet.Views.p8_Tx.Activity.DetailTxActivity;
import io.grabity.planetwallet.Views.p8_Tx.Activity.TxListActivity;
import io.grabity.planetwallet.Views.p8_Tx.Adapter.TxAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.CustomToast;
import io.grabity.planetwallet.Widgets.OverScrollWrapper.OverScrollWrapper;
import io.grabity.planetwallet.Widgets.RippleEffectView;
import io.grabity.planetwallet.Widgets.SlideDrawerLayout;
import io.grabity.planetwallet.Widgets.ToolBar;

public class MainActivity extends PlanetWalletActivity implements AdvanceArrayAdapter.OnAttachViewListener, ToolBar.OnToolBarClickListener, RippleEffectView.OnRippleEffectListener, AdvanceRecyclerView.OnItemClickListener, OverScrollWrapper.OnRefreshListener, SyncManager.OnSyncListener, NodeService.OnNodeServiceListener {

    private ViewMapper viewMapper;

    private TopLauncherComponent topLauncherComponent;
    private BottomLauncherComponent bottomLauncherComponent;

    private FooterViewComponent footerViewComponent;
    private HeaderViewComponent headerViewComponent;

    private RefreshAnimationComponent refreshAnimationComponent;

    private ArrayList< Planet > planetList;
    private ArrayList< Tx > txList;

    private Planet selectedPlanet;
    private TxAdapter adapter;


    private long backPressedTime = 0;


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setStatusTransparent( true );
        setContentView( R.layout.activity_main );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.overScrollWrapper.addOnRefreshListener( this );

        // Toolbar
        viewMapper.toolBar.setToolBarHeight( );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.toolBar.setLeftButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_MENU ) );
        viewMapper.toolBar.setRightButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_MUTIUNIVERSE ) );

        // RippleView
        Objects.requireNonNull( Utils.getAndroidContentViewGroup( this ) ).addView( viewMapper.rippleView );
        viewMapper.rippleView.setOnRippleEffectListener( this );
        viewMapper.rippleView.setTrigger( viewMapper.toolBar.getButtonItems( ).get( 0 ).getView( ) );

        // Main List
        viewMapper.listMain.setOnItemClickListener( this );
        viewMapper.listMain.setOnAttachViewListener( this );
        viewMapper.listMain.addHeaderView( R.layout.header_main );
        viewMapper.listMain.addFooterView( R.layout.footer_main );

        //refresh bottom launcher not working
        viewMapper.slideDrawer.addNotEventArea( viewMapper.listMain );

        // View Components
        topLauncherComponent = new TopLauncherComponent( this, viewMapper.slideDrawer );
        topLauncherComponent.setTrigger( viewMapper.toolBar.getButtonItems( ).get( 1 ).getView( ) );
        bottomLauncherComponent = new BottomLauncherComponent( this, viewMapper.slideDrawer );
        refreshAnimationComponent = new RefreshAnimationComponent( this, viewMapper.overScrollWrapper, viewMapper.listMain );

        viewMapper.textNotice.setOnClickListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        NodeService.getInstance( ).setOnNodeServiceListener( this );

        planetList = PlanetStore.getInstance( ).getPlanetList( false );
        topLauncherComponent.setPlanetList( planetList );

        String keyId = Utils.getPreferenceData( this, C.pref.LAST_PLANET_KEYID );
        if ( keyId.length( ) == 0 ) {
            if ( planetList.size( ) > 0 ) {
                selectedPlanet = planetList.get( 0 );
                setUpViews( );
            }
        } else {

            if ( planetList.size( ) == 1 ) {
                selectedPlanet = planetList.get( 0 );
                setUpViews( );
            } else {
                for ( Planet planet : planetList ) {
                    if ( Utils.equals( planet.getKeyId( ), keyId ) ) {
                        selectedPlanet = planet;
                        setUpViews( );
                        break;
                    }
                }
            }
        }

    }


    void setUpViews( ) {
        if ( selectedPlanet != null ) {

            viewMapper.toolBar.setTitle( CoinType.of( selectedPlanet.getCoinType( ) ).name( ) );

            Utils.setPreferenceData( this, C.pref.LAST_PLANET_KEYID, selectedPlanet.getKeyId( ) );

            if ( Utils.equals( CoinType.ETH.getCoinType( ), selectedPlanet.getCoinType( ) ) ) {

                selectedPlanet.setItems( MainItemStore.getInstance( ).getMainItem( selectedPlanet.getKeyId( ), false ) );

                viewMapper.listMain.setAdapter( new MainAdapter( this, selectedPlanet.getItems( ) == null ? new ArrayList<>( ) : selectedPlanet.getItems( ) ) );

            } else if ( Utils.equals( CoinType.BTC.getCoinType( ), selectedPlanet.getCoinType( ) ) ) {

                selectedPlanet.setItems( MainItemStore.getInstance( ).getMainItem( selectedPlanet.getKeyId( ), false ) );
                String btcPrefTx = Utils.getPreferenceData( this, Utils.prefKey( CoinType.of( selectedPlanet.getCoinType( ) ).getDefaultUnit( ), selectedPlanet.getSymbol( ), selectedPlanet.getKeyId( ) ) );
                if ( !Utils.equals( btcPrefTx, "" ) ) {
                    ReturnVO returnVO = Utils.jsonToVO( btcPrefTx, ReturnVO.class, Tx.class );
                    if ( returnVO.isSuccess( ) ) {
                        txList = ( ArrayList< Tx > ) returnVO.getResult( );
                    }
                } else {
                    if ( txList == null ) {
                        txList = new ArrayList<>( );
                    } else {
                        txList.clear( );
                    }
                }
                viewMapper.listMain.setAdapter( adapter = new TxAdapter( this, txList == null ? new ArrayList<>( ) : txList ) );

            }

            bottomLauncherComponent.setPlanet( selectedPlanet );

            if ( viewMapper.overScrollWrapper.isRefreshing( ) ) {
                viewMapper.overScrollWrapper.completeRefresh( );
            }

            if ( bottomLauncherComponent != null )
                bottomLauncherComponent.updateBlurView( getCurrentTheme( ) );

            setUpNotice( );

            NodeService.getInstance( ).getBalance( selectedPlanet );
            NodeService.getInstance( ).getMainList( selectedPlanet );

        }

    }


    void setUpNotice( ) {

        if ( Utils.equals( CoinType.BTC.getCoinType( ), selectedPlanet.getCoinType( ) ) ) {

            viewMapper.textNotice.setVisibility( selectedPlanet.getPathIndex( ) >= 0 && Utils.equals( Utils.getPreferenceData( this, C.pref.BACK_UP_MNEMONIC_BTC, String.valueOf( false ) ), String.valueOf( false ) ) ?
                    View.VISIBLE : View.INVISIBLE );

        } else if ( Utils.equals( CoinType.ETH.getCoinType( ), selectedPlanet.getCoinType( ) ) ) {

            viewMapper.textNotice.setVisibility( selectedPlanet.getPathIndex( ) >= 0 && Utils.equals( Utils.getPreferenceData( this, C.pref.BACK_UP_MNEMONIC_ETH, String.valueOf( false ) ), String.valueOf( false ) ) ?
                    View.VISIBLE : View.INVISIBLE );
        }


    }

    @Override
    public void onAttachView( int resId, int position, View view, boolean screenShotFlag ) {

        if ( !screenShotFlag ) {

            if ( resId == R.layout.header_main && position == 0 ) {
                headerViewComponent = new HeaderViewComponent( this, viewMapper.listMain, view );
                headerViewComponent.setPlanet( selectedPlanet );

            } else if ( resId == R.layout.footer_main ) {

                footerViewComponent = new FooterViewComponent( this, viewMapper.listMain, view );
                footerViewComponent.setPlanet( selectedPlanet );

            }
        }

    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.textNotice ) {

            setTransition( Transition.NO_ANIMATION ).sendAction( C.requestCode.PLANET_MNEMONIC_EXPORT, PinCodeCertificationActivity.class );

        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.MAIN_TOKEN_ADD && resultCode == RESULT_OK ) {

            setUpViews( );

        } else if ( requestCode == C.requestCode.PLANET_MNEMONIC_EXPORT && resultCode == RESULT_OK ) {

            if ( data != null ) {

                char[] pinCode = data.getCharArrayExtra( C.bundleKey.PINCODE );

                if ( pinCode != null ) {

                    if ( Utils.equals( Utils.sha256( Utils.join( pinCode ) ), KeyValueStore.getInstance( ).getValue( C.pref.PASSWORD, pinCode ) ) ) {

                        setTransition( Transition.SLIDE_UP ).sendAction( MnemonicExportActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, selectedPlanet ) );

                    }

                }
            }
        } else if ( requestCode == C.requestCode.MAIN_PLANET_ADD && resultCode == RESULT_OK ) {

            planetList = PlanetStore.getInstance( ).getPlanetList( false );
            selectedPlanet = planetList.get( planetList.size( ) - 1 );
            if ( selectedPlanet != null ) setUpViews( );
        }
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {

        if ( Utils.equals( tag, C.tag.TOOLBAR_MENU ) ) {

            if ( !viewMapper.rippleView.isRippleOn( ) && viewMapper.rippleView.isRippleAnimationStatus( ) ) {

                viewMapper.rippleView.ripple( true );

            }

        } else if ( Utils.equals( tag, C.tag.TOOLBAR_MUTIUNIVERSE ) ) {

            viewMapper.slideDrawer.open( SlideDrawerLayout.Position.TOP );

        }

    }

    @Override
    public void onBackPressed( ) {
        if ( viewMapper.slideDrawer.isOpen( ) ) {

            viewMapper.slideDrawer.close( );

        } else {

            if ( getPopupViewStack( ) != null && !getPopupViewStack( ).isEmpty( ) ) {

                onRemovePopup( getPopupViewStack( ).peek( ) );
                getPopupViewStack( ).pop( ).onBackPressed( );

            } else {

                if ( System.currentTimeMillis( ) > backPressedTime + 2000 ) {

                    backPressedTime = System.currentTimeMillis( );
                    CustomToast.makeText( this, localized( R.string.main_back_pressed_finish_title ) ).show( );

                } else {

                    super.onBackPressed( );

                }
            }
        }
    }

    @Override
    protected void onResume( ) {
        super.onResume( );
        viewMapper.rippleView.ripple( false );
        viewMapper.rippleView.setTheme( getCurrentTheme( ) );

        planetList = PlanetStore.getInstance( ).getPlanetList( false );

        topLauncherComponent.setPlanetList( planetList );

        if ( headerViewComponent != null ) {
            String planetName = PlanetStore.getInstance( ).getPlanet( selectedPlanet.getKeyId( ) ).getName( );
            selectedPlanet.setName( planetName );
            headerViewComponent.setPlanet( selectedPlanet );
            headerViewComponent.setTheme( getCurrentTheme( ) );
        }


        setUpNotice( );


    }

    @Override
    public void onRippleEffect( boolean on ) {
        if ( on ) {
            if ( viewMapper.rippleView.isBackPressed( ) ) {
                setTransition( Transition.NO_ANIMATION );
                sendAction( SettingActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, selectedPlanet ) );
            }
        }
    }

    @Override
    public void onItemClick( AdvanceRecyclerView recyclerView, View view, int position ) {

        if ( recyclerView == viewMapper.listMain ) {

            CoinType coinType = CoinType.of( selectedPlanet.getCoinType( ) );

            if ( coinType == CoinType.ETH ) {

                setTransition( Transition.SLIDE_SIDE );
                sendAction( TxListActivity.class,
                        Utils.mergeBundles(
                                Utils.createSerializableBundle( C.bundleKey.PLANET, selectedPlanet ),
                                Utils.createSerializableBundle( C.bundleKey.MAIN_ITEM, selectedPlanet.getItems( ).get( position ) ) ) );

            } else if ( coinType == CoinType.BTC ) {

                setTransition( Transition.SLIDE_UP );
                sendAction( DetailTxActivity.class,
                        Utils.createSerializableBundle( C.bundleKey.TX, ( Serializable ) Objects.requireNonNull( recyclerView.getAdapter( ) ).getObjects( ).get( position ) ) );

            }

        } else if ( recyclerView == topLauncherComponent.getListView( ) ) {

            if ( position < planetList.size( ) ) {

                selectedPlanet = planetList.get( position );
                viewMapper.slideDrawer.close( );
                setUpViews( );

                bottomLauncherComponent.updateBlurView( getCurrentTheme( ) );

            } else {

                viewMapper.slideDrawer.close( );
                Utils.postDelayed( ( ) -> setTransition( Transition.SLIDE_UP ).sendAction( C.requestCode.MAIN_PLANET_ADD, WalletAddActivity.class ), 250 );

            }

        }
    }


    @Override
    protected void onUpdateTheme( boolean theme ) {
        super.onUpdateTheme( theme );
        headerViewComponent.setTheme( theme );
        bottomLauncherComponent.updateBlurView( theme );
    }


    @Override
    public void onRefresh( ) {
        NodeService.getInstance( ).getBalance( selectedPlanet );
        NodeService.getInstance( ).getMainList( selectedPlanet );
        SyncManager.getInstance( ).syncPlanet( this );
    }

    @Override
    public void onSyncComplete( SyncManager.SyncType syncType, boolean complete, boolean isUpdated ) {
        if ( complete && isUpdated ) {
            selectedPlanet = PlanetStore.getInstance( ).getPlanet( selectedPlanet.getKeyId( ) );
            setUpViews( );
        }

    }

    @Override
    public void onBalance( Planet p, String balance ) {
        if ( selectedPlanet.getItems( ) != null && selectedPlanet.getItems( ).size( ) > 0 ) {
            selectedPlanet.getItems( ).get( 0 ).setBalance( balance );
        }

        if ( CoinType.of( bottomLauncherComponent.getMainItem( ).getCoinType( ) ) != CoinType.ERC20 )
            bottomLauncherComponent.setPlanet( selectedPlanet );

    }

    @Override
    public void onTokenBalance( Planet p, ArrayList< MainItem > tokenList ) {
        if ( viewMapper.overScrollWrapper.isRefreshing( ) ) {
            viewMapper.overScrollWrapper.completeRefresh( );
            Utils.postDelayed( ( ) -> Objects.requireNonNull( viewMapper.listMain.getAdapter( ) ).notifyDataSetChanged( ), 300 );
        } else {
            Objects.requireNonNull( viewMapper.listMain.getAdapter( ) ).notifyDataSetChanged( );
        }

        if ( CoinType.of( bottomLauncherComponent.getMainItem( ).getCoinType( ) ) == CoinType.ERC20 )
            bottomLauncherComponent.setPlanet( selectedPlanet );

    }

    @Override
    public void onTxList( Planet p, ArrayList< Tx > txList, String result ) {
        Utils.setPreferenceData( this, Utils.prefKey( CoinType.of( p.getCoinType( ) ).getDefaultUnit( ), p.getSymbol( ), p.getKeyId( ) ), result );
        if ( viewMapper.overScrollWrapper.isRefreshing( ) ) {
            viewMapper.overScrollWrapper.completeRefresh( );
            adapter.setObjects( txList );
            Utils.postDelayed( ( ) -> Objects.requireNonNull( viewMapper.listMain.getAdapter( ) ).notifyItemRangeChanged( 1, txList.size( ) ), 300 );
        } else {
            adapter.setObjects( txList );
            Objects.requireNonNull( viewMapper.listMain.getAdapter( ) ).notifyItemRangeChanged( 1, txList.size( ) );
        }


    }

    public class ViewMapper {

        OverScrollWrapper overScrollWrapper;

        ToolBar toolBar;
        RippleEffectView rippleView;

        SlideDrawerLayout slideDrawer;

        AdvanceRecyclerView listMain;
        TextView textNotice;

        public ViewMapper( ) {

            overScrollWrapper = findViewById( R.id.refresh );

            toolBar = findViewById( R.id.toolBar );
            rippleView = new RippleEffectView( MainActivity.this );

            slideDrawer = findViewById( R.id.slideDrawer );

            listMain = findViewById( R.id.list_main );

            textNotice = findViewById( R.id.text_main_notice );

        }
    }

}
