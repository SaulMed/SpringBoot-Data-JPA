package mx.springboot.datajpa.app.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import mx.springboot.datajpa.app.models.entity.Cliente;
import mx.springboot.datajpa.app.models.service.IClienteService;
import mx.springboot.datajpa.app.util.paginator.PageRender;


@Controller
@SessionAttributes("cliente")
public class ClienteController {

	// Inyeccion de Service
	@Autowired
	@Qualifier(value = "clienteService")
	private IClienteService clienteService;

	@RequestMapping(value = { "", "/", "/listar" }, method = RequestMethod.GET)
	public String listar(@RequestParam(name="pagina", defaultValue = "0") int page, Model modelo) {
		//Trabajar la paginacion con su metodo estatico, indicanto 4 registros por pagina
		Pageable pageRequest = PageRequest.of(page, 5);
		
		//Realizar la invocacion del service findAll(Pageable)
		Page<Cliente> clientes = clienteService.obtenerTodosLosClientes(pageRequest);
		
		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
		//Cargar atributos al modelo
		modelo.addAttribute("titulo", "Listado Clientes");
		
		//Llamar al service para obtener ALGUNOS cliente, TRABAJANDO PAGINACION
		modelo.addAttribute("clientes", clientes);
		modelo.addAttribute("page", pageRender);
		
		//Llamar al service para obtener TODOS los clientes , SIN PAGINACION
		//modelo.addAttribute("clientes", clienteService.obtenerTodosLosClientes());
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
	public String editarCliente(Map<String, Object> modelo, @PathVariable(value = "id") Long id, RedirectAttributes flash) {
		
		Cliente clienteToEdit = null;
		
		if (id > 0) {
			clienteToEdit = clienteService.obtenerPorId(id);
			if(clienteToEdit == null) {
				flash.addFlashAttribute("error","Cliente no existe en la Base  de Datos.");
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error","Id de Cliente no puede ser menor o igual a 0.");
			return "redirect:/listar";
		}
		
		modelo.put("cliente", clienteToEdit);
		modelo.put("titulo", "Editar Cliente");
		return "form";
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String guardarCliente(@Valid Cliente cliente, BindingResult result, Model modelo, SessionStatus status, RedirectAttributes flash) { 
		// Recibe y valida el cliente con los datos cargados

		if (result.hasErrors()) { // Si se encuentrar errores, retornar al formulario con sus errores detectados
			// El cliente no se agrega el modelo por que gracias a que lleva el mismo nombre
			// de atributo como el obj , se reconoce automaticamente
			modelo.addAttribute("titulo", "Agregar Cliente");
			return "form";
		}
		//Modifica el mensaje si existe o no el id del cliente actual en la session
		String mensajeFlash = (cliente.getId() != null) ? "Cliente actualizado exitosamente." : "Cliente creado exitosamente.";
		
		clienteService.guardarCliente(cliente);
		status.setComplete();	//Eliminar el obj Cliente de la Sesion
		flash.addFlashAttribute("success",mensajeFlash);
		return "redirect:/listar";
	}

	@RequestMapping(value="/eliminar/{id}")
	public String eliminarCliente(@PathVariable(name="id") Long id, RedirectAttributes flash) {
		
		if(id >0) {
			clienteService.eliminarCliente(id);
			flash.addFlashAttribute("success","Cliente eliminado exitosamente.");
		}
		
		return"redirect:/listar";
	}
	
}
