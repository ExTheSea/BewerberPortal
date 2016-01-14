package com.example.bewerberportal;

import com.example.bewerberportal.objects.Studienplatz;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;

public class SucheStudentView extends VerticalLayout implements View {

	public SucheStudentView() {
		setMargin(true);
		setSpacing(true);
		
		addComponent(new Label("Dies ist ein Beispiel View"));
		
		Button btn_openPopUp = new Button("PopUp öffnen");
		btn_openPopUp.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				new BeispielPopUp();
			}
		});
		addComponent(btn_openPopUp);
		BeanContainer<Integer, Studienplatz> data = new BeanContainer<Integer, Studienplatz>(Studienplatz.class);
		data.setBeanIdProperty("id");
		Grid grid = new Grid(data);
		addComponent(grid);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
