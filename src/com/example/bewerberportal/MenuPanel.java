package com.example.bewerberportal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class MenuPanel extends Panel {
	
	private Navigator navigator;
	private VerticalLayout vl_menu;
    private Map<String, Button> viewButtons = new HashMap<String, Button>();
    
	public MenuPanel(CssLayout componentContainer) {
		setStyleName(ValoTheme.PANEL_BORDERLESS);
		addStyleName(ValoTheme.PANEL_WELL);
		addStyleName("menupnl");
		vl_menu = new VerticalLayout();
		setContent(vl_menu);
		setWidth("200px");
		setHeight("100%");
		

//		Panel pnl_logo = new Panel();
//		HorizontalLayout hl_logo = new HorizontalLayout();
//		hl_logo.setWidth("100%");
//		pnl_logo.setContent(hl_logo);
//		pnl_logo.setStyleName(ValoTheme.PANEL_BORDERLESS);
//		pnl_logo.addStyleName("logo");
//		pnl_logo.setWidth("100%");
//		pnl_logo.setHeight(null);
//		Label lbl_logo = new Label("GO2DHBW");
//		lbl_logo.setSizeUndefined();
		Image img_logo = new Image(null, new ThemeResource("Logo.png"));
		img_logo.setHeight("90px");
		img_logo.setWidth("100%");
//		hl_logo.addComponent(img_logo);
		//hl_logo.setComponentAlignment(img_logo, Alignment.MIDDLE_CENTER);
		vl_menu.addComponent(img_logo);
		
		navigator = new Navigator(BewerberportalUI.getCurrent(), componentContainer);
		BewerberportalUI.getCurrent().setNavigator(navigator);
	}
	
	public void removeAllViews(){
		for (Iterator it_views = viewButtons.keySet().iterator(); it_views.hasNext();) {
			String viewname = (String) it_views.next();
			navigator.removeView(viewname);
			vl_menu.removeComponent(viewButtons.get(viewname));
		}
	}
	
	public void addView(View view, String name, String caption, Resource icon){
		navigator.addView(name, view);
        createViewButton(name, caption, icon);
	}
	
	private void createViewButton(final String name, String caption,
            Resource icon) {
        Button button = new Button(caption, new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                navigator.navigateTo(name);
                setActiveView(name);

            }
        });
        button.setPrimaryStyleName(ValoTheme.MENU_ITEM);
        button.setIcon(icon);
        vl_menu.addComponent(button);
        viewButtons.put(name, button);
    }

    public void setActiveView(String viewName) {
        for (Button button : viewButtons.values()) {
            button.removeStyleName("selected");
        }
        Button selected = viewButtons.get(viewName);
        if (selected != null) {
            selected.addStyleName("selected");
        }
    	navigator.navigateTo(viewName);
    }
}
