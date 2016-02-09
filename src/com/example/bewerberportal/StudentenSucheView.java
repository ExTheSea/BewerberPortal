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
import com.vaadin.data.util.filter.Between;
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
import com.vaadin.ui.themes.ValoTheme;

public class StudentenSucheView extends VerticalLayout implements View {

	
	private static final long serialVersionUID = 1L;
	//Definieren der Felder für Konstruktor und Enter
	SQLContainer cont_test = null;
	Or orfilter = null;
	Like filterAlles = null;
	public StudentenSucheView() {
		setMargin(true);
		setSpacing(true);
		setSizeFull();
		//Abrufen des Studentensucheview
		Grid testgrid = new Grid();
        TableQuery tq_test = new TableQuery("studentensucheview", DatabaseConnector.getPool()){

			private static final long serialVersionUID = 1L;

			public void fetchMetaData() {
                primaryKeyColumns= new ArrayList<String>();     
                primaryKeyColumns.add("id");
                super.fetchMetaData();
            };
        };
        try {
            cont_test = new SQLContainer(tq_test);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        try {
			cont_test.commit();
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        GridCellFilter filter = new GridCellFilter(testgrid);
        //Abrufen der Studiengänge, die die Firma anbietet und der firmenprofil id
        Connection con = null;
        Statement statement = null;
        String firmenprofil_id = null;
        ArrayList<String> studiengang_firma = new  ArrayList<String>();
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
		        	studiengang_firma.add(rsStudiengangID.getString("bezeichnung"));
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
			try {
				DatabaseConnector.getPool().releaseConnection(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        //Matching auf angebotene Studiengänge
        if(!studiengang_firma.isEmpty()){
        	if(filterAlles!=null)cont_test.removeContainerFilter(filterAlles);
        	if(orfilter!=null)cont_test.removeContainerFilter(orfilter);
        	Like[] filters = new Like[studiengang_firma.size()];
    		for (int i = 0; i < studiengang_firma.size(); i++) {
    			String string = studiengang_firma.get(i).toString();
    			Like filterID = new Like("studiengang", "%"+string+"%", false);
    			filters[i] = filterID;
    		}
    		orfilter = new Or(filters);
    		cont_test.addContainerFilter(orfilter);
        }
        else{
        	filterAlles = new Like("studiengang", "", true);
        	cont_test.addContainerFilter(filterAlles);
        }
        //Entfernen unnötiger Spalten und erstellen des Studiengang Filters
        testgrid.setContainerDataSource(cont_test);
        testgrid.setSizeFull();
        testgrid.removeAllColumns();
        testgrid.addColumn("studiengang");
        TextField studiengangFilter = new TextField();
        studiengangFilter.setStyleName(ValoTheme.TEXTFIELD_TINY);
        studiengangFilter.setWidth("100%");
        ArrayList<Filter> stud_filters = new ArrayList<>();
        studiengangFilter.addTextChangeListener(new TextChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void textChange(TextChangeEvent event) {
				for (Iterator it_filters = stud_filters.iterator(); it_filters.hasNext();) {
					Filter filter = (Filter) it_filters.next();
					cont_test.removeContainerFilter(filter);
				}
				stud_filters.clear();
				String search_text = event.getText();
				String[] arr_search = search_text.split(",");
				for (int i = 0; i < arr_search.length; i++) {
					String string = arr_search[i];
					Like filter = new Like("studiengang", "%"+string+"%", false);
					cont_test.addContainerFilter(filter);
					stud_filters.add(filter);
				}
				
			}
		});
        

        //Hinzufügen der Spalten
        studiengangFilter.setInputPrompt("Filter Studiengang, Studiengang");
        filter.getFilterRow().getCell("studiengang").setComponent(studiengangFilter);
        testgrid.addColumn("name");
        filter.setTextFilter("name", true, true).setInputPrompt("Filter Name");
        testgrid.addColumn("plz").setHeaderCaption("PLZ");
        filter.setTextFilter("plz", true, true).setInputPrompt("Filter PLZ");
        testgrid.addColumn("zeugnisschnitt");
        FieldGroup group_dist = filter.setNumberFilter("zeugnisschnitt");
        ((TextField)group_dist.getField("smallest")).setInputPrompt("Min");
        ((TextField)group_dist.getField("biggest")).setInputPrompt("Max");
        
        testgrid.sort("zeugnisschnitt");

        testgrid.setSizeFull();
        addComponent(testgrid);
        //Aufruf der Auswahl
        testgrid.addSelectionListener(new SelectionListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void select(SelectionEvent event) {
				if(!event.getSelected().isEmpty()) new StudentPopUp(testgrid.getContainerDataSource().getItem(event.getSelected().toArray()[0]));
			}
		});
        
	}
	
	//Neu Aufruf der Seite
	@Override
	public void enter(ViewChangeEvent event) {
		cont_test.refresh();
        Connection con = null;
        Statement statement = null;
        String firmenprofil_id = null;
        ArrayList<String> studiengang_firma = new  ArrayList<String>();
        //Abrufen der Angebotenen Studiengänge
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
		        	studiengang_firma.add(rsStudiengangID.getString("bezeichnung"));
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
			try {
				DatabaseConnector.getPool().releaseConnection(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        //Matching entfernen und setzen
        if(!studiengang_firma.isEmpty()){
        	if(filterAlles!=null)cont_test.removeContainerFilter(filterAlles);
        	if(orfilter!=null)cont_test.removeContainerFilter(orfilter);
        	Like[] filters = new Like[studiengang_firma.size()];
    		for (int i = 0; i < studiengang_firma.size(); i++) {
    			String string = studiengang_firma.get(i).toString();
    			Like filterID = new Like("studiengang", "%"+string+"%", false);
    			filters[i] = filterID;
    		}
    		orfilter = new Or(filters);
    		cont_test.addContainerFilter(orfilter);
        }
        else{
        	filterAlles = new Like("studiengang", "", true);
        	cont_test.addContainerFilter(filterAlles);
        }
	}

}
