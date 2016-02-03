package com.example.bewerberportal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.example.data.DatabaseConnector;
import com.sun.xml.internal.ws.encoding.soap.SOAP12Constants;
import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class FirmenProfil extends Panel implements View {

	SQLContainer cont = null;
	FieldGroup binder;
	String benutzer_id;
	String firmenprofil_id;
	int index = 0;
	VerticalLayout vl_fir = new VerticalLayout();

	public FirmenProfil(String benutzer_id) {

		this.benutzer_id = benutzer_id;
		String ansprechpartner_id;
		setSizeFull();
		setStyleName(ValoTheme.PANEL_BORDERLESS);
		//VerticalLayout vl_fir = new VerticalLayout();
		setContent(vl_fir);
		vl_fir.setHeight(null);
		vl_fir.setMargin(true);
		vl_fir.setSpacing(true);

		Connection con = null;
		Statement statement = null;

		try {
			con = DatabaseConnector.getPool().reserveConnection();
			statement = con.createStatement();
			ResultSet rs = statement
					.executeQuery("SELECT * FROM go2dhbw.benutzer_firmenprofil WHERE benutzer_id = " + benutzer_id);
			System.out.println("SELECT * FROM go2dhbw.benutzer_firmenprofil WHERE benutzer_id = " + benutzer_id);
			rs.first();
			firmenprofil_id = rs.getString("firmenprofil_id");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		TableQuery tq_firma = new TableQuery("firmenprofil", DatabaseConnector.getPool());
		try {
			cont = new SQLContainer(tq_firma);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		cont.addContainerFilter(new Like("id", firmenprofil_id));
		Item item_firma = null;
		if (cont.size() > 0) {
			item_firma = cont.getItem(cont.firstItemId());
		}
		//firmenprofil_id = item1.getItemProperty("id").getValue().toString();

		binder = new FieldGroup(item_firma);
		

		vl_fir.addComponent(buildFirma(), index++);
		
		
		
		

		TableQuery tq_standort = new TableQuery("standort", DatabaseConnector.getPool());
		try {
			cont = new SQLContainer(tq_standort);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		cont.addContainerFilter(new Like("firmenprofil_id", firmenprofil_id));
		Item item_standort = null;
		if (cont.size() > 0) {
			item_standort = cont.getItem(cont.firstItemId());
			binder = new FieldGroup(item_standort);
			ansprechpartner_id = item_standort.getItemProperty("ansprechpartner_id").getValue().toString();
			vl_fir.addComponent(buildStandort(ansprechpartner_id), index++);
			
			for (int x = 1; x < cont.size(); x++) {
				
				cont.getItem(x);
				item_standort = cont.getItem(cont.nextItemId(item_standort.getItemProperty("id")));
				System.out.println(cont.nextItemId(item_standort.getItemProperty("id")));
				System.out.println(cont.nextItemId(item_standort.getItemPropertyIds()));
				System.out.println(cont.nextItemId(item_standort));
				binder = new FieldGroup(item_standort);
				ansprechpartner_id = item_standort.getItemProperty("ansprechpartner_id").getValue().toString();
				vl_fir.addComponent(buildStandort(ansprechpartner_id), index++);
				
			}
		}
		
		System.out.println(cont.size());
		vl_fir.addComponent(buildAddStandort());

	}
	
	
	
	
	

	public Panel buildFirma() {

		Panel pnl_firma = new Panel();
		pnl_firma.setWidth("100%");
		FormLayout formfirma = new FormLayout();
		formfirma.setWidth("100%");
		formfirma.setMargin(true);
		formfirma.setSpacing(true);
		pnl_firma.setContent(formfirma);

		HorizontalLayout hl_firma = new HorizontalLayout();
		hl_firma.setWidth("100%");
		Button btn_edit = new Button("Bearbeiten");
		formfirma.addComponent(hl_firma);
		hl_firma.addComponent(btn_edit);
		hl_firma.setComponentAlignment(btn_edit, Alignment.TOP_RIGHT);

		Field<?> namefield;
		Field<?> webfield;
		//Field<?> logofield;

		formfirma.addComponent(namefield = binder.buildAndBind("Name", "name"));
		formfirma.addComponent(webfield = binder.buildAndBind("Website", "website"));
		//formfirma.addComponent(logofield = binder.buildAndBind("Logo", "logo"));
		
		namefield.setReadOnly(true);
		webfield.setReadOnly(true);
		//logofield.setReadOnly(true);

		HorizontalLayout hl_botbtns = new HorizontalLayout();
		hl_botbtns.setWidth("100%");
		hl_botbtns.setSpacing(true);
		Button btn_save = new Button("Speichern");
		btn_save.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		hl_botbtns.addComponent(btn_save);
		hl_botbtns.setExpandRatio(btn_save, 1f);
		hl_botbtns.setComponentAlignment(btn_save, Alignment.MIDDLE_RIGHT);
		Button btn_cancel = new Button("Abbrechen");
		btn_cancel.setStyleName(ValoTheme.BUTTON_QUIET);
		hl_botbtns.addComponent(btn_cancel);
		hl_botbtns.setVisible(false);
		formfirma.addComponent(hl_botbtns);

		// Edit Button
		btn_edit.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				namefield.setReadOnly(false);
				webfield.setReadOnly(false);
				//logofield.setReadOnly(false);
				hl_botbtns.setVisible(true);
			}
		});

		// Cancel Button
		btn_cancel.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				namefield.setReadOnly(true);
				webfield.setReadOnly(true);
				//logofield.setReadOnly(true);
				btn_edit.setEnabled(true);
				hl_botbtns.setVisible(false);
				binder.discard();
			}
		});

		// Save Button
		btn_save.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if (binder.isValid()) {
						binder.commit();
						cont.commit();
						namefield.setReadOnly(true);
						webfield.setReadOnly(true);
						//logofield.setReadOnly(true);
						btn_edit.setEnabled(true);
						hl_botbtns.setVisible(false);
					}
				} catch (CommitException e) {
					e.printStackTrace();
				} catch (UnsupportedOperationException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		return pnl_firma;

	}

	public Panel buildStandort(String ansprechpartner_id) {
		
		SQLContainer cont_anpartner = null;
		
		Panel pnl_ort = new Panel();
		pnl_ort.setWidth("100%");
		FormLayout formort = new FormLayout();
		formort.setWidth("100%");
		formort.setMargin(true);
		formort.setSpacing(true);
		pnl_ort.setContent(formort);

		HorizontalLayout hl_ort = new HorizontalLayout();
		hl_ort.setWidth("100%");
		Button btn_edit = new Button("Bearbeiten");
		formort.addComponent(hl_ort);
		hl_ort.addComponent(btn_edit);
		hl_ort.setComponentAlignment(btn_edit, Alignment.TOP_RIGHT);

		Field<?> aliasfield;
		Field<?> strassefield;
		Field<?> plzfield;
		Field<?> ortfield;
		Field<?> anpartnerfield;
		Field<?> mailfield;
		Field<?> telefield;

		/*HorizontalLayout hl_ortfield = new HorizontalLayout();
		hl_ortfield.setWidth("40%");
		TextField tf_plz = new TextField();
		binder.bind(tf_plz, "plz");
		TextField tf_ort = new TextField();
		binder.bind(tf_ort, "ort");
		hl_ortfield.addComponent(tf_plz);
		hl_ortfield.addComponent(tf_ort);*/
		

		formort.addComponent(aliasfield = binder.buildAndBind("Alias", "alias"));
		formort.addComponent(strassefield = binder.buildAndBind("Straﬂe", "strasse"));
		//formort.addComponent(hl_ortfield);
		formort.addComponent(plzfield = binder.buildAndBind("PLZ", "plz"));
		formort.addComponent(ortfield = binder.buildAndBind("Ort", "ort"));
		
		
		TableQuery tq_ansprechpartner = new TableQuery("ansprechpartner", DatabaseConnector.getPool());
		try {
			cont_anpartner = new SQLContainer(tq_ansprechpartner);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		cont_anpartner.addContainerFilter(new Like("id", ansprechpartner_id));
		Item item_ansprechpartner = null;
		if (cont_anpartner.size() > 0) {
			item_ansprechpartner = cont_anpartner.getItem(cont.firstItemId());
		}
		
		binder = new FieldGroup(item_ansprechpartner);
		
		formort.addComponent(anpartnerfield = binder.buildAndBind("Ansprechpartner", "name"));
		formort.addComponent(mailfield = binder.buildAndBind("E-Mail", "email"));
		formort.addComponent(telefield = binder.buildAndBind("Telefonnummer", "telefonnummer"));
		

		aliasfield.setReadOnly(true);
		strassefield.setReadOnly(true);
		//tf_ort.setReadOnly(true);
		//tf_plz.setReadOnly(true);
		plzfield.setReadOnly(true);
		ortfield.setReadOnly(true);
		anpartnerfield.setReadOnly(true);
		mailfield.setReadOnly(true);
		telefield.setReadOnly(true);

		HorizontalLayout hl_botbtns = new HorizontalLayout();
		hl_botbtns.setWidth("100%");
		hl_botbtns.setSpacing(true);
		Button btn_save = new Button("Speichern");
		btn_save.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		hl_botbtns.addComponent(btn_save);
		hl_botbtns.setExpandRatio(btn_save, 1f);
		hl_botbtns.setComponentAlignment(btn_save, Alignment.MIDDLE_RIGHT);
		Button btn_cancel = new Button("Abbrechen");
		btn_cancel.setStyleName(ValoTheme.BUTTON_QUIET);
		hl_botbtns.addComponent(btn_cancel);
		hl_botbtns.setVisible(false);
		formort.addComponent(hl_botbtns);

		// Edit Button
		btn_edit.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				aliasfield.setReadOnly(false);
				strassefield.setReadOnly(false);
				//tf_ort.setReadOnly(false);
				//tf_plz.setReadOnly(false);
				plzfield.setReadOnly(false);
				ortfield.setReadOnly(false);
				anpartnerfield.setReadOnly(false);
				mailfield.setReadOnly(false);
				telefield.setReadOnly(false);
				hl_botbtns.setVisible(true);
			}
		});

		// Cancel Button
		btn_cancel.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				aliasfield.setReadOnly(true);
				strassefield.setReadOnly(true);
				//tf_ort.setReadOnly(true);
				//tf_plz.setReadOnly(true);
				plzfield.setReadOnly(true);
				ortfield.setReadOnly(true);
				anpartnerfield.setReadOnly(true);
				mailfield.setReadOnly(true);
				telefield.setReadOnly(true);
				hl_botbtns.setVisible(false);
				binder.discard();
			}
		});

		// Save Button
		btn_save.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if (binder.isValid()) {
						binder.commit();
						cont.commit();
						aliasfield.setReadOnly(true);
						strassefield.setReadOnly(true);
						//tf_ort.setReadOnly(true);
						//tf_plz.setReadOnly(true);
						plzfield.setReadOnly(true);
						ortfield.setReadOnly(true);
						anpartnerfield.setReadOnly(true);
						mailfield.setReadOnly(true);
						telefield.setReadOnly(true);
						btn_edit.setEnabled(true);
						hl_botbtns.setVisible(false);
					}
				} catch (CommitException e) {
					e.printStackTrace();
				} catch (UnsupportedOperationException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		return pnl_ort;

	}
	
	public Panel buildAddStandort() {

		Panel pnl_button = new Panel();
		pnl_button.setWidth("100%");
		FormLayout formbutton = new FormLayout();
		formbutton.setWidth("100%");
		formbutton.setMargin(true);
		formbutton.setSpacing(true);
		pnl_button.setContent(formbutton);
		Button btn_standort = new Button("+ Standort");
		btn_standort.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				createStandort();
			}
		});
		formbutton.addComponent(btn_standort);
		formbutton.setComponentAlignment(btn_standort, Alignment.BOTTOM_CENTER);
		return pnl_button;
	}
	
	public void createStandort() {
		
		Panel pnl_ort = new Panel();
		pnl_ort.setWidth("100%");
		FormLayout formort = new FormLayout();
		formort.setWidth("100%");
		formort.setMargin(true);
		formort.setSpacing(true);
		pnl_ort.setContent(formort);

		HorizontalLayout hl_ort = new HorizontalLayout();
		hl_ort.setWidth("100%");
		Button btn_edit = new Button("Bearbeiten");
		formort.addComponent(hl_ort);
		hl_ort.addComponent(btn_edit);
		hl_ort.setComponentAlignment(btn_edit, Alignment.TOP_RIGHT);

		TextField tf_alias = new TextField("Alias");
		TextField tf_strasse = new TextField("Straﬂe");
		TextField tf_plz = new TextField("PLZ");
		TextField tf_ort = new TextField("Ort");
		TextField tf_anpartner = new TextField("Ansprechpartner");
		TextField tf_mail = new TextField("E-Mail");
		TextField tf_tele = new TextField("Telefonnummer");
		

		/*HorizontalLayout hl_ortfield = new HorizontalLayout();
		hl_ortfield.setWidth("40%");
		TextField tf_plz = new TextField();
		TextField tf_ort = new TextField();
		hl_ortfield.addComponent(tf_plz);
		hl_ortfield.addComponent(tf_ort);*/
		
		
		tf_alias.addValidator(new Validator() {
			
			@Override
			public void validate(Object value) throws InvalidValueException {
				if( ( tf_alias.getValue() == null ) || ( tf_alias.getValue().toString() == "" ) )
					throw new InvalidValueException("Feld kann nicht leer sein");
			}
		});

		tf_strasse.addValidator(new Validator() {
			
			@Override
			public void validate(Object value) throws InvalidValueException {
				if( ( tf_strasse.getValue() == null ) || ( tf_strasse.getValue().toString() == "" ) )
					throw new InvalidValueException("Feld kann nicht leer sein");
			}
		});
		tf_anpartner.addValidator(new Validator() {
			
			@Override
			public void validate(Object value) throws InvalidValueException {
				if( ( tf_anpartner.getValue() == null ) || ( tf_anpartner.getValue().toString() == "" ) )
					throw new InvalidValueException("Feld kann nicht leer sein");
			}
		});
		tf_mail.addValidator(new Validator() {
			
			@Override
			public void validate(Object value) throws InvalidValueException {
				if( ( tf_mail.getValue() == null ) || ( tf_mail.getValue().toString() == "" ) )
					throw new InvalidValueException("Feld kann nicht leer sein");
			}
		});
		tf_tele.addValidator(new Validator() {
			
			@Override
			public void validate(Object value) throws InvalidValueException {
				if( ( tf_tele.getValue() == null ) || ( tf_tele.getValue().toString() == "" ) )
					throw new InvalidValueException("Feld kann nicht leer sein");
			}
		});
		tf_ort.addValidator(new Validator() {
			
			@Override
			public void validate(Object value) throws InvalidValueException {
				if( ( tf_ort.getValue() == null ) || ( tf_ort.getValue().toString() == "" ) )
					throw new InvalidValueException("Feld kann nicht leer sein");
			}
		});
		tf_plz.addValidator(new Validator() {
			
			@Override
			public void validate(Object value) throws InvalidValueException {
				if( ( tf_plz.getValue() == null ) || ( tf_plz.getValue().toString() == "" ) )
					throw new InvalidValueException("Feld kann nicht leer sein");
			}
		});
		
		tf_alias.setValidationVisible(false);
		tf_strasse.setValidationVisible(false);
		tf_anpartner.setValidationVisible(false);
		tf_mail.setValidationVisible(false);
		tf_tele.setValidationVisible(false);
		tf_ort.setValidationVisible(false);
		tf_plz.setValidationVisible(false);
		
		formort.addComponent(tf_alias);
		formort.addComponent(tf_strasse);
		//formort.addComponent(hl_ortfield);
		formort.addComponent(tf_plz);
		formort.addComponent(tf_ort);
		formort.addComponent(tf_anpartner);
		formort.addComponent(tf_mail);
		formort.addComponent(tf_tele);
		

		
		HorizontalLayout hl_botbtns = new HorizontalLayout();
		hl_botbtns.setWidth("100%");
		hl_botbtns.setSpacing(true);
		Button btn_save = new Button("Speichern");
		btn_save.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		hl_botbtns.addComponent(btn_save);
		hl_botbtns.setExpandRatio(btn_save, 1f);
		hl_botbtns.setComponentAlignment(btn_save, Alignment.MIDDLE_RIGHT);
		Button btn_cancel = new Button("Abbrechen");
		btn_cancel.setStyleName(ValoTheme.BUTTON_QUIET);
		hl_botbtns.addComponent(btn_cancel);
		hl_botbtns.setVisible(true);
		formort.addComponent(hl_botbtns);



		// Cancel Button
		btn_cancel.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				vl_fir.removeComponent(pnl_ort);
				index--;
			}
		});

		// Save Button
		btn_save.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				tf_alias.setValidationVisible(true);
				tf_strasse.setValidationVisible(true);
				tf_anpartner.setValidationVisible(true);
				tf_mail.setValidationVisible(true);
				tf_tele.setValidationVisible(true);
				tf_ort.setValidationVisible(true);
				tf_plz.setValidationVisible(true);
				
				if(tf_alias.isValid()&tf_strasse.isValid() &tf_anpartner.isValid()
						&tf_mail.isValid()&tf_tele.isValid()&tf_ort.isValid()&tf_plz.isValid()) {
					//INSERT
					
				}
					
	
			}
		});
		
		vl_fir.addComponent(pnl_ort, index++);

	}

	@Override
	public void enter(ViewChangeEvent event) {
		
		

	}

}
