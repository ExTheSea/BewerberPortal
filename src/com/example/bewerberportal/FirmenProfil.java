package com.example.bewerberportal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.StreamVariable.StreamingEndEvent;
import com.vaadin.server.StreamVariable.StreamingErrorEvent;
import com.vaadin.server.StreamVariable.StreamingProgressEvent;
import com.vaadin.server.StreamVariable.StreamingStartEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class FirmenProfil extends Panel implements View {


	File  file;
	byte[] bas;
	
	SQLContainer cont_firma = null;
	SQLContainer cont_standort = null;
	SQLContainer cont_anpartner = null;
	SQLContainer cont_stellan = null;

	FieldGroup binder_firma;
	private Image firmen_logo;

	String benutzer_id;
	String firmenprofil_id;
	int index = 0;
	VerticalLayout vl_fir = new VerticalLayout();

	
	
	public FirmenProfil(String benutzer_id) {

		
		
		this.benutzer_id = benutzer_id;
		String ansprechpartner_id;
		
		//Bildschirmaufbau
		setSizeFull();
		setStyleName(ValoTheme.PANEL_BORDERLESS);
		setContent(vl_fir);
		vl_fir.setHeight(null);
		vl_fir.setMargin(true);
		vl_fir.setSpacing(true);

		//Selektion der Firmen_ID
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

		//Selektion & Filterung der Firmentabelle nach Firmen_ID
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

		//Hinzufügen eines Firmenpanels
		binder_firma = new FieldGroup(item_firma);

		vl_fir.addComponent(buildFirma(item_firma), index++);
		
		//Selektion der Standorttabelle
		TableQuery tq_standort = new TableQuery("standort", DatabaseConnector.getPool());
		try {
			cont_standort = new SQLContainer(tq_standort);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//Selektion  der Ansprechpartnertabelle
		TableQuery tq_ansprechpartner = new TableQuery("ansprechpartner", DatabaseConnector.getPool());
		try {
			cont_anpartner = new SQLContainer(tq_ansprechpartner);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//Selektion  der Stellenangebotstabelle
		com.example.data.TableQuery tq_stellenangebote = new com.example.data.TableQuery("firmensucheview", DatabaseConnector.getPool()) {

			private static final long serialVersionUID = 1L;

			public void fetchMetaData() {
                primaryKeyColumns= new ArrayList<String>();     
                primaryKeyColumns.add("id");
                super.fetchMetaData();
            };
		};
		try {
			cont_stellan = new SQLContainer(tq_stellenangebote);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//Filtern der Selektierten Standorttabelle nach Firmen_ID
		cont_standort.addContainerFilter(new Like("firmenprofil_id", firmenprofil_id));
		Item item_standort = null;
		//Hinzufügen aller gefundenen Standorte als Panels
		for (Iterator it_standorte = cont_standort.getItemIds().iterator(); it_standorte.hasNext();) {
			Object itemID = (Object) it_standorte.next();
			item_standort = cont_standort.getItem(itemID);
			ansprechpartner_id = item_standort.getItemProperty("ansprechpartner_id").getValue().toString();
			vl_fir.addComponent(buildStandort(ansprechpartner_id, item_standort), index++);
		}

		//Knopf zum erstellen neuer Standortpanels
		vl_fir.addComponent(buildAddStandort());

	}

	public Panel buildFirma(Item item_firma) {

		Panel pnl_firma = new Panel("Firma");
		pnl_firma.setWidth("100%");
		FormLayout formfirma = new FormLayout();
		formfirma.setWidth("100%");
		formfirma.setMargin(true);
		formfirma.setSpacing(true);
		pnl_firma.setContent(formfirma);

		//Bearbeiten Knopf
		HorizontalLayout hl_firma = new HorizontalLayout();
		hl_firma.setWidth("100%");
		Button btn_edit = new Button("Bearbeiten");
		btn_edit.setIcon(FontAwesome.PENCIL);
		formfirma.addComponent(hl_firma);
		hl_firma.addComponent(btn_edit);
		hl_firma.setComponentAlignment(btn_edit, Alignment.TOP_RIGHT);
		
		
		//Laden des Firmenlogos
	    StreamSource streamSource = new StreamSource()
	      {

			private static final long serialVersionUID = 1L;

			public InputStream getStream()
		        {
			        byte[] bas = (byte[]) item_firma.getItemProperty("logo").getValue();
			        FirmenProfil.this.bas = bas;
			        return (bas == null) ? null : new ByteArrayInputStream(bas);
		        }
	      };
	      
		if(streamSource.getStream()!=null) {
			firmen_logo = new Image("", new StreamResource(streamSource, "Logo"));
		}
		else {
			firmen_logo = new Image(null, new ThemeResource("kein_logo.png"));
		}
		firmen_logo.setWidth("300");

		
		//Initialisieren & hinzufügen der Logodropbox
		ImageDropBox dropbox = new ImageDropBox(firmen_logo);
		dropbox.setSizeUndefined();
		dropbox.setEnabled(false);

				
		formfirma.addComponent(dropbox);		
		Label lbl_infologo = new Label("Zum Ändern, neues Bild auf aktuelles Logo ziehen");
		lbl_infologo.setVisible(false);
		formfirma.addComponent(lbl_infologo);
				
		//Textfelder hinzufügen
		Field<?> namefield;
		Field<?> webfield;	    

		formfirma.addComponent(namefield = binder_firma.buildAndBind("Name", "name"));
		formfirma.addComponent(webfield = binder_firma.buildAndBind("Website", "website"));
		
		//Validator zu den Textfeldern hinzufügen
		Validator notNullValidator = new Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				if ((value == null) || (value == "" || (value.toString().isEmpty()))){
					throw new InvalidValueException("Feld kann nicht leer sein");
				}
			}
		};
		
		((TextField)namefield).addValidator(notNullValidator);
		((TextField)webfield).addValidator(notNullValidator);
		

		namefield.setWidth("100%");
		webfield.setWidth("100%");

		namefield.setReadOnly(true);
		webfield.setReadOnly(true);

		//Speicher und Abbrechen Knöpfe
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
				dropbox.setEnabled(true);
				lbl_infologo.setVisible(true);
				hl_botbtns.setVisible(true);
				
			}
		});

		// Cancel Button
		btn_cancel.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				namefield.setReadOnly(true);
				webfield.setReadOnly(true);
				dropbox.setEnabled(false);
				lbl_infologo.setVisible(false);
				btn_edit.setEnabled(true);
				hl_botbtns.setVisible(false);
				binder_firma.discard();
			    StreamSource streamSource = new StreamSource()
			      {

					private static final long serialVersionUID = 1L;

					public InputStream getStream()
				        {
					        byte[] bas = (byte[]) item_firma.getItemProperty("logo").getValue();
					        FirmenProfil.this.bas = bas;
					        return (bas == null) ? null : new ByteArrayInputStream(bas);
				        }
			      };
			      
				if(streamSource.getStream()!=null) {
					firmen_logo.setSource(new StreamResource(streamSource, "Logo"));
				}
				else {
					bas = null;
					firmen_logo.setSource(new ThemeResource("kein_logo.png"));
		
				}
				
			}
		});

		// Save Button
		btn_save.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if (binder_firma.isValid()) {
						binder_firma.commit();
						if(bas != null)
							item_firma.getItemProperty("logo").setValue(bas);
						cont_firma.commit();
						namefield.setReadOnly(true);
						webfield.setReadOnly(true);
						btn_edit.setEnabled(true);
						dropbox.setEnabled(false);
						lbl_infologo.setVisible(false);
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

		//Bearbeiten Knopf
		HorizontalLayout hl_ort = new HorizontalLayout();
		hl_ort.setWidth("100%");
		Button btn_edit = new Button("Bearbeiten");
		btn_edit.setIcon(FontAwesome.PENCIL);
		formort.addComponent(hl_ort);
		hl_ort.addComponent(btn_edit);
		hl_ort.setComponentAlignment(btn_edit, Alignment.TOP_RIGHT);

		
		//Textfelder hinzufügen
		Field<?> aliasfield;
		Field<?> strassefield;
		Field<?> plzfield;
		Field<?> ortfield;
		Field<?> anpartnerfield;
		Field<?> mailfield;
		Field<?> telefield;

		//Standortfelder
		binder_standort = new FieldGroup(item_standort);
		formort.addComponent(aliasfield = binder_standort.buildAndBind("Alias", "alias"));
		formort.addComponent(strassefield = binder_standort.buildAndBind("Straße", "strasse"));
		formort.addComponent(plzfield = binder_standort.buildAndBind("PLZ", "plz"));
		formort.addComponent(ortfield = binder_standort.buildAndBind("Ort", "ort"));

		//Filter nach Ansprechpartner mit Standort_ID
		cont_anpartner.removeAllContainerFilters();
		cont_anpartner.addContainerFilter(new Like("id", ansprechpartner_id));
		Item item_ansprechpartner = null;
		if (cont_anpartner.size() > 0) {
			item_ansprechpartner = cont_anpartner.getItem(cont_anpartner.firstItemId());
		}
		
		//Ansprechpartnerfelder
		binder_anpartner = new FieldGroup(item_ansprechpartner);
		formort.addComponent(anpartnerfield = binder_anpartner.buildAndBind("Ansprechpartner", "name"));
		formort.addComponent(mailfield = binder_anpartner.buildAndBind("E-Mail", "email"));
		formort.addComponent(telefield = binder_anpartner.buildAndBind("Telefonnummer", "telefonnummer"));
		

		//Validator zu den Textfeldern hinzufügen
		Validator notNullValidator = new Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				if ((value == null) || (value == "" || (value.toString().isEmpty()))){
					throw new InvalidValueException("Feld kann nicht leer sein");
				}
			}
		};
		
		((TextField)aliasfield).addValidator(notNullValidator);
		((TextField)strassefield).addValidator(notNullValidator);
		((TextField)ortfield).addValidator(notNullValidator);
		((TextField)anpartnerfield).addValidator(notNullValidator);
		((TextField)mailfield).addValidator(notNullValidator);
		((TextField)telefield).addValidator(notNullValidator);
		
		((TextField)plzfield).addValidator((new Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				try {
				  	Integer.parseInt(value.toString());
					if ((value == null) || (value == "" ) || (value.toString().isEmpty())){
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

		//Speichern, Abbrechen und Löschen Knopf
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
			}
		});
		
		// Delete Button
		btn_delete.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				
				
				cont_stellan.removeAllContainerFilters();
				cont_stellan.addContainerFilter(new Like("standort_id", item_standort.getItemProperty("id").getValue().toString()));
				Item item_stellan = null;
				if (cont_stellan.size() > 0) {
					//item_stellan = cont_stellan.getItem(cont_stellan.firstItemId());
					Notification.show("Error", "Es gibt noch " + cont_stellan.size() + " Stellenangebot(e) zu diesem Standort", Notification.Type.ERROR_MESSAGE);
				}
				
				else {
						     
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
    	
		//Hinzufügen Knopf
		Button btn_standort = new Button(" Standort");
		btn_standort.setIcon(FontAwesome.PLUS_CIRCLE);
		btn_standort.addClickListener(new Button.ClickListener() {

			//Neuer Standort erstellen
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

		
		//Textfelder erstellen
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
		//Validator zu den Textfeldern hinzufügen
		Validator validator = new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				if ((value == null) || (value.toString() == ""))
					throw new InvalidValueException("Feld kann nicht leer sein");
			}
		};

		tf_alias.addValidator(validator);
		tf_strasse.addValidator(validator);
		tf_anpartner.addValidator(validator);
		tf_mail.addValidator(validator);
		tf_tele.addValidator(validator);
		tf_ort.addValidator(validator);
		
		
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

		//Textfelder hinzufügen
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

		//Speichern und Abbrechen Knopf
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
		
		vl_fir.addComponent(pnl_ort, index++);

	}
	
	@Override
	public void enter(ViewChangeEvent event) {

	}

	
	//Logik für Drag&Drop Box
    private class ImageDropBox extends DragAndDropWrapper implements
    DropHandler {
       
    Image logo;
    public ImageDropBox(final Component root) {
        super(root);
    	this.logo = (Image) root;
        setDropHandler(this);
    }
   
    @Override
    public void drop(final DragAndDropEvent dropEvent) {
   
        // (expecting this to be an html5 drag)
        final WrapperTransferable tr = (WrapperTransferable) dropEvent
                .getTransferable();
        final Html5File[] files = tr.getFiles();
        if (files != null) {
            for (final Html5File html5File : files) {
                final String fileName = html5File.getFileName();
                    final ByteArrayOutputStream bas = new ByteArrayOutputStream();
                    final StreamVariable streamVariable = new StreamVariable() {
   
                        @Override
                        public OutputStream getOutputStream() {
                            return bas;
                        }
   
                        @Override
                        public boolean listenProgress() {
                            return false;
                        }
   
                        @Override
                        public void onProgress(
                                final StreamingProgressEvent event) {
                        }
   
                        @Override
                        public void streamingStarted(
                                final StreamingStartEvent event) {
                        }
   
                        @Override
                        public void streamingFinished(
                                final StreamingEndEvent event) {
                            showFile(fileName, html5File.getType(), bas);
                        }
   
                        @Override
                        public void streamingFailed(
                                final StreamingErrorEvent event) {
                        }
   
                        @Override
                        public boolean isInterrupted() {
                            return false;
                        }
                    };
                    html5File.setStreamVariable(streamVariable);
                	BewerberportalUI.getCurrent().setPollInterval(1000);
            }
   
        } else {
            final String text = tr.getText();
            if (text != null) {
                //(showText("File nicht angekommen");)
            }
        }
    }
    
   
    private void showFile(final String name, final String type,
            final ByteArrayOutputStream bas) {
        final byte[] byteArray = bas.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
        FirmenProfil.this.bas = bas.toByteArray();
        final StreamSource streamSource = new StreamSource() {
            @Override
            public InputStream getStream() {
                if (bas != null) {
                	return bis;
                }else
                	return null;
            }
        };
        final StreamResource resource = new StreamResource(streamSource,
                name);
   
        logo.setSource(resource);
    	BewerberportalUI.getCurrent().setPollInterval(-1);
    }
    

	@Override
	public AcceptCriterion getAcceptCriterion() {
		return AcceptAll.get();
	}
	
	
    }

}
