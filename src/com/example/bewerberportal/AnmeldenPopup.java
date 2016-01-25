package com.example.bewerberportal;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

public class AnmeldenPopup extends Window {
	
	public AnmeldenPopup() {
		
		VerticalLayout vl_popup = new VerticalLayout();
		vl_popup.setMargin(true);
		vl_popup.setSpacing(true);
		vl_popup.setSizeFull();
		setContent(vl_popup);
		
		//Hinzufügen des Logos
		Panel pnl_logo = new Panel();
		HorizontalLayout hl_logo = new HorizontalLayout();
		hl_logo.setWidth("100%");
		pnl_logo.setContent(hl_logo);
		pnl_logo.setStyleName(ValoTheme.PANEL_BORDERLESS);
		pnl_logo.addStyleName("logo");
		pnl_logo.setWidth("50%");
		pnl_logo.setHeight(null);
		Label lbl_logo = new Label("GO2DHBW");
		lbl_logo.setSizeUndefined();
		hl_logo.addComponent(lbl_logo);
		hl_logo.setComponentAlignment(lbl_logo, Alignment.MIDDLE_CENTER);
		vl_popup.addComponent(pnl_logo);
		vl_popup.setComponentAlignment(pnl_logo, Alignment.MIDDLE_CENTER);
		
		//Hinzufügen des Email Texfeld
		TextField tf_mail = new TextField("E-Mail:");
		tf_mail.setValidationVisible(false);
		tf_mail.setInputPrompt("MaxMustermann@gmail.com");
		tf_mail.addValidator(new StringLengthValidator("Kann nicht leer sein", 1, null, false));
		tf_mail.addValidator(new EmailValidator("Keine gültige E-Mail"));
		tf_mail.setWidth("90%");
		vl_popup.addComponent(tf_mail);
		
		//Hinzufügen des Passwortfeldes
		PasswordField tf_password = new PasswordField("Passwort:");
		tf_password.setWidth("90%");
		vl_popup.addComponent(tf_password);

		
		//Hinzufügen des Login-Button
		Button btn_login = new Button("Anmelden");
		btn_login.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				//Logik zum Anmelden
				tf_mail.setValidationVisible(true);
				if(tf_mail.isValid()) {
					close();
				}
			}
		});
		btn_login.setStyleName(ValoTheme.BUTTON_DANGER);
		btn_login.setWidth("100%");
		vl_popup.addComponent(btn_login);
		vl_popup.setComponentAlignment(btn_login, Alignment.BOTTOM_CENTER);
		vl_popup.setExpandRatio(btn_login, 1f);

		
		setModal(true);
		center();
		
		//Können auch Prozente sein
		setWidth("400px");
		setHeight("300px");
		
		//Um standard window buttons zu deaktivieren. Einsetzen wenn nötig
		setResizable(false);
		//setClosable(false);
		
		//PopUp öffnen
		BewerberportalUI.getCurrent().addWindow(this);
	}
}
