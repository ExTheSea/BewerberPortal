package com.example.bewerberportal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.vaadin.gridutil.cell.GridCellFilter;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer.EditDeleteButtonClickListener;

import com.example.data.DatabaseConnector;
import com.example.data.TableQuery;
import com.example.login.CurrentUser;
import com.vaadin.data.Item;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;

public class StellenangebotView extends VerticalLayout implements View {

	
	private static final long serialVersionUID = 1L;

	
    private SQLContainer cont = null;
	public StellenangebotView() {
		setMargin(true);
		setSpacing(true);
		setSizeFull();
		Button anlegen = new Button("Studienplatz anlegen");
		anlegen.setIcon(FontAwesome.PLUS);
		anlegen.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				new StellenangebotPopUp();
			}
		});
		addComponent(anlegen);
		Grid grid = new Grid();
        TableQuery tq = new TableQuery("firmensucheview", DatabaseConnector.getPool()){

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
        
        Connection con = null;
        Statement statement = null;
        String firmenprofil_id = null;
        try {
			con = DatabaseConnector.getPool().reserveConnection();
	        statement = con.createStatement();
	        ResultSet rs = statement.executeQuery("SELECT firmenprofil_id from benutzer_firmenprofil where benutzer_id = '"+CurrentUser.get()+"'");
	        rs.first();
	        firmenprofil_id = rs.getString("firmenprofil_id");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}finally {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        
        
        CurrentUser.get();
        
        GeneratedPropertyContainer cont_gen = new GeneratedPropertyContainer(cont);
        cont_gen.addGeneratedProperty("Bearbeiten", new PropertyValueGenerator<Integer>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Integer getValue(Item item, Object itemId, Object propertyId) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Class<Integer> getType() {
				// TODO Auto-generated method stub
				return Integer.class;
			}
		});
        
        cont_gen.addContainerFilter(new Like("firmenprofil_id", firmenprofil_id, true));
        grid.setContainerDataSource(cont_gen);
        grid.setSizeFull();
        
        grid.removeAllColumns();
        grid.addColumn("Bearbeiten");
        grid.addColumn("alias");
        grid.addColumn("ort");
        grid.addColumn("Bezeichnung");
        grid.addColumn("anzahl");
        
        GridCellFilter filter = new GridCellFilter(grid);

        filter.setTextFilter("alias", true, true);
        filter.setTextFilter("ort", true, true);
        filter.setTextFilter("Bezeichnung", true, true);
        
        grid.setSizeFull();
        grid.getColumn("Bearbeiten").setRenderer(new EditDeleteButtonValueRenderer(new EditDeleteButtonClickListener() {
			
			@Override
			public void onEdit(RendererClickEvent event) {
				new StellenangebotPopUp(cont_gen.getItem(event.getItemId()));
			}
			
			@Override
			public void onDelete(RendererClickEvent event) {
		        Connection con_delete = null;
		        Statement statement_delete = null;
		        try {
		        	con_delete = DatabaseConnector.getPool().reserveConnection();
		        	statement_delete = con_delete.createStatement();
		        	int rs = statement_delete.executeUpdate("DELETE FROM studienplaetze WHERE id = '"+cont_gen.getItem(event.getItemId()).getItemProperty("id").getValue().toString()+"'");
			        con_delete.commit();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}finally {
					try {
						statement_delete.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					try {
						con_delete.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
		        cont.refresh();
				
			}
		})).setWidth(140);
       
        addComponent(grid);
        setExpandRatio(grid, 1f);

	}
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
