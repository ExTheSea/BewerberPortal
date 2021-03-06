
package com.example.bewerberportal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.vaadin.data.Item;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
//Aufruf f�r ausgew�hlten Studienplatz den sich Bewerber anschaut
public class StudienplatzPopUp extends Window {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StudienplatzPopUp(Item item) {
		VerticalLayout vl_popup = new VerticalLayout();
		vl_popup.setMargin(true);
		vl_popup.setSpacing(true);
		setContent(vl_popup);
		
		HorizontalLayout hl_popup = new HorizontalLayout();
		vl_popup.addComponent(hl_popup);
		hl_popup.setSizeFull();
		hl_popup.setSpacing(true);
		//Einbinden des Firmenlogos
	    StreamSource streamSource = new StreamSource()
	      {

			private static final long serialVersionUID = 1L;

			public InputStream getStream()
		        {
			        byte[] bas = (byte[]) item.getItemProperty("logo").getValue();
			        return (bas == null) ? null : new ByteArrayInputStream(bas);
		        }
	      };

	    Image firmen_logo = null;
		if(streamSource.getStream()!=null) firmen_logo = new Image("", new StreamResource(streamSource, "Logo"));
		else firmen_logo = new Image(null, new ThemeResource("kein_logo.png"));
		firmen_logo.setSizeFull();
		hl_popup.addComponent(firmen_logo);
		
		VerticalLayout vl_firma = new VerticalLayout();
		hl_popup.addComponent(vl_firma);
		vl_firma.setSpacing(true);
		//Einbinden des Firmennamens
		Label label_firma = new Label();
		if(item.getItemProperty("name").getValue()!=null)label_firma.setValue(item.getItemProperty("name").getValue().toString());
		label_firma.setCaption("Firma:");
		vl_firma.addComponent(label_firma);
		//Einbinden der Website
		Label label_website = new Label();
		label_website.setContentMode(ContentMode.HTML);
		if(item.getItemProperty("website").getValue()!=null)label_website.setValue("<a href='"+item.getItemProperty("website").getValue().toString()+"'>"+item.getItemProperty("website").getValue().toString()+"</a>");
		label_website.setCaption("Website:");
		vl_firma.addComponent(label_website);
		//Einbinden des Standorts
		Label label_standort = new Label();
		if(item.getItemProperty("strasse").getValue()!=null)label_standort.setValue(item.getItemProperty("strasse").getValue().toString());
		label_standort.setCaption("Standort");
		vl_firma.addComponent(label_standort);
		vl_firma.addComponent(new Label(item.getItemProperty("ort").getValue().toString()));
		
		//Einbinden der Studienrichtung
		Label label_studienrichtung = new Label();
		if(item.getItemProperty("Bezeichnung").getValue()!=null)label_studienrichtung.setValue(item.getItemProperty("Bezeichnung").getValue().toString());
		label_studienrichtung.setCaption("Studienrichtung:");
		vl_popup.addComponent(label_studienrichtung);
		//Einbinden der Anzahl an freien Pl�tzen
		Label label_freie_plaetze = new Label();
		if(item.getItemProperty("anzahl").getValue()!=null)label_freie_plaetze.setValue(item.getItemProperty("anzahl").getValue().toString());
		label_freie_plaetze.setCaption("Freie Pl�tze:");
		vl_popup.addComponent(label_freie_plaetze);
		//Einbinden des Ansprechpartner
		Label label_ansprechpartner = new Label();
		if(item.getItemProperty("name").getValue()!=null)label_ansprechpartner.setValue(item.getItemProperty("name").getValue().toString());
		label_ansprechpartner.setCaption("Ansprechpartner:");
		vl_popup.addComponent(label_ansprechpartner);
		//Einbinden der Telefonnummer des Ansprechpartner
		Label label_telefonnummer = new Label();
		if(item.getItemProperty("telefonnummer").getValue()!=null)label_telefonnummer.setValue(item.getItemProperty("telefonnummer").getValue().toString());
		label_telefonnummer.setCaption("Telefonnummer:");
		vl_popup.addComponent(label_telefonnummer);
		//Einbinden der E-Mail Adresse des Ansprechpartner
		Label label_email = new Label();
		if(item.getItemProperty("email").getValue()!=null)label_email.setValue(item.getItemProperty("email").getValue().toString());
		label_email.setCaption("E-Mail:");
		vl_popup.addComponent(label_email);
		
		setModal(true);
		center();
		
		//K�nnen auch Prozente sein
		setWidth("450px");
		setHeight("550px");
		
		//Um standard window buttons zu deaktivieren. Einsetzen wenn n�tig
		setResizable(true);
		
		//PopUp �ffnen
		BewerberportalUI.getCurrent().addWindow(this);
	}
}