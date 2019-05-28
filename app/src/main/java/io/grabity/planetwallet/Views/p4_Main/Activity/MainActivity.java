package io.grabity.planetwallet.Views.p4_Main.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Views.p4_Main.Adapter.PlanetsAdapter;
import io.grabity.planetwallet.Views.p4_Main.Adapter.TestAdapter;
import io.grabity.planetwallet.Views.p4_Main.Etc.ViewController;
import io.grabity.planetwallet.Views.p7_Setting.Activity.SettingActivity;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.BarcodeView;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.RippleEffectView;
import io.grabity.planetwallet.Widgets.SlideDrawerLayout;
import io.grabity.planetwallet.Widgets.StretchImageView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class MainActivity extends PlanetWalletActivity implements AdvanceArrayAdapter.OnAttachViewListener, ToolBar.OnToolBarClickListener, RippleEffectView.OnRippleEffectListener, AdvanceRecyclerView.OnItemClickListener {

    private ViewMapper viewMapper;

    private ArrayList< String > items;
    private TestAdapter adapter;
    private ViewController viewController;

    private ArrayList< Planet > itemss;
    private PlanetsAdapter planetsAdapter;


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

        viewMapper.toolBar.addLeftButton( new ToolBar.ButtonItem( R.drawable.image_toolbar_planetmenu_gray ).setTag( C.tag.TOOLBAR_MENU ) );
        viewMapper.toolBar.addRightButton( new ToolBar.ButtonItem( R.drawable.image_toolbar_mutiuniverse_gray ).setTag( C.tag.TOOLBAR_MUTIUNIVERSE ) );
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
         * Test
         */
        items = new ArrayList<>( );
        items.add( "1" );
        items.add( "2" );
        items.add( "3" );
        items.add( "4" );
        items.add( "5" );
        items.add( "6" );
        items.add( "7" );
        items.add( "8" );
        items.add( "9" );

        viewMapper.listView.setAdapter( adapter = new TestAdapter( this, items ) );

        itemss = new ArrayList<>( );
        itemss.add( new Planet( "", "ETH", "choi111" ) );
        itemss.add( new Planet( "2323434", "ETH", "choi222" ) );
        itemss.add( new Planet( "adwd124", "BTC", "choi333" ) );
        itemss.add( new Planet( "", "ETH", "choi111" ) );
        itemss.add( new Planet( "2323434", "ETH", "choi222" ) );
        itemss.add( new Planet( "adwd124", "BTC", "choi333" ) );

        planetsAdapter = new PlanetsAdapter( this, itemss );

        viewMapper.planetslistView.setAdapter( planetsAdapter );
        viewMapper.planetsName.setText( "choi3950 Planet" );


        viewMapper.barcodeView.setData( "0x2133498349813afbrtdfetsff" );
        viewMapper.coinBalance.setText( "1.245" );
        viewMapper.coinName.setText( "BTC" );

    }

    @Override
    public void onAttachView( int resId, int position, View view ) {
        if ( resId == R.layout.header_main && position == 0 ) {
            PlanetView planetView = view.findViewById( R.id.icon_planet );
            planetView.setData( "가즈아" );
        }
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        //barcode Test
        if ( v == viewMapper.btnCopy ) {
            viewMapper.barcodeView.setData( "0x2133498349813afbrtdfetsff" );
        } else if ( v == viewMapper.btnSend ) {
            viewMapper.barcodeView.setData( "0x2234341133498349813afbrtdfefgtsff" );
        }
    }

    @Override
    public void onToolBarClick( Object tag, View view, int direction, int index ) {

        if ( Utils.equals( tag, C.tag.TOOLBAR_MENU ) ) {
            viewMapper.rippleView.ripple( true );
        } else if ( Utils.equals( tag, C.tag.TOOLBAR_MUTIUNIVERSE ) ) {
            viewMapper.slideDrawer.open( SlideDrawerLayout.Position.TOP );
        }
    }

    @Override
    public void onBackPressed( ) {

        if ( viewMapper.slideDrawer.isOpen( ) && viewMapper.slideDrawer.getIsOpenPosition( ) == 0 ) {
            viewMapper.slideDrawer.close( SlideDrawerLayout.Position.TOP );
        } else if ( viewMapper.slideDrawer.isOpen( ) && viewMapper.slideDrawer.getIsOpenPosition( ) == 3 ) {
            viewMapper.slideDrawer.close( SlideDrawerLayout.Position.BOTTOM );
        } else {
            super.onBackPressed( );
        }

    }

    @Override
    protected void onResume( ) {
        super.onResume( );
        viewMapper.rippleView.ripple( false );
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
            PLog.e( "플레닛츠리스트뷰 클릭 : " + position );
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


        }
    }
}
