package mx.springboot.datajpa.app.util.paginator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

public class PageRender<T> {

	private String url;	//Se establece como "listar" , definido en el "pageRender"
	private Page<T> page;
	private List<PageItem> paginas;

	private int totalPaginas;
	private int numElementosPorPagina;
	private int paginaActual;

	public PageRender(String url, Page<T> page) {
		this.url = url;
		this.page = page;
		this.paginas = new ArrayList<PageItem>();

		// Obtener num. de elementos por página junto con el Total de paginas
		numElementosPorPagina = page.getSize();
		totalPaginas = page.getTotalPages();
		paginaActual = page.getNumber() + 1;

		int desde, hasta;

		if (totalPaginas <= numElementosPorPagina) {	//Mostrar paginador por completo
			desde = 1;
			hasta = totalPaginas;
		} else {
			if (paginaActual <= numElementosPorPagina / 2) { // Rango inicial
				desde = 1;
				hasta = numElementosPorPagina;
			} else if (paginaActual >= totalPaginas - numElementosPorPagina / 2) { // Rango Final
				desde = totalPaginas - numElementosPorPagina + 1;
				hasta = numElementosPorPagina;
			} else { // Rango intermedio
				desde = paginaActual - numElementosPorPagina / 2;
				hasta = numElementosPorPagina;
			}

		}

		for (int i = 0; i < hasta; i++) {	//Agregar items a la vista
			paginas.add(new PageItem(desde + i, paginaActual == desde + i));
		}

	}

	public String getUrl() {
		return url;
	}

	public List<PageItem> getPaginas() {
		return paginas;
	}

	public int getTotalPaginas() {
		return totalPaginas;
	}

	public int getPaginaActual() {
		return paginaActual;
	}
	
	public boolean isFirst() {
		return page.isFirst();
	}

	public boolean isLast()	{
		return page.isLast();
	}
	
	public boolean hasNext() {
		return page.hasNext();
	}
	
	public boolean hasPrevious() {
		return page.hasPrevious()
;	}
}
