package com.example.admin;

import java.sql.SQLException;
import java.util.ArrayList;

import org.vaadin.gridutil.cell.GridCellFilter;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer.EditDeleteButtonClickListener;

import com.example.bewerberportal.BewerberProfil;
import com.example.bewerberportal.BewerberportalUI;
import com.example.bewerberportal.PopupLöschen;
import com.example.bewerberportal.PopupLöschen.DeleteListener;
import com.example.data.DatabaseConnector;
import com.example.data.TableQuery;
import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
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
        

        Button btn_addNew = new Button("Neues Bewerberprofil");
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
						BewerberProfil prof = new BewerberProfil(txt_user.getValue());
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
				BewerberProfil prof = new BewerberProfil(cont.getItem(event.getItemId()).getItemProperty("benutzer_id").getValue().toString());
				addComponent(prof);
				setExpandRatio(prof, 1f);
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
