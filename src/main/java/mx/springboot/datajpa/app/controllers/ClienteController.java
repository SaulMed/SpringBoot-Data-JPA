package mx.springboot.datajpa.app.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import jakarta.validation.Valid;
import mx.springboot.datajpa.app.models.entity.Cliente;
import mx.springboot.datajpa.app.models.service.IClienteService;


@Controller
@SessionAttributes("cliente")
public class ClienteController {

	// Inyeccion de DAO
	@Autowired
	@Qualifier(value = "clienteService")
	private IClienteService clienteService;

	@RequestMapping(value = { "", "/", "/listar" }, method = RequestMethod.GET)
	public String listar(Model modelo) {
		modelo.addAttribute("titulo", "Listado Clientes");
		modelo.addAttribute("clientes", clienteService.obtenerTodosLosClientes());
		return "listar";
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET) // Mostrar Form para AGREGAR datos de cliente
	public String crearCliente(Map<String, Object> modelo) {
		modelo.put("titulo", "Agregar Cliente");
		Cliente cliente = new Cliente();
		modelo.put("cliente", cliente);
		return "form";
	}

	@RequestMapping(value = "/form/{id}") // Mostrar Form con data del Cliente para comenzar a EDITAR
	public String editarCliente(Map<String, Object> modelo, @PathVariable(value = "id") Long id) {
		
		Cliente clienteToEdit = null;
		
		if (id > 0) {
			clienteToEdit = clienteService.obtenerPorId(id);
		} else {
			return "redirect:/listar";
		}
		
		modelo.put("cliente", clienteToEdit);
		modelo.put("titulo", "Editar Cliente");
		return "form";
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String guardarCliente(@Valid Cliente cliente, BindingResult result, Model modelo, SessionStatus status) { 
		// Recibe y valida el cliente con los datos cargados

		if (result.hasErrors()) { // Si se encuentrar errores, retornar al formulario con sus errores detectados
			// El cliente no se agrega el modelo por que gracias a que lleva el mismo nombre
			// de atributo como el obj , se reconoce automaticamente
			modelo.addAttribute("titulo", "Agregar Cliente");
			return "form";
		}

		clienteService.guardarCliente(cliente);
		status.setComplete();	//Eliminar el obj Cliente de la Sesion
		return "redirect:/listar";
	}

	@RequestMapping(value="/eliminar/{id}")
	public String eliminarCliente(@PathVariable(name="id") Long id) {
		
		if(id >0) {
			clienteService.eliminarCliente(id);
		}
		
		return"redirect:/listar";
	}
	
}
