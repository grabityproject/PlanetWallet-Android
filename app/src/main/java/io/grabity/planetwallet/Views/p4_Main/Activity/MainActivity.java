


package io.grabity.planetwallet.Views.p4_Main.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Coin;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Views.p4_Main.Adapter.CoinAdapter;
import io.grabity.planetwallet.Views.p4_Main.Adapter.PlanetsAdapter;
import io.grabity.planetwallet.Views.p4_Main.Etc.ViewController;
import io.grabity.planetwallet.Views.p5_Token.Activity.TokenAddActivity;
import io.grabity.planetwallet.Views.p7_Setting.Activity.SettingActivity;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.BarcodeView;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.RippleEffectView;
import io.grabity.planetwallet.Widgets.ShadowView;
import io.grabity.planetwallet.Widgets.SlideDrawerLayout;
import io.grabity.planetwallet.Widgets.StretchImageView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class MainActivity extends PlanetWalletActivity implements AdvanceArrayAdapter.OnAttachViewListener, ToolBar.OnToolBarClickListener, RippleEffectView.OnRippleEffectListener, AdvanceRecyclerView.OnItemClickListener, AdvanceRecyclerView.OnScrollListener {

    private ViewMapper viewMapper;
    private HeaderViewMapper headerViewMapper;
    private FooterViewMapper footerViewMapper;

    private ArrayList< Coin > items;
    private CoinAdapter coinAdapter;

    private ViewController viewController;

    private ArrayList< Planet > itemss;
    private PlanetsAdapter planetsAdapter;

    //eth -> false;
    private Boolean coin = false;


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        ( ( ViewGroup ) findViewById( android.R.id.content ) ).addView( viewMapper.rippleView );

        viewController = new ViewController( this, viewMapper );
        viewMapper.rippleView.setOnRippleEffectListener( this );

        viewMapper.listView.setOnAttachViewListener( this );
        viewMapper.listView.addHeaderView( R.layout.header_main );
        viewMapper.listView.addFooterView( R.layout.footer_main );
        viewMapper.listView.addOnScrollListener( this );

        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_MENU ) );
        viewMapper.toolBar.setRightButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_MUTIUNIVERSE ) );
        viewMapper.toolBar.setTitle( "ETH" );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.rippleView.setTrigger( viewMapper.toolBar.getButtonItems( ).get( 0 ).getView( ) );
        viewMapper.slideDrawer.setTrigger( SlideDrawerLayout.Position.TOP, viewMapper.toolBar.getButtonItems( ).get( 1 ).getView( ) );


        viewMapper.listView.setOnItemClickListener( this );
        viewMapper.planetslistView.setOnItemClickListener( this );

        viewMapper.btnCopy.setOnClickListener( this );
        viewMapper.btnSend.setOnClickListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );

        /**
         * Test main item list
         */

        PLog.e( " setData " );

        if ( items == null ) {

            items = new ArrayList<>( );
            //ETH
            items.add( new Coin( "ETH", R.drawable.icon_eth, "12.023", "ETH", "1111 USD" ) );
            items.add( new Coin( "ETH", R.drawable.icon_gbt, "805.023", "GBT", "2222 USD" ) );
            items.add( new Coin( "ETH", R.drawable.icon_iota, "2.023", "IOTA", "3333 USD" ) );
            items.add( new Coin( "ETH", R.drawable.icon_omg, "32.023", "OMG", "4444 USD" ) );
            items.add( new Coin( "ETH", R.drawable.icon_eth, "12.023", "ETH", "1111 USD" ) );
            items.add( new Coin( "ETH", R.drawable.icon_gbt, "805.023", "GBT", "2222 USD" ) );
            items.add( new Coin( "ETH", R.drawable.icon_iota, "2.023", "IOTA", "3333 USD" ) );
            items.add( new Coin( "ETH", R.drawable.icon_omg, "32.023", "OMG", "4444 USD" ) );

        }


        //BTC
