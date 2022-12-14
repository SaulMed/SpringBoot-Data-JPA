package mx.springboot.datajpa.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.springboot.datajpa.app.models.dao.IClienteDao;
import mx.springboot.datajpa.app.models.entity.Cliente;

@Service(value="clienteService")	
public class ClienteServiceImp implements IClienteService {
	
	@Autowired
	private IClienteDao clienteDao;
		
	@Transactional(readOnly = true)
	@Override
	public List<Cliente> obtenerTodosLosClientes() {
		return clienteDao.obtenerTodosLosClientes();
	}

	@Transactional // Indicar que permite la escritura
	@Override
	public void guardarCliente(Cliente cliente) {
		clienteDao.guardarCliente(cliente);
	}

	@Transactional(readOnly = true)
	@Override
	public Cliente obtenerPorId(Long id) {
		return clienteDao.obtenerPorId(id);
	}

	@Transactional
	@Override
	public void eliminarCliente(Long id) {
		clienteDao.eliminarCliente(id);
	}

}
