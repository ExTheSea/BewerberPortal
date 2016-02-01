package com.example.bewerberportal;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.example.data.DatabaseConnector;
import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class RegistrierungsPopUp extends Window {
	
	private SQLContainer cont_user = null;
	public RegistrierungsPopUp() {
		VerticalLayout vl_popup = new VerticalLayout();
		vl_popup.setMargin(true);
		vl_popup.setSpacing(true);
		vl_popup.setSizeFull();
		setContent(vl_popup);
//		
//		TextField txt_firstname = new TextField("Vorname:");
//		txt_firstname.setWidth("100%");
//		vl_popup.addComponent(txt_firstname);
//
//		TextField txt_lastname = new TextField("Nachname:");
//		txt_lastname.setWidth("100%");
//		vl_popup.addComponent(txt_lastname);
		TableQuery tq_user = new TableQuery("benutzer", DatabaseConnector.getPool());
	
		try {
			cont_user = new SQLContainer(tq_user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Object itemID = cont_user.addItem();
		Item item = cont_user.getItem(itemID);
		item.getItemProperty("account_id").setValue(1);
		FieldGroup group_user = new FieldGroup(item);
		TextField txt_mail = new TextField("E-Mail:");
		txt_mail.setValidationVisible(false);
		txt_mail.addValidator(new StringLengthValidator("Kann nicht leer sein", 1, null, false));
		txt_mail.addValidator(new EmailValidator("Keine gültige E-Mail"));
		txt_mail.setWidth("100%");
		vl_popup.addComponent(txt_mail);
		group_user.bind(txt_mail, "email");
		txt_mail.setValue("");
		
		PasswordField txt_pw = new PasswordField("Password:");
		txt_pw.setWidth("100%");
		vl_popup.addComponent(txt_pw);
		group_user.bind(txt_pw, "passwort");
		txt_pw.setValue("");
		
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
				cont_user.removeAllContainerFilters();
				cont_user.addContainerFilter(new Like("email", txt_mail.getValue()));
				if(cont_user.size()==0){
					if(txt_mail.isValid()&txt_pwreset.isValid()){
						try {
							group_user.commit();
							cont_user.commit();
						} catch (CommitException e) {
							e.printStackTrace();
						} catch (UnsupportedOperationException e) {
							e.printStackTrace();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						BewerberportalUI.getCurrent().control.signIn(txt_mail.getValue(), txt_pw.getValue());
						close();
					}
				}else{
					Notification.show("Error", "E-Mail schon genutzt", Notification.Type.ERROR_MESSAGE);
				}
			}
		});
		vl_popup.addComponent(btn_register);
		
		setModal(true);
		center();
		
		//Können auch Prozente sein
		setWidth("350px");
		setHeight("300px");
		
		//Um standard window buttons zu deaktivieren. Einsetzen wenn nötig
		setResizable(false);
		
		//PopUp öffnen
		BewerberportalUI.getCurrent().addWindow(this);
	}
}
