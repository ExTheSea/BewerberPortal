package com.example.bewerberportal;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;

public class PopupL�schen extends Window {
	
	DeleteListener listener;
	
	public PopupL�schen(String firmenname, DeleteListener deleteListener) {
		this.listener = deleteListener;
		VerticalLayout vl_popup = new VerticalLayout();
		vl_popup.setMargin(true);
		vl_popup.setSpacing(true);
		vl_popup.setSizeFull();
		setContent(vl_popup);
		
		
		vl_popup.addComponent(new Label("Soll " + firmenname + " wirklich gel�scht werden?"));
		
		HorizontalLayout hl_buttons = new HorizontalLayout();
		hl_buttons.setWidth("100%");
		
		Button btn_delete = new Button("L�schen");
		btn_delete.setStyleName(ValoTheme.BUTTON_DANGER);
		btn_delete.setWidth("90%");
		btn_delete.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				deleteListener.delete();
				close();
		}});
		hl_buttons.addComponent(btn_delete);
		
		
		
		
		Button btn_cancel = new Button("Abbrechen");
		btn_cancel.setWidth("90%");
		btn_cancel.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
		}});
		hl_buttons.addComponent(btn_cancel);
		
		
		vl_popup.addComponent(hl_buttons);
		
		
		setModal(true);
		center();
		
		//K�nnen auch Prozente sein
		setWidth("400px");
		setHeight("150px");
		
		//Um standard window buttons zu deaktivieren. Einsetzen wenn n�tig
		setResizable(false);
		setClosable(false);
		
		addCloseListener(new CloseListener() {
			
			@Override
			public void windowClose(CloseEvent e) {
				deleteListener.close();
			}
		});
		
		//PopUp �ffnen
		BewerberportalUI.getCurrent().addWindow(this);
	}
	
	public interface DeleteListener{
		public void delete();
		public void close();
	}
}