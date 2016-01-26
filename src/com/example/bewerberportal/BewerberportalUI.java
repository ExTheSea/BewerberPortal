package com.example.bewerberportal;

import javax.servlet.annotation.WebServlet;

import com.example.login.BasicAccessControl;
import com.example.login.CurrentUser;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@Theme("bewerberportal")
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
//		pnl_header.setStyleName(ValoTheme.PANEL_BORDERLESS);
		pnl_header.setStyleName("headerborderless");
		pnl_header.setHeight(null);
		layout.addComponent(pnl_header);
		
		Button btn_menu = new Button();
		btn_menu.setIcon(FontAwesome.BARS);
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
		
		CssLayout css_content = new CssLayout();
		css_content.setSizeFull();
		
		pnl_menu = new MenuPanel(css_content);
		hl_content.addComponent(pnl_menu);
		hl_content.addComponent(css_content);
		hl_content.setExpandRatio(css_content, 1f);
		
		pnl_menu.addView(new TestView(), "Test", "Test", FontAwesome.ASTERISK);
		pnl_menu.addView(new FirmenSucheView(), "Suche", "Suche", FontAwesome.ASTERISK);

		pnl_menu.setActiveView("Test");
	}
	
	public static BewerberportalUI getCurrent(){
		return (BewerberportalUI) UI.getCurrent();
	}

	public void login(int type, String user){
		account_id = type;
		pnl_menu.removeAllViews();
		switch (type) {
		case 0:
			pnl_menu.addView(new TestView(), "Test", "Test", FontAwesome.ASTERISK);
			pnl_menu.addView(new TestView2(), "Test2", "Test2", FontAwesome.ASTERISK);
			pnl_menu.setActiveView("Test");
			break;
		case 1:
			pnl_menu.addView(new BewerberProfil(CurrentUser.get()), "Profil", "Bewerber", FontAwesome.USER);
			pnl_menu.addView(new FirmenSucheView(), "Suche", "Suche", FontAwesome.ASTERISK);
			pnl_menu.setActiveView("Profil");
			break;
		case 2:
			pnl_menu.addView(new TestView2(), "Test2", "Test2", FontAwesome.ASTERISK);
			pnl_menu.addView(new StudentenSucheView(), "Suche", "Suche", FontAwesome.ASTERISK);
			pnl_menu.setActiveView("Test2");
			break;
		default:
			break;
		}
		Button btn_logout = new Button("Logout");
		btn_logout.setStyleName(ValoTheme.BUTTON_BORDERLESS);
		btn_logout.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				VaadinSession.getCurrent().getSession().invalidate();
                Page.getCurrent().reload();
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
}