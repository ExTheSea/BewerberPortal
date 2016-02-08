package com.example.admin;

import java.util.Iterator;

import com.example.bewerberportal.BewerberportalUI;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class ManageDataPopUp extends Window {
	
	public ManageDataPopUp(Item item, SaveListener listener) {
		
		VerticalLayout vl_popup = new VerticalLayout();
		vl_popup.setMargin(true);
		vl_popup.setSpacing(true);
		vl_popup.setWidth("100%");
		vl_popup.setHeight(null);
		setContent(vl_popup);
		
		FieldGroup binder = new FieldGroup(item);
		for (Iterator it_props = item.getItemPropertyIds().iterator(); it_props.hasNext();) {
			Object propId = (Object) it_props.next();
			if(!propId.equals("id"))
			vl_popup.addComponent(binder.buildAndBind(propId));
		}
		
		Button btn_save = new Button("Speichern");
		btn_save.setIcon(FontAwesome.SAVE);
		btn_save.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		vl_popup.addComponent(btn_save);
		btn_save.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					binder.commit();
					listener.save();
					close();
				} catch (CommitException e) {
					Notification.show(e.getMessage());
				}
			}
		});
		
		setModal(true);
		center();
		
		//Können auch Prozente sein
		setWidth("400px");
		setHeight(null);
		
		//Um standard window buttons zu deaktivieren. Einsetzen wenn nötig
		setResizable(false);
		setClosable(false);
		
		//PopUp öffnen
		BewerberportalUI.getCurrent().addWindow(this);
	}
	
	public static interface SaveListener{
		public void save();
	}
}
