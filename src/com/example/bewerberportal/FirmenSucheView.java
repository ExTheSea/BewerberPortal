package com.example.bewerberportal;

import java.sql.SQLException;
import java.util.ArrayList;

import com.example.data.DatabaseConnector;
import com.example.data.TableQuery;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

public class FirmenSucheView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public FirmenSucheView() {
		setMargin(true);
		setSpacing(true);
		
		Grid testgrid = new Grid();
        TableQuery tq_test = new TableQuery("firmensucheview", DatabaseConnector.getPool()){

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
        testgrid.setContainerDataSource(cont_test);
        testgrid.setSizeFull();
        //testgrid.removeColumn(new Object[]{
        //		"note_deutsch", "note_englisch", "note_mathe", "zeugnisschnitt", "firmenprofil_id", "firmenprofil_id", "studiengang_id"});
        testgrid.removeColumn("note_deutsch");
        testgrid.removeColumn("note_englisch");
        testgrid.removeColumn("note_mathe");
        testgrid.removeColumn("zeugnisschnitt");
        testgrid.removeColumn("firmenprofil_id");
        testgrid.removeColumn("studiengang_id");
        testgrid.removeColumn("id");
        testgrid.removeColumn("name");
        testgrid.removeColumn("email");
        testgrid.removeColumn("telefonnummer");
        testgrid.removeColumn("website");
        testgrid.removeColumn("strasse");
        testgrid.setColumnOrder(new Object[]{"logo", "Bezeichnung", "freie_plaetze", "ort", "distanz"});
        addComponent(testgrid);
        testgrid.addSelectionListener(new SelectionListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void select(SelectionEvent event) {
				//Notification.show("Studienplatz ID: " + event.getAdded());
				//System.out.println(testgrid.getContainerDataSource().getContainerProperty(2, "logo").getValue());
				//System.out.println(testgrid.getSelectedRow().toString());
				//System.out.println((String)testgrid.getContainerDataSource().getItem(event.getSelected().toArray()[0]).getItemProperty("logo").getValue());
				new StudienplatzPopUp(testgrid.getContainerDataSource().getItem(event.getSelected().toArray()[0]));
			}
		});
        
	}
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
