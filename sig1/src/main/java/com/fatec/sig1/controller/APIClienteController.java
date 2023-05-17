package com.fatec.sig1.controller;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.sig1.model.Cliente;
import com.fatec.sig1.model.ClienteDTO;
import com.fatec.sig1.model.Endereco;
import com.fatec.sig1.services.MantemClienteI;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/clientes")
/*
 * Trata as requisicoes HTTP enviadas pelo usuario do servico
 */
public class APIClienteController {
	@Autowired
	MantemClienteI mantemCliente;

	Logger logger = LogManager.getLogger(this.getClass());

	@CrossOrigin // desabilita o cors do spring security
	@PostMapping
	public ResponseEntity<Object> saveCliente(@RequestBody @Valid ClienteDTO c, BindingResult result) {
		Cliente cliente = new Cliente();
		
		if (result.hasErrors()) {
			logger.info(">>>>>> apicontroller validacao da entrada dados invalidos" + result.getFieldError());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos.");
		}
		if (mantemCliente.consultaPorCpf(c.getCpf()).isPresent()) {
			logger.info(">>>>>> apicontroller consultaporcpf cpf ja cadastrado");
			return ResponseEntity.status(HttpStatus.CONFLICT).body("CPF já cadastrado");
		}
		try {
			cliente.setDataNascimento(c.getDataNascimento());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		Optional<Endereco> endereco = mantemCliente.obtemEndereco(c.getCep());
		logger.info(">>>>>> apicontroller obtem endereco => " + c.getCep());
		if (endereco.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CEP invalido");
		}
		try {
			if (c.getNome().equals("") || c.getCpf().equals("") 
					|| c.getDataNascimento().equals("") || c.getSexo().equals("") || c.getComplemento().equals("")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Infomações obrigatorias invalidas");
			}
			return ResponseEntity.status(HttpStatus.CREATED).body(mantemCliente.save(c.retornaUmCliente()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro não esperado ");
		}
	}

	@CrossOrigin // desabilita o cors do spring security
	@GetMapping
	public ResponseEntity<List<Cliente>> consultaTodos() {
		return ResponseEntity.status(HttpStatus.OK).body(mantemCliente.consultaTodos());
	}

	@CrossOrigin // desabilita o cors do spring security
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deletePorId(@PathVariable(value = "id") Long id) {
		Optional<Cliente> cliente = mantemCliente.consultaPorId(id);
		if (cliente.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id não encontrado.");
		}
		mantemCliente.delete(cliente.get().getId());
		return ResponseEntity.status(HttpStatus.OK).body("Cliente excluido");
	}

	@CrossOrigin // desabilita o cors do spring security
	@PutMapping("/{id}")
	public ResponseEntity<Object> atualiza(@PathVariable long id, @RequestBody @Valid ClienteDTO clienteDTO,
			BindingResult result) {
		logger.info(">>>>>> api atualiza informações de cliente chamado");
		if (result.hasErrors()) {
			logger.info(">>>>>> apicontroller atualiza informações de cliente chamado dados invalidos");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos.");
		}
		Optional<Cliente> c = mantemCliente.consultaPorId(id);
		if (c.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id não encontrado.");
		}
		Optional<Endereco> e = mantemCliente.obtemEndereco(clienteDTO.getCep());
		if (e.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CEP não localizado.");
		}
		Optional<Cliente> cliente = mantemCliente.atualiza(id, clienteDTO.retornaUmCliente());
		return ResponseEntity.status(HttpStatus.OK).body(cliente.get());
	}

	@CrossOrigin // desabilita o cors do spring security
	@GetMapping("/{id}")
	public ResponseEntity<Object> consultaPorId(@PathVariable Long id) {
		logger.info(">>>>>> apicontroller consulta por id chamado");
		Optional<Cliente> cliente = mantemCliente.consultaPorId(id);
		if (cliente.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id não encontrado.");
		}
		return ResponseEntity.status(HttpStatus.OK).body(cliente.get());
	}
}
