package com.example.admin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.vaadin.gridutil.cell.GridCellFilter;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer.EditDeleteButtonClickListener;

import com.example.admin.ManageDataPopUp.SaveListener;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;

public class AdminStudienplaetzeView extends VerticalLayout implements View {

	
	private static final long serialVersionUID = 1L;


    private SQLContainer cont = null;
	public AdminStudienplaetzeView() {
		setMargin(true);
		setSpacing(true);
		setSizeFull();
		
		Grid grid = new Grid();
		
        TableQuery tq = new TableQuery("studienplaetze", DatabaseConnector.getPool());
        try {
            cont = new SQLContainer(tq);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        Button btn_addNew = new Button("Neuer Studienplatz");
        btn_addNew.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Object itemId = cont.addItem();
				Item item = cont.getItem(itemId);
				item.getItemProperty("studiengang_id").setValue(0);
				item.getItemProperty("standort_id").setValue(0);
				item.getItemProperty("anzahl").setValue(0);
				item.getItemProperty("note_deutsch").setValue(0d);
				item.getItemProperty("note_englisch").setValue(0d);
				item.getItemProperty("note_mathe").setValue(0d);
				item.getItemProperty("zeugnisschnitt").setValue(0d);
				new ManageDataPopUp(item, new SaveListener() {
					
					@Override
					public void save() {
						try {
							grid.scrollTo(itemId);
							cont.commit();
						} catch (UnsupportedOperationException | SQLException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
        addComponent(btn_addNew);
        
        grid.setContainerDataSource(cont);
        grid.setSizeFull();
        GridCellFilter filter = new GridCellFilter(grid);
        
        filter.setNumberFilter("id");
        //filter.setNumberFilter("firmenprofil_id");
        filter.setNumberFilter("studiengang_id");
        filter.setNumberFilter("standort_id");
        filter.setNumberFilter("anzahl");
        filter.setNumberFilter("note_deutsch");
        filter.setNumberFilter("note_englisch");
        filter.setNumberFilter("note_mathe");
        filter.setNumberFilter("zeugnisschnitt");
        
        grid.setSizeFull();
        grid.getColumn("id").setEditable(false).setRenderer(new EditDeleteButtonValueRenderer(new EditDeleteButtonClickListener() {
			
			@Override
			public void onEdit(RendererClickEvent event) {
				new ManageDataPopUp(cont.getItem(event.getItemId()), new SaveListener() {
					
					@Override
					public void save() {
						try {
							cont.commit();
						} catch (UnsupportedOperationException | SQLException e) {
							e.printStackTrace();
						}
					}
				});
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
							Notification.show("Fehler beim Löschen", "Datensatz kann nicht gelöschen da Verknüpfungen existieren", Type.ERROR_MESSAGE);
							try {
								cont.rollback();
							} catch (UnsupportedOperationException | SQLException e1) {
							}
							cont.refresh();
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
