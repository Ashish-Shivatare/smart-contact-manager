package com.smart.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.smart.entities.Contact;

public interface ContactService {
	Page<Contact> findContactsByUser(int id, Pageable pageable);
	Optional<Contact> findById(int cId);
	void delete(Contact contact);
	void save(Contact contact);
}
