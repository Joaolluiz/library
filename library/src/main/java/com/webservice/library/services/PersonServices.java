package com.webservice.library.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import com.webservice.library.controllers.PersonController;
import com.webservice.library.data.vo.v1.PersonVO;
import com.webservice.library.entities.Person;
import com.webservice.library.exceptions.ResourceNotFoundException;
import com.webservice.library.mapper.DozzerMapper;
import com.webservice.library.repositories.PersonRepository;

@Service
public class PersonServices {

	private Logger logger = Logger.getLogger(PersonServices.class.getName());

	@Autowired
	PersonRepository personRepository;
	
	public List<PersonVO> findAll() {
		
		logger.info("Finding all people!");

		return DozzerMapper.parseListObjects(personRepository.findAll(), PersonVO.class);
	}

	public PersonVO findById(Long id) {

		logger.info("Finding one person!");
		
		var entity = personRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		PersonVO vo =  DozzerMapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return vo;
	}

	public PersonVO createPerson(PersonVO person) {
		
		logger.info("Creating one person!");
		var entity = DozzerMapper.parseObject(person, Person.class);
		var vo = DozzerMapper.parseObject(personRepository.save(entity), PersonVO.class);
		return vo;
	}
	
	public PersonVO updatePerson(PersonVO person) {

		logger.info("Updating one person!");

		var entity = personRepository.findById(person.getKey())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		
		var vo = DozzerMapper.parseObject(personRepository.save(entity), PersonVO.class);
		return vo;
	}

	public void deletePerson(Long id) {

		logger.info("Deleting one person!");

		Person entity = personRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		
		personRepository.delete(entity);
	}

}
