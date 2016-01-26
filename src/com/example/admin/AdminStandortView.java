package com.example.admin;

import java.sql.SQLException;

import org.vaadin.gridutil.cell.GridCellFilter;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer.EditDeleteButtonClickListener;

import com.example.bewerberportal.PopupLöschen;
import com.example.bewerberportal.PopupLöschen.DeleteListener;
import com.example.data.DatabaseConnector;
import com.example.data.TableQuery;
import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;

public class AdminStandortView extends VerticalLayout implements View {

	
	private static final long serialVersionUID = 1L;


    private SQLContainer cont = null;
	public AdminStandortView() {
		setMargin(true);
		setSpacing(true);
		setSizeFull();
		
		Grid grid = new Grid();
		
		/**
		 * {
			
			@Override
			public void editItem(Object itemId) throws IllegalStateException, IllegalArgumentException {
				super.editItem(itemId);
			}
			
			@Override
			public void saveEditor() throws CommitException {
				super.saveEditor();
				try {
					cont.commit();
				} catch (UnsupportedOperationException | SQLException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void cancelEditor() {
				super.cancelEditor();
			}
		}
		 */
        TableQuery tq = new TableQuery("standort", DatabaseConnector.getPool());
        try {
            cont = new SQLContainer(tq);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cont.setAutoCommit(true);
        
        
        Button btn_addNew = new Button("Neuer Standort");
        btn_addNew.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Object itemId = cont.addItem();
				Item item = cont.getItem(cont.lastItemId());
				item.getItemProperty("alias").setValue("");
				item.getItemProperty("strasse").setValue("");
				item.getItemProperty("ort").setValue("");
				item.getItemProperty("plz").setValue("");
				grid.cancelEditor();
				grid.editItem(cont.lastItemId());
			}
		});
        addComponent(btn_addNew);
        
        grid.setContainerDataSource(cont);
        grid.setSizeFull();
        GridCellFilter filter = new GridCellFilter(grid);
        
        filter.setNumberFilter("id");
        filter.setNumberFilter("ansprechpartner_id");
        filter.setTextFilter("alias", true, true);
        filter.setTextFilter("strasse", true, true);
        filter.setTextFilter("ort", true, true);
        filter.setTextFilter("plz", true, true);
        
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
