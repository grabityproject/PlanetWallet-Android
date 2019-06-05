package io.grabity.planetwallet.Views.p4_Main.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.Date;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.BTC;
import io.grabity.planetwallet.VO.MainItems.CoinType;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.MainItems.ETH;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Views.p4_Main.Adapter.MainAdapter;
import io.grabity.planetwallet.Views.p4_Main.Adapter.PlanetAdapter;
import io.grabity.planetwallet.Views.p4_Main.Etc.ViewController;
import io.grabity.planetwallet.Views.p5_Token.Activity.TokenAddActivity;
import io.grabity.planetwallet.Views.p7_Setting.Activity.SettingActivity;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.BarcodeView;
import io.grabity.planetwallet.Widgets.OverScrollWrapper.OverScrollWrapper;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.RippleEffectView;
import io.grabity.planetwallet.Widgets.RoundButton.RoundButton;
import io.grabity.planetwallet.Widgets.ShadowView;
import io.grabity.planetwallet.Widgets.SlideDrawerLayout;
import io.grabity.planetwallet.Widgets.StretchImageView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class MainActivity extends PlanetWalletActivity implements AdvanceArrayAdapter.OnAttachViewListener, ToolBar.OnToolBarClickListener, RippleEffectView.OnRippleEffectListener, AdvanceRecyclerView.OnItemClickListener, AdvanceRecyclerView.OnScrollListener, OverScrollWrapper.OnRefreshListener {

    private ViewMapper viewMapper;
    private HeaderViewMapper headerViewMapper;
    private FooterViewMapper footerViewMapper;
    private ViewController viewController;

    private Planet selectedPlanet;

    private ArrayList< Planet > planetList;

    private PlanetAdapter planetAdapter;

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
        ( ( ViewGroup ) findViewById( android.R.id.content ) ).addView( viewMapper.rippleView );

        viewMapper.refresh.addOnRefreshListener( this );

        viewController = new ViewController( this, viewMapper );

        ( ( ViewGroup.MarginLayoutParams ) viewMapper.toolBar.getLayoutParams( ) ).height = ( int ) ( Utils.dpToPx( this, 68 ) + getResources( ).getDimensionPixelSize( getResources( ).getIdentifier( "status_bar_height", "dimen", "android" ) ) );
        viewMapper.toolBar.requestLayout( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_MENU ) );
        viewMapper.toolBar.setRightButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_MUTIUNIVERSE ) );
        viewMapper.toolBar.setTitle( "ETH" );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.rippleView.setOnRippleEffectListener( this );
        viewMapper.rippleView.setTrigger( viewMapper.toolBar.getButtonItems( ).get( 0 ).getView( ) );

        viewMapper.listMain.setOnAttachViewListener( this );
        viewMapper.listMain.addHeaderView( R.layout.header_main );
        viewMapper.listMain.addFooterView( R.layout.footer_main );
        viewMapper.listMain.addOnScrollListener( this );
        viewMapper.listMain.setOnItemClickListener( this );

        viewMapper.listPlanets.setOnItemClickListener( this );

        viewMapper.slideDrawer.setTrigger( SlideDrawerLayout.Position.TOP, viewMapper.toolBar.getButtonItems( ).get( 1 ).getView( ) );

        viewMapper.btnCopy.setOnClickListener( this );
        viewMapper.btnSend.setOnClickListener( this );
        viewMapper.btnBottomBlur.setOnClickListener( this );

        viewMapper.slideDrawer.addBypassArea( viewMapper.btnBottomBlur );

    }

    @Override
    protected void setData( ) {
        super.setData( );
        selectedPlanet = new Planet( );
        planetList = new ArrayList<>( );
        viewMapper.listPlanets.setAdapter( planetAdapter = new PlanetAdapter( this, planetList ) );
        viewMapper.listMain.setAdapter( new MainAdapter( this, selectedPlanet.getItems( ) ) );


        setDummy( );
    }

    void setUpViews( ) {
        viewMapper.listMain.setAdapter( new MainAdapter( this, selectedPlanet.getItems( ) ) );

        viewMapper.textPlanetName.setText( selectedPlanet.getName( ) );
        viewMapper.toolBar.setTitle( selectedPlanet.getCoinType( ).name( ) );
        viewMapper.barcodeView.setData( selectedPlanet.getAddress( ) );
        viewMapper.textBalance.setText( "1.245" );
        viewMapper.textCoinName.setText( selectedPlanet.getCoinType( ).name( ) );

        headerViewMapper.planetView.setData( selectedPlanet.getAddress( ) );
        viewMapper.planetBackground.setData( selectedPlanet.getAddress( ) );
        viewMapper.planetBlur.setData( selectedPlanet.getAddress( ) );
        viewMapper.barcodeView.setPlanetView( headerViewMapper.planetView );
    }

    @Override
    public void onAttachView( int resId, int position, View view ) {
        if ( resId == R.layout.header_main && position == 0 ) {
            headerViewMapper = new HeaderViewMapper( view );

            setUpViews( );
            if ( viewController != null )
                viewController.setHeaderViewMapper( headerViewMapper );

        } else if ( resId == R.layout.footer_main ) {
            footerViewMapper = new FooterViewMapper( view );
            footerViewMapper.btnAddToken.setOnClickListener( this );

            footerViewMapper.groupAddToken.setVisibility( selectedPlanet.getCoinType( ) == CoinType.ETH ? View.VISIBLE : View.GONE );
            footerViewMapper.groupMessage.setVisibility( selectedPlanet.getItems( ).size( ) == 0 ? View.VISIBLE : View.GONE );

            footerViewMapper.btnAddToken.setBorderColorNormal( Color.parseColor( getCurrentTheme( ) ? "#EDEDED" : "#1E1E28" ) );
            footerViewMapper.btnAddToken.setBorderColorHighlight( Color.parseColor( getCurrentTheme( ) ? "#EDEDED" : "#1E1E28" ) );


        }
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnCopy ) {
            // TODO: 2019-05-31 ClipToCopy
        } else if ( v == viewMapper.btnSend ) {
            // TODO: 2019-05-31 sendAction Transfer Activity
        } else if ( v == footerViewMapper.btnAddToken ) {
            setTransition( Transition.SLIDE_SIDE );
            sendAction( C.requestCode.MAIN_TOKEN_ADD, TokenAddActivity.class );
        } else if ( v == viewMapper.btnBottomBlur ) {
            viewMapper.slideDrawer.open( SlideDrawerLayout.Position.BOTTOM );
        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.MAIN_TOKEN_ADD && resultCode == RESULT_OK ) {
            Toast.makeText( this, "Token Add", Toast.LENGTH_SHORT ).show( );
            setUpViews( );
        }
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {

        if ( Utils.equals( tag, C.tag.TOOLBAR_MENU ) ) {
            if ( !viewMapper.rippleView.isRippleOn( ) )
                viewMapper.rippleView.ripple( true );

        } else if ( Utils.equals( tag, C.tag.TOOLBAR_MUTIUNIVERSE ) ) {

            viewMapper.slideDrawer.open( SlideDrawerLayout.Position.TOP );

        }
    }

    @Override
    public void onBackPressed( ) {
        if ( viewMapper.slideDrawer.isOpen( ) ) {
            viewMapper.slideDrawer.close( );
        } else {
            setTransition( Transition.NO_ANIMATION );
            super.onBackPressed( );
        }
    }

    @Override
    protected void onResume( ) {
        super.onResume( );
        viewMapper.rippleView.ripple( false );
        viewMapper.rippleView.setTheme( getCurrentTheme( ) );
        viewMapper.shadowBackground.setShadowColor(
                Color.parseColor( getCurrentTheme( ) ? "#FFFFFF" : "#000000" ),
                Color.parseColor( getCurrentTheme( ) ? "#C8FFFFFF" : "#A8000000" )
        );
    }

    @Override
    public void onRippleEffect( boolean on ) {
        if ( on ) {
            setTransition( Transition.NO_ANIMATION );
            sendAction( SettingActivity.class );
        }
    }

    @Override
    public void onItemClick( AdvanceRecyclerView recyclerView, View view, int position ) {

        if ( recyclerView == viewMapper.listMain ) {


        } else if ( recyclerView == viewMapper.listPlanets ) {
            viewMapper.slideDrawer.close( );
            selectedPlanet = planetList.get( position );
            setUpViews( );
            viewController.updateBlurView( getCurrentTheme( ) );
        }
    }


    @Override
    protected void onUpdateTheme( boolean theme ) {
        super.onUpdateTheme( theme );
        viewMapper.shadowBackground.setShadowColor(
                Color.parseColor( theme ? "#FFFFFF" : "#000000" ),
                Color.parseColor( theme ? "#C8FFFFFF" : "#AA000000" )
        );
        viewController.updateBlurView( theme );
    }

    @Override
    public void onScrolled( RecyclerView recyclerView, int dx, int dy, float scrollX, float scrollY ) {

    }

    @Override
    public void onRefresh( ) {
        new Handler( ).postDelayed( new Runnable( ) {
            @Override
            public void run( ) {
                viewMapper.refresh.completeRefresh( );
            }
        }, 2500 );
    }


    public class ViewMapper {

        public OverScrollWrapper refresh;

        public ToolBar toolBar;
        public RippleEffectView rippleView;

        public SlideDrawerLayout slideDrawer;

        public AdvanceRecyclerView listMain;

        public TextView textNotice;
        public View viewTrigger;
        public View groupBlur;
        public StretchImageView imageBlurView;
        public View groupBottom;

        AdvanceRecyclerView listPlanets;
        TextView textPlanetName;

        View btnCopy;
        View btnSend;
        BarcodeView barcodeView;

        TextView textBalance;
        TextView textCoinName;


        public View groupBackground;
        public PlanetView planetBackground;
        public ShadowView shadowBackground;

        public PlanetView planetBlur;

        View btnBottomBlur;

        public LottieAnimationView lottiePullToRefresh;

        public ViewMapper( ) {

            refresh = findViewById( R.id.refresh );

            toolBar = findViewById( R.id.toolBar );
            rippleView = new RippleEffectView( MainActivity.this );

            slideDrawer = findViewById( R.id.slideDrawer );

            listMain = findViewById( R.id.list_main );

            textNotice = findViewById( R.id.text_main_notice );
            viewTrigger = findViewById( R.id.view_main_bottom_trigger );
            groupBlur = findViewById( R.id.group_main_blur );
            imageBlurView = findViewById( R.id.image_main_blur );
            groupBottom = findViewById( R.id.group_main_bottom );

            listPlanets = findViewById( R.id.list_main_planets_list );
            textPlanetName = findViewById( R.id.text_main_planets_name );

            btnCopy = findViewById( R.id.btn_main_bottom_copy );
            btnSend = findViewById( R.id.btn_main_bottom_send );
            barcodeView = findViewById( R.id.barcode_main_bottom_barcodeview );

            textBalance = findViewById( R.id.text_main_bottom_balance );
            textCoinName = findViewById( R.id.text_main_bottom_coin_name );

            planetBackground = findViewById( R.id.planet_main_background );
            groupBackground = findViewById( R.id.group_main_background );
            shadowBackground = findViewById( R.id.shadow_main_background );

            planetBlur = findViewById( R.id.planet_main_blur_planetview );

            btnBottomBlur = findViewById( R.id.btn_main_blur );

            lottiePullToRefresh = findViewById( R.id.lottie_main_pull_to_refresh );

        }
    }

    public class HeaderViewMapper {

        public View groupHeaderPlanet;
        public View headerView;
        public PlanetView planetView;

        TextView textName;
        TextView textAddress;
        View btnCopy;

        public HeaderViewMapper( View headerView ) {
            this.headerView = headerView;
            groupHeaderPlanet = headerView.findViewById( R.id.group_main_header_planet );
            planetView = headerView.findViewById( R.id.planet_main_header );
            textName = headerView.findViewById( R.id.text_main_header_planet_name );
            textAddress = headerView.findViewById( R.id.text_main_header_planet_address );
            btnCopy = headerView.findViewById( R.id.btn_main_header_copy );
        }
    }


    public class FooterViewMapper {

        public View footerView;
        RoundButton btnAddToken;

        ViewGroup groupAddToken;
        ViewGroup groupMessage;

        public FooterViewMapper( View footerView ) {
            this.footerView = footerView;
            btnAddToken = footerView.findViewById( R.id.btn_footer_main_manage_token );

            groupAddToken = footerView.findViewById( R.id.group_footer_main_manage_token );
            groupMessage = footerView.findViewById( R.id.group_footer_main_bit_message );
        }
    }


    //    DUMMY
    void setDummy( ) {
        planetList = new ArrayList<>( );
        if ( planetAdapter == null ) planetAdapter = new PlanetAdapter( this, planetList );

        { // 1 Planet
            Planet planet = new Planet( );
            planet.setCoinType( CoinType.BTC );
            planet.setName( "Jacob Park" );
            planet.setAddress( "0x36072b48604d6d83b5bb304d36887b00213433d5" );

            ArrayList< MainItem > items = new ArrayList<>( );
            for ( int i = 0; i < 10; i++ ) {
                BTC item = new BTC( );
                if ( i % 2 == 0 )
                    item.setBalance( String.valueOf( ( ( ( float ) i + 1 ) * 11.234f ) ) );
                else
                    item.setBalance( String.valueOf( ( ( ( float ) i + 1 ) * 2.112f ) ) );

                if ( i % 3 == 1 )
                    item.setBalance( "-" + item.getBalance( ) );
                item.setPlanetName( planet.getName( ) );
                item.setDate( Utils.dateFormat( new Date( System.currentTimeMillis( ) - 1000 * 60 * 60 * 24 * i ), "MMMM dd, HH:mm" ) );
                items.add( item );
            }
            planet.setItems( items );

            planetList.add( planet );
        }

        {
            Planet planet = new Planet( );
            planet.setCoinType( CoinType.ETH );
            planet.setName( "Choi" );
            planet.setAddress( "0x501c94659d2c00b134a9ba418aa182f14bf72e56" );

            ArrayList< MainItem > items = new ArrayList<>( );
            {
                ETH item = new ETH( );
                item.setAddress( "0x501c94659d2c00b134a9ba418aa182f14bf72e56" );
                item.setBalance( "12.0" );
                item.setName( "ETH" );
                item.setPrice( "3600$" );
                items.add( item );
            }

            {
                ERC20 item = new ERC20( );
                item.setAddress( "0x501c94659d2c00b134a9ba418aa182f14bf72e56" );
                item.setBalance( "244500" );
                item.setName( "GBT" );
                item.setPrice( "0$" );
                item.setIconRes( R.drawable.icon_gbt );
                items.add( item );
            }

            {
                ERC20 item = new ERC20( );
                item.setAddress( "0x501c94659d2c00b134a9ba418aa182f14bf72e56" );
                item.setBalance( "50.0" );
                item.setName( "iOTA" );
                item.setPrice( "0$" );
                item.setIconRes( R.drawable.icon_iota );
                items.add( item );
            }

            {
                ERC20 item = new ERC20( );
                item.setAddress( "0x501c94659d2c00b134a9ba418aa182f14bf72e56" );
                item.setBalance( "0.0" );
                item.setName( "OMG" );
                item.setPrice( "0$" );
                item.setIconRes( R.drawable.icon_omg );
                items.add( item );
            }
            planet.setItems( items );

            planetList.add( planet );
        }

        {
            Planet planet = new Planet( );
            planet.setCoinType( CoinType.BTC );
            planet.setName( "Jacob Park" );
            planet.setAddress( "0x43fedf6faf58a666b18f8cccebf0787b29591ede" );

            ArrayList< MainItem > items = new ArrayList<>( );
            planet.setItems( items );

            planetList.add( planet );
        }


        selectedPlanet = planetList.get( 0 );

        viewMapper.listPlanets.setAdapter( planetAdapter = new PlanetAdapter( this, planetList ) );
        viewMapper.listMain.setAdapter( new MainAdapter( this, selectedPlanet.getItems( ) ) );
    }

}
