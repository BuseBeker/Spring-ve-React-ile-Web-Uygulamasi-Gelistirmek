package com.hoaxify.ws.hoax;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hoaxify.ws.file.FileAttachment;
import com.hoaxify.ws.file.FileAttachmentRepository;
import com.hoaxify.ws.file.FileService;
import com.hoaxify.ws.hoax.vm.HoaxSubmitVM;
import com.hoaxify.ws.user.Users;
import com.hoaxify.ws.user.UserService;

@Service
public class HoaxService {
	
	HoaxRepository hoaxRepository;
	
	UserService userService;
	
	FileAttachmentRepository fileAttachmentRepository;
	
	FileService fileService;

	public HoaxService(HoaxRepository hoaxRepository, UserService userService, FileAttachmentRepository fileAttachmentRepository
			,FileService fileService) {
		super();
		this.hoaxRepository = hoaxRepository;
		this.fileAttachmentRepository = fileAttachmentRepository;
		this.fileService = fileService;
		this.userService = userService;
	}
	
	public void save(HoaxSubmitVM hoaxSubmitVM, Users user) {
		Hoaxes hoax = new Hoaxes();
		hoax.setContent(hoaxSubmitVM.getContent());
		hoax.setTimestamp(new Date());
		hoax.setUser(user);
		hoaxRepository.save(hoax);
		Optional<FileAttachment> optionalFileAttachment = fileAttachmentRepository.findById(hoaxSubmitVM.getAttachmentId());
		if(optionalFileAttachment.isPresent()) {
			FileAttachment fileAttachment = optionalFileAttachment.get();
			fileAttachment.setHoax(hoax);
			fileAttachmentRepository.save(fileAttachment);
		}
	}

	public Page<Hoaxes> getHoaxes(Pageable page) {
		return hoaxRepository.findAll(page);
	}

	public Page<Hoaxes> getHoaxesOfUser(String username, Pageable page) {
		Users inDB = userService.getByUsername(username);
		return hoaxRepository.findByUser(inDB, page);
	}

	public Page<Hoaxes> getOldHoaxes(long id, String username, Pageable page) {
		Specification<Hoaxes> specification = idLessThan(id);
		if(username != null) {
			Users inDB = userService.getByUsername(username);
			specification = specification.and(userIs(inDB));
		}
		return hoaxRepository.findAll(specification, page);
	}

	public long getNewHoaxesCount(long id, String username) {
		Specification<Hoaxes> specification = idGreaterThan(id);
		if(username != null) {
			Users inDB = userService.getByUsername(username);
			specification = specification.and(userIs(inDB));
		}
		return hoaxRepository.count(specification);
	}

	public List<Hoaxes> getNewHoaxes(long id, String username, Sort sort) {
		Specification<Hoaxes> specification = idGreaterThan(id);
		if(username != null) {
			Users inDB = userService.getByUsername(username);
			specification = specification.and(userIs(inDB));
		}
		return hoaxRepository.findAll(specification, sort);
	}
	
	
	Specification<Hoaxes> idLessThan(long id){
		return (root, query, criteriaBuilder) -> {
			return criteriaBuilder.lessThan(root.get("id"), id);
		};
	}
	
	Specification<Hoaxes> userIs(Users user){
		return (root, query, criteriaBuilder) -> {
			return criteriaBuilder.equal(root.get("user"), user);
		};
	}
	
	Specification<Hoaxes> idGreaterThan(long id){
		return (root, query, criteriaBuilder) -> {
			return criteriaBuilder.greaterThan(root.get("id"), id);
		};
	}

	public void delete(long id) {
		Hoaxes inDB = hoaxRepository.getReferenceById(id);
		if(inDB.getFileAttachment() != null) {
			String fileName = inDB.getFileAttachment().getName();
			fileService.deleteAttachmentFile(fileName);
		}
		hoaxRepository.deleteById(id);
	}

}
