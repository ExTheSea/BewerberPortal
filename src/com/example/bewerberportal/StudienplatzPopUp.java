
package com.example.bewerberportal;

import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class StudienplatzPopUp extends Window {
	public StudienplatzPopUp(Item item) {
		VerticalLayout vl_popup = new VerticalLayout();
		vl_popup.setMargin(true);
		vl_popup.setSpacing(true);
		//vl_popup.setSizeFull();
		setContent(vl_popup);
		
		HorizontalLayout hl_popup = new HorizontalLayout();
		vl_popup.addComponent(hl_popup);
		//hl_popup.setSizeFull();
		
		Label label_bild = new Label("Bild Bild Bild Bild Bild Bild Bild Bild Bild Bild Bild Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  Bild  ");
		label_bild.setHeight("50px");
		label_bild.setWidth("200px");
		hl_popup.addComponent(label_bild);
		
		VerticalLayout vl_firma = new VerticalLayout();
		hl_popup.addComponent(vl_firma);
		vl_firma.setWidth("250px");
		
		Label label_firma = new Label("Firma: ");
		vl_firma.addComponent(label_firma);
		vl_firma.addComponent(new Label(item.getItemProperty("logo").getValue().toString()));
		
		Label label_website = new Label("Website: ");
		vl_firma.addComponent(label_website);
		vl_firma.addComponent(new Label(item.getItemProperty("website").getValue().toString()));
		
		Label label_standort = new Label("Standort: ");
		vl_firma.addComponent(label_standort);
		vl_firma.addComponent(new Label(item.getItemProperty("strasse").getValue().toString()));
		vl_firma.addComponent(new Label(item.getItemProperty("ort").getValue().toString()));
		
		
		Label label_studienrichtung = new Label("Studienrichtung:");
		vl_popup.addComponent(label_studienrichtung);
		vl_popup.addComponent(new Label(item.getItemProperty("Bezeichnung").getValue().toString()));
		
		Label label_freie_plaetze = new Label("Freie Plätze:");
		vl_popup.addComponent(label_freie_plaetze);
		vl_popup.addComponent(new Label(item.getItemProperty("freie_plaetze").getValue().toString()));
		
		Label label_ansprechpartner = new Label("Ansprechpartner:");
		vl_popup.addComponent(label_ansprechpartner);
		vl_popup.addComponent(new Label(item.getItemProperty("name").getValue().toString()));
		
		Label label_telefonnummer = new Label("Telefonnummer:");
		vl_popup.addComponent(label_telefonnummer);
		vl_popup.addComponent(new Label(item.getItemProperty("telefonnummer").getValue().toString()));
		
		Label label_email = new Label("E-Mail:");
		vl_popup.addComponent(label_email);
		vl_popup.addComponent(new Label(item.getItemProperty("email").getValue().toString()));
		

		

		
		setModal(true);
		center();
		
		//Können auch Prozente sein
		setWidth("450px");
		setHeight("600px");
		
		//Um standard window buttons zu deaktivieren. Einsetzen wenn nötig
		setResizable(false);
		
		//PopUp öffnen
		BewerberportalUI.getCurrent().addWindow(this);
	}
}