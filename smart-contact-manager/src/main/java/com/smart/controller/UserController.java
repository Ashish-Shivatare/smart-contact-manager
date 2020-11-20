package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.doa.ContactRepository;
import com.smart.doa.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;

	//Get user by username
	@ModelAttribute
	public void getUser(Model model, Principal principal) {
		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);
		model.addAttribute("user", user);
	}


	@RequestMapping("/dashboard")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}


	@GetMapping("/add-contact")
	public String openAddContact(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact";
	}

	//processing add contact form
	@PostMapping("/add-contact-form")
	public String addContactForm(@ModelAttribute Contact contact, @RequestParam("image") MultipartFile file, Principal principal, HttpSession session) {
		try {

			String username = principal.getName();
			User user = this.userRepository.getUserByUserName(username);
		
			//processing and uploading file
			if(file.isEmpty()) {
				System.out.println("file is empty");
				contact.setProfileImage("contact.png");
			}
			else {
				contact.setProfileImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			
			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);
			session.setAttribute("message", new Message("Contact Added Successfully !!","success"));
		}
		catch (Exception e) {
			session.setAttribute("message", new Message("Something went wrong !! Please try Again !!","danger"));
			System.out.println("Error" + e.getMessage());
			e.printStackTrace();
		}
		return "normal/add_contact";
	}
	
	
	// per page = 5 [n]
	// current page = 0 [page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") int page,Model model, Principal principal) {
		model.addAttribute("title","Show Contacts");

		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);
		
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
		
		model.addAttribute("contacts", contacts);
		model.addAttribute("totalPages", contacts.getTotalPages());
		model.addAttribute("currentPage", page);
		
		return "normal/show_contact";
	}
	
	@GetMapping("/contact/{cId}")
	public String showContactDetail(@PathVariable("cId") int cId,Model model, Principal principal) {
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);
		if(user.getId() == contact.getUser().getId()) {
			model.addAttribute("title", contact.getName());
			model.addAttribute("contact", contact);
		}
		return "normal/contact_detail";
	}
	
	@GetMapping("/contact/delete/{cId}")
	public String deleteContact(@PathVariable("cId") int cId, Model model, Principal principal, HttpSession session) {
		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);
		Contact contact = this.contactRepository.findById(cId).get();
		
		if(user.getId() == contact.getUser().getId()) {
			contact.setUser(null);
			this.contactRepository.delete(contact);
			session.setAttribute("message", new Message("Contact Deleted Successfully", "success"));
		}else {
			session.setAttribute("message", new Message("You do not have access to delete this contact", "danger"));
		}
		return "redirect:/user/show-contacts/0";
	}
	
	@PostMapping("/contact/update/{cId}")
	public String updateContact(@PathVariable("cId") int cId, Model model) {
		Contact contact = this.contactRepository.findById(cId).get();
		model.addAttribute("title", contact.getName());
		model.addAttribute("contact",contact);
		return "normal/update_contact";
	}
	
	@PostMapping("/contact/updateForm")
	public String updateForm(@ModelAttribute Contact contact, @RequestParam("image") MultipartFile file, Model model,Principal principal, HttpSession session) {
		try {
			Contact oldContact = this.contactRepository.findById(contact.getcId()).get();
			if(!file.isEmpty()) {
				//delete old photo
				File deleteFile = new ClassPathResource("static/images").getFile();
				File f = new File(deleteFile, oldContact.getProfileImage());
				f.delete();
				
				
				//update new photo
				File saveFile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setProfileImage(file.getOriginalFilename());
			}else {
				contact.setProfileImage(oldContact.getProfileImage());
			}
			String username = principal.getName();
			User user = this.userRepository.getUserByUserName(username);
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Your Contact Updated Successfully", "success"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/contact/"+contact.getcId();
	}
}
