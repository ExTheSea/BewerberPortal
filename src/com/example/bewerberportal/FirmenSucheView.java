package com.example.bewerberportal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.vaadin.gridutil.cell.GridCellFilter;

import com.example.data.DatabaseConnector;
import com.example.data.GeoHelper;
import com.example.data.TableQuery;
import com.example.login.CurrentUser;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class FirmenSucheView extends VerticalLayout implements View {
	
	
	private static final long serialVersionUID = 1L;

	//Erstellen von Variablen die im Kontstruktor und erneuten Enter der Seite ben�tigt werden
    SQLContainer cont_test = null;
	Or studiengangFilter = null;
	CheckBox studiengangMatching = new CheckBox("Studiengang");;
	CheckBox deutschMatching = new CheckBox("Deutsch");
	CheckBox englischMatching = new CheckBox("Englisch");
	CheckBox matheMatching = new CheckBox("Mathe");
	CheckBox zeugnisschnittMatching = new CheckBox("Zeugnisschnitt");
    String bewerberprofil_id = null;
    String bewerberprofil_deutsch = null;
    String bewerberprofil_englisch = null;
    String bewerberprofil_mathe = null;
    String bewerberprofil_zeugnisschnitt = null;
    String bewerberprofil_lat = null;
    String bewerberprofil_lng = null;
    ArrayList<String> studiengang_bewerber;
	Between deutschFilter = null;
	Between englischFilter = null;
	Between matheFilter = null;
	Between zeugnisschnittFilter = null;
	HorizontalLayout hlCheckbox = new HorizontalLayout();
	Grid testgrid;
	GridCellFilter filter;
	public FirmenSucheView() {
		setMargin(true);
		setSpacing(true);
		setSizeFull();
		//Abruf Datenbank des firmensucheview
		testgrid = new Grid();
        TableQuery tq_test = new TableQuery("firmensucheview", DatabaseConnector.getPool()){

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
        //Hinzuf�gen der Distanz Spalte und Berechnung der Distanz
        GeneratedPropertyContainer cont_gen = new GeneratedPropertyContainer(cont_test);
        cont_gen.addGeneratedProperty("Distanz", new PropertyValueGenerator<Integer>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Integer getValue(Item item, Object itemId, Object propertyId) {
				if(bewerberprofil_lat != null && !bewerberprofil_lat.isEmpty() && item.getItemProperty("lat").getValue()!=null && !item.getItemProperty("lat").getValue().toString().isEmpty()){
					return Double.valueOf(GeoHelper.distance(Double.valueOf(bewerberprofil_lat), Double.valueOf(bewerberprofil_lng), 
							Double.valueOf(item.getItemProperty("lat").getValue().toString()), 
							Double.valueOf(item.getItemProperty("lng").getValue().toString()), "K")).intValue();
				}else			
					return null;
			}

			@Override
			public Class<Integer> getType() {
				return Integer.class;
			}
		});
        //Matching f�r Angemeldeten User
        if((CurrentUser.get()!=null) && (CurrentUser.get().toString()!="")){
            hlCheckbox.setSizeFull();
            hlCheckbox.setHeight(null);
            hlCheckbox.addComponent(new Label("Matching: "));
            Connection con = null;
            Statement statement = null;
            //Abruf der User Informationen
            studiengang_bewerber = new  ArrayList<String>();
            try {
    			con = DatabaseConnector.getPool().reserveConnection();
    	        statement = con.createStatement();
    	        ResultSet rsProfil = statement.executeQuery("SELECT id, note_deutsch, note_englisch, note_mathe, zeugnisschnitt, lat, lng from bewerberprofil where benutzer_id = '"+CurrentUser.get()+"'");
    	        if(rsProfil.first()){
    		        bewerberprofil_id = rsProfil.getString("id");
    		        bewerberprofil_deutsch = rsProfil.getString("note_deutsch");
    		        bewerberprofil_englisch = rsProfil.getString("note_englisch");
    		        bewerberprofil_mathe = rsProfil.getString("note_mathe");
    		        bewerberprofil_zeugnisschnitt = rsProfil.getString("zeugnisschnitt");
    		        bewerberprofil_lat = rsProfil.getString("lat");
    		        bewerberprofil_lng = rsProfil.getString("lng");
    		        ResultSet rsStudiengang = statement.executeQuery("SELECT bezeichnung from studiengang, studiengang_bewerberprofil where studiengang_bewerberprofil.bewerberprofil_id = '"+bewerberprofil_id+"' AND studiengang.id = studiengang_bewerberprofil.studiengang_id");
    		        rsStudiengang.first();
    		        if(rsStudiengang.first()){
    			        while (! rsStudiengang.isAfterLast()) {
    			        	studiengang_bewerber.add(rsStudiengang.getString("bezeichnung"));
    			        	rsStudiengang.next();
    					}	
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
            try{
            	//Matching des Studiengang
				studiengangMatching.addValueChangeListener(new ValueChangeListener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						if(studiengangMatching.getValue()){
							cont_test.addContainerFilter(studiengangFilter);
						}
						else{
							cont_test.removeContainerFilter(studiengangFilter);
						}
						
					}
				});
    			if(!studiengang_bewerber.isEmpty()){
    	            Like[] filters = new Like[studiengang_bewerber.size()];
    	    		for (int i = 0; i < studiengang_bewerber.size(); i++) {
    	    			String string = studiengang_bewerber.get(i).toString();
    	    			Like filterID = new Like("Bezeichnung", string, false);
    	    			filters[i] = filterID;
    	    		}
    	    		studiengangFilter = new Or(filters);
    				studiengangMatching.setValue(true);
    				hlCheckbox.addComponent(studiengangMatching);
    			}
            }catch(Exception e){
				e.printStackTrace();;
			}

			try{
				//Matching der Deutschnote
				deutschMatching.addValueChangeListener(new ValueChangeListener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						if(deutschMatching.getValue()){
							cont_test.addContainerFilter(deutschFilter);						}
						else{
							cont_test.removeContainerFilter(deutschFilter);
						}
						
					}
				});
				if((bewerberprofil_deutsch!="")&&((Double.parseDouble(bewerberprofil_deutsch)!=0))&&(bewerberprofil_deutsch!=null)){
					deutschFilter= new Between("note_deutsch", bewerberprofil_deutsch, 6);

					deutschMatching.setValue(true);
					
					hlCheckbox.addComponent(deutschMatching);
				}
				else{
					deutschFilter= new Between("note_deutsch", 0, 6);
					deutschMatching.setValue(true);
				}
			}catch(Exception e){
				e.printStackTrace();;
			}
			try{
				//Matching der Englischnote
				englischMatching.addValueChangeListener(new ValueChangeListener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						if(englischMatching.getValue()){
							cont_test.addContainerFilter(englischFilter);
						}
						else{
							cont_test.removeContainerFilter(englischFilter);
						}
						
					}
				});
				if((bewerberprofil_englisch!="")&&((Double.parseDouble(bewerberprofil_englisch)!=0))&&(bewerberprofil_englisch!=null)){
					englischFilter= new Between("note_englisch", bewerberprofil_englisch, 6);
					englischMatching.setValue(true);
					hlCheckbox.addComponent(englischMatching);
				}
				else{
					englischFilter= new Between("note_englisch", 0, 6);
					englischMatching.setValue(true);
				}
			}catch(Exception e){
				e.printStackTrace();;
			}

			try{
				//Matching der Mathenote
				matheMatching.addValueChangeListener(new ValueChangeListener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						if(matheMatching.getValue()){
							cont_test.addContainerFilter(matheFilter);
						}
						else{
							cont_test.removeContainerFilter(matheFilter);
						}
						
					}
				});
				if((bewerberprofil_mathe!="")&&((Double.parseDouble(bewerberprofil_mathe)!=0))&&(bewerberprofil_mathe!=null)){
					matheFilter= new Between("note_mathe", bewerberprofil_mathe, 6);
					matheMatching.setValue(true);
					hlCheckbox.addComponent(matheMatching);
				}
				else{
					matheFilter= new Between("note_mathe", 0, 6);
					matheMatching.setValue(true);
				}
			}catch(Exception e){
				e.printStackTrace();;
			}
			try{
				//Matching des Zeugnisschnitt
				zeugnisschnittMatching.addValueChangeListener(new ValueChangeListener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						if(zeugnisschnittMatching.getValue()){
							cont_test.addContainerFilter(zeugnisschnittFilter);
						}
						else{
							cont_test.removeContainerFilter(zeugnisschnittFilter);
						}
						
					}
				});
				if((bewerberprofil_zeugnisschnitt!="")&&((Double.parseDouble(bewerberprofil_zeugnisschnitt)!=0))&&(bewerberprofil_zeugnisschnitt!=null)){
					zeugnisschnittFilter= new Between("zeugnisschnitt", bewerberprofil_zeugnisschnitt, 6);
					zeugnisschnittMatching.setValue(true);
					hlCheckbox.addComponent(zeugnisschnittMatching);
				}
				else{
					zeugnisschnittFilter= new Between("zeugnisschnitt", 0, 6);
					zeugnisschnittMatching.setValue(true);
				}
			}catch(Exception e){
				e.printStackTrace();;
			}

			addComponent(hlCheckbox);
        }
        //Entfernen und Hinzuf�gen der gew�nschten Spalten
        testgrid.setContainerDataSource(cont_gen);
        testgrid.setSizeFull();
        testgrid.removeAllColumns();
        testgrid.addColumn("name");
        testgrid.addColumn("Bezeichnung").setHeaderCaption("Studiengang");
        testgrid.addColumn("anzahl");
        testgrid.addColumn("ort");
        testgrid.addColumn("ansprechpartnername").setHeaderCaption("Ansprechpartner");
        testgrid.sort("Bezeichnung");
        filter = new GridCellFilter(testgrid);
        filter.setTextFilter("name", true, true).setInputPrompt("Filter Name");
        filter.setTextFilter("ansprechpartnername", true, true).setInputPrompt("Filter Ansprechpartner");;
        filter.setTextFilter("Bezeichnung", true, true).setInputPrompt("Filter Studiengang");
        filter.setTextFilter("ort", true, true).setInputPrompt("Filter Ort");
        //Hinzuf�gen der Distanz wenn berechenbar
        if((CurrentUser.get()!=null) && (CurrentUser.get().toString()!="") && (bewerberprofil_lat != null) && (!bewerberprofil_lat.isEmpty())){
        	//am besten nur Einf�gen wenn PLZ vorhanden und Entfernung berechnet werden kann
        	testgrid.addColumn("Distanz");
        }

        testgrid.setColumnOrder(new Object[]{"name", "Bezeichnung", "anzahl", "ort"});
        testgrid.setSizeFull();
        addComponent(testgrid);
        setExpandRatio(testgrid, 1f);
        testgrid.addSelectionListener(new SelectionListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void select(SelectionEvent event) {
				if(!event.getSelected().isEmpty())new StudienplatzPopUp(testgrid.getContainerDataSource().getItem(event.getSelected().toArray()[0]));
			}
		});
	}
	
	//Erneuter Aufruf der Seite
	@Override
	public void enter(ViewChangeEvent event) {
		cont_test.refresh();
		if((CurrentUser.get()!=null) && (CurrentUser.get().toString()!="")){
			//Erneuter Abruf der User Informationen die evtl. ge�ndert wurden
            Connection con = null;
            Statement statement = null;

            studiengang_bewerber = new  ArrayList<String>();
            try {
    			con = DatabaseConnector.getPool().reserveConnection();
    	        statement = con.createStatement();
    	        ResultSet rsProfil = statement.executeQuery("SELECT id, note_deutsch, note_englisch, note_mathe, zeugnisschnitt, lat, lng from bewerberprofil where benutzer_id = '"+CurrentUser.get()+"'");
    	        if(rsProfil.first()){
    		        bewerberprofil_id = rsProfil.getString("id");
    		        bewerberprofil_deutsch = rsProfil.getString("note_deutsch");
    		        bewerberprofil_englisch = rsProfil.getString("note_englisch");
    		        bewerberprofil_mathe = rsProfil.getString("note_mathe");
    		        bewerberprofil_zeugnisschnitt = rsProfil.getString("zeugnisschnitt");
    		        bewerberprofil_lat = rsProfil.getString("lat");
    		        bewerberprofil_lng = rsProfil.getString("lng");
    		        ResultSet rsStudiengang = statement.executeQuery("SELECT bezeichnung from studiengang, studiengang_bewerberprofil where studiengang_bewerberprofil.bewerberprofil_id = '"+bewerberprofil_id+"' AND studiengang.id = studiengang_bewerberprofil.studiengang_id");
    		        rsStudiengang.first();
    		        if(rsStudiengang.first()){
    			        while (! rsStudiengang.isAfterLast()) {
    			        	studiengang_bewerber.add(rsStudiengang.getString("bezeichnung"));
    			        	rsStudiengang.next();
    					}	
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
            //Erneuter Aufbau des Matching auf Studiengang
			if(!studiengang_bewerber.isEmpty()){
				if(hlCheckbox.getComponentIndex(studiengangMatching)==-1)hlCheckbox.addComponent(studiengangMatching);
				studiengangMatching.setValue(false);
	            Like[] filters = new Like[studiengang_bewerber.size()];
	    		for (int i = 0; i < studiengang_bewerber.size(); i++) {
	    			String string = studiengang_bewerber.get(i).toString();
	    			Like filterID = new Like("Bezeichnung", string, false);
	    			filters[i] = filterID;
	    		}
	    		studiengangFilter = new Or(filters);
				studiengangMatching.setValue(true);
			}
			//Erneuter Aufbau des Matching auf Deutsch
			if((bewerberprofil_deutsch!="")&&(bewerberprofil_deutsch!=null)&&((Double.parseDouble(bewerberprofil_deutsch)!=0.0))){
				if(hlCheckbox.getComponentIndex(deutschMatching)==-1)hlCheckbox.addComponent(deutschMatching);
				deutschMatching.setValue(false);
				deutschFilter= new Between("note_deutsch", bewerberprofil_deutsch, 6);
				deutschMatching.setValue(true);
			}
			//Erneuter Aufbau des Matching auf Englisch
			if((bewerberprofil_englisch!="")&&(bewerberprofil_englisch!=null)&&((Double.parseDouble(bewerberprofil_englisch)!=0.0))){
				if(hlCheckbox.getComponentIndex(englischMatching)==-1)hlCheckbox.addComponent(englischMatching);
				englischMatching.setValue(false);
				englischFilter= new Between("note_englisch", bewerberprofil_englisch, 6);
				englischMatching.setValue(true);
			}
			//Erneuter Aufbau des Matching auf Mathe
			if((bewerberprofil_mathe!="")&&(bewerberprofil_mathe!=null)&&((Double.parseDouble(bewerberprofil_mathe)!=0.0))){
				if(hlCheckbox.getComponentIndex(matheMatching)==-1)hlCheckbox.addComponent(matheMatching);
				matheMatching.setValue(false);
				matheFilter= new Between("note_mathe", bewerberprofil_mathe, 6);
				matheMatching.setValue(true);
			}
			if((bewerberprofil_zeugnisschnitt!="")&&(bewerberprofil_zeugnisschnitt!=null)&&((Double.parseDouble(bewerberprofil_zeugnisschnitt)!=0.0))){
				if(hlCheckbox.getComponentIndex(zeugnisschnittMatching)==-1)hlCheckbox.addComponent(zeugnisschnittMatching);
				zeugnisschnittMatching.setValue(false);
				zeugnisschnittFilter = new Between("zeugnisschnitt", bewerberprofil_zeugnisschnitt, 6);
				zeugnisschnittMatching.setValue(true);
			}
			//Erneuter Aufbau des Matching auf Zeugnisschnitt
	        if((testgrid.getColumn("Distanz")==null) && (bewerberprofil_lat != null) && (!bewerberprofil_lat.isEmpty())){
	        	testgrid.addColumn("Distanz");
	        }
		}
	}

}