//            items.add( new Coin( "BTC", "0.21352", "choi3950", "April 04, 11:23", R.drawable.image_btc_increase ) );
//            items.add( new Coin( "BTC", "1.65", "choi3950", "April 04, 20:23", R.drawable.image_btc_increase ) );
//            items.add( new Coin( "BTC", "0.422", "choi3950", "April 04, 09:18", R.drawable.image_btc_discrease ) );
//            items.add( new Coin( "BTC", "0.21352", "choi3950", "April 04, 11:23", R.drawable.image_btc_increase ) );
//            items.add( new Coin( "BTC", "1.65", "choi3950", "April 04, 20:23", R.drawable.image_btc_increase ) );
//            items.add( new Coin( "BTC", "0.422", "choi3950", "April 04, 09:18", R.drawable.image_btc_discrease ) );

        coinAdapter = new CoinAdapter( this, items );
        viewMapper.listView.setAdapter( coinAdapter );

        if ( itemss == null ) {


            itemss = new ArrayList<>( );
            itemss.add( new Planet( "", "ETH", "choi111" ) );
            itemss.add( new Planet( "2323434", "ETH", "choi222" ) );
            itemss.add( new Planet( "adwd124", "BTC", "choi333" ) );
            itemss.add( new Planet( "", "ETH", "choi111" ) );
            itemss.add( new Planet( "2323434", "ETH", "choi222" ) );
            itemss.add( new Planet( "adwd124", "BTC", "choi333" ) );

        }

        planetsAdapter = new PlanetsAdapter( this, itemss );

        viewMapper.planetslistView.setAdapter( planetsAdapter );
        viewMapper.planetsName.setText( "choi3950 Planet" );


        viewMapper.barcodeView.setData( "0x2133498349813afbrtdfetsff" );
        viewMapper.coinBalance.setText( "1.245" );
        viewMapper.coinName.setText( "BTC" );

    }

    @Override
    public void onAttachView( int resId, int position, View view ) {

        PLog.e( " onAttachView " );

        if ( resId == R.layout.header_main && position == 0 ) {
            headerViewMapper = new HeaderViewMapper( view );
            headerViewMapper.planetView.setData( "가즈아" );
            viewMapper.barcodeView.setPlanetView( headerViewMapper.planetView );
            if ( viewController != null )
                viewController.setHeaderViewMapper( headerViewMapper );
        } else if ( resId == R.layout.footer_main ) {
            footerViewMapper = new FooterViewMapper( view );
            footerViewMapper.btnAddToken.setOnClickListener( this );

            footerViewMapper.groupAddToken.setVisibility( !coin ? View.VISIBLE : View.GONE );
            footerViewMapper.groupMessage.setVisibility( items.size() == 0 ? View.VISIBLE : View.GONE );


        }
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        //barcode Test // notice Test
        if ( v == viewMapper.btnCopy ) {
            viewMapper.barcodeView.setData( "0x2133498349813afbrtdfetsff" );
        } else if ( v == viewMapper.btnSend ) {
            viewMapper.barcodeView.setData( "0x2234341133498349813afbrtdfefgtsff" );
        } else if ( v == footerViewMapper.btnAddToken ) {
            setTransition( Transition.SLIDE_SIDE );
            sendAction( C.requestCode.MAIN_TOKEN_ADD, TokenAddActivity.class );
        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.MAIN_TOKEN_ADD && resultCode == RESULT_OK ) {
            Toast.makeText( this, "토큰추가확인", Toast.LENGTH_SHORT ).show( );
        }
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {

        if ( Utils.equals( tag, C.tag.TOOLBAR_MENU ) ) {
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
            super.onBackPressed( );
        }

    }

    @Override
    protected void onResume( ) {
        super.onResume( );
        viewMapper.rippleView.ripple( false );

        if ( !getPlanetWalletApplication( ).getCurrentTheme( ) ) {
            viewMapper.shadowView.setShadowColor( Color.parseColor( "#000000" ), Color.parseColor( "#7A000000" ) );
        } else {
            viewMapper.shadowView.setShadowColor( Color.parseColor( "#FFFFFF" ), Color.parseColor( "#7AFFFFFF" ) );
        }
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
        if ( recyclerView == viewMapper.listView ) {
            PLog.e( "메인리스트뷰 클릭 : " + position );
        } else if ( recyclerView == viewMapper.planetslistView ) {

            viewMapper.slideDrawer.close( );

            //Test
            if ( itemss.get( position ).getCurrency( ).equals( "ETH" ) ) {
                viewMapper.toolBar.setTitle( "ETH" );
                items = new ArrayList<>( );
                items.add( new Coin( "ETH", R.drawable.icon_eth, "12.023", "ETH", "1111 USD" ) );
                items.add( new Coin( "ETH", R.drawable.icon_gbt, "805.023", "GBT", "2222 USD" ) );
                items.add( new Coin( "ETH", R.drawable.icon_iota, "2.023", "IOTA", "3333 USD" ) );
                items.add( new Coin( "ETH", R.drawable.icon_omg, "32.023", "OMG", "4444 USD" ) );
                items.add( new Coin( "ETH", R.drawable.icon_eth, "12.023", "ETH", "1111 USD" ) );
                items.add( new Coin( "ETH", R.drawable.icon_gbt, "805.023", "GBT", "2222 USD" ) );
                items.add( new Coin( "ETH", R.drawable.icon_iota, "2.023", "IOTA", "3333 USD" ) );
                items.add( new Coin( "ETH", R.drawable.icon_omg, "32.023", "OMG", "4444 USD" ) );

                coin = false;

            } else if ( itemss.get( position ).getCurrency( ).equals( "BTC" ) ) {
                viewMapper.toolBar.setTitle( "BTC" );
                items = new ArrayList<>( );


                if ( itemss.size( ) -1 != position ){
                    items.add( new Coin( "BTC", "0.21352", "choi3950", "April 04, 11:23", R.drawable.image_btc_increase ) );
                    items.add( new Coin( "BTC", "1.65", "choi3950", "April 04, 20:23", R.drawable.image_btc_increase ) );
                    items.add( new Coin( "BTC", "0.422", "choi3950", "April 04, 09:18", R.drawable.image_btc_discrease ) );
                    items.add( new Coin( "BTC", "0.21352", "choi3950", "April 04, 11:23", R.drawable.image_btc_increase ) );
                    items.add( new Coin( "BTC", "1.65", "choi3950", "April 04, 20:23", R.drawable.image_btc_increase ) );
                    items.add( new Coin( "BTC", "0.422", "choi3950", "April 04, 09:18", R.drawable.image_btc_discrease ) );
                }

                coin = true;

            }
            setData( );


        }
    }


    @Override
    protected void onUpdateTheme( boolean theme ) {
        super.onUpdateTheme( theme );
        if ( !theme ) {
            viewMapper.shadowView.setShadowColor( Color.parseColor( "#000000" ), Color.parseColor( "#7A000000" ) );
        } else {
            viewMapper.shadowView.setShadowColor( Color.parseColor( "#FFFFFF" ), Color.parseColor( "#7AFFFFFF" ) );
        }
    }

    @Override
    public void onScrolled( RecyclerView recyclerView, int dx, int dy, float scrollX, float scrollY ) {

        if ( scrollY > 0 ) {
            viewMapper.groupShadow.setY( -scrollY );
        } else {

            viewMapper.groupShadow.setScaleX( 1.0f + ( -scrollY * 0.001f ) );
            viewMapper.groupShadow.setScaleY( 1.0f + ( -scrollY * 0.001f ) );

        }


    }

    public class ViewMapper {

        public ToolBar toolBar;
        public RippleEffectView rippleView;

        public SlideDrawerLayout slideDrawer;

        public AdvanceRecyclerView listView;

        public TextView textNotice;
        public View viewTrigger;
        public View groupBlur;
        public StretchImageView blurView;
        public View groupBottom;

        AdvanceRecyclerView planetslistView;
        TextView planetsName;

        View btnCopy;
        View btnSend;
        BarcodeView barcodeView;

        TextView coinBalance;
        TextView coinName;

        View groupShadow;

        ShadowView shadowView;

        public ViewMapper( ) {

            toolBar = findViewById( R.id.toolBar );
            rippleView = new RippleEffectView( MainActivity.this );

            slideDrawer = findViewById( R.id.slideDrawer );

            listView = findViewById( R.id.list_main );

            textNotice = findViewById( R.id.text_main_notice );
            viewTrigger = findViewById( R.id.view_main_bottom_trigger );
            groupBlur = findViewById( R.id.group_main_blur );
            blurView = findViewById( R.id.image_main_blur );
            groupBottom = findViewById( R.id.group_main_bottom );

            planetslistView = findViewById( R.id.list_main_planets_list );
            planetsName = findViewById( R.id.text_main_planets_name );

            btnCopy = findViewById( R.id.btn_main_bottom_copy );
            btnSend = findViewById( R.id.btn_main_bottom_send );
            barcodeView = findViewById( R.id.barcode_main_bottom_barcodeview );

            coinBalance = findViewById( R.id.text_main_bottom_balance );
            coinName = findViewById( R.id.text_main_bottom_coin_name );

            groupShadow = findViewById( R.id.group_main_shadow );
            shadowView = findViewById( R.id.shadow_main_shadow_view );

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
        View btnAddToken;

        ViewGroup groupAddToken;
        ViewGroup groupMessage;

        public FooterViewMapper( View footerView ) {
            this.footerView = footerView;
            btnAddToken = footerView.findViewById( R.id.btn_footer_main_manage_token );

            groupAddToken = footerView.findViewById( R.id.group_footer_main_manage_token );
            groupMessage = footerView.findViewById( R.id.group_footer_main_bit_message );
        }
    }
}
