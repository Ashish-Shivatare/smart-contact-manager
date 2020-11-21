package com.smart.service;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.smart.doa.ContactRepository;
import com.smart.entities.Contact;

@Service
public class ContactServiceImpl implements ContactService {

	@Autowired
	private ContactRepository contactRepository;

	@Override
	public Page<Contact> findContactsByUser(int id, Pageable pageable) {
		return this.contactRepository.findContactsByUser(id, pageable);
	}

	@Override
	public Optional<Contact> findById(int cId) {
		return this.contactRepository.findById(cId);
	}
	
	@Override
	public void delete(Contact contact) {
		this.contactRepository.delete(contact);
	}

	@Override
	public void save(Contact contact) {
		this.contactRepository.save(contact);
	}
}
