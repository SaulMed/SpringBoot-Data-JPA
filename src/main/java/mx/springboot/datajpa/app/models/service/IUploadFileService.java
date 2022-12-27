package mx.springboot.datajpa.app.models.service;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileService {

	public Resource cargarFoto(String filename) throws MalformedURLException;
	
	public String copiar(MultipartFile file) throws IOException;
	
	public Boolean eliminar(String filename);
	
	public void eliminarArchivos();
	
	public void iniciar() throws IOException;
	
}
