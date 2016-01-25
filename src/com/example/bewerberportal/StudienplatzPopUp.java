
package com.example.bewerberportal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.vaadin.data.Item;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

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
	    System.out.println(item.getItemProperty("logo").getValue());
	    System.out.println(item.getItemProperty("logo").getValue().getClass());
	    StreamSource streamSource = new StreamSource()
	      {

			private static final long serialVersionUID = 1L;

			public InputStream getStream()
		        {
			        byte[] bas = (byte[]) item.getItemProperty("logo").getValue();
			        return (bas == null) ? null : new ByteArrayInputStream(bas);
		        }
	      };

		Image firmen_logo = new Image("", new StreamResource(streamSource, "Logo"));
		firmen_logo.setSizeFull();
		hl_popup.addComponent(firmen_logo);
		
		VerticalLayout vl_firma = new VerticalLayout();
		hl_popup.addComponent(vl_firma);
		vl_firma.setSpacing(true);
		
		Label label_firma = new Label(item.getItemProperty("name").getValue().toString());
		label_firma.setCaption("Firma:");
		vl_firma.addComponent(label_firma);
		
		Label label_website = new Label(item.getItemProperty("website").getValue().toString());
		label_website.setCaption("Website:");
		vl_firma.addComponent(label_website);
		
		Label label_standort = new Label(item.getItemProperty("strasse").getValue().toString());
		label_standort.setCaption("Standort");
		vl_firma.addComponent(label_standort);
		vl_firma.addComponent(new Label(item.getItemProperty("ort").getValue().toString()));
		
		
		Label label_studienrichtung = new Label(item.getItemProperty("Bezeichnung").getValue().toString());
		label_studienrichtung.setCaption("Studienrichtung:");
		vl_popup.addComponent(label_studienrichtung);
		
		Label label_freie_plaetze = new Label(item.getItemProperty("anzahl").getValue().toString());
		label_freie_plaetze.setCaption("Freie Plätze:");
		vl_popup.addComponent(label_freie_plaetze);
		
		Label label_ansprechpartner = new Label(item.getItemProperty("ansprechpartnername").getValue().toString());
		label_ansprechpartner.setCaption("Ansprechpartner:");
		vl_popup.addComponent(label_ansprechpartner);
		
		Label label_telefonnummer = new Label(item.getItemProperty("telefonnummer").getValue().toString());
		label_telefonnummer.setCaption("Telefonnummer:");
		vl_popup.addComponent(label_telefonnummer);
		
		Label label_email = new Label(item.getItemProperty("email").getValue().toString());
		label_email.setCaption("E-Mail:");
		vl_popup.addComponent(label_email);
		
		setModal(true);
		center();
		
		//Können auch Prozente sein
		setWidth("450px");
		setHeight("550px");
		
		//Um standard window buttons zu deaktivieren. Einsetzen wenn nötig
		setResizable(true);
		
		//PopUp öffnen
		BewerberportalUI.getCurrent().addWindow(this);
	}
}