package io.grabity.planetwallet.Views.p4_Main.Components;

import android.view.View;

import java.util.ArrayList;
import java.util.Objects;

import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.Common.components.ViewComponent;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Views.p4_Main.Adapter.PlanetAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.SlideDrawerLayout;

public class TopLauncherComponent extends ViewComponent {

    private ViewMapper viewMapper;
    private SlideDrawerLayout slideDrawerLayout;

    public TopLauncherComponent( PlanetWalletActivity activity, SlideDrawerLayout slideDrawerLayout ) {
        super( activity );
        this.viewMapper = new ViewMapper( );
        this.slideDrawerLayout = slideDrawerLayout;
        viewInit( );
    }

    public void setTrigger( View trigger ) {
        Objects.requireNonNull( slideDrawerLayout ).setTrigger( SlideDrawerLayout.Position.TOP, trigger );
    }

    public void viewInit( ) {
        viewMapper.listView.setOnItemClickListener( ( AdvanceRecyclerView.OnItemClickListener ) getActivity( ) );
        viewMapper.listView.addFooterView( R.layout.footer_planets );
    }

    public void setPlanetList( ArrayList< Planet > planetList ) {
        viewMapper.listView.setAdapter( new PlanetAdapter( getActivity( ), planetList ) );
    }

    public AdvanceRecyclerView getListView( ) {
        return viewMapper.listView;
    }

    class ViewMapper {

        AdvanceRecyclerView listView;

        ViewMapper( ) {
            listView = findViewById( R.id.list_main_planets_list );
        }
    }

}
