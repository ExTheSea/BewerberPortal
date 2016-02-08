package com.example.bewerberportal;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;

public class HomeView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;

	public HomeView() {
		setMargin(true);
		setSpacing(true);
		
		setSizeFull();
		CustomLayout homelay = new CustomLayout("Go2DHBW");

		Image img_logo = new Image(null, new ThemeResource("Logo.png"));
		homelay.addComponent(img_logo, "logo");
		
		homelay.setSizeFull();
		addComponent(homelay);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
