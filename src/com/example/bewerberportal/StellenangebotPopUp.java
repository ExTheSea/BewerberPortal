	package com.example.bewerberportal;

	import java.sql.Connection;
	import java.sql.ResultSet;
	import java.sql.SQLException;
	import java.sql.Statement;
	import java.util.ArrayList;
	import com.example.data.DatabaseConnector;
	import com.vaadin.data.Item;
	import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.sqlcontainer.RowId;
	import com.vaadin.data.util.sqlcontainer.SQLContainer;
	import com.example.data.TableQuery;
	import com.example.login.CurrentUser;
	import com.vaadin.shared.ui.label.ContentMode;
	import com.vaadin.ui.Button;
	import com.vaadin.ui.HorizontalLayout;
	import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;
	import com.vaadin.ui.ComboBox;
	import com.vaadin.ui.TextField;
	import com.vaadin.ui.VerticalLayout;
	import com.vaadin.ui.Window;
	import com.vaadin.ui.themes.ValoTheme;


	public class StellenangebotPopUp extends Window {
		
		private static final long serialVersionUID = 1L;
		
		private SQLContainer cont_studiengang = null;
		private SQLContainer cont_standorte = null;
		private PopUpCloseListener listener;
		
		public StellenangebotPopUp(Item item, PopUpCloseListener listener) {
			this.listener = listener;
			VerticalLayout vl_popup = new VerticalLayout();
			vl_popup.setMargin(true);
			vl_popup.setSpacing(true);
			vl_popup.setSizeFull();
			setContent(vl_popup);

	        TableQuery tq_studiengang = new TableQuery("studiengang", DatabaseConnector.getPool()){

				private static final long serialVersionUID = 1L;

				public void fetchMetaData() {
	                primaryKeyColumns= new ArrayList<String>();     
	                primaryKeyColumns.add("id");
	                super.fetchMetaData();
	            };
	        };
	        try {
	        	cont_studiengang = new SQLContainer(tq_studiengang);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
			
	        TableQuery tq_standort = new TableQuery("standort", DatabaseConnector.getPool()){

				private static final long serialVersionUID = 1L;

				public void fetchMetaData() {
	                primaryKeyColumns= new ArrayList<String>();     
	                primaryKeyColumns.add("id");
	                super.fetchMetaData();
	            };
	        };
	        try {
	        	cont_standorte = new SQLContainer(tq_standort);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        
			
			ComboBox dropdown_studiengang = new ComboBox("Studiengang", cont_studiengang);
			dropdown_studiengang.setNullSelectionAllowed(false);
			dropdown_studiengang.setValue(new RowId((Integer)item.getItemProperty("studiengang_id").getValue()));
			dropdown_studiengang.setWidth("100%");
			dropdown_studiengang.setItemCaptionPropertyId("bezeichnung");
			dropdown_studiengang.addValidator(new Validator() {

				private static final long serialVersionUID = 1L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					if(dropdown_studiengang.getValue()==null) throw new InvalidValueException("Studiengang wählen");
				}
			});
			vl_popup.addComponent(dropdown_studiengang);

			cont_standorte.addContainerFilter("firmenprofil_id", item.getItemProperty("firmenprofil_id").getValue().toString(), false, false);
			ComboBox dropdown_standort = new ComboBox("Standort", cont_standorte);
			dropdown_standort.setNullSelectionAllowed(false);
			dropdown_standort.setValue(new RowId((Integer)item.getItemProperty("standort_id").getValue()));
			dropdown_standort.setWidth("100%");
			dropdown_standort.setItemCaptionPropertyId("alias");
			dropdown_standort.addValidator(new Validator() {

				private static final long serialVersionUID = 1L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					if(dropdown_standort.getValue()==null) throw new InvalidValueException("Studiengang wählen");
				}
			});
			vl_popup.addComponent(dropdown_standort);
			
			
			HorizontalLayout hl_freie_plaetze = new HorizontalLayout();
			hl_freie_plaetze.setSizeFull();
			vl_popup.addComponent(hl_freie_plaetze);
			hl_freie_plaetze.addComponent(new Label("Freie Plätze"));
			hl_freie_plaetze.addComponent(new Label("&nbsp", ContentMode.HTML));
			
			TextField freie_plaetze = new TextField();
			if(item.getItemProperty("anzahl").getValue()!=null)freie_plaetze.setValue(item.getItemProperty("anzahl").getValue().toString());
			hl_freie_plaetze.addComponent(freie_plaetze);
			
			freie_plaetze.addValidator(new Validator() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					if(value.toString()=="");
					else{
						try{
							if((Integer.parseInt(value.toString())<1) || (Integer.parseInt(value.toString())>6)){
								throw new InvalidValueException("Ganze Zahl zwischen 1 und 5");
							}
						}catch(ClassCastException e){
							e.printStackTrace();
							throw new InvalidValueException("Ganze Zahl zwischen 1 und 5");
						}catch(Exception e){
							e.printStackTrace();
							throw new InvalidValueException("Ganze Zahl zwischen 1 und 5");
						}
					}
				}
			});
			
			HorizontalLayout hl_durschnitt = new HorizontalLayout();
			hl_durschnitt.setSizeFull();
			vl_popup.addComponent(hl_durschnitt);
			hl_durschnitt.addComponent(new Label("Notendurchschnitt"));
			hl_durschnitt.addComponent(new Label("&nbsp", ContentMode.HTML));
			
			TextField durchschnitt = new TextField();
			if(item.getItemProperty("zeugnisschnitt").getValue()!=null)durchschnitt.setValue(item.getItemProperty("zeugnisschnitt").getValue().toString());
			hl_durschnitt.addComponent(durchschnitt);
			
			durchschnitt.addValidator(new Validator() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					if(value.toString()=="");
					else{
						try{
							if((Double.parseDouble(value.toString())<1) || (Double.parseDouble(value.toString())>6)){
								throw new InvalidValueException("Note zwischen 1.0 und 6.0");
							}
						}catch(ClassCastException e){
							e.printStackTrace();
							throw new InvalidValueException("Note zwischen 1.0 und 6.0");
						}catch(Exception e){
							e.printStackTrace();
							throw new InvalidValueException("Note zwischen 1.0 und 6.0");
						}

					}

				}
			});
			
			HorizontalLayout hl_deutsch = new HorizontalLayout();
			hl_deutsch.setSizeFull();
			vl_popup.addComponent(hl_deutsch);
			hl_deutsch.addComponent(new Label("Deutsch"));
			hl_deutsch.addComponent(new Label("&nbsp", ContentMode.HTML));
			
			TextField deutsch = new TextField();
			if(item.getItemProperty("note_deutsch").getValue()!=null)deutsch.setValue(item.getItemProperty("note_deutsch").getValue().toString());
			hl_deutsch.addComponent(deutsch);
			
			deutsch.addValidator(new Validator() {

				private static final long serialVersionUID = 1L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					if(value.toString()=="");
					else{
						try{
							if((Double.parseDouble(value.toString())<1) || (Double.parseDouble(value.toString())>6)){
								throw new InvalidValueException("Note zwischen 1.0 und 6.0");
							}
						}catch(ClassCastException e){
							e.printStackTrace();
							throw new InvalidValueException("Note zwischen 1.0 und 6.0");
						}catch(Exception e){
							e.printStackTrace();
							throw new InvalidValueException("Note zwischen 1.0 und 6.0");
						}
					}
				}
			});
			
			HorizontalLayout hl_englisch = new HorizontalLayout();
			hl_englisch.setSizeFull();
			vl_popup.addComponent(hl_englisch);
			hl_englisch.addComponent(new Label("Englisch"));
			hl_englisch.addComponent(new Label("&nbsp", ContentMode.HTML));
			
			TextField englisch = new TextField();
			if(item.getItemProperty("note_englisch").getValue()!=null)englisch.setValue(item.getItemProperty("note_englisch").getValue().toString());
			hl_englisch.addComponent(englisch);
			
			englisch.addValidator(new Validator() {

				private static final long serialVersionUID = 1L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					if(value.toString()=="");
					else{
						try{
							if((Double.parseDouble(value.toString())<1) || (Double.parseDouble(value.toString())>6)){
								throw new InvalidValueException("Note zwischen 1.0 und 6.0");
							}
						}catch(ClassCastException e){
							e.printStackTrace();
							throw new InvalidValueException("Note zwischen 1.0 und 6.0");
						}catch(Exception e){
							e.printStackTrace();
							throw new InvalidValueException("Note zwischen 1.0 und 6.0");
						}
					}
				}
			});
			
			HorizontalLayout hl_mathe = new HorizontalLayout();
			hl_mathe.setSizeFull();
			vl_popup.addComponent(hl_mathe);
			hl_mathe.addComponent(new Label("Mathe"));
			hl_mathe.addComponent(new Label("&nbsp", ContentMode.HTML));
			
			TextField mathe = new TextField();
			if(item.getItemProperty("note_mathe").getValue()!=null)mathe.setValue(item.getItemProperty("note_mathe").getValue().toString());
			hl_mathe.addComponent(mathe);
			
			mathe.addValidator(new Validator() {

				private static final long serialVersionUID = 1L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					if(value.toString()=="");
					else{
						try{
							if((Double.parseDouble(value.toString())<1) || (Double.parseDouble(value.toString())>6)){
								throw new InvalidValueException("Note zwischen 1.0 und 6.0");
							}
						}catch(ClassCastException e){
							e.printStackTrace();
							throw new InvalidValueException("Note zwischen 1.0 und 6.0");
						}catch(Exception e){
							e.printStackTrace();
							throw new InvalidValueException("Note zwischen 1.0 und 6.0");
						}
					}
				}
			});
			
			Button btn_register = new Button("Speichern");
			btn_register.setStyleName(ValoTheme.BUTTON_DANGER);
			btn_register.setWidth("100%");
			btn_register.addClickListener(new Button.ClickListener() {
				

				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
			        Connection con_save = null;
			        Statement statement_save = null;
			        try {
			        	if (dropdown_standort.isValid()&&dropdown_studiengang.isValid()&&deutsch.isValid()&&mathe.isValid()&&englisch.isValid()&&durchschnitt.isValid()){
				        	con_save = DatabaseConnector.getPool().reserveConnection();
				        	statement_save = con_save.createStatement();
				        	StringBuilder sql = new StringBuilder();
				        	sql.append("UPDATE studienplaetze SET ");
				        	if((deutsch!=null) && (deutsch.getValue().toString()!="")) sql.append("note_deutsch="+deutsch.getValue().toString()+",");
				        	if((englisch!=null) && (englisch.getValue().toString()!="")) sql.append("note_englisch="+englisch.getValue().toString()+",");
				        	if((mathe!=null) && (mathe.getValue().toString()!="")) sql.append("note_mathe="+mathe.getValue().toString()+",");
				        	if((durchschnitt!=null) && (durchschnitt.getValue().toString()!="")) sql.append("zeugnisschnitt="+durchschnitt.getValue().toString()+",");
				        	if((freie_plaetze!=null) && (freie_plaetze.getValue().toString()!="")) sql.append("anzahl="+freie_plaetze.getValue().toString()+",");
				        	sql.append("studiengang_id="+dropdown_studiengang.getValue().toString()+",standort_id="+dropdown_standort.getValue().toString()+" WHERE ID="+item.getItemProperty("id").getValue().toString());
					        int rs = statement_save.executeUpdate(sql.toString());
					        con_save.commit();
					        close();
					        listener.close();
			        	}

					} catch (SQLException e1) {
						e1.printStackTrace();
					}finally {
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
					}
				}
			});
			vl_popup.addComponent(btn_register);
			
			setModal(true);
			center();
			
			//Können auch Prozente sein
			setWidth("550px");
			setHeight("600px");
			
			//Um standard window buttons zu deaktivieren. Einsetzen wenn nötig
			setResizable(false);
			
			//PopUp öffnen
			BewerberportalUI.getCurrent().addWindow(this);
		}
		
		public StellenangebotPopUp(PopUpCloseListener listener){
			VerticalLayout vl_popup = new VerticalLayout();
			vl_popup.setMargin(true);
			vl_popup.setSpacing(true);
			vl_popup.setSizeFull();
			setContent(vl_popup);

	        TableQuery tq_studiengang = new TableQuery("studiengang", DatabaseConnector.getPool()){

				private static final long serialVersionUID = 1L;

				public void fetchMetaData() {
	                primaryKeyColumns= new ArrayList<String>();     
	                primaryKeyColumns.add("id");
	                super.fetchMetaData();
	            };
	        };
	        try {
	        	cont_studiengang = new SQLContainer(tq_studiengang);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
			
	        TableQuery tq_standort = new TableQuery("standort", DatabaseConnector.getPool()){

				private static final long serialVersionUID = 1L;

				public void fetchMetaData() {
	                primaryKeyColumns= new ArrayList<String>();     
	                primaryKeyColumns.add("id");
	                super.fetchMetaData();
	            };
	        };
	        try {
	        	cont_standorte = new SQLContainer(tq_standort);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        
			
			ComboBox dropdown_studiengang = new ComboBox("Studiengang", cont_studiengang);
			dropdown_studiengang.setNullSelectionAllowed(false);
			dropdown_studiengang.setWidth("100%");
			dropdown_studiengang.setItemCaptionPropertyId("bezeichnung");
			dropdown_studiengang.setValidationVisible(false);
			dropdown_studiengang.addValidator(new Validator() {

				private static final long serialVersionUID = 1L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					if(dropdown_studiengang.getValue()==null) throw new InvalidValueException("Studiengang wählen");
				}
			});
			vl_popup.addComponent(dropdown_studiengang);
			
	        Connection con = null;
	        Statement statement = null;
	        String firmenprofil_id = null;
	        try {
				con = DatabaseConnector.getPool().reserveConnection();
		        statement = con.createStatement();
		        ResultSet rs = statement.executeQuery("SELECT firmenprofil_id from benutzer_firmenprofil where benutzer_id = '"+CurrentUser.get()+"'");
		        rs.first();
		        firmenprofil_id = rs.getString("firmenprofil_id");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}finally {
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
			
			cont_standorte.addContainerFilter("firmenprofil_id", firmenprofil_id, false, false);
			ComboBox dropdown_standort = new ComboBox("Standort", cont_standorte);
			dropdown_standort.setNullSelectionAllowed(false);
			dropdown_standort.setWidth("100%");
			dropdown_standort.setItemCaptionPropertyId("alias");
			dropdown_standort.setValidationVisible(false);
			dropdown_standort.addValidator(new Validator() {

				private static final long serialVersionUID = 1L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					if(dropdown_standort.getValue()==null) throw new InvalidValueException("Studiengang wählen");
				}
			});
			vl_popup.addComponent(dropdown_standort);
			
			HorizontalLayout hl_freie_plaetze = new HorizontalLayout();
			hl_freie_plaetze.setSizeFull();
			vl_popup.addComponent(hl_freie_plaetze);
			hl_freie_plaetze.addComponent(new Label("Freie Plätze"));
			hl_freie_plaetze.addComponent(new Label("&nbsp", ContentMode.HTML));
			
			TextField freie_plaetze = new TextField();
			hl_freie_plaetze.addComponent(freie_plaetze);
			
			freie_plaetze.addValidator(new Validator() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					if(value.toString()=="");
					else{
						try{
							if((Integer.parseInt(value.toString())<1) || (Integer.parseInt(value.toString())>6)){
								throw new InvalidValueException("Ganze Zahl zwischen 1 und 5");
							}
						}catch(ClassCastException e){
							e.printStackTrace();
							throw new InvalidValueException("Ganze Zahl zwischen 1 und 5");
						}catch(Exception e){
							e.printStackTrace();
							throw new InvalidValueException("Ganze Zahl zwischen 1 und 5");
						}
					}
				}
			});
			HorizontalLayout hl_durschnitt = new HorizontalLayout();
			hl_durschnitt.setSizeFull();
			vl_popup.addComponent(hl_durschnitt);
			hl_durschnitt.addComponent(new Label("Notendurchschnitt"));
			hl_durschnitt.addComponent(new Label("&nbsp", ContentMode.HTML));
			
			TextField durchschnitt = new TextField();
			hl_durschnitt.addComponent(durchschnitt);
			
			durchschnitt.addValidator(new Validator() {

				private static final long serialVersionUID = 1L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					if(value=="");
					else{
						try{
							if((Double.parseDouble(value.toString())<1) || (Double.parseDouble(value.toString())>6)){
								throw new InvalidValueException("Note zwischen 1.0 und 6.0");
							}
						}catch(ClassCastException e){
							e.printStackTrace();
							throw new InvalidValueException("Note zwischen 1.0 und 6.0");
						}catch(Exception e){
							e.printStackTrace();
							throw new InvalidValueException("Note zwischen 1.0 und 6.0");
						}
					}
				}
			});
			
			HorizontalLayout hl_deutsch = new HorizontalLayout();
			hl_deutsch.setSizeFull();
			vl_popup.addComponent(hl_deutsch);
			hl_deutsch.addComponent(new Label("Deutsch"));
			hl_deutsch.addComponent(new Label("&nbsp", ContentMode.HTML));
			
			TextField deutsch = new TextField();
			hl_deutsch.addComponent(deutsch);
			
			deutsch.addValidator(new Validator() {

				private static final long serialVersionUID = 1L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					if(value.toString()=="");
					else{
						try{
							if((Double.parseDouble(value.toString())<1) || (Double.parseDouble(value.toString())>6)){
								throw new InvalidValueException("Note zwischen 1.0 und 6.0");
							}
						}catch(ClassCastException e){
							e.printStackTrace();
							throw new InvalidValueException("Note zwischen 1.0 und 6.0");
						}catch(Exception e){
							e.printStackTrace();
							throw new InvalidValueException("Note zwischen 1.0 und 6.0");
						}
					}
				}
			});
			
			HorizontalLayout hl_englisch = new HorizontalLayout();
			hl_englisch.setSizeFull();
			vl_popup.addComponent(hl_englisch);
			hl_englisch.addComponent(new Label("Englisch"));
			hl_englisch.addComponent(new Label("&nbsp", ContentMode.HTML));
			
			TextField englisch = new TextField();
			hl_englisch.addComponent(englisch);
			
			englisch.addValidator(new Validator() {

				private static final long serialVersionUID = 1L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					if(value.toString()=="");
					else{
						try{
							if((Double.parseDouble(value.toString())<1) || (Double.parseDouble(value.toString())>6)){
								throw new InvalidValueException("Note zwischen 1.0 und 6.0");
							}
						}catch(ClassCastException e){
							e.printStackTrace();
							throw new InvalidValueException("Note zwischen 1.0 und 6.0");
						}catch(Exception e){
							e.printStackTrace();
							throw new InvalidValueException("Note zwischen 1.0 und 6.0");
						}
					}
				}
			});
			
			HorizontalLayout hl_mathe = new HorizontalLayout();
			hl_mathe.setSizeFull();
			vl_popup.addComponent(hl_mathe);
			hl_mathe.addComponent(new Label("Mathe"));
			hl_mathe.addComponent(new Label("&nbsp", ContentMode.HTML));
			
			TextField mathe = new TextField();
			hl_mathe.addComponent(mathe);
			
			mathe.addValidator(new Validator() {

				private static final long serialVersionUID = 1L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					if(value.toString()=="");
					else{
						try{
							if((Double.parseDouble(value.toString())<1) || (Double.parseDouble(value.toString())>6)){
								throw new InvalidValueException("Note zwischen 1.0 und 6.0");
							}
						}catch(ClassCastException e){
							e.printStackTrace();
							throw new InvalidValueException("Note zwischen 1.0 und 6.0");
						}catch(Exception e){
							e.printStackTrace();
							throw new InvalidValueException("Note zwischen 1.0 und 6.0");
						}
					}
				}
			});
			
			Button btn_register = new Button("Speichern");
			btn_register.setStyleName(ValoTheme.BUTTON_DANGER);
			btn_register.setWidth("100%");
			btn_register.addClickListener(new Button.ClickListener() {
				

				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
			        Connection con_save = null;
			        Statement statement_save = null;
			        try {
			        	if (dropdown_standort.isValid()&&dropdown_studiengang.isValid()&&deutsch.isValid()&&mathe.isValid()&&englisch.isValid()&&durchschnitt.isValid()){
				        	con_save = DatabaseConnector.getPool().reserveConnection();
				        	statement_save = con_save.createStatement();
				        	ResultSet vorhanden = null;
				        	vorhanden = statement_save.executeQuery("SELECT ID FROM go2dhbw.studienplaetze WHERE studiengang_id='"+dropdown_studiengang.getValue().toString()+"' AND standort_id='"+dropdown_standort.getValue().toString()+"'");
				        	if(!vorhanden.first()){
					        	StringBuilder sql = new StringBuilder();
					        	sql.append("INSERT INTO studienplaetze (");
					        	if((deutsch!=null) && (deutsch.getValue().toString()!="")) sql.append("note_deutsch,");
					        	if((englisch!=null) && (englisch.getValue().toString()!="")) sql.append("note_englisch,");
					        	if((mathe!=null) && (mathe.getValue().toString()!="")) sql.append("note_mathe,");
					        	if((durchschnitt!=null) && (durchschnitt.getValue().toString()!="")) sql.append("zeugnisschnitt,");
					        	if((freie_plaetze!=null) && (freie_plaetze.getValue().toString()!="")) sql.append("anzahl,");
					        	sql.append("studiengang_id,standort_id) VALUES(");
					        	if((deutsch!=null) && (deutsch.getValue().toString()!="")) sql.append(deutsch.getValue().toString()+",");
					        	if((englisch!=null) && (englisch.getValue().toString()!="")) sql.append(englisch.getValue().toString()+",");
					        	if((mathe!=null) && (mathe.getValue().toString()!="")) sql.append(mathe.getValue().toString()+",");
					        	if((durchschnitt!=null) && (durchschnitt.getValue().toString()!="")) sql.append(durchschnitt.getValue().toString()+",");
					        	if((freie_plaetze!=null) && (freie_plaetze.getValue().toString()!="")) sql.append(freie_plaetze.getValue().toString()+",");
					        	sql.append(dropdown_studiengang.getValue().toString()+","+dropdown_standort.getValue().toString()+")");
					        	System.out.println(sql.toString());
					        	int rs = statement_save.executeUpdate(sql.toString());
						        con_save.commit();
						        close();
						        listener.close();
				        	}
				        	else{
								Notification.show("Fehler", "Studiengangs- und Standortkombination bereits vorhanden", Notification.Type.ERROR_MESSAGE);
				        	}
				        }
			        	else{
			        		dropdown_standort.setValidationVisible(true);
			        		dropdown_studiengang.setValidationVisible(true);
			        	}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}finally {
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
					}
				}
			});
			vl_popup.addComponent(btn_register);
			
			setModal(true);
			center();
			
			//Können auch Prozente sein
			setWidth("550px");
			setHeight("600px");
			
			//Um standard window buttons zu deaktivieren. Einsetzen wenn nötig
			setResizable(false);
			
			//PopUp öffnen
			BewerberportalUI.getCurrent().addWindow(this);
		}
		
		static interface PopUpCloseListener{
			public void close();
		}
	}