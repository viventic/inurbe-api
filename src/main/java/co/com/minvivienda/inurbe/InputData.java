package co.com.minvivienda.inurbe;


public class InputData {
	
	private Long identificacion;
	
	private String nombreSolicitante;
	
	private Long expediente;
	
	private String matricula;
	
	private Long pagina;
	
	private Long filas;
	
	private String ordenarPor;
	
	private String orden;

	
	public Long getIdentificacion() {
		return identificacion;
	}

	public void setIdentificacion(Long identificacion) {
		this.identificacion = identificacion;
	}

	public String getNombreSolicitante() {
		return nombreSolicitante;
	}

	public void setNombreSolicitante(String nombreSolicitante) {
		this.nombreSolicitante = nombreSolicitante;
	}

	public Long getExpediente() {
		return expediente;
	}

	public void setExpediente(Long expediente) {
		this.expediente = expediente;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public Long getPagina() {
		return pagina;
	}

	public void setPagina(Long pagina) {
		this.pagina = pagina;
	}

	public Long getFilas() {
		return filas;
	}

	public void setFilas(Long filas) {
		this.filas = filas;
	}

	public String getOrdenarPor() {
		return ordenarPor;
	}

	public void setOrdenarPor(String ordenarPor) {
		this.ordenarPor = ordenarPor;
	}

	public String getOrden() {
		return orden;
	}

	public void setOrden(String orden) {
		this.orden = orden;
	}

}
