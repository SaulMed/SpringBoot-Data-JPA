package mx.springboot.datajpa.app.models.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mx.springboot.datajpa.app.models.entity.Cliente;

@Repository(value = "clienteDaoJPA") // Si se tiene mas de un componente que implemente el Dao se trabaja con
										// Qualifier
public class ClienteDaoImp implements IClienteDao {

	// Inyeccion del EntityManager
	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	@Override
	public List<Cliente> obtenerTodosLosClientes() {
		// Nombre de la CLASE , no de la tabla
		return em.createQuery("from Cliente").getResultList();
	}

	@Override
	public Cliente obtenerPorId(Long id) {
		return em.find(Cliente.class, id); // Obtener objeto Cliente por id
	}

	@Override
	public void guardarCliente(Cliente cliente) {
		if (cliente.getId() != null && cliente.getId() > 0) { // Si el cliente cuenta con Id , se actualiza sino se agrega
			em.merge(cliente);
		} else {
			em.persist(cliente); // Se guarda el nuevo obj cliente dentro del contexto JPA
		}
	}

	@Override
	public void eliminarCliente(Long id) {
		// Obtener cliente por id y elimina
		em.remove(obtenerPorId(id));
	}

}
