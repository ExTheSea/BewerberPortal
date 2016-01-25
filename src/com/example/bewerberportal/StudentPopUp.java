
package com.example.bewerberportal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.vaadin.data.Item;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class StudentPopUp extends Window {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StudentPopUp(Item item) {
		VerticalLayout vl_popup = new VerticalLayout();
		vl_popup.setMargin(true);
		vl_popup.setSpacing(true);
		setContent(vl_popup);
		
		HorizontalLayout hl_popup = new HorizontalLayout();
		vl_popup.addComponent(hl_popup);
		hl_popup.setSizeFull();
		hl_popup.setSpacing(true);
		
		VerticalLayout vl_basic = new VerticalLayout();
		hl_popup.addComponent(vl_basic);
		vl_basic.setSizeFull();
		vl_basic.setSpacing(true);
		
		Label name = new Label(item.getItemProperty("name").getValue().toString());
		name.setCaption("Name:");
		vl_basic.addComponent(name);
		
		Label geburtsjahr = new Label(item.getItemProperty("geburtsjahr").getValue().toString());
		geburtsjahr.setCaption("Geburtsjahr:");
		vl_basic.addComponent(geburtsjahr);
		
		VerticalLayout vl_contact = new VerticalLayout();
		hl_popup.addComponent(vl_contact);
		vl_contact.setSpacing(true);
		
		Label label_email = new Label(item.getItemProperty("email").getValue().toString());
		label_email.setCaption("E-Mail:");
		vl_contact.addComponent(label_email);
		
		Label label_telefonnummer = new Label(item.getItemProperty("telefonnummer").getValue().toString());
		label_telefonnummer.setCaption("Telefonnummer:");
		vl_contact.addComponent(label_telefonnummer);
		
		Label label_ort = new Label(item.getItemProperty("plz").getValue().toString() + " Ort" /*item.getItemProperty("ort").getValue().toString()*/);
		label_ort.setCaption("Wohnort:");
		vl_popup.addComponent(label_ort);
		
		Label label_hobbies = new Label(item.getItemProperty("hobbies").getValue().toString());
		label_hobbies.setCaption("Hobbys:");
		vl_popup.addComponent(label_hobbies);
		
		Label label_zusatzqualifikationen = new Label(item.getItemProperty("zusatzqualifikationen").getValue().toString());
		label_zusatzqualifikationen.setCaption("Zusatzqualifikationen:");
		vl_popup.addComponent(label_zusatzqualifikationen);
		
		vl_popup.addComponent(new Label("&nbsp", ContentMode.HTML));

		HorizontalLayout hl_information = new HorizontalLayout();
		vl_popup.addComponent(hl_information);
		hl_information.setSizeFull();
		hl_information.setSpacing(true);
		
		VerticalLayout vl_noten = new VerticalLayout();
		hl_information.addComponent(vl_noten);
		vl_noten.setSizeFull();
		vl_noten.setSpacing(true);
		
		HorizontalLayout hl_deutsch = new HorizontalLayout();
		vl_noten.addComponent(hl_deutsch);
		hl_deutsch.setSizeFull();
		hl_deutsch.setSpacing(true);
		
		Label label_deutsch_text = new Label("Deutsch");
		hl_deutsch.addComponent(label_deutsch_text);
		
		hl_deutsch.addComponent(new Label("&nbsp", ContentMode.HTML));
		
		Label label_deutsch = new Label(item.getItemProperty("note_deutsch").getValue().toString());
		hl_deutsch.addComponent(label_deutsch);
		
		HorizontalLayout hl_englisch = new HorizontalLayout();
		vl_noten.addComponent(hl_englisch);
		hl_englisch.setSizeFull();
		hl_englisch.setSpacing(true);
		
		Label label_englisch_text = new Label("Englisch");
		hl_englisch.addComponent(label_englisch_text);
		
		hl_englisch.addComponent(new Label("&nbsp", ContentMode.HTML));
		
		Label label_englisch = new Label(item.getItemProperty("note_englisch").getValue().toString());
		hl_englisch.addComponent(label_englisch);
		
		HorizontalLayout hl_mathe = new HorizontalLayout();
		vl_noten.addComponent(hl_mathe);
		hl_mathe.setSizeFull();
		hl_mathe.setSpacing(true);
		
		Label label_mathe_text = new Label("Mathe");
		hl_mathe.addComponent(label_mathe_text);
		
		hl_mathe.addComponent(new Label("&nbsp", ContentMode.HTML));
		
		Label label_mathe = new Label(item.getItemProperty("note_mathe").getValue().toString());
		hl_mathe.addComponent(label_mathe);
		
		vl_noten.addComponent(new Label("_____________________"));
		
		HorizontalLayout hl_noten_gesamt = new HorizontalLayout();
		vl_noten.addComponent(hl_noten_gesamt);
		hl_noten_gesamt.setSizeFull();
		hl_noten_gesamt.setSpacing(true);
		
		Label label_noten_gesamt_text = new Label("Gesamt");
		hl_noten_gesamt.addComponent(label_noten_gesamt_text);
		
		hl_noten_gesamt.addComponent(new Label("&nbsp", ContentMode.HTML));
		
		Label label_noten_gesamt = new Label(item.getItemProperty("zeugnisschnitt").getValue().toString());
		hl_noten_gesamt.addComponent(label_noten_gesamt);
		
		VerticalLayout vl_auswahl = new VerticalLayout();
		hl_information.addComponent(vl_auswahl);
		vl_auswahl.setSizeFull();
		vl_auswahl.setSpacing(true);
		
		Label label_lieblingsfaecher = new Label(item.getItemProperty("lieblingsfach").getValue().toString());
		label_lieblingsfaecher.setCaption("Lieblingsfach:");
		vl_auswahl.addComponent(label_lieblingsfaecher);
		
		Label label_studiengang = new Label(item.getItemProperty("studiengang").getValue().toString());
		label_studiengang.setCaption("Wunschstudienrichtung:");
		vl_auswahl.addComponent(label_studiengang);

		
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