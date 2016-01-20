package com.example.bewerberportal;

import java.sql.SQLException;

import com.example.data.DatabaseConnector;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

public class BewerberProfil extends Panel implements View {
	SQLContainer cont = null;
	FieldGroup binder;

	public BewerberProfil(String benutzer_Id) {
		setSizeFull();
		setStyleName(ValoTheme.PANEL_BORDERLESS);
		VerticalLayout vl_bew = new VerticalLayout();
		setContent(vl_bew);
		vl_bew.setHeight(null);
		vl_bew.setMargin(true);
		vl_bew.setSpacing(true);
		
		TableQuery tq = new TableQuery("bewerberprofil", DatabaseConnector.getPool());
				try {
			cont = new SQLContainer(tq);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		cont.addContainerFilter(new Like("benutzer_id", benutzer_Id));
		Item item = null;
		if(cont.size()>0){
			item = cont.getItem(cont.firstItemId());
		}

		binder = new FieldGroup(item);
		
		vl_bew.addComponent(buildTop());
		
		vl_bew.addComponent(buildTätig());
		
		vl_bew.addComponent(buildNoten());
		
//		vl_bew.addComponent(buildRichtung());
		
		
	}
	
	public Panel buildTop(){
		Panel pnl_top = new Panel();
		pnl_top.setWidth("100%");
		FormLayout formtop = new FormLayout();
		formtop.setWidth("100%");
		formtop.setMargin(true);
		formtop.setSpacing(true);
		pnl_top.setContent(formtop);
		
		HorizontalLayout hl_top = new HorizontalLayout();
		hl_top.setWidth("100%");
		Button btn_edit= new Button("Bearbeiten");
        formtop.addComponent(hl_top);
        hl_top.addComponent(btn_edit);
        hl_top.setComponentAlignment(btn_edit, Alignment.TOP_RIGHT);
		Field<?> namefield;
		Field<?> jahrfield;
		Field<?> mailfield;
		Field<?> telnrfield;
		formtop.addComponent(namefield = binder.buildAndBind("Name", "name"));
		formtop.addComponent(jahrfield = binder.buildAndBind("Geburtsjahr", "geburtsjahr"));
		formtop.addComponent(mailfield = binder.buildAndBind("E-Mail", "email"));
		formtop.addComponent(telnrfield = binder.buildAndBind("Telefonnummer", "telefonnummer"));
		mailfield.addValidator(new EmailValidator("Keine gültige E-Mail"));
		namefield.setReadOnly(true);
		jahrfield.setReadOnly(true);
		mailfield.setReadOnly(true);
		telnrfield.setReadOnly(true);
		
		
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
		formtop.addComponent(hl_botbtns);
		
		btn_edit.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				namefield.setReadOnly(false);
				jahrfield.setReadOnly(false);
				mailfield.setReadOnly(false);
				telnrfield.setReadOnly(false);
				btn_edit.setEnabled(false);
				hl_botbtns.setVisible(true);
			}
		});
		
		btn_cancel.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				namefield.setReadOnly(true);
				jahrfield.setReadOnly(true);
				mailfield.setReadOnly(true);
				telnrfield.setReadOnly(true);
				btn_edit.setEnabled(true);
				hl_botbtns.setVisible(false);
				binder.discard();
			}
		});
		
		btn_save.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if(binder.isValid()){
						binder.commit();
						cont.commit();
						namefield.setReadOnly(true);
						jahrfield.setReadOnly(true);
						mailfield.setReadOnly(true);
						telnrfield.setReadOnly(true);
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
		return pnl_top;
	}
	
	public Panel buildTätig(){
		Panel pnl_tätig = new Panel();
		pnl_tätig.setWidth("100%");
		FormLayout formtätig = new FormLayout();
		formtätig.setWidth("100%");
		formtätig.setMargin(true);
		formtätig.setSpacing(true);
		pnl_tätig.setContent(formtätig);
		
		HorizontalLayout hl_tätigedit = new HorizontalLayout();
		hl_tätigedit.setWidth("100%");
		Button btn_edittätig= new Button("Bearbeiten");
        formtätig.addComponent(hl_tätigedit);
        hl_tätigedit.addComponent(btn_edittätig);
        hl_tätigedit.setComponentAlignment(btn_edittätig, Alignment.TOP_RIGHT);
		TextArea hobbyfield = new TextArea("Hobbies");
		TextArea tätigkeitfield = new TextArea("Zusatzqualifikationen");
		formtätig.addComponent(hobbyfield);
		binder.bind(hobbyfield, "hobbies");
		formtätig.addComponent(tätigkeitfield);
		binder.bind(tätigkeitfield, "zusatzqualifikationen");
		hobbyfield.setReadOnly(true);
		tätigkeitfield.setReadOnly(true);
		
		
		HorizontalLayout hl_tätigbtns = new HorizontalLayout();
		hl_tätigbtns.setWidth("100%");
		hl_tätigbtns.setSpacing(true);
		Button btn_save = new Button("Speichern");
		btn_save.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		hl_tätigbtns.addComponent(btn_save);
		hl_tätigbtns.setExpandRatio(btn_save, 1f);
		hl_tätigbtns.setComponentAlignment(btn_save, Alignment.MIDDLE_RIGHT);
		Button btn_cancel = new Button("Abbrechen");
		btn_cancel.setStyleName(ValoTheme.BUTTON_QUIET);
		hl_tätigbtns.addComponent(btn_cancel);
		hl_tätigbtns.setVisible(false);
		formtätig.addComponent(hl_tätigbtns);
		
		btn_edittätig.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				hobbyfield.setReadOnly(false);
				tätigkeitfield.setReadOnly(false);
				btn_edittätig.setEnabled(false);
				hl_tätigbtns.setVisible(true);
			}
		});
		
		btn_cancel.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				hobbyfield.setReadOnly(true);
				tätigkeitfield.setReadOnly(true);
				btn_edittätig.setEnabled(true);
				hl_tätigbtns.setVisible(false);
				binder.discard();
			}
		});
		
		btn_save.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if(binder.isValid()){
						binder.commit();
						cont.commit();
						hobbyfield.setReadOnly(true);
						tätigkeitfield.setReadOnly(true);
						btn_edittätig.setEnabled(true);
						hl_tätigbtns.setVisible(false);
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
		return pnl_tätig;
	}
	
	
	
	public Panel buildNoten(){
		Panel pnl_noten = new Panel();
		pnl_noten.setWidth("100%");
		FormLayout formnoten = new FormLayout();
		formnoten.setWidth("100%");
		formnoten.setMargin(true);
		formnoten.setSpacing(true);
		pnl_noten.setContent(formnoten);
		
		HorizontalLayout hl_top = new HorizontalLayout();
		hl_top.setWidth("100%");
		Button btn_edit= new Button("Bearbeiten");
        formnoten.addComponent(hl_top);
        hl_top.addComponent(btn_edit);
        hl_top.setComponentAlignment(btn_edit, Alignment.TOP_RIGHT);
		Field<?> schnittfield;
		Field<?> deutschfield;
		Field<?> englischfield;
		Field<?> mathefield;
		formnoten.addComponent(schnittfield = binder.buildAndBind("Schnitt", "zeugnisschnitt"));
		formnoten.addComponent(deutschfield = binder.buildAndBind("Deutschnote", "note_deutsch"));
		formnoten.addComponent(englischfield = binder.buildAndBind("Englischnote", "note_englisch"));
		formnoten.addComponent(mathefield = binder.buildAndBind("Mathenote", "note_mathe"));
		schnittfield.setReadOnly(true);
		deutschfield.setReadOnly(true);
		englischfield.setReadOnly(true);
		mathefield.setReadOnly(true);
		
		
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
		formnoten.addComponent(hl_botbtns);
		
		btn_edit.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				schnittfield.setReadOnly(false);
				deutschfield.setReadOnly(false);
				englischfield.setReadOnly(false);
				mathefield.setReadOnly(false);
				btn_edit.setEnabled(false);
				hl_botbtns.setVisible(true);
			}
		});
		
		btn_cancel.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				schnittfield.setReadOnly(true);
				deutschfield.setReadOnly(true);
				englischfield.setReadOnly(true);
				mathefield.setReadOnly(true);
				btn_edit.setEnabled(true);
				hl_botbtns.setVisible(false);
				binder.discard();
			}
		});
		
		btn_save.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if(binder.isValid()){
						binder.commit();
						cont.commit();
						schnittfield.setReadOnly(true);
						deutschfield.setReadOnly(true);
						englischfield.setReadOnly(true);
						mathefield.setReadOnly(true);
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
		return pnl_noten;
	}
	
	
	public Panel buildRichtung(){
		Panel pnl_richtung = new Panel();
		pnl_richtung.setWidth("100%");
		FormLayout formRichtung = new FormLayout();
		formRichtung.setWidth("100%");
		formRichtung.setMargin(true);
		formRichtung.setSpacing(true);
		pnl_richtung.setContent(formRichtung);
		
		HorizontalLayout hl_edit = new HorizontalLayout();
		hl_edit.setWidth("100%");
		Button btn_edit= new Button("Bearbeiten");
        formRichtung.addComponent(hl_edit);
        hl_edit.addComponent(btn_edit);
        hl_edit.setComponentAlignment(btn_edit, Alignment.TOP_RIGHT);
		Field<?> richtungfield;
		formRichtung.addComponent(richtungfield = binder.buildAndBind("Wunschrichtung", "hobbies"));
		richtungfield.setReadOnly(true);
		
		
		HorizontalLayout hl_tätigbtns = new HorizontalLayout();
		hl_tätigbtns.setWidth("100%");
		hl_tätigbtns.setSpacing(true);
		Button btn_save = new Button("Speichern");
		btn_save.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		hl_tätigbtns.addComponent(btn_save);
		hl_tätigbtns.setExpandRatio(btn_save, 1f);
		hl_tätigbtns.setComponentAlignment(btn_save, Alignment.MIDDLE_RIGHT);
		Button btn_cancel = new Button("Abbrechen");
		btn_cancel.setStyleName(ValoTheme.BUTTON_QUIET);
		hl_tätigbtns.addComponent(btn_cancel);
		hl_tätigbtns.setVisible(false);
		formRichtung.addComponent(hl_tätigbtns);
		
		btn_edit.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				richtungfield.setReadOnly(false);
				btn_edit.setEnabled(false);
				hl_tätigbtns.setVisible(true);
			}
		});
		
		btn_cancel.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				richtungfield.setReadOnly(true);
				btn_edit.setEnabled(true);
				hl_tätigbtns.setVisible(false);
				binder.discard();
			}
		});
		
		btn_save.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if(binder.isValid()){
						binder.commit();
						cont.commit();
						richtungfield.setReadOnly(true);
						btn_edit.setEnabled(true);
						hl_tätigbtns.setVisible(false);
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
		return pnl_richtung;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
