package mx.springboot.datajpa.app.models.dao;

import java.util.List;

import mx.springboot.datajpa.app.models.entity.Cliente;

public interface IClienteDao {

	public List<Cliente> obtenerTodosLosClientes();

	public void guardarCliente(Cliente cliente);

	public Cliente obtenerPorId(Long id);

	public void eliminarCliente(Long id);

}