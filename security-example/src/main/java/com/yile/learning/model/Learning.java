package com.yile.learning.model;

import javax.ws.rs.FormParam;

public class Learning implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@FormParam("id")
	private String id;
	@FormParam("name")
	private String name;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
