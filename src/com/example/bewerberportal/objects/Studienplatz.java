package com.example.bewerberportal.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Studienplatz implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	public Studienplatz (Integer id, String name){
		this.setId(id);
		this.setName(name);
	}
	public Studienplatz(){
		
	}
	
	private Integer id;
	private String name;
	
	
	
	public static List<Studienplatz> getStudienplatz (){
		ArrayList<Studienplatz> liste = new ArrayList<Studienplatz>();
		liste.add(new Studienplatz(1, "Wirtschaftsinformatik"));
		liste.add(new Studienplatz(2, "Informatik"));
		liste.add(new Studienplatz(3, "Betriebswirtschaftslehre"));
		return liste;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
}
