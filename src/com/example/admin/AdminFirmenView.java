package com.example.admin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.vaadin.gridutil.cell.GridCellFilter;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer.EditDeleteButtonClickListener;

import com.example.bewerberportal.BewerberportalUI;
import com.example.bewerberportal.FirmenProfil;
import com.example.bewerberportal.PopupLöschen;
import com.example.bewerberportal.PopupLöschen.DeleteListener;
import com.example.data.DatabaseConnector;
import com.example.data.TableQuery;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;

public class AdminFirmenView extends VerticalLayout implements View {

	
	private static final long serialVersionUID = 1L;


    SQLContainer cont = null;
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
        try {
            cont = new SQLContainer(tq);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        

        Button btn_addNew = new Button("Neues Firmenprofil");
        btn_addNew.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Window wind_user = new Window();
				VerticalLayout vl_user = new VerticalLayout();
				vl_user.setMargin(true);
				vl_user.setSpacing(true);
				wind_user.setContent(vl_user);
				Label lbl_user = new Label("Welche UserId soll mit dem neuen Profil verlinkt werden?");
				vl_user.addComponent(lbl_user);
				TextField txt_user = new TextField();
				txt_user.setConverter(Integer.class);
				txt_user.setValue("1");
				vl_user.addComponent(txt_user);
				
				Button btn_next = new Button("Weiter");
				btn_next.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						if(!txt_user.isValid())
							return;
						wind_user.close();
						Connection con = null;
						Statement statement = null;
						try {
							con = DatabaseConnector.getPool().reserveConnection();
							statement = con.createStatement();
							statement.addBatch("INSERT INTO `go2dhbw`.`firmenprofil` (`name`, `website`) VALUES ('', '');");
							statement.addBatch("INSERT INTO `go2dhbw`.`benutzer_firmenprofil` (`benutzer_id`, `firmenprofil_id`) VALUES ('"+txt_user.getValue()+"', LAST_INSERT_ID());");
							statement.addBatch("COMMIT");
							statement.executeBatch();
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
							DatabaseConnector.getPool().releaseConnection(con);
						}
						Button btn_back = new Button("Zurück");
						btn_back.setIcon(FontAwesome.ARROW_LEFT);
						removeAllComponents();
						addComponent(btn_back);
						btn_back.addClickListener(new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								removeAllComponents();
								cont.refresh();
						        addComponent(btn_addNew);
						        addComponent(grid);
						        setExpandRatio(grid, 1f);
							}
						});
						FirmenProfil prof = new FirmenProfil(txt_user.getValue());
						addComponent(prof);
						setExpandRatio(prof, 1f);
					}
				});
				vl_user.addComponent(btn_next);
				wind_user.center();
				wind_user.setModal(true);
				
				BewerberportalUI.getCurrent().addWindow(wind_user);
				
				
			}
		});
        addComponent(btn_addNew);
        
        
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
				Button btn_back = new Button("Zurück");
				btn_back.setIcon(FontAwesome.ARROW_LEFT);
				removeAllComponents();
				addComponent(btn_back);
				btn_back.addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						removeAllComponents();
						cont.refresh();
				        addComponent(btn_addNew);
				        addComponent(grid);
				        setExpandRatio(grid, 1f);
					}
				});
				Connection con = null;
				Statement statement = null;
				String benID = "";
				try {
					con = DatabaseConnector.getPool().reserveConnection();
					statement = con.createStatement();
					ResultSet rs = statement.executeQuery("SELECT benutzer_id FROM go2dhbw.benutzer_firmenprofil where firmenprofil_id = '"+cont.getItem(event.getItemId()).getItemProperty("id").getValue().toString()+"'");
					if(rs.first())
						benID = rs.getString("benutzer_id");
					statement.executeBatch();
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
					DatabaseConnector.getPool().releaseConnection(con);
				}
				FirmenProfil prof = new FirmenProfil(benID);
				addComponent(prof);
				setExpandRatio(prof, 1f);
			}
			
			@Override
			public void onDelete(RendererClickEvent event) {
				new PopupLöschen(cont.getItem(event.getItemId()).getItemProperty("name").getValue().toString(), new DeleteListener() {
					
					@Override
					public void delete() {
					
						cont.removeItem(event.getItemId());
						
						try {
							cont.commit();
						} catch (UnsupportedOperationException | SQLException e) {
							Notification.show("Fehler beim Löschen", "Datensatz kann nicht gelöscht werden da Verknüpfungen existieren", Type.ERROR_MESSAGE);
							try {
								cont.rollback();
							} catch (UnsupportedOperationException | SQLException e1) {
							}
							cont.refresh();
						}
					}
					
					@Override
					public void close() {}
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
