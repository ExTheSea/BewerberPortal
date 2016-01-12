package com.example.bewerberportal;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class TestView extends VerticalLayout implements View {

	public TestView() {
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
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
