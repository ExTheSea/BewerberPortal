package com.example.bewerberportal;

import javax.servlet.annotation.WebServlet;

import com.example.admin.AdminAnsprechpartnerView;
import com.example.admin.AdminBenutzerFirmenprofilView;
import com.example.admin.AdminBenutzerView;
import com.example.admin.AdminBewerberView;
import com.example.admin.AdminFirmenView;
import com.example.admin.AdminLieblingsfaecherView;
import com.example.admin.AdminStandortView;
import com.example.admin.AdminStudiengangView;
import com.example.admin.AdminStudienplaetzeView;
import com.example.login.BasicAccessControl;
import com.example.login.CurrentUser;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Viewport;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@Theme("bewerberportal")
@Viewport("initial-scale = 1.0,maximum-scale = 1.0")
@PreserveOnRefresh
public class BewerberportalUI extends UI {

	BasicAccessControl control = new BasicAccessControl();
	int account_id = 0;
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = BewerberportalUI.class, widgetset = "com.example.bewerberportal.widgetset.BewerberportalWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	private MenuPanel pnl_menu;
	private Button btn_register;
	private Button btn_login;
	private HorizontalLayout hl_header;
	public CssLayout css_content;
	
	@Override
	protected void init(VaadinRequest request) {
		addStyleName("whitebackground");
		Responsive.makeResponsive(this);
		final VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		setContent(layout);
		
		Panel pnl_header = new Panel();
		hl_header = new HorizontalLayout();
		hl_header.setMargin(true);
		hl_header.setWidth("100%");
		pnl_header.setContent(hl_header);
		pnl_header.setWidth("100%");
		pnl_header.setStyleName("headerborderless");
		pnl_header.setHeight(null);
		layout.addComponent(pnl_header);
		
		Button btn_menu = new Button();
		btn_menu.setIcon(FontAwesome.BARS);
		btn_menu.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				pnl_menu.setVisible(!pnl_menu.isVisible());
			}
		});
		hl_header.addComponent(btn_menu);
		
		btn_login = new Button("Anmelden");
		btn_login.setStyleName(ValoTheme.BUTTON_BORDERLESS);
		btn_login.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				new AnmeldenPopup();
			}
		});
		hl_header.addComponent(btn_login);
		hl_header.setExpandRatio(btn_login, 1f);
		hl_header.setComponentAlignment(btn_login, Alignment.MIDDLE_RIGHT);

		
		
		btn_register = new Button("Registrieren");
		btn_register.setStyleName(ValoTheme.BUTTON_BORDERLESS);
		btn_register.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				new RegistrierungsPopUp();
			}
		});
		hl_header.addComponent(btn_register);
		
		HorizontalLayout hl_content = new HorizontalLayout();
		hl_content.setSizeFull();
		layout.addComponent(hl_content);
		layout.setExpandRatio(hl_content, 1f);
		
		css_content = new CssLayout();
		css_content.setSizeFull();
		
		pnl_menu = new MenuPanel(css_content);
		hl_content.addComponent(pnl_menu);
		hl_content.addComponent(css_content);
		hl_content.setExpandRatio(css_content, 1f);
		
		pnl_menu.addView(new HomeView(), "Home", "Home", FontAwesome.HOME);
		pnl_menu.addView(new FirmenSucheView(), "Suche", "Suche", FontAwesome.SEARCH);
		pnl_menu.setActiveView("Home");
		getNavigator().setErrorView(new HomeView());
	}
	
	public static BewerberportalUI getCurrent(){
		return (BewerberportalUI) UI.getCurrent();
	}

	public void login(int type, String user){
		account_id = type;
		pnl_menu.removeAllViews();
		switch (type) {
		case 0:
			pnl_menu.addView(new AdminBenutzerView(), "Benutzer", "Benutzer", FontAwesome.USERS);
			pnl_menu.addView(new AdminBewerberView(), "Bewerber", "Bewerber", FontAwesome.USER);
			pnl_menu.addView(new AdminFirmenView(), "Firma", "Firma", FontAwesome.BUILDING);
			pnl_menu.addView(new AdminBenutzerFirmenprofilView(), "benfirm", "Benutzer-Firma", FontAwesome.LINK);
			pnl_menu.addView(new AdminStandortView(), "Standort", "Standort", FontAwesome.BUILDING_O);
			pnl_menu.addView(new AdminAnsprechpartnerView(), "Ansprechpartner", "Ansprechpartner", FontAwesome.PHONE);
			pnl_menu.addView(new AdminStudienplaetzeView(), "Studienplaetze", "Studienplaetze", FontAwesome.LIST);
			pnl_menu.addView(new AdminStudiengangView(), "Studiengang", "Studiengang", FontAwesome.INSTITUTION);
			pnl_menu.addView(new AdminLieblingsfaecherView(), "Lieblingsfaecher", "Lieblingsfaecher", FontAwesome.HEART);
			pnl_menu.setActiveView("Benutzer");
			break;
		case 1:
			pnl_menu.addView(new BewerberProfil(CurrentUser.get()), "Profil", "Bewerber", FontAwesome.USER);
			pnl_menu.addView(new FirmenSucheView(), "Suche", "Suche", FontAwesome.SEARCH);
			pnl_menu.setActiveView("Profil");
			break;
		case 2:
			pnl_menu.addView(new FirmenProfil(CurrentUser.get()), "Profil", "Profil", FontAwesome.BUILDING);
			pnl_menu.addView(new StellenangebotView(), "Stellenangebot", "Stellenangebot", FontAwesome.CLIPBOARD);
			pnl_menu.addView(new StudentenSucheView(), "Suche", "Suche", FontAwesome.SEARCH);
			pnl_menu.setActiveView("Profil");
			break;
		default:
			break;
		}
		Button btn_logout = new Button("Logout");
		btn_logout.setStyleName(ValoTheme.BUTTON_BORDERLESS);
		btn_logout.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				CurrentUser.set("");
				VaadinSession.getCurrent().getSession().invalidate();
                Page.getCurrent().reload();
				CurrentUser.set("");
			}
		});
		hl_header.replaceComponent(btn_register, btn_logout);
		
		Button btn_user = new Button(user);
		btn_user.setStyleName(ValoTheme.BUTTON_BORDERLESS);
		btn_user.setIcon(FontAwesome.USER);
		hl_header.replaceComponent(btn_login, btn_user);
		btn_user.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(type != 0)
					pnl_menu.setActiveView("Profil");
			}
		});
		
	}
	@Override
	public void close() {
		CurrentUser.set("");
		super.close();
	}
}