package com.example.admin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.vaadin.gridutil.cell.GridCellFilter;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer.EditDeleteButtonClickListener;

import com.example.bewerberportal.PopupL�schen;
import com.example.bewerberportal.PopupL�schen.DeleteListener;
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

public class AdminBenutzerFirmenprofilView extends VerticalLayout implements View {

	
	private static final long serialVersionUID = 1L;


    private SQLContainer cont = null;
	public AdminBenutzerFirmenprofilView() {
		setMargin(true);
		setSpacing(true);
		setSizeFull();
		
		Grid grid = new Grid();
        TableQuery tq = new TableQuery("benutzer_firmenprofil", DatabaseConnector.getPool()){

			private static final long serialVersionUID = 1L;

			public void fetchMetaData() {
                primaryKeyColumns= new ArrayList<String>();     
                primaryKeyColumns.add("benutzer_id");
                super.fetchMetaData();
            };
        };
        try {
            cont = new SQLContainer(tq);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cont.setAutoCommit(true);

        
        Button btn_addNew = new Button("Neue Verkn�pfung");
        btn_addNew.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Object itemId = cont.addItem();
				Item item = cont.getItem(itemId);
				item.getItemProperty("benutzer_id").setValue(0);
				item.getItemProperty("firmenprofil_id").setValue(0);
				grid.cancelEditor();
				grid.editItem(itemId);
			}
		});
        addComponent(btn_addNew);
        
        grid.setContainerDataSource(cont);
        grid.setSizeFull();
        GridCellFilter filter = new GridCellFilter(grid);
        
        filter.setNumberFilter("benutzer_id");
        filter.setNumberFilter("firmenprofil_id");
        
        grid.setSizeFull();
        grid.setEditorEnabled(true);
        grid.getColumn("benutzer_id").setRenderer(new EditDeleteButtonValueRenderer(new EditDeleteButtonClickListener() {
			
			@Override
			public void onEdit(RendererClickEvent event) {
				grid.cancelEditor();
				grid.editItem(event.getItemId());
			}
			
			@Override
			public void onDelete(RendererClickEvent event) {
				Item item = cont.getItem(event.getItemId());
				new PopupL�schen(item.getItemProperty("benutzer_id").getValue().toString(), new DeleteListener() {
					
					@Override
					public void delete() {
//						Statement statement = null;
//						Connection con = null;
//						try {
//							con = DatabaseConnector.getPool().reserveConnection();
//							statement = con.createStatement();
//							if(account_id.equals("1")){
//								statement.execute("DELETE from bewerberprofil where benutzer_id = "+item.getItemProperty("id").getValue().toString());
//							}else if(account_id.equals("2")){
//								statement.execute("DELETE from benutzer_firmenprofil where benutzer_id = "+item.getItemProperty("id").getValue().toString());
//							}
//							statement.execute("COMMIT");
//						} catch (SQLException e1) {
//							e1.printStackTrace();
//						}finally {
//							try {
//								statement.close();
//							} catch (SQLException e) {
//								e.printStackTrace();
//							}
//							try {
//								con.close();
//							} catch (SQLException e) {
//								e.printStackTrace();
//							}
//						}
						
						
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
