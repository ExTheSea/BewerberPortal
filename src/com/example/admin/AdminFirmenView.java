package com.example.admin;

import java.sql.SQLException;
import java.util.ArrayList;

import org.vaadin.gridutil.cell.GridCellFilter;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer.EditDeleteButtonClickListener;

import com.example.data.DatabaseConnector;
import com.example.data.TableQuery;
import com.vaadin.data.Item;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;

public class AdminFirmenView extends VerticalLayout implements View {

	
	private static final long serialVersionUID = 1L;


	public AdminFirmenView() {
		setMargin(true);
		setSpacing(true);
		setSizeFull();
		
		Grid grid = new Grid();
        TableQuery tq = new TableQuery("firmenprofil", DatabaseConnector.getPool()){

			private static final long serialVersionUID = 1L;

			public void fetchMetaData() {
                primaryKeyColumns= new ArrayList<String>();     
                primaryKeyColumns.add("id");
                super.fetchMetaData();
            };
        };
        SQLContainer cont = null;
        try {
            cont = new SQLContainer(tq);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        grid.setContainerDataSource(cont);
        grid.setSizeFull();
        GridCellFilter filter = new GridCellFilter(grid);
        filter.setTextFilter("name", true, true);
        filter.setTextFilter("website", true, true);
        
        
        grid.setSizeFull();
        grid.removeColumn("logo");
        grid.getColumn("id").setHeaderCaption("ID");
        grid.getColumn("id").setRenderer(new EditDeleteButtonValueRenderer(new EditDeleteButtonClickListener() {
			
			@Override
			public void onEdit(RendererClickEvent event) {
				// TODO Auto-generated method stub
				
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
