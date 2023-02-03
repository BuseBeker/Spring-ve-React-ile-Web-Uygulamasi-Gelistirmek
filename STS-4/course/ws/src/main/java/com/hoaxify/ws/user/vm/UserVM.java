package com.hoaxify.ws.user.vm;

import com.hoaxify.ws.user.Users;

import lombok.Data;

@Data
public class UserVM {
	
	private String username;
	
	private String displayName;
	
	private String image;
	
	public UserVM(Users user) {
		this.setUsername(user.getUsername());
		this.setDisplayName(user.getDisplayName());
		this.setImage(user.getImage());
	}
	
}
