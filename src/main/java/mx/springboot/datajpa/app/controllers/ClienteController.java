package mx.springboot.datajpa.app.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import mx.springboot.datajpa.app.models.entity.Cliente;
import mx.springboot.datajpa.app.models.service.IClienteService;
import mx.springboot.datajpa.app.models.service.IUploadFileService;
import mx.springboot.datajpa.app.util.paginator.PageRender;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	// Inyeccion de Service
	@Autowired
	@Qualifier(value = "clienteService")
	private IClienteService clienteService;

	@Autowired
	private IUploadFileService uploadFileService;

	// Carga de imagenes de forma programatica
	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> subirFoto(@PathVariable String filename) {

		Resource recurso = null;
		try {
			recurso = uploadFileService.cargarFoto(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok() // Carga de imagen en el cuerpo de la respuesta Http
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment: filename= \"" + recurso.getFilename() + "\" ")
				.body(recurso);
	}

	@GetMapping("/detalles/{id}")
	public String detalles(@PathVariable(name = "id") Long id, Map<String, Object> modelo, RedirectAttributes flash) {
		Cliente cliente = clienteService.obtenerPorId(id);

		if (cliente == null) {
			flash.addFlashAttribute("error", "Cliente no encontrado en la base de datos.");
			return "redirect:/listar";
		}

		modelo.put("cliente", cliente);
		modelo.put("titulo", "Detalles de " + cliente.getNombre() + " " + cliente.getApellido());

		return "detalles";
	}

	@RequestMapping(value = { "", "/", "/listar" }, method = RequestMethod.GET)
	public String listar(@RequestParam(name = "pagina", defaultValue = "0") int page, Model modelo) {
		// Trabajar la paginacion con su metodo estatico, indicanto 4 registros por
		// pagina
		Pageable pageRequest = PageRequest.of(page, 5);

		// Realizar la invocacion del service findAll(Pageable)
		Page<Cliente> clientes = clienteService.obtenerTodosLosClientes(pageRequest);

		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
		// Cargar atributos al modelo
		modelo.addAttribute("titulo", "Listado Clientes");

		// Llamar al service para obtener ALGUNOS cliente, TRABAJANDO PAGINACION
		modelo.addAttribute("clientes", clientes);
		modelo.addAttribute("page", pageRender);

		// Llamar al service para obtener TODOS los clientes , SIN PAGINACION
		// modelo.addAttribute("clientes", clienteService.obtenerTodosLosClientes());
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
	public String editarCliente(Map<String, Object> modelo, @PathVariable(value = "id") Long id,
			RedirectAttributes flash) {

		Cliente clienteToEdit = null;

		if (id > 0) {
			clienteToEdit = clienteService.obtenerPorId(id);
			if (clienteToEdit == null) {
				flash.addFlashAttribute("error", "Cliente no existe en la Base  de Datos.");
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", "Id de Cliente no puede ser menor o igual a 0.");
			return "redirect:/listar";
		}

		modelo.put("cliente", clienteToEdit);
		modelo.put("titulo", "Editar Cliente");
		return "form";
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String guardarCliente(@Valid Cliente cliente, BindingResult result, Model modelo,
			@RequestParam("file") MultipartFile foto, SessionStatus status, RedirectAttributes flash) {
		// Recibe y valida el cliente con los datos cargados

		if (result.hasErrors()) { // Si se encuentrar errores, retornar al formulario con sus errores detectados
			// El cliente no se agrega el modelo por que gracias a que lleva el mismo nombre
			// de atributo como el obj , se reconoce automaticamente
			modelo.addAttribute("titulo", "Agregar Cliente");
			return "form";
		}

		if (!foto.isEmpty()) {// Si foto no es vacio guardar en uploads

			if (cliente.getId() != null && // Confirmar si este usuario esta en la DB y contiene una foto a reemplazar
					cliente.getId() > 0 && cliente.getFoto() != null && cliente.getFoto().length() > 0) {
				// Validar que el archivo exista para su reemplazo
				uploadFileService.eliminar(cliente.getFoto());
			}

			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copiar(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			flash.addFlashAttribute("info", uniqueFilename + " subida correctamente."); // Arrojar alerta de exito
			cliente.setFoto(uniqueFilename); // Guardar en la DB

		}

		// Modifica el mensaje si existe o no el id del cliente actual en la session
		String mensajeFlash = (cliente.getId() != null) ? "Cliente actualizado exitosamente."
				: "Cliente creado exitosamente.";

		clienteService.guardarCliente(cliente);
		status.setComplete(); // Eliminar el obj Cliente de la Sesion
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:/listar";
	}

	@RequestMapping(value = "/eliminar/{id}")
	public String eliminarCliente(@PathVariable(name = "id") Long id, RedirectAttributes flash) {

		if (id > 0) {
			Cliente cliente = clienteService.obtenerPorId(id);

			clienteService.eliminarCliente(id);
			flash.addFlashAttribute("success", "Cliente eliminado exitosamente.");

			if (uploadFileService.eliminar(cliente.getFoto())) {
				flash.addFlashAttribute("info", "Foto " + cliente.getFoto() + " eliminada con ??xito.");
			}

		}

		return "redirect:/listar";
	}

}
