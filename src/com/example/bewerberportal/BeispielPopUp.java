package com.example.bewerberportal;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class BeispielPopUp extends Window {
	public BeispielPopUp() {
		VerticalLayout vl_popup = new VerticalLayout();
		vl_popup.setMargin(true);
		vl_popup.setSpacing(true);
		setContent(vl_popup);
		vl_popup.addComponent(new Label("Ich bin ein Beispiel PopUp"));
		Button btn_close = new Button("Schließen");
		btn_close.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		vl_popup.addComponent(btn_close);
		
		setModal(true);
		center();
		
		//Können auch Prozente sein
		setWidth("300px");
		setHeight("100px");
		
		//Um standard window buttons zu deaktivieren. Einsetzen wenn nötig
		setResizable(false);
		setClosable(false);
		
		//PopUp öffnen
		BewerberportalUI.getCurrent().addWindow(this);
	}
}
