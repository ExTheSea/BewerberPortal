package com.example.bewerberportal;

import javax.servlet.annotation.WebServlet;

import com.example.data.DatabaseConnector;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("bewerberportal")
public class BewerberportalUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = BewerberportalUI.class, widgetset = "com.example.bewerberportal.widgetset.BewerberportalWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		addStyleName("whitebackground");
		Responsive.makeResponsive(this);
		
		final VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		setContent(layout);
		
		Panel pnl_header = new Panel();
		HorizontalLayout hl_header = new HorizontalLayout();
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
		
		Button btn_login = new Button("Anmelden");
		btn_login.setStyleName(ValoTheme.BUTTON_BORDERLESS);
		hl_header.addComponent(btn_login);
		hl_header.setExpandRatio(btn_login, 1f);
		hl_header.setComponentAlignment(btn_login, Alignment.MIDDLE_RIGHT);
		
		
		Button btn_register = new Button("Registrieren");
		btn_register.setStyleName(ValoTheme.BUTTON_BORDERLESS);
		hl_header.addComponent(btn_register);
		
		HorizontalLayout hl_content = new HorizontalLayout();
		hl_content.setSizeFull();
		layout.addComponent(hl_content);
		layout.setExpandRatio(hl_content, 1f);
		
		CssLayout css_content = new CssLayout();
		css_content.setSizeFull();
		
		MenuPanel pnl_menu = new MenuPanel(css_content);
		hl_content.addComponent(pnl_menu);
		hl_content.addComponent(css_content);
		hl_content.setExpandRatio(css_content, 1f);
		
		pnl_menu.addView(new TestView(), "Test", "Test", FontAwesome.ASTERISK);
		pnl_menu.addView(new TestView2(), "Test2", "Test2", FontAwesome.ASTERISK);
		pnl_menu.addView(new SucheStudentView(), "Suche", "Suche", FontAwesome.ASTERISK);

		pnl_menu.setActiveView("Test");
	}
	
	public static BewerberportalUI getCurrent(){
		return (BewerberportalUI) UI.getCurrent();
	}

}