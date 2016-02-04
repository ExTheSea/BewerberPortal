package com.example.bewerberportal;

import java.sql.SQLException;
import java.util.ArrayList;

import com.example.data.DatabaseConnector;
import com.example.data.TableQuery;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

public class AnsprechpartnerPopUp extends Window {
	
	
	private SQLContainer cont_anpartner = null;
	private SQLContainer cont_standort = null;
	
	public AnsprechpartnerPopUp(String firmenid) {
	
	VerticalLayout vl_popup = new VerticalLayout();
	vl_popup.setMargin(true);
	vl_popup.setSpacing(true);
	vl_popup.setSizeFull();
	setContent(vl_popup);
	

	TableQuery tq_standort = new TableQuery("standort", DatabaseConnector.getPool());
	try {
		cont_standort = new SQLContainer(tq_standort);
	} catch (SQLException e) {
		e.printStackTrace();
	}

    cont_standort.addContainerFilter(new Like("firmenprofil_id", firmenid));
    
    
    
	TableQuery tq_anpartner = new TableQuery("ansprechpartner", DatabaseConnector.getPool());
	try {
		cont_anpartner = new SQLContainer(tq_anpartner);
	} catch (SQLException e) {
		e.printStackTrace();
	}
	
	//cont_anpartner.addContainerFilter(new Like("id", cont_standort));
    
	
	
	
	
	
	
	
	
    ComboBox dropdown_anpartner = new ComboBox("Ansprechpartner", cont_anpartner);
	dropdown_anpartner.setNullSelectionAllowed(false);
	//dropdown_anpartner.setValue(new RowId((Integer)item.getItemProperty("studiengang_id").getValue()));
	dropdown_anpartner.setWidth("100%");
	dropdown_anpartner.setItemCaptionPropertyId("name");
	dropdown_anpartner.addValidator(new Validator() {

		private static final long serialVersionUID = 1L;

		@Override
		public void validate(Object value) throws InvalidValueException {
			if(dropdown_anpartner.getValue()==null) throw new InvalidValueException("Ansprechpartner wählen");
		}
	});
	
	
	HorizontalLayout hl_btns = new HorizontalLayout();
	Button btn_choose = new Button("Auswählen");
	btn_choose.setStyleName(ValoTheme.BUTTON_FRIENDLY);
	btn_choose.setWidth("100%");
	
	btn_choose.addClickListener(new Button.ClickListener() {
		
		@Override
		public void buttonClick(ClickEvent event) {
			close();

		}
	});
	
	
	hl_btns.addComponent(btn_choose);
	
	TextField tf_name = new TextField("Name:");
	TextField tf_email = new TextField("E-Mail:");
	TextField tf_tele = new TextField("Telefonnummer:");
	
	tf_name.setReadOnly(true);
	tf_email.setReadOnly(true);
	tf_tele.setReadOnly(true);
	
	vl_popup.addComponent(dropdown_anpartner);
	vl_popup.addComponent(tf_name);
	vl_popup.addComponent(tf_email);
	vl_popup.addComponent(tf_tele);
	vl_popup.addComponent(hl_btns);
	
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
