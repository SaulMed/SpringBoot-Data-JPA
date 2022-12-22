package mx.springboot.datajpa.app.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.springboot.datajpa.app.models.entity.Cliente;
	//Jpa extiende de PagingAndSortingRepository para trabajra con paginacion
public interface IClienteDao extends JpaRepository<Cliente,Long>{
	
}
