package com.example.bewerberportal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.vaadin.gridutil.cell.GridCellFilter;

import com.example.data.DatabaseConnector;
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


    SQLContainer cont_test = null;

	public FirmenSucheView() {
		setMargin(true);
		setSpacing(true);
		setSizeFull();

		Grid testgrid = new Grid();
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
        GeneratedPropertyContainer cont_gen = new GeneratedPropertyContainer(cont_test);
        cont_gen.addGeneratedProperty("Distanz", new PropertyValueGenerator<Integer>() {

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
        if((CurrentUser.get()!=null) && (CurrentUser.get().toString()!="")){
        	HorizontalLayout hlCheckbox = new HorizontalLayout();
            hlCheckbox.setSizeFull();
            hlCheckbox.setHeight(null);
            hlCheckbox.addComponent(new Label("Matching: "));
            Connection con = null;
            Statement statement = null;
            String bewerberprofil_id = null;
            String bewerberprofil_deutsch = null;
            String bewerberprofil_englisch = null;
            String bewerberprofil_mathe = null;
            String bewerberprofil_zeugnisschnitt = null;
            ArrayList<String> studiengang_bewerber = new  ArrayList<String>();
            try {
    			con = DatabaseConnector.getPool().reserveConnection();
    	        statement = con.createStatement();
    	        ResultSet rsProfil = statement.executeQuery("SELECT id, note_deutsch, note_englisch, note_mathe, zeugnisschnitt from bewerberprofil where benutzer_id = '"+CurrentUser.get()+"'");
    	        if(rsProfil.first()){
    		        bewerberprofil_id = rsProfil.getString("id");
    		        bewerberprofil_deutsch = rsProfil.getString("note_deutsch");
    		        bewerberprofil_englisch = rsProfil.getString("note_englisch");
    		        bewerberprofil_mathe = rsProfil.getString("note_mathe");
    		        bewerberprofil_zeugnisschnitt = rsProfil.getString("zeugnisschnitt");
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
        	Between deutschFilter;
        	Between englischFilter;
        	Between matheFilter;
        	Between zeugnisschnittFilter;
        	Or studiengangFilter;
        	CheckBox studiengangMatching;
        	CheckBox deutschMatching;
        	CheckBox englischMatching;
        	CheckBox matheMatching;
        	CheckBox zeugnisschnittMatching;
			if(!studiengang_bewerber.isEmpty()){
	            Like[] filters = new Like[studiengang_bewerber.size()];
	    		for (int i = 0; i < studiengang_bewerber.size(); i++) {
	    			String string = studiengang_bewerber.get(i).toString();
	    			Like filterID = new Like("Bezeichnung", string, false);
	    			filters[i] = filterID;
	    		}
	    		studiengangFilter = new Or(filters);
	    		
				studiengangMatching = new CheckBox("Studiengang");
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
				studiengangMatching.setValue(true);
				hlCheckbox.addComponent(studiengangMatching);
			}
			if(bewerberprofil_deutsch!=""){
				deutschFilter= new Between("note_deutsch", bewerberprofil_deutsch, 6);
				deutschMatching = new CheckBox("Deutsch");
				deutschMatching.addValueChangeListener(new ValueChangeListener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						if(deutschMatching.getValue()){
							cont_test.addContainerFilter(deutschFilter);
						}
						else{
							cont_test.removeContainerFilter(deutschFilter);
						}
						
					}
				});
				deutschMatching.setValue(true);
				hlCheckbox.addComponent(deutschMatching);
			}
			if(bewerberprofil_englisch!=""){
				englischFilter= new Between("note_englisch", bewerberprofil_englisch, 6);
				englischMatching = new CheckBox("Englisch");
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
				englischMatching.setValue(true);
				hlCheckbox.addComponent(englischMatching);
			}
			if(bewerberprofil_mathe!=""){
				matheFilter= new Between("note_mathe", bewerberprofil_mathe, 6);
				matheMatching = new CheckBox("Mathe");
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
				matheMatching.setValue(true);
				hlCheckbox.addComponent(matheMatching);
			}
			if(bewerberprofil_zeugnisschnitt!=""){
				zeugnisschnittFilter= new Between("zeugnisschnitt", bewerberprofil_zeugnisschnitt, 6);
				zeugnisschnittMatching = new CheckBox("Zeugnisschnitt");
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
				zeugnisschnittMatching.setValue(true);
				hlCheckbox.addComponent(zeugnisschnittMatching);
			}
			addComponent(hlCheckbox);
        }

        testgrid.setContainerDataSource(cont_gen);
        testgrid.setSizeFull();
        testgrid.removeColumn("note_deutsch");
        testgrid.removeColumn("note_englisch");
        testgrid.removeColumn("note_mathe");
        testgrid.removeColumn("zeugnisschnitt");
        testgrid.removeColumn("firmenprofil_id");
        testgrid.removeColumn("studiengang_id");
        testgrid.removeColumn("standort_id");
        testgrid.removeColumn("alias");
        testgrid.removeColumn("id");
        testgrid.removeColumn("email");
        testgrid.removeColumn("telefonnummer");
        testgrid.removeColumn("website");
        testgrid.removeColumn("strasse");
        testgrid.removeColumn("logo");
        
        GridCellFilter filter = new GridCellFilter(testgrid);
        filter.setTextFilter("name", true, true).setInputPrompt("Filter Name");
        filter.setTextFilter("ansprechpartnername", true, true).setInputPrompt("Filter Ansprechpartner");;
        filter.setTextFilter("Bezeichnung", true, true).setInputPrompt("Filter Bezeichnung");
        filter.setTextFilter("ort", true, true).setInputPrompt("Filter Ort");
        FieldGroup group_dist = filter.setNumberFilter("Distanz");
        ((TextField)group_dist.getField("smallest")).setInputPrompt("Min");
        ((TextField)group_dist.getField("biggest")).setInputPrompt("Max");


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
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
