package com.valdir.helpdesk.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.valdir.helpdesk.domain.Pessoa;
import com.valdir.helpdesk.domain.Tecnico;
import com.valdir.helpdesk.domain.dtos.TecnicoDto;
import com.valdir.helpdesk.repositories.PessoaRepository;
import com.valdir.helpdesk.repositories.TecnicoRepository;
import com.valdir.helpdesk.services.exceptions.DataIntegrityViolationException;
import com.valdir.helpdesk.services.exceptions.ObjectNotFoundException;

@Service
public class TecnicoService {

	@Autowired
	private TecnicoRepository repository;
	
	@Autowired
	private PessoaRepository pessoaRepository;

	public Tecnico findById(Integer id) {
		Optional<Tecnico> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! id: " + id));
	}

	public List<Tecnico> findAll() {
		return repository.findAll();
	}

	public Tecnico create(TecnicoDto objDto) {
		objDto.setId(null);
		validaPorCpfEEmail(objDto);
		Tecnico newOBj = new Tecnico(objDto);
		return repository.save(newOBj);
	}
	
	public Tecnico update(Integer id, @Valid TecnicoDto objDto) {
		objDto.setId(id);
		Tecnico oldObj = findById(id);
		validaPorCpfEEmail(objDto);
		oldObj = new Tecnico(objDto);
		return repository.save(oldObj);
	}

	private void validaPorCpfEEmail(TecnicoDto objDto) {
		Optional<Pessoa> obj = pessoaRepository.findByCpf(objDto.getCpf());
		if(obj.isPresent() && obj.get().getId() != objDto.getId()) {
			throw new DataIntegrityViolationException("CPF já cadastrado no sistema!");
		}
		
		obj = pessoaRepository.findByEmail(objDto.getEmail());
		if(obj.isPresent() && obj.get().getId() != objDto.getId()) {
			throw new DataIntegrityViolationException("E-mail já cadastrado no sistema!");
		}
	}

	public void delete(Integer id) {
		Tecnico obj = findById(id);
		if (obj.getChamados().size() > 0) {
			throw new DataIntegrityViolationException("Técnico possui ordens de serviço e não pode ser deletado!");
		}
		repository.deleteById(id);
	}

}
