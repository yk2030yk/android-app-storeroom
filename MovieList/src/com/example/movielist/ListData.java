package com.example.movielist;

public class ListData {
	public int id;
	public String name;
	public String detail = null;
	public int checked;
	public int del;
	public int tid;

	public ListData(int id, String name, int checked, int del, int tid) {
		this.id = id;
		this.name = name;
		this.checked = checked;
		this.del = del;
		this.tid = tid;
	}

	public void setDetail(String d) {
		this.detail = d;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDetail() {
		return detail;
	}

	public int getChecked() {
		return checked;
	}

	public int getDel() {
		return del;
	}

	public int getTid() {
		return tid;
	}
}
