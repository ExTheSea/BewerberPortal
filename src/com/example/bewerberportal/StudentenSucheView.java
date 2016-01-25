package com.example.bewerberportal;

import java.sql.SQLException;
import java.util.ArrayList;

import com.example.data.DatabaseConnector;
import com.example.data.TableQuery;
import com.vaadin.data.Item;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickListener;

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
        
        testgrid.setContainerDataSource(cont_gen);
        testgrid.setSizeFull();
        testgrid.removeAllColumns();
        testgrid.addColumn("studiengang");
        testgrid.addColumn("name");
        testgrid.addColumn("plz").setHeaderCaption("PLZ");
        testgrid.addColumn("Ort");
        testgrid.addColumn("zeugnisschnitt");
        

        testgrid.setSizeFull();
        addComponent(testgrid);
        testgrid.addSelectionListener(new SelectionListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void select(SelectionEvent event) {
				new StudentPopUp(testgrid.getContainerDataSource().getItem(event.getSelected().toArray()[0]));
			}
		});
        
	}
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
