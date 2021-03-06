package com.example.bewerberportal;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class AnmeldenPopup extends Window {
	
	public AnmeldenPopup() {
		
		VerticalLayout vl_popup = new VerticalLayout();
		vl_popup.setMargin(true);
		vl_popup.setSpacing(true);
		vl_popup.setSizeFull();
		setContent(vl_popup);
		
		//Hinzuf�gen des Logos
		Image img_logo = new Image(null, new ThemeResource("Logo.png"));
		img_logo.setWidth("50%");
		vl_popup.addComponent(img_logo);
		vl_popup.setComponentAlignment(img_logo, Alignment.MIDDLE_CENTER);
		
		//Hinzuf�gen des Email Texfeld
		TextField tf_mail = new TextField("E-Mail:");
		tf_mail.setValidationVisible(false);
		tf_mail.setInputPrompt("MaxMustermann@gmail.com");
		tf_mail.addValidator(new StringLengthValidator("Kann nicht leer sein", 1, null, false));
		tf_mail.addValidator(new EmailValidator("Keine g�ltige E-Mail"));
		tf_mail.setWidth("90%");
		vl_popup.addComponent(tf_mail);
		
		//Hinzuf�gen des Passwortfeldes
		PasswordField tf_password = new PasswordField("Passwort:");
		tf_password.setWidth("90%");
		vl_popup.addComponent(tf_password);

		
		//Hinzuf�gen des Login-Button
		Button btn_login = new Button("Anmelden");
		btn_login.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				//Logik zum Anmelden
				tf_mail.setValidationVisible(true);
				if(tf_mail.isValid()) {
					if(BewerberportalUI.getCurrent().control.signIn(tf_mail.getValue(), tf_password.getValue()))
						close();
					else
						Notification.show("Error", "User oder Passwort ist nicht korrekt", Notification.Type.ERROR_MESSAGE);
				}
			}
		});
		btn_login.setStyleName(ValoTheme.BUTTON_DANGER);
		btn_login.setWidth("100%");
		vl_popup.addComponent(btn_login);
		vl_popup.setComponentAlignment(btn_login, Alignment.BOTTOM_CENTER);
		vl_popup.setExpandRatio(btn_login, 1f);

		
		tf_password.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				btn_login.click();
			}
		});
		
		setModal(true);
		center();
		
		//K�nnen auch Prozente sein
		setWidth("400px");
		setHeight("330px");
		
		//Um standard window buttons zu deaktivieren. Einsetzen wenn n�tig
		setResizable(false);
		//setClosable(false);
		
		//PopUp �ffnen
		BewerberportalUI.getCurrent().addWindow(this);
	}
}
