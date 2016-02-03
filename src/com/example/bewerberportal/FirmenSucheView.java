package com.example.bewerberportal;

import java.sql.SQLException;
import java.util.ArrayList;

import org.vaadin.gridutil.cell.GridCellFilter;

import com.example.data.DatabaseConnector;
import com.example.data.TableQuery;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class FirmenSucheView extends VerticalLayout implements View {

	
	private static final long serialVersionUID = 1L;


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
        FieldGroup group_anz = filter.setNumberFilter("anzahl");
        ((TextField)group_anz.getField("smallest")).setInputPrompt("Min");
        ((TextField)group_anz.getField("biggest")).setInputPrompt("Max");
        filter.setTextFilter("name", true, true).setInputPrompt("Filter Name");
        filter.setTextFilter("ansprechtpartnername", true, true).setInputPrompt("Filter Ansprechpartner");;
        filter.setTextFilter("Bezeichnung", true, true).setInputPrompt("Filter Bezeichnung");
        filter.setTextFilter("ort", true, true).setInputPrompt("Filter Ort");
        FieldGroup group_dist = filter.setNumberFilter("Distanz");
        ((TextField)group_dist.getField("smallest")).setInputPrompt("Min");
        ((TextField)group_dist.getField("biggest")).setInputPrompt("Max");

        /*testgrid.getColumn("TestBtn").setRenderer(new ButtonRenderer(new RendererClickListener() {

			@Override
			public void click(RendererClickEvent event) {
				System.out.println("TEst");
			}
        	
		}));*/
        testgrid.setColumnOrder(new Object[]{"name", "Bezeichnung", "anzahl", "ort"});
        testgrid.setSizeFull();
        addComponent(testgrid);
        testgrid.addSelectionListener(new SelectionListener() {
			
			/**
			 * 
			 */
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
