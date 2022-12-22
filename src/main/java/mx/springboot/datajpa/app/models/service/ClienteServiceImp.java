package mx.springboot.datajpa.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
		return (List<Cliente>) clienteDao.findAll();
	}
	
	@Transactional(readOnly = true)
	@Override
	public Page<Cliente> obtenerTodosLosClientes(Pageable pageable) {
		return clienteDao.findAll(pageable);
	}

	@Transactional // Indicar que permite la escritura
	@Override
	public void guardarCliente(Cliente cliente) {
		clienteDao.save(cliente);
	}

	@Transactional(readOnly = true)
	@Override
	public Cliente obtenerPorId(Long id) {
		return clienteDao.findById(id).orElse(null);
	}

	@Transactional
	@Override
	public void eliminarCliente(Long id) {
		clienteDao.deleteById(id);
	}

}
