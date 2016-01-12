package com.example.bewerberportal;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class TestView2 extends VerticalLayout implements View {

	public TestView2() {
		setMargin(true);
		setSpacing(true);
		addComponent(new Label("Dies ist auch ein Beispiel View"));
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
