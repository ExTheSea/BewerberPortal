package com.example.bewerberportal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.vaadin.tokenfield.TokenField;

import com.example.data.DatabaseConnector;
import com.example.data.GeoHelper;
import com.example.login.CurrentUser;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanUtil;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.sqlcontainer.RowId;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class BewerberProfil extends Panel implements View {
	private SQLContainer cont = null;
	private FieldGroup binder;
	private String  benutzer_id;
	private String bewerberprofil_id;
	private SQLContainer cont_bewricht = null;
	private SQLContainer cont_bewliebfach = null;
	
	public BewerberProfil(String benutzer_Id) {
		this.benutzer_id = benutzer_Id;
		setSizeFull();
		setStyleName(ValoTheme.PANEL_BORDERLESS);
		VerticalLayout vl_bew = new VerticalLayout();
		setContent(vl_bew);
		vl_bew.setHeight(null);
		vl_bew.setMargin(true);
		vl_bew.setSpacing(true);
		
		//Datenbank-Anbindung erstellen
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
		}else{
			//Erstellen eines neuen Profils wenn noch keins vorhanden f�r den Nutzer
			cont.removeAllContainerFilters();
			item = cont.getItem(cont.addItem());
			item.getItemProperty("benutzer_id").setValue(Integer.valueOf(benutzer_id));
			item.getItemProperty("name").setValue("");
			item.getItemProperty("geburtsjahr").setValue("");
			item.getItemProperty("telefonnummer").setValue("");
			item.getItemProperty("hobbies").setValue("");
			item.getItemProperty("zusatzqualifikationen").setValue("");
			item.getItemProperty("note_deutsch").setValue(0d);
			item.getItemProperty("note_englisch").setValue(0d);
			item.getItemProperty("note_mathe").setValue(0d);
			item.getItemProperty("zeugnisschnitt").setValue(0d);
			item.getItemProperty("plz").setValue("");
			try {
				cont.commit();
			} catch (UnsupportedOperationException | SQLException e) {
				e.printStackTrace();
			}
			

			cont.addContainerFilter(new Like("benutzer_id", benutzer_Id));
			item = cont.getItem(cont.firstItemId());
		}
		bewerberprofil_id = item.getItemProperty("id").getValue().toString();
		
		//Erstellen der FieldGroup zur Datenbindung
		binder = new FieldGroup(item);
		
		vl_bew.addComponent(buildTop());
		
		vl_bew.addComponent(buildT�tig());
		
		vl_bew.addComponent(buildNoten());
		
		vl_bew.addComponent(buildRichtung());
		
		
	}
	
	/**
	 * Erstes Panel mit Editierfelder f�r Name, PLZ, etc.
	 * @return
	 */
	public Panel buildTop(){
		Panel pnl_top = new Panel("Daten zur Person");
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
		Field<?> plzfield;
		//Erstellen der Editierfelder, Anbindung an den Datenbank-Item und hinzuf�gen der Felder zum Layout
		formtop.addComponent(namefield = binder.buildAndBind("Name", "name"));
		formtop.addComponent(jahrfield = binder.buildAndBind("Geburtsjahr", "geburtsjahr"));
		formtop.addComponent(telnrfield = binder.buildAndBind("Telefonnummer", "telefonnummer"));
		formtop.addComponent(plzfield = binder.buildAndBind("PLZ", "plz"));
		namefield.setReadOnly(true);
		jahrfield.setReadOnly(true);
		telnrfield.setReadOnly(true);
		plzfield.setReadOnly(true);
		
		//Speichern und Abbrechen Buttons
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
				telnrfield.setReadOnly(false);
				plzfield.setReadOnly(false);
				btn_edit.setEnabled(false);
				hl_botbtns.setVisible(true);
			}
		});
		
		btn_cancel.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				//Felder editierbar machen wenn auf Editieren geklickt wurde
				namefield.setReadOnly(true);
				jahrfield.setReadOnly(true);
				telnrfield.setReadOnly(true);
				plzfield.setReadOnly(true);
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
						//Commit der Daten zur Datenbank
						binder.commit();
						if(!binder.getField("plz").getValue().toString().isEmpty()){
							//Koordinaten suchen f�r PLZ
							String[] koord = GeoHelper.getKoordinaten(binder.getField("plz").getValue().toString());
							binder.getItemDataSource().getItemProperty("lat").setValue(koord[0]);
							binder.getItemDataSource().getItemProperty("lng").setValue(koord[1]);
						}else{
							binder.getItemDataSource().getItemProperty("lat").setValue("");
							binder.getItemDataSource().getItemProperty("lng").setValue("");
						}
						cont.commit();
						namefield.setReadOnly(true);
						jahrfield.setReadOnly(true);
						telnrfield.setReadOnly(true);
						plzfield.setReadOnly(true);
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
	
	/**
	 * T�tigkeit und Hobbies Felder
	 * @return
	 */
	public Panel buildT�tig(){
		Panel pnl_t�tig = new Panel("T�tigkeiten & Qualifikationen");
		pnl_t�tig.setWidth("100%");
		FormLayout formt�tig = new FormLayout();
		formt�tig.setWidth("100%");
		formt�tig.setMargin(true);
		formt�tig.setSpacing(true);
		pnl_t�tig.setContent(formt�tig);
		
		HorizontalLayout hl_t�tigedit = new HorizontalLayout();
		hl_t�tigedit.setWidth("100%");
		Button btn_editt�tig= new Button("Bearbeiten");
        formt�tig.addComponent(hl_t�tigedit);
        hl_t�tigedit.addComponent(btn_editt�tig);
        hl_t�tigedit.setComponentAlignment(btn_editt�tig, Alignment.TOP_RIGHT);
		TextArea hobbyfield = new TextArea("Hobbies");
		TextArea t�tigkeitfield = new TextArea("Zusatzqualifikationen");
		formt�tig.addComponent(hobbyfield);
		binder.bind(hobbyfield, "hobbies");
		formt�tig.addComponent(t�tigkeitfield);
		binder.bind(t�tigkeitfield, "zusatzqualifikationen");
		hobbyfield.setReadOnly(true);
		t�tigkeitfield.setReadOnly(true);
		
		
		HorizontalLayout hl_t�tigbtns = new HorizontalLayout();
		hl_t�tigbtns.setWidth("100%");
		hl_t�tigbtns.setSpacing(true);
		Button btn_save = new Button("Speichern");
		btn_save.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		hl_t�tigbtns.addComponent(btn_save);
		hl_t�tigbtns.setExpandRatio(btn_save, 1f);
		hl_t�tigbtns.setComponentAlignment(btn_save, Alignment.MIDDLE_RIGHT);
		Button btn_cancel = new Button("Abbrechen");
		btn_cancel.setStyleName(ValoTheme.BUTTON_QUIET);
		hl_t�tigbtns.addComponent(btn_cancel);
		hl_t�tigbtns.setVisible(false);
		formt�tig.addComponent(hl_t�tigbtns);
		
		btn_editt�tig.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				hobbyfield.setReadOnly(false);
				t�tigkeitfield.setReadOnly(false);
				btn_editt�tig.setEnabled(false);
				hl_t�tigbtns.setVisible(true);
			}
		});
		
		btn_cancel.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				hobbyfield.setReadOnly(true);
				t�tigkeitfield.setReadOnly(true);
				btn_editt�tig.setEnabled(true);
				hl_t�tigbtns.setVisible(false);
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
						t�tigkeitfield.setReadOnly(true);
						btn_editt�tig.setEnabled(true);
						hl_t�tigbtns.setVisible(false);
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
		return pnl_t�tig;
	}
	
	
	/**
	 * Noten-Panel
	 * @return
	 */
	public Panel buildNoten(){
		Panel pnl_noten = new Panel("Noten");
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
		
		//Converter, der Kommas zu Punkte konvertiert zur Speicherung der Noten
		Converter notenconverter = new Converter<String, Double>() {

			@Override
			public Double convertToModel(String value, Class<? extends Double> targetType, Locale locale)
					throws com.vaadin.data.util.converter.Converter.ConversionException {
				String tmp = value.replace(",", ".");
				try{
					return Double.valueOf(tmp);
				}catch(Exception e){
					return 0.0;
				}
				
			}

			@Override
			public String convertToPresentation(Double value, Class<? extends String> targetType, Locale locale)
					throws com.vaadin.data.util.converter.Converter.ConversionException {
				return ""+value;
			}

			@Override
			public Class<Double> getModelType() {
				return null;
			}

			@Override
			public Class<String> getPresentationType() {
				return null;
			}
		};
		((TextField)schnittfield).setConverter(notenconverter);
		((TextField)deutschfield).setConverter(notenconverter);
		((TextField)englischfield).setConverter(notenconverter);
		((TextField)mathefield).setConverter(notenconverter);
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
	
	/**
	 * Studienrichtung und Lieblingsf�cher
	 * @return
	 */
	public Panel buildRichtung(){
		Panel pnl_richtung = new Panel("Wunschstudienrichtung");
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
		TableQuery tq_richt = new TableQuery("studiengang", DatabaseConnector.getPool());
		SQLContainer richtcont = null;
		try {
			richtcont = new SQLContainer(tq_richt);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		//Erstellen des Studiengangrichtung-Feld mit dem externen Tokenfield Add-On 
		TokenField richtfield = new TokenField("Wunschrichtung"){
			//�berschreiben einige der Funktionen um die Funktion des Felds leicht zu ver�ndern
			
			@Override
			public void addToken(Object tokenId) {
				if(getContainerDataSource().containsId(tokenId))
					if(!isReadOnly())
						super.addToken(tokenId);
					else{
						super.setReadOnly(false);
						super.addToken(tokenId);
						super.setReadOnly(true);
					}
			}
			@Override
			protected void rememberToken(String tokenId) {
				if(getContainerDataSource().containsId(tokenId))
					super.rememberToken(tokenId);
			}
			@Override
			public void removeToken(Object tokenId) {
				if(isReadOnly())
					return;
				super.removeToken(tokenId);
			}
		};
		//Sortieren des Containers nach dem Namen und anschlie�endes Setzen des Containers am TokenField
		richtcont.sort(new Object[]{"bezeichnung"}, new boolean[]{true});
		richtfield.setContainerDataSource(richtcont);
		richtfield.setReadOnly(true);
		richtfield.setTokenCaptionPropertyId("bezeichnung");
		
		com.example.data.TableQuery tq_bewricht = new com.example.data.TableQuery("studiengang_bewerberprofil", DatabaseConnector.getPool()){
			@Override
			public void fetchMetaData() {
				primaryKeyColumns = new ArrayList<>();
				primaryKeyColumns.add("studiengang_id");
				super.fetchMetaData();
			}
		};
		try {
			cont_bewricht = new SQLContainer(tq_bewricht);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		//Hinzuf�gen der schon vorhandenen Daten
		cont_bewricht.addContainerFilter(new Like("bewerberprofil_id", bewerberprofil_id));
		ArrayList<String> initvalue = new ArrayList<String>();
		for (Iterator it_gang = cont_bewricht.getItemIds().iterator(); it_gang.hasNext();) {
			Object itemId = (Object) it_gang.next();
			Item item = cont_bewricht.getItem(itemId);
			for (Iterator it_tokens = richtfield.getTokenIds().iterator(); it_tokens.hasNext();) {
				Object token = (Object) it_tokens.next();
				if(token.toString().equals(item.getItemProperty("studiengang_id").getValue().toString())){
					richtfield.addToken(token);
				}
			}
		}
		formRichtung.addComponent(richtfield);
		
		
		
		
		
		
		//lieblingsFach Tokenfield erstellen wie Studiengangrichtungtokenfield
		TableQuery tq_liebfach = new TableQuery("lieblingsfaecher", DatabaseConnector.getPool());
		SQLContainer liebfachcont = null;
		try {
			liebfachcont = new SQLContainer(tq_liebfach);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		liebfachcont.sort(new Object[]{"bezeichnung"}, new boolean[]{true});
		TokenField liebfachfield = new TokenField("Lieblingsf�cher"){
			@Override
			public void addToken(Object tokenId) {
				if(getContainerDataSource().containsId(tokenId))
					if(!isReadOnly())
						super.addToken(tokenId);
					else{
						super.setReadOnly(false);
						super.addToken(tokenId);
						super.setReadOnly(true);
					}
			}
			@Override
			protected void rememberToken(String tokenId) {
				if(getContainerDataSource().containsId(tokenId))
					super.rememberToken(tokenId);
			}

			@Override
			public void removeToken(Object tokenId) {
				if(isReadOnly())
					return;
				super.removeToken(tokenId);
			}
		};
		liebfachfield.setContainerDataSource(liebfachcont);
		liebfachfield.setReadOnly(true);
		liebfachfield.setTokenCaptionPropertyId("bezeichnung");
		
		com.example.data.TableQuery tq_bewrliebfach = new com.example.data.TableQuery("lieblingsfaecher_bewerberprofil", DatabaseConnector.getPool()){
			@Override
			public void fetchMetaData() {
				primaryKeyColumns = new ArrayList<>();
				primaryKeyColumns.add("lieblingsfaecher_id");
				super.fetchMetaData();
			}
		};
		try {
			cont_bewliebfach = new SQLContainer(tq_bewrliebfach);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		cont_bewliebfach.addContainerFilter(new Like("bewerberprofil_id", bewerberprofil_id));
		ArrayList<String> initvaluefach = new ArrayList<String>();
		for (Iterator it_gang = cont_bewliebfach.getItemIds().iterator(); it_gang.hasNext();) {
			Object itemId = (Object) it_gang.next();
			Item item = cont_bewliebfach.getItem(itemId);
			for (Iterator it_tokens = liebfachfield.getTokenIds().iterator(); it_tokens.hasNext();) {
				Object token = (Object) it_tokens.next();
				if(token.toString().equals(item.getItemProperty("lieblingsfaecher_id").getValue().toString()))
					liebfachfield.addToken(token);
			}
		}
		formRichtung.addComponent(liebfachfield);
		
		
		HorizontalLayout hl_t�tigbtns = new HorizontalLayout();
		hl_t�tigbtns.setWidth("100%");
		hl_t�tigbtns.setSpacing(true);
		Button btn_save = new Button("Speichern");
		btn_save.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		hl_t�tigbtns.addComponent(btn_save);
		hl_t�tigbtns.setExpandRatio(btn_save, 1f);
		hl_t�tigbtns.setComponentAlignment(btn_save, Alignment.MIDDLE_RIGHT);
		Button btn_cancel = new Button("Abbrechen");
		btn_cancel.setStyleName(ValoTheme.BUTTON_QUIET);
		hl_t�tigbtns.addComponent(btn_cancel);
		hl_t�tigbtns.setVisible(false);
		formRichtung.addComponent(hl_t�tigbtns);
		
		btn_edit.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				richtfield.setReadOnly(false);
				liebfachfield.setReadOnly(false);
				btn_edit.setEnabled(false);
				hl_t�tigbtns.setVisible(true);
			}
		});
		
		btn_cancel.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				richtfield.setReadOnly(true);
				liebfachfield.setReadOnly(true);
				btn_edit.setEnabled(true);
				hl_t�tigbtns.setVisible(false);
				binder.discard();
			}
		});
		
		btn_save.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				//Speichern der Daten durch L�schen der alten Daten, iterieren �ber die aktuellen Daten und dann speichern dieser �ber einen Container
				richtfield.setReadOnly(true);
				LinkedHashSet<RowId> richtvalues = (LinkedHashSet<RowId>) richtfield.getValue();
				cont_bewricht.removeAllContainerFilters();
				cont_bewricht.setAutoCommit(true);
				for (Iterator it_richtremove = cont_bewricht.getItemIds().iterator(); it_richtremove
						.hasNext();) {
					Object ItemID = (Object) it_richtremove.next();
					if(ItemID!=null){
					Item item = cont_bewricht.getItem(ItemID);
					if(item != null && cont_bewricht.getItem(ItemID).getItemProperty("bewerberprofil_id").getValue().toString().equals(bewerberprofil_id))
						cont_bewricht.removeItem(ItemID);
				
					}
				}
				cont_bewricht.setAutoCommit(false);

				Collection<Filter> arr_bewfilt = cont_bewricht.getContainerFilters();
				try {
					cont_bewricht.commit();
				} catch (UnsupportedOperationException | SQLException e1) {
					e1.printStackTrace();
				}
				if(richtvalues!=null)
				for (Iterator it_tokens = richtvalues.iterator(); it_tokens.hasNext();) {
					Object token = it_tokens.next();
					Object itemID = cont_bewricht.addItem();
					Item item = cont_bewricht.getItem(itemID);
					item.getItemProperty("bewerberprofil_id").setValue(Integer.valueOf(bewerberprofil_id));
					item.getItemProperty("studiengang_id").setValue(Integer.valueOf(token.toString()));
				}
				try {
					cont_bewricht.commit();
				} catch (UnsupportedOperationException | SQLException e) {
					e.printStackTrace();
				}
				for (Iterator it_filter = arr_bewfilt.iterator(); it_filter
						.hasNext();) {
					Filter filter = (Filter) it_filter.next();
					cont_bewricht.addContainerFilter(filter);
				}
				
				liebfachfield.setReadOnly(true);
				LinkedHashSet<RowId> liblvalues = (LinkedHashSet<RowId>) liebfachfield.getValue();
				cont_bewliebfach.removeAllContainerFilters();
				cont_bewliebfach.setAutoCommit(true);
				for (Iterator it_richtremove = cont_bewliebfach.getItemIds().iterator(); it_richtremove
						.hasNext();) {
					Object ItemID = (Object) it_richtremove.next();
					if(ItemID!=null){
					Item item = cont_bewliebfach.getItem(ItemID);
					if(item != null && cont_bewliebfach.getItem(ItemID).getItemProperty("bewerberprofil_id").getValue().toString().equals(bewerberprofil_id))
						cont_bewliebfach.removeItem(ItemID);
				
					}
				}
				cont_bewliebfach.setAutoCommit(false);

				Collection<Filter> arr_lieblfilt = cont_bewliebfach.getContainerFilters();
				try {
					cont_bewliebfach.commit();
				} catch (UnsupportedOperationException | SQLException e1) {
					e1.printStackTrace();
				}
				if(liblvalues!=null)
					for (Iterator it_tokens = liblvalues.iterator(); it_tokens.hasNext();) {
						Object token = it_tokens.next();
						Object itemID = cont_bewliebfach.addItem();
						Item item = cont_bewliebfach.getItem(itemID);
						item.getItemProperty("bewerberprofil_id").setValue(Integer.valueOf(bewerberprofil_id));
						item.getItemProperty("lieblingsfaecher_id").setValue(Integer.valueOf(token.toString()));
					}
				try {
					cont_bewliebfach.commit();
				} catch (UnsupportedOperationException | SQLException e) {
					e.printStackTrace();
				}
				for (Iterator it_filter = arr_lieblfilt.iterator(); it_filter
						.hasNext();) {
					Filter filter = (Filter) it_filter.next();
					cont_bewliebfach.addContainerFilter(filter);
				}
				
				btn_edit.setEnabled(true);
				hl_t�tigbtns.setVisible(false);
			}
		});
		return pnl_richtung;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}

}
