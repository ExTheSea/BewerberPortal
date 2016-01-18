package com.example.bewerberportal;

import java.sql.SQLException;

import com.example.data.DatabaseConnector;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;

public class FirmenSucheView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public FirmenSucheView() {
		setMargin(true);
		setSpacing(true);
		
		addComponent(new Label("Dies ist ein Beispiel View"));
		
		Button btn_openPopUp = new Button("PopUp öffnen");
		btn_openPopUp.addClickListener(new Button.ClickListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				new BeispielPopUp();
			}
		});
		addComponent(btn_openPopUp);
		Grid testgrid = new Grid();
        TableQuery tq_test = new TableQuery("studienplaetze", DatabaseConnector.getPool());
        SQLContainer cont_test = null;
        try {
            cont_test = new SQLContainer(tq_test);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        testgrid.setContainerDataSource(cont_test);
        testgrid.setSizeFull();
        //testgrid.setColumnOrder(new Object[]{"firmenprofil_id","studiengang_id", ""});
        addComponent(testgrid);
        testgrid.addSelectionListener(new SelectionListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void select(SelectionEvent event) {
				Notification.show("TEST");
				
			}
		});
        
	}
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
