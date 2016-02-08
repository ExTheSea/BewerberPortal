package com.example.bewerberportal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import com.example.bewerberportal.PopupLöschen.DeleteListener;
import com.example.data.DatabaseConnector;
import com.example.data.GeoHelper;
import com.sun.xml.internal.ws.encoding.soap.SOAP12Constants;
import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class FirmenProfil extends Panel implements View, Receiver, SucceededListener{


	File  file;
	Upload upload;
	
	boolean edit = false;
	
	SQLContainer cont_firma = null;
	SQLContainer cont_standort = null;
	SQLContainer cont_anpartner = null;

	FieldGroup binder_firma;

	String benutzer_id;
	String firmenprofil_id;
	int index = 0;
	VerticalLayout vl_fir = new VerticalLayout();

	public FirmenProfil(String benutzer_id) {

		this.benutzer_id = benutzer_id;
		String ansprechpartner_id;
		setSizeFull();
		setStyleName(ValoTheme.PANEL_BORDERLESS);
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
			try {
				DatabaseConnector.getPool().releaseConnection(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		TableQuery tq_firma = new TableQuery("firmenprofil", DatabaseConnector.getPool());
		try {
			cont_firma = new SQLContainer(tq_firma);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		cont_firma.addContainerFilter(new Like("id", firmenprofil_id));
		Item item_firma = null;
		if (cont_firma.size() > 0) {
			item_firma = cont_firma.getItem(cont_firma.firstItemId());
		}

		binder_firma = new FieldGroup(item_firma);

		vl_fir.addComponent(buildFirma(), index++);

		TableQuery tq_standort = new TableQuery("standort", DatabaseConnector.getPool());
		try {
			cont_standort = new SQLContainer(tq_standort);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		TableQuery tq_ansprechpartner = new TableQuery("ansprechpartner", DatabaseConnector.getPool());
		try {
			cont_anpartner = new SQLContainer(tq_ansprechpartner);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		cont_standort.addContainerFilter(new Like("firmenprofil_id", firmenprofil_id));
		Item item_standort = null;
		for (Iterator it_standorte = cont_standort.getItemIds().iterator(); it_standorte.hasNext();) {
			Object itemID = (Object) it_standorte.next();
			item_standort = cont_standort.getItem(itemID);
			ansprechpartner_id = item_standort.getItemProperty("ansprechpartner_id").getValue().toString();
			vl_fir.addComponent(buildStandort(ansprechpartner_id, item_standort), index++);
		}

		vl_fir.addComponent(buildAddStandort());

	}

	public Panel buildFirma() {

		Panel pnl_firma = new Panel("Firma");
		pnl_firma.setWidth("100%");
		FormLayout formfirma = new FormLayout();
		formfirma.setWidth("100%");
		formfirma.setMargin(true);
		formfirma.setSpacing(true);
		pnl_firma.setContent(formfirma);

		HorizontalLayout hl_firma = new HorizontalLayout();
		hl_firma.setWidth("100%");
		Button btn_edit = new Button("Bearbeiten");
		btn_edit.setIcon(FontAwesome.PENCIL);
		formfirma.addComponent(hl_firma);
		hl_firma.addComponent(btn_edit);
		hl_firma.setComponentAlignment(btn_edit, Alignment.TOP_RIGHT);

		Field<?> namefield;
		Field<?> webfield;
		// Field<?> logofield;

		formfirma.addComponent(namefield = binder_firma.buildAndBind("Name", "name"));
		formfirma.addComponent(webfield = binder_firma.buildAndBind("Website", "website"));
		// formfirma.addComponent(logofield = binder.buildAndBind("Logo","logo"));
		
		//ImageUploader receiver = new ImageUploader();
		upload = new Upload();
		upload.setReceiver(this);
	    upload.setButtonCaption("Logo hochladen");
	    upload.addSucceededListener(this);
	    
	    
		formfirma.addComponent(upload);
		
		namefield.setWidth("100%");
		webfield.setWidth("100%");

		namefield.setReadOnly(true);
		webfield.setReadOnly(true);

		upload.setVisible(false);

		HorizontalLayout hl_botbtns = new HorizontalLayout();
		hl_botbtns.setWidth("100%");
		hl_botbtns.setSpacing(true);
		Button btn_save = new Button("Speichern");

		btn_save.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		hl_botbtns.addComponent(btn_save);
		hl_botbtns.setExpandRatio(btn_save, 1f);
		hl_botbtns.setComponentAlignment(btn_save, Alignment.MIDDLE_RIGHT);
		Button btn_cancel = new Button("Abbrechen");
		btn_cancel.setIcon(FontAwesome.CLOSE);
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
				upload.setVisible(true);
				hl_botbtns.setVisible(true);
				edit = true;
			}
		});

		// Cancel Button
		btn_cancel.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				namefield.setReadOnly(true);
				webfield.setReadOnly(true);
				upload.setVisible(false);
				btn_edit.setEnabled(true);
				hl_botbtns.setVisible(false);
				binder_firma.discard();
				edit = false;
			}
		});

		// Save Button
		btn_save.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if (binder_firma.isValid()) {
						binder_firma.commit();
						cont_firma.commit();
						namefield.setReadOnly(true);
						webfield.setReadOnly(true);
						upload.setVisible(false);
						btn_edit.setEnabled(true);
						hl_botbtns.setVisible(false);
						edit = false;
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

	public Panel buildStandort(String ansprechpartner_id, Item item_standort) {
		
		FieldGroup binder_anpartner;
		FieldGroup binder_standort;

		Panel pnl_ort = new Panel("Standort");
		pnl_ort.setWidth("100%");
		FormLayout formort = new FormLayout();
		formort.setWidth("100%");
		formort.setMargin(true);
		formort.setSpacing(true);
		pnl_ort.setContent(formort);

		HorizontalLayout hl_ort = new HorizontalLayout();
		hl_ort.setWidth("100%");
		Button btn_edit = new Button("Bearbeiten");
		btn_edit.setIcon(FontAwesome.PENCIL);
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


		binder_standort = new FieldGroup(item_standort);
		formort.addComponent(aliasfield = binder_standort.buildAndBind("Alias", "alias"));
		formort.addComponent(strassefield = binder_standort.buildAndBind("Straße", "strasse"));
		formort.addComponent(plzfield = binder_standort.buildAndBind("PLZ", "plz"));
		formort.addComponent(ortfield = binder_standort.buildAndBind("Ort", "ort"));

		cont_anpartner.removeAllContainerFilters();
		cont_anpartner.addContainerFilter(new Like("id", ansprechpartner_id));
		Item item_ansprechpartner = null;
		if (cont_anpartner.size() > 0) {
			item_ansprechpartner = cont_anpartner.getItem(cont_anpartner.firstItemId());
		}

		binder_anpartner = new FieldGroup(item_ansprechpartner);
		formort.addComponent(anpartnerfield = binder_anpartner.buildAndBind("Ansprechpartner", "name"));
		formort.addComponent(mailfield = binder_anpartner.buildAndBind("E-Mail", "email"));
		formort.addComponent(telefield = binder_anpartner.buildAndBind("Telefonnummer", "telefonnummer"));
		
		((TextField)plzfield).addValidator((new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				
				try {
					
				  	Integer.parseInt(value.toString());
				  	
					if ((value == null) || (value == "")){
						throw new InvalidValueException("Feld kann nicht leer sein");
					}
					else {
						if (value.toString().length() != 5) {
							throw new InvalidValueException("Postleitzahlen haben 5 Stellen");
						}
					}
				  	
		        } catch (NumberFormatException e) {
		        	throw new InvalidValueException("Nur Ziffern verwenden");	
		        }
			}
		}));

		aliasfield.setReadOnly(true);
		strassefield.setReadOnly(true);
		plzfield.setReadOnly(true);
		ortfield.setReadOnly(true);
		anpartnerfield.setReadOnly(true);
		mailfield.setReadOnly(true);
		telefield.setReadOnly(true);
		
		aliasfield.setWidth("100%");
		strassefield.setWidth("100%");
		plzfield.setWidth("100%");
		ortfield.setWidth("100%");
		anpartnerfield.setWidth("100%");
		mailfield.setWidth("100%");
		telefield.setWidth("100%");

		HorizontalLayout hl_botbtns = new HorizontalLayout();
		hl_botbtns.setWidth("100%");
		hl_botbtns.setSpacing(true);
		Button btn_delete = new Button("Löschen");
		btn_delete.setStyleName(ValoTheme.BUTTON_DANGER);
		hl_botbtns.addComponent(btn_delete);
		hl_botbtns.setExpandRatio(btn_delete, 1f);
		hl_botbtns.setComponentAlignment(btn_delete, Alignment.MIDDLE_LEFT);
		Button btn_save = new Button("Speichern");
		btn_save.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		hl_botbtns.addComponent(btn_save);
		hl_botbtns.setExpandRatio(btn_save, 1f);
		hl_botbtns.setComponentAlignment(btn_save, Alignment.MIDDLE_RIGHT);
		Button btn_cancel = new Button("Abbrechen");
		btn_cancel.setIcon(FontAwesome.CLOSE);
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
				plzfield.setReadOnly(false);
				ortfield.setReadOnly(false);
				anpartnerfield.setReadOnly(false);
				mailfield.setReadOnly(false);
				telefield.setReadOnly(false);
				hl_botbtns.setVisible(true);
				edit = true;
			}
		});

		// Cancel Button
		btn_cancel.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				aliasfield.setReadOnly(true);
				strassefield.setReadOnly(true);
				plzfield.setReadOnly(true);
				ortfield.setReadOnly(true);
				anpartnerfield.setReadOnly(true);
				mailfield.setReadOnly(true);
				telefield.setReadOnly(true);
				hl_botbtns.setVisible(false);
				binder_standort.discard();
				binder_anpartner.discard();
				edit = false;
			}
		});
		
		// Delete Button
		btn_delete.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
						     
				new PopupLöschen("der Standort", new DeleteListener() {
					
					@Override
					public void delete() {
						// TODO Auto-generated method stub
						
						Connection con_delete = null;
					    Statement statement_delete = null;
					      	try {
					        	con_delete = DatabaseConnector.getPool().reserveConnection();
					        	statement_delete = con_delete.createStatement();
					        	statement_delete.execute("DELETE FROM standort WHERE id = '"+item_standort.getItemProperty("id").getValue().toString()+"'");
					        	statement_delete.execute("DELETE FROM ansprechpartner WHERE id = '"+item_standort.getItemProperty("ansprechpartner_id").getValue().toString()+"'");
					        	statement_delete.execute("COMMIT");
					        	con_delete.commit();
					        	cont_standort.refresh();
					        	edit = false;
								vl_fir.removeComponent(pnl_ort);
								index--;
							} catch (SQLException e1) {
								e1.printStackTrace();
							}finally {
								try {
									statement_delete.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
								try {
									con_delete.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
								try {
									DatabaseConnector.getPool().releaseConnection(con_delete);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						
					}
					
					@Override
					public void close() {
						// TODO Auto-generated method stub
						
					}
				});
			}
				
		});

		// Save Button
		btn_save.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {	
					
					if (binder_standort.isValid() && binder_anpartner.isValid()) {
						binder_standort.commit();
						binder_anpartner.commit();
						
						String[] koord = GeoHelper.getKoordinaten(binder_standort.getField("strasse").getValue()+" "+binder_standort.getField("plz").getValue()+" "+binder_standort.getField("ort").getValue());
						item_standort.getItemProperty("lat").setValue(koord[0]);
						item_standort.getItemProperty("lng").setValue(koord[1]);
						cont_standort.commit();
						cont_anpartner.removeAllContainerFilters();
						for (Iterator it_anspr = cont_anpartner.getItemIds().iterator(); it_anspr.hasNext();) {
							Object itemID = (Object) it_anspr.next();
						}
						cont_anpartner.commit();
						aliasfield.setReadOnly(true);
						strassefield.setReadOnly(true);
						plzfield.setReadOnly(true);
						ortfield.setReadOnly(true);
						anpartnerfield.setReadOnly(true);
						mailfield.setReadOnly(true);
						telefield.setReadOnly(true);
						btn_edit.setEnabled(true);
						hl_botbtns.setVisible(false);
			        	edit = false;

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
    	

		Button btn_standort = new Button(" Standort");
		btn_standort.setIcon(FontAwesome.PLUS_CIRCLE);
		btn_standort.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				createStandort();
			}
		});

		HorizontalLayout hl_button = new HorizontalLayout();
		hl_button.setWidth("100%");
		formbutton.addComponent(hl_button);
		hl_button.addComponent(btn_standort);
		hl_button.setComponentAlignment(btn_standort, Alignment.BOTTOM_CENTER);

		formbutton.addComponent(hl_button);

		return pnl_button;
	}

	public void createStandort() {
		
		edit = true;

		Panel pnl_ort = new Panel("Standort");
		pnl_ort.setWidth("100%");
		FormLayout formort = new FormLayout();
		formort.setWidth("100%");
		formort.setMargin(true);
		formort.setSpacing(true);
		pnl_ort.setContent(formort);
		

		HorizontalLayout hl_ort = new HorizontalLayout();
		hl_ort.setWidth("100%");
		Button btn_edit = new Button("Bearbeiten");
		btn_edit.setIcon(FontAwesome.PENCIL);
		formort.addComponent(hl_ort);
		hl_ort.addComponent(btn_edit);
		hl_ort.setComponentAlignment(btn_edit, Alignment.TOP_RIGHT);

		TextField tf_alias = new TextField("Alias");
		TextField tf_strasse = new TextField("Straße");
		TextField tf_plz = new TextField("PLZ");
		TextField tf_ort = new TextField("Ort");
		//Button btn_anpartner = new Button("Ansprechpartner wählen");
		TextField tf_anpartner = new TextField("Ansprechpartner");
		TextField tf_mail = new TextField("E-Mail");
		TextField tf_tele = new TextField("Telefonnummer");
		
		/*btn_anpartner.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				new AnsprechpartnerPopUp(firmenprofil_id);
			}
		});
*/

		tf_alias.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				if ((tf_alias.getValue() == null) || (tf_alias.getValue().toString() == ""))
					throw new InvalidValueException("Feld kann nicht leer sein");
			}
		});

		tf_strasse.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				if ((tf_strasse.getValue() == null) || (tf_strasse.getValue().toString() == ""))
					throw new InvalidValueException("Feld kann nicht leer sein");
			}
		});
		tf_anpartner.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				if ((tf_anpartner.getValue() == null) || (tf_anpartner.getValue().toString() == ""))
					throw new InvalidValueException("Feld kann nicht leer sein");
			}
		});
		tf_mail.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				if ((tf_mail.getValue() == null) || (tf_mail.getValue().toString() == ""))
					throw new InvalidValueException("Feld kann nicht leer sein");
			}
		});
		tf_tele.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				if ((tf_tele.getValue() == null) || (tf_tele.getValue().toString() == ""))
					throw new InvalidValueException("Feld kann nicht leer sein");
			}
		});
		tf_ort.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				if ((tf_ort.getValue() == null) || (tf_ort.getValue().toString() == ""))
					throw new InvalidValueException("Feld kann nicht leer sein");
			}
		});
		tf_plz.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				
				try {
					
				  	Integer.parseInt(value.toString());
				  	
					if ((value == null) || (value == "")){
						throw new InvalidValueException("Feld kann nicht leer sein");
					}
					else {
						if (value.toString().length() != 5) {
							throw new InvalidValueException("Postleitzahlen haben 5 Stellen");
						}
					}
				  	
		        } catch (NumberFormatException e) {
		        	throw new InvalidValueException("Nur Ziffern verwenden");	
		        }
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
		formort.addComponent(tf_plz);
		formort.addComponent(tf_ort);
		//formort.addComponent(btn_anpartner);
		formort.addComponent(tf_anpartner);
		formort.addComponent(tf_mail);
		formort.addComponent(tf_tele);
		
		tf_alias.setWidth("100%");
		tf_strasse.setWidth("100%");
		tf_plz.setWidth("100%");
		tf_ort.setWidth("100%");
		tf_anpartner.setWidth("100%");
		tf_mail.setWidth("100%");
		tf_tele.setWidth("100%");

		HorizontalLayout hl_botbtns = new HorizontalLayout();
		hl_botbtns.setWidth("100%");
		hl_botbtns.setSpacing(true);
		Button btn_save = new Button("Speichern");
		btn_save.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		hl_botbtns.addComponent(btn_save);
		hl_botbtns.setExpandRatio(btn_save, 1f);
		hl_botbtns.setComponentAlignment(btn_save, Alignment.MIDDLE_RIGHT);
		Button btn_cancel = new Button("Abbrechen");
		btn_cancel.setIcon(FontAwesome.CLOSE);
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
		    	edit = false;
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

				if (tf_alias.isValid() & tf_strasse.isValid() & tf_anpartner.isValid() & tf_mail.isValid()
						& tf_tele.isValid() & tf_ort.isValid() & tf_plz.isValid()) {

					String[] koord = GeoHelper.getKoordinaten(tf_strasse.getValue()+" "+tf_plz.getValue()+" "+tf_ort.getValue());
					Connection con_save = null;
					Statement statement_save = null;
					try {
						
						con_save = DatabaseConnector.getPool().reserveConnection();
						statement_save = con_save.createStatement();
						statement_save.execute("INSERT INTO ansprechpartner (name, email, telefonnummer) VALUES ('"+tf_anpartner.getValue().toString()+"', '"+tf_mail.getValue().toString()+"','"+tf_tele.getValue().toString()+"')");
						ResultSet rs = statement_save.executeQuery("SELECT id FROM ansprechpartner WHERE name='"+tf_anpartner.getValue().toString()+"' AND email='"+tf_mail.getValue().toString()+"' AND telefonnummer='"+tf_tele.getValue().toString()+"'");
						rs.first();
						String anpartner_id = rs.getString("id");
						statement_save.execute("INSERT INTO standort (firmenprofil_id, ansprechpartner_id, alias, strasse, ort, plz, lat, lng) VALUES ('"+firmenprofil_id+"', '"+rs.getString("id")+"', '"+tf_alias.getValue().toString()+"', '"+tf_strasse.getValue().toString()+"', '"+tf_ort.getValue().toString()+"', '"+ tf_plz.getValue().toString()+"', '"+koord[0]+"', '"+koord[1]+"')");

						con_save.commit();
						try {
							
							ResultSet rs2 = statement_save.executeQuery("SELECT id FROM standort WHERE alias='"+tf_alias.getValue().toString()+"' AND strasse='"+tf_strasse.getValue().toString()+"' AND plz='"+tf_plz.getValue().toString()+"' AND ort='"+tf_ort.getValue().toString()+"'");
							rs2.first();
							cont_standort.removeAllContainerFilters();
							cont_standort.addContainerFilter((new Like("id", rs2.getString("id"))));
							Item item_add = cont_standort.getItem(cont_standort.firstItemId());						
							vl_fir.removeComponent(pnl_ort);
							index--;
							vl_fir.addComponent(buildStandort(anpartner_id, item_add), index++);
							
							} catch (SQLException e) {
								e.printStackTrace();
							}
						
						
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						try {
							statement_save.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						try {
							con_save.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						try {
							DatabaseConnector.getPool().releaseConnection(con_save);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					


				}

			}
		});
		
    	edit = false;
		vl_fir.addComponent(pnl_ort, index++);

	}
	
	public OutputStream receiveUpload(String filename,
            String mimeType) {
		
		 // Create upload stream
        FileOutputStream fos = null; // Stream to write to
        try {
            // Open the file for writing.
            file = new File("/tmp/uploads/" + filename);
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            new Notification("Could not open file<br/>",
                             e.getMessage(),
                             Notification.Type.ERROR_MESSAGE)
                .show(Page.getCurrent());
            return null;
        }
        return fos;
		
	}
	

    @Override
    public void uploadSucceeded(SucceededEvent event) {

        //upload.setSource(new FileResource(file));
    }

	@Override
	public void enter(ViewChangeEvent event) {

	}

}
