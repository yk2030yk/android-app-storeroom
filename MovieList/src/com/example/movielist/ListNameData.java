package com.example.movielist;

public class ListNameData {
	public int id;
	public String name;
	public int del;

	public ListNameData(int id, String name, int del) {
		this.id = id;
		this.name = name;
		this.del = del;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getDel() {
		return del;
	}
}
