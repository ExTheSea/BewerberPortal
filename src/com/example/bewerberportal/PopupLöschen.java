package com.example.bewerberportal;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Button.ClickEvent;

public class PopupLöschen extends Window {
	
	public PopupLöschen(String firmenname) {
		
		VerticalLayout vl_popup = new VerticalLayout();
		vl_popup.setMargin(true);
		vl_popup.setSpacing(true);
		vl_popup.setSizeFull();
		setContent(vl_popup);
		
		Panel panel = new Panel();
		panel.setContent(new Label("Soll die Firma " + firmenname + " wirklich gelöscht werden?"));
		vl_popup.addComponent(panel);
		
		HorizontalLayout hl_buttons = new HorizontalLayout();
		hl_buttons.setWidth("100%");
		
		Button btn_delete = new Button("Löschen");
		btn_delete.setStyleName(ValoTheme.BUTTON_DANGER);
		btn_delete.setWidth("90%");
		btn_delete.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				//Coding zum Löschen aus der DB
		}});
		hl_buttons.addComponent(btn_delete);
		
		
		
		
		Button btn_cancel = new Button("Abbrechen");
		btn_cancel.setWidth("90%");
		btn_cancel.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
		}});
		hl_buttons.addComponent(btn_cancel);
		
		
		vl_popup.addComponent(hl_buttons);
		
		
		setModal(true);
		center();
		
		//Können auch Prozente sein
		setWidth("400px");
		setHeight("150px");
		
		//Um standard window buttons zu deaktivieren. Einsetzen wenn nötig
		setResizable(false);
		setClosable(false);
		
		//PopUp öffnen
		BewerberportalUI.getCurrent().addWindow(this);
	}
}