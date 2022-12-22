package mx.springboot.datajpa.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import mx.springboot.datajpa.app.models.entity.Cliente;

public interface IClienteService {

	public List<Cliente> obtenerTodosLosClientes();
	
	//Trabajar con registros paginables
	public Page<Cliente> obtenerTodosLosClientes(Pageable pageable);

	public void guardarCliente(Cliente cliente);

	public Cliente obtenerPorId(Long id);

	public void eliminarCliente(Long id);
	
}
