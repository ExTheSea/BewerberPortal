package com.example.admin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.vaadin.gridutil.cell.GridCellFilter;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer.EditDeleteButtonClickListener;

import com.example.bewerberportal.PopupLöschen;
import com.example.bewerberportal.PopupLöschen.DeleteListener;
import com.example.data.DatabaseConnector;
import com.example.data.TableQuery;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;

public class AdminAnsprechpartnerView extends VerticalLayout implements View {

	
	private static final long serialVersionUID = 1L;


    private SQLContainer cont = null;
	public AdminAnsprechpartnerView() {
		setMargin(true);
		setSpacing(true);
		setSizeFull();
		
		Grid grid = new Grid();
		
        TableQuery tq = new TableQuery("ansprechpartner", DatabaseConnector.getPool());
        try {
            cont = new SQLContainer(tq);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cont.setAutoCommit(true);
        
        
        Button btn_addNew = new Button("Neuer Ansprechpartner");
        btn_addNew.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Object itemId = cont.addItem();
				Item item = cont.getItem(cont.lastItemId());
				item.getItemProperty("name").setValue("");
				item.getItemProperty("email").setValue("");
				item.getItemProperty("telefonnummer").setValue("");
				grid.cancelEditor();
				grid.editItem(cont.lastItemId());
			}
		});
        addComponent(btn_addNew);
        
        grid.setContainerDataSource(cont);
        grid.setSizeFull();
        GridCellFilter filter = new GridCellFilter(grid);
        
        filter.setNumberFilter("id");
        filter.setTextFilter("name", true, true);
        filter.setTextFilter("email", true, true);
        filter.setTextFilter("telefonnummer", true, true);
        
        grid.setSizeFull();
        grid.setEditorEnabled(true);
        grid.getColumn("id").setEditable(false).setRenderer(new EditDeleteButtonValueRenderer(new EditDeleteButtonClickListener() {
			
			@Override
			public void onEdit(RendererClickEvent event) {
				grid.cancelEditor();
				grid.editItem(event.getItemId());
			}
			
			@Override
			public void onDelete(RendererClickEvent event) {
				Item item = cont.getItem(event.getItemId());
				new PopupLöschen(item.getItemProperty("id").getValue().toString(), new DeleteListener() {
					
					@Override
					public void delete() {
						cont.removeItem(event.getItemId());
						try {
							cont.commit();
						} catch (UnsupportedOperationException | SQLException e) {
							e.printStackTrace();
						}
					}
					
					@Override
					public void close() {
						
					}
				});
			}
		})).setWidth(140);
       
        addComponent(grid);
        setExpandRatio(grid, 1f);
	}
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
