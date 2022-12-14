package mx.springboot.datajpa.app.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.springboot.datajpa.app.models.entity.Cliente;

public interface IClienteDao extends JpaRepository<Cliente,Long>{
	
}
