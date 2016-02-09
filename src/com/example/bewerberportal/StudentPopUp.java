
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
//Aufruf wenn Firma Bewerber auswählt
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
		//Einbinden des Namens des Bewerbers
		Label name = new Label();
		if(((item.getItemProperty("name").getValue()!=null) && ((item.getItemProperty("name").getValue().toString()!="")))) name.setValue(item.getItemProperty("name").getValue().toString());
		name.setCaption("Name:");
		vl_basic.addComponent(name);
		//Einbinden des Geburtsjahr
		Label geburtsjahr = new Label();
		if(((item.getItemProperty("geburtsjahr").getValue()!=null) && ((item.getItemProperty("geburtsjahr").getValue().toString()!="")))) geburtsjahr.setValue(item.getItemProperty("geburtsjahr").getValue().toString());
		geburtsjahr.setCaption("Geburtsjahr:");
		vl_basic.addComponent(geburtsjahr);

		VerticalLayout vl_contact = new VerticalLayout();
		hl_popup.addComponent(vl_contact);
		vl_contact.setSpacing(true);
		//Einbinden der E-Mail Adresse
		Label label_email = new Label();
		if(((item.getItemProperty("email").getValue()!=null) && ((item.getItemProperty("email").getValue().toString()!="")))) label_email.setValue(item.getItemProperty("email").getValue().toString());
		label_email.setCaption("E-Mail:");
		vl_contact.addComponent(label_email);
		//Einbinden der Telefonnummer
		Label label_telefonnummer = new Label();
		if(((item.getItemProperty("telefonnummer").getValue()!=null) && ((item.getItemProperty("telefonnummer").getValue().toString()!="")))) label_telefonnummer.setValue(item.getItemProperty("telefonnummer").getValue().toString());
		label_telefonnummer.setCaption("Telefonnummer:");
		vl_contact.addComponent(label_telefonnummer);
		//Einbinden der PLZ
		Label label_ort = new Label();
		if(((item.getItemProperty("plz").getValue()!=null) && ((item.getItemProperty("plz").getValue().toString()!="")))) label_ort.setValue((item.getItemProperty("plz").getValue().toString()));
		label_ort.setCaption("Wohnort:");
		vl_popup.addComponent(label_ort);
		//Einbinden der Hobbies
		Label label_hobbies = new Label();
		if(((item.getItemProperty("hobbies").getValue()!=null) && ((item.getItemProperty("hobbies").getValue().toString()!="")))) label_hobbies.setValue(item.getItemProperty("hobbies").getValue().toString());
		label_hobbies.setCaption("Hobbys:");
		vl_popup.addComponent(label_hobbies);
		//Einbinden der Zusatzqualifikationen
		Label label_zusatzqualifikationen = new Label();
		if(((item.getItemProperty("zusatzqualifikationen").getValue()!=null) && ((item.getItemProperty("zusatzqualifikationen").getValue().toString()!="")))) label_zusatzqualifikationen.setValue(item.getItemProperty("zusatzqualifikationen").getValue().toString());
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
		//Einbinden der Deutschnote
		HorizontalLayout hl_deutsch = new HorizontalLayout();
		vl_noten.addComponent(hl_deutsch);
		hl_deutsch.setSizeFull();
		hl_deutsch.setSpacing(true);
		
		Label label_deutsch_text = new Label("Deutsch");
		hl_deutsch.addComponent(label_deutsch_text);
		
		hl_deutsch.addComponent(new Label("&nbsp", ContentMode.HTML));
		
		Label label_deutsch = new Label();
		if(((item.getItemProperty("note_deutsch").getValue()!=null) && ((item.getItemProperty("note_deutsch").getValue().toString()!="")))) label_deutsch.setValue(item.getItemProperty("note_deutsch").getValue().toString());
		hl_deutsch.addComponent(label_deutsch);
		
		HorizontalLayout hl_englisch = new HorizontalLayout();
		vl_noten.addComponent(hl_englisch);
		hl_englisch.setSizeFull();
		hl_englisch.setSpacing(true);
		//Einbinden der Englischnote
		Label label_englisch_text = new Label("Englisch");
		hl_englisch.addComponent(label_englisch_text);
		
		hl_englisch.addComponent(new Label("&nbsp", ContentMode.HTML));
		
		Label label_englisch = new Label();
		if(((item.getItemProperty("note_englisch").getValue()!=null) && ((item.getItemProperty("note_englisch").getValue().toString()!="")))) label_englisch.setValue(item.getItemProperty("note_englisch").getValue().toString());
		hl_englisch.addComponent(label_englisch);
		
		HorizontalLayout hl_mathe = new HorizontalLayout();
		vl_noten.addComponent(hl_mathe);
		hl_mathe.setSizeFull();
		hl_mathe.setSpacing(true);
		//Einbinden der Mathenote
		Label label_mathe_text = new Label("Mathe");
		hl_mathe.addComponent(label_mathe_text);
		
		hl_mathe.addComponent(new Label("&nbsp", ContentMode.HTML));
		
		Label label_mathe = new Label();
		if(((item.getItemProperty("note_mathe").getValue()!=null) && ((item.getItemProperty("note_mathe").getValue().toString()!="")))) label_mathe.setValue(item.getItemProperty("note_mathe").getValue().toString());
		hl_mathe.addComponent(label_mathe);
		
		HorizontalLayout hl_noten_gesamt = new HorizontalLayout();
		vl_noten.addComponent(hl_noten_gesamt);
		hl_noten_gesamt.setSizeFull();
		hl_noten_gesamt.setSpacing(true);
		//Einbinden des Zeugnisschnitt
		Label label_noten_gesamt_text = new Label("Zeugnisschnitt");
		hl_noten_gesamt.addComponent(label_noten_gesamt_text);
		
		hl_noten_gesamt.addComponent(new Label("&nbsp", ContentMode.HTML));
		
		Label label_noten_gesamt = new Label();
		if(((item.getItemProperty("zeugnisschnitt").getValue()!=null) && ((item.getItemProperty("zeugnisschnitt").getValue().toString()!="")))) label_noten_gesamt.setValue(item.getItemProperty("zeugnisschnitt").getValue().toString());
		hl_noten_gesamt.addComponent(label_noten_gesamt);
		
		VerticalLayout vl_auswahl = new VerticalLayout();
		hl_information.addComponent(vl_auswahl);
		vl_auswahl.setSizeFull();
		vl_auswahl.setSpacing(true);
		//Einbinden des Lieblingsfach
		Label label_lieblingsfaecher = new Label();
		if(((item.getItemProperty("lieblingsfach").getValue()!=null) && ((item.getItemProperty("lieblingsfach").getValue().toString()!="")))) label_lieblingsfaecher.setValue(item.getItemProperty("lieblingsfach").getValue().toString());
		label_lieblingsfaecher.setCaption("Lieblingsfach:");
		vl_auswahl.addComponent(label_lieblingsfaecher);
		//Einbinden der Wunschstudienrichtung
		Label label_studiengang = new Label();
		if(((item.getItemProperty("studiengang").getValue()!=null) && ((item.getItemProperty("studiengang").getValue().toString()!="")))) label_studiengang.setValue(item.getItemProperty("studiengang").getValue().toString());
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