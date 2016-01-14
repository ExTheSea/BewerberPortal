package com.example.bewerberportal.objects;

import java.io.Serializable;

public class Studienplatz implements Serializable{
	public Studienplatz (Integer id, String name){
		this.id = id;
		this.name = name;
	}
	public Studienplatz(){
		
	}
	
	private Integer id;
	private String name;
	
	
}
