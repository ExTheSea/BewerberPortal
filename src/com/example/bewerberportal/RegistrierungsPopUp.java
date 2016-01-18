package com.example.bewerberportal;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Button.ClickEvent;

public class RegistrierungsPopUp extends Window {
	public RegistrierungsPopUp() {
		VerticalLayout vl_popup = new VerticalLayout();
		vl_popup.setMargin(true);
		vl_popup.setSpacing(true);
		vl_popup.setSizeFull();
		setContent(vl_popup);
		
		TextField txt_firstname = new TextField("Vorname:");
		txt_firstname.setWidth("100%");
		vl_popup.addComponent(txt_firstname);

		TextField txt_lastname = new TextField("Nachname:");
		txt_lastname.setWidth("100%");
		vl_popup.addComponent(txt_lastname);
		
		TextField txt_mail = new TextField("E-Mail:");
		txt_mail.setValidationVisible(false);
		txt_mail.addValidator(new StringLengthValidator("Kann nicht leer sein", 1, null, false));
		txt_mail.addValidator(new EmailValidator("Keine gültige E-Mail"));
		txt_mail.setWidth("100%");
		vl_popup.addComponent(txt_mail);

		PasswordField txt_pw = new PasswordField("Password:");
		txt_pw.setWidth("100%");
		vl_popup.addComponent(txt_pw);
		
		PasswordField txt_pwreset = new PasswordField("Password wiederholen:");
		txt_pwreset.setValidationVisible(false);
		txt_pwreset.addValidator(new Validator() {
			
			@Override
			public void validate(Object value) throws InvalidValueException {
				if(txt_pw.getValue() == null)
					throw new InvalidValueException("Passwort kann nicht leer sein");
					
				if(!txt_pw.getValue().equals(txt_pwreset.getValue())){
					throw new InvalidValueException("Passwort stimmt nicht überein");
				}
			}
		});
		txt_pwreset.setWidth("100%");
		vl_popup.addComponent(txt_pwreset);
		
		Button btn_register = new Button("Registrieren");
		btn_register.setStyleName(ValoTheme.BUTTON_DANGER);
		btn_register.setWidth("100%");
		btn_register.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				txt_mail.setValidationVisible(true);
				txt_pwreset.setValidationVisible(true);
				if(txt_mail.isValid()&txt_pwreset.isValid())
					close();
			}
		});
		vl_popup.addComponent(btn_register);
		
		setModal(true);
		center();
		
		//Können auch Prozente sein
		setWidth("350px");
		setHeight("450px");
		
		//Um standard window buttons zu deaktivieren. Einsetzen wenn nötig
		setResizable(false);
		
		//PopUp öffnen
		BewerberportalUI.getCurrent().addWindow(this);
	}
}
