package com.example.bewerberportal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.vaadin.gridutil.cell.GridCellFilter;

import com.example.data.DatabaseConnector;
import com.example.data.TableQuery;
import com.example.login.CurrentUser;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class StudentenSucheView extends VerticalLayout implements View {

	
	private static final long serialVersionUID = 1L;


	public StudentenSucheView() {
		setMargin(true);
		setSpacing(true);
		setSizeFull();
		
		Grid testgrid = new Grid();
        TableQuery tq_test = new TableQuery("studentensucheview", DatabaseConnector.getPool()){

			private static final long serialVersionUID = 1L;

			public void fetchMetaData() {
                primaryKeyColumns= new ArrayList<String>();     
                primaryKeyColumns.add("id");
                super.fetchMetaData();
            };
        };
        SQLContainer cont_test = null;
        try {
            cont_test = new SQLContainer(tq_test);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        //cont_test.getItem(cont_test.firstItemId()).getItemProperty("note_deutsch").setValue(3.2d);
        try {
			cont_test.commit();
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        GeneratedPropertyContainer cont_gen = new GeneratedPropertyContainer(cont_test);
        cont_gen.addGeneratedProperty("Ort", new PropertyValueGenerator<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Class<String> getType() {
				// TODO Auto-generated method stub
				return String.class;
			}
		});
        GridCellFilter filter = new GridCellFilter(testgrid);
        
        Connection con = null;
        Statement statement = null;
        String firmenprofil_id = null;
        ArrayList<String> studiengang_id_firma = new  ArrayList<String>();
        try {
			con = DatabaseConnector.getPool().reserveConnection();
	        statement = con.createStatement();
	        ResultSet rsFirmenID = statement.executeQuery("SELECT firmenprofil_id from benutzer_firmenprofil where benutzer_id = '"+CurrentUser.get()+"'");
	        rsFirmenID.first();
	        firmenprofil_id = rsFirmenID.getString("firmenprofil_id");
	        ResultSet rsStudiengangID = statement.executeQuery("SELECT studiengang.bezeichnung FROM studienplaetze, standort, studiengang WHERE standort.firmenprofil_id = '"+firmenprofil_id+"' AND standort.id = studienplaetze.standort_id AND studienplaetze.studiengang_id = studiengang.id");
	        rsStudiengangID.first();
	        if(rsStudiengangID.first()){
		        while (! rsStudiengangID.isAfterLast()) {
		        	studiengang_id_firma.add(rsStudiengangID.getString("bezeichnung"));
		        	rsStudiengangID.next();
				}	
	        }        
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

        Like[] filters = new Like[studiengang_id_firma.size()];
		for (int i = 0; i < studiengang_id_firma.size(); i++) {
			String string = studiengang_id_firma.get(i).toString();
			Like filterID = new Like("studiengang", "%"+string+"%", false);
			System.out.println(string);
			filters[i] = filterID;
		}
		Or orfilter = new Or(filters);
		cont_test.addContainerFilter(orfilter);


			
        
        testgrid.setContainerDataSource(cont_gen);
        testgrid.setSizeFull();
        testgrid.removeAllColumns();
        testgrid.addColumn("studiengang");
        TextField studiengangFilter = new TextField();
        ArrayList<Filter> stud_filters = new ArrayList<>();
        studiengangFilter.addTextChangeListener(new TextChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void textChange(TextChangeEvent event) {
				System.out.println(cont_gen.getContainerFilters().size());
				for (Iterator it_filters = stud_filters.iterator(); it_filters.hasNext();) {
					Filter filter = (Filter) it_filters.next();
					cont_gen.removeContainerFilter(filter);
					System.out.println(cont_gen.getContainerFilters().size());
				}
				stud_filters.clear();
				String search_text = event.getText();
				String[] arr_search = search_text.split(",");
				for (int i = 0; i < arr_search.length; i++) {
					String string = arr_search[i];
					Like filter = new Like("studiengang", "%"+string+"%", false);
					System.out.println(string);
					cont_gen.addContainerFilter(filter);
					stud_filters.add(filter);
				}
				
			}
		});
        filter.getFilterRow().getCell("studiengang").setComponent(studiengangFilter);
        //filter.setTextFilter("studiengang", true, true).setInputPrompt("Filter Studiengang");
        testgrid.addColumn("name");
        filter.setTextFilter("name", true, true).setInputPrompt("Filter Name");
        testgrid.addColumn("plz").setHeaderCaption("PLZ");
        filter.setTextFilter("plz", true, true).setInputPrompt("Filter PLZ");
        testgrid.addColumn("Ort");
        filter.setTextFilter("Ort", true, true).setInputPrompt("Filter Ort");
        testgrid.addColumn("zeugnisschnitt");
        FieldGroup group_dist = filter.setNumberFilter("zeugnisschnitt");
        ((TextField)group_dist.getField("smallest")).setInputPrompt("Min");
        ((TextField)group_dist.getField("biggest")).setInputPrompt("Max");
        

        testgrid.setSizeFull();
        addComponent(testgrid);
        testgrid.addSelectionListener(new SelectionListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void select(SelectionEvent event) {
				if(!event.getSelected().isEmpty()) new StudentPopUp(testgrid.getContainerDataSource().getItem(event.getSelected().toArray()[0]));
			}
		});
        
	}
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
