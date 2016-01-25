package com.example.bewerberportal;

import java.sql.SQLException;
import java.util.ArrayList;

import org.vaadin.gridutil.cell.GridCellFilter;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer.EditDeleteButtonClickListener;

import com.example.data.DatabaseConnector;
import com.example.data.TableQuery;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;

public class AdminBewerberView extends VerticalLayout implements View {

	
	private static final long serialVersionUID = 1L;


    private SQLContainer cont = null;
	public AdminBewerberView() {
		setMargin(true);
		setSpacing(true);
		setSizeFull();
		
		Grid grid = new Grid();
        TableQuery tq = new TableQuery("bewerberprofil", DatabaseConnector.getPool()){

			private static final long serialVersionUID = 1L;

			public void fetchMetaData() {
                primaryKeyColumns= new ArrayList<String>();     
                primaryKeyColumns.add("id");
                super.fetchMetaData();
            };
        };
        try {
            cont = new SQLContainer(tq);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        grid.setContainerDataSource(cont);
        grid.setSizeFull();
        GridCellFilter filter = new GridCellFilter(grid);
        filter.setTextFilter("name", true, true);
        
        filter.setNumberFilter("id");
        filter.setNumberFilter("benutzer_id");
        filter.setNumberFilter("geburtsjahr");
        filter.setTextFilter("telefonnummer", true, true);
        filter.setTextFilter("hobbies", true, true);
        filter.setTextFilter("zusatzqualifikationen", true, true);
        filter.setTextFilter("plz", true, true);
        filter.setNumberFilter("note_deutsch");
        filter.setNumberFilter("note_englisch");
        filter.setNumberFilter("note_mathe");
        filter.setNumberFilter("zeugnisschnitt");
        
        grid.setSizeFull();
//        grid.removeColumn("logo");
        grid.getColumn("id").setHeaderCaption("ID");
        grid.getColumn("id").setRenderer(new EditDeleteButtonValueRenderer(new EditDeleteButtonClickListener() {
			
			@Override
			public void onEdit(RendererClickEvent event) {
				Window wind = new Window();
				wind.center();
				wind.setModal(true);
				wind.setSizeFull();
				wind.setContent(new BewerberProfil(cont.getItem(event.getItemId()).getItemProperty("benutzer_id").getValue().toString()));
				BewerberportalUI.getCurrent().addWindow(wind);
				wind.addCloseListener(new CloseListener() {
					
					@Override
					public void windowClose(CloseEvent e) {
						cont.refresh();
					}
				});
			}
			
			@Override
			public void onDelete(RendererClickEvent event) {
				// TODO Auto-generated method stub
				
			}
		})).setWidth(140);
       
        addComponent(grid);

	}
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
