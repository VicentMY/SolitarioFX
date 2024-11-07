package modelo;

public class Naipe {

	private final Palo palo;
	private final Valores valor;
	private boolean revelada;
	private boolean seleccionada;

	public Naipe(Palo palo, Valores valor) {

		this.palo = palo;
		this.valor = valor;
		this.revelada = false;
		this.seleccionada = false;
	}

	public Palo getPalo() {
		return this.palo;
	}

	public Valores getValor() {
		return this.valor;
	}

	public boolean estaRevelada() {
		return this.revelada;
	}

	public boolean estaSeleccionada() {
		return this.seleccionada;
	}

	public void revelar() {
		this.revelada = true;
	}

	public void alternarSeleccionada() {
		this.seleccionada = !this.seleccionada;
	}

	public Colores obtenerColor() {

		if (this.palo == Palo.CORAZONES || this.palo == Palo.DIAMANTES) {

			return Colores.ROJO;
		}
		else {

			return Colores.NEGRO;
		}
	}

	@Override
	public String toString() {

		return this.valor +" "+ this.palo;
	}


}
