package com.hoaxify.ws.hoax;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.hoaxify.ws.user.Users;

public interface HoaxRepository extends JpaRepository<Hoaxes, Long>, JpaSpecificationExecutor<Hoaxes>{
	
	Page<Hoaxes> findByUser(Users user, Pageable page);

}
