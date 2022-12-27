package mx.springboot.datajpa.app.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileServiceImp implements IUploadFileService {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final static String UPLOADS_FOLDER = "uploads";

	@Override
	public Resource cargarFoto(String filename) throws MalformedURLException {

		Path pathFoto = getAbsolutePath(filename);
		log.info("pathFoto: " + pathFoto);
		Resource recurso = null;

		recurso = new UrlResource(pathFoto.toUri()); // Carga de imagen
		if (!recurso.exists() || !recurso.isReadable()) {
			throw new RuntimeException("Error: No se puede cargar la imagen " + pathFoto.toString());
		}

		return recurso;
	}

	@Override
	public String copiar(MultipartFile file) throws IOException {
		// Generar nombre unico dentro del server
		String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

		// Ruta dentro del proyecto
		Path rootPathAbsolute = getAbsolutePath(uniqueFilename);

		// Muestra por consola las rutas
		log.info("rootPathAbsolute: " + rootPathAbsolute);

		Files.copy(file.getInputStream(), rootPathAbsolute);

		return uniqueFilename;
	}

	@Override
	public Boolean eliminar(String filename) {//Validar que la foto exista antes de eliminarla
		Path rootAbsolutePath = getAbsolutePath(filename);
		File archivo = rootAbsolutePath.toFile();
		
		if(archivo.canRead() && archivo.exists()) {	//Eliminar la foto de la carpeta uploads
			if(archivo.delete()){
				return true;		
			}
		}
		return false;
	}

	public Path getAbsolutePath(String filename) {
		return Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();
	}

	@Override
	public void eliminarArchivos() {	//Eliminar archivos de carpeta uploads
		FileSystemUtils.deleteRecursively(Paths.get(UPLOADS_FOLDER).toFile());		
	}

	@Override
	public void iniciar() throws IOException {	//Creacion de directorio uploads automaticamente
		Files.createDirectory(Paths.get(UPLOADS_FOLDER));
	}

}
