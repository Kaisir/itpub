package com.wisedu.emap.itpub.bean;

import lombok.Data;

public @Data class Receivers {

	private String userId;
	private String mobile;
	private String email;
	private Short flag;// 标记抄送或密送

	public Receivers(String userId, String mobile, String email, Short flag) {
		super();
		this.userId = userId;
		this.mobile = mobile;
		this.email = email;
		this.flag = flag;
	}

	public Receivers() {
	}
}
