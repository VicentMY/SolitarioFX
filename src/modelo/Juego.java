package modelo;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import vista.Carta;

public class Juego {

	private Random random;
	public Stack<Naipe> mazo;
	public Stack<Naipe> resto;
	public ArrayList<Stack<Naipe>> tableros;
	public ArrayList<Stack<Naipe>> cimientos;

	public Stack<Carta> listaCartas;

	/** Crea la instancia del juego. */
	public Juego() {

		this.random = new Random();
		this.mazo = new Stack<Naipe>();
		this.resto = new Stack<Naipe>();
		this.tableros = new ArrayList<Stack<Naipe>>();
		this.cimientos = new ArrayList<Stack<Naipe>>();
		this.listaCartas = new Stack<Carta>();
		llenarPackNaipes();
		barajarPackNaipes();
		ponerTableros();
		ponerCimientos();
	}

	/** Comprueba si la jugada es válida para los tableros.<br>
	 * <ul>
	 * <li>Si el valor es 'REY' y no hay padre devuelve <b>verdadero</b>. Por el contrario, devuelve <b>falso</b><br></li>
	 * <li>Si el color es el mismo que el padre devuelve <b>falso</b>.<br></li>
	 * <li>Si el valor numérico del hijo +1 es distinto a el valor del padre devuelve <b>falso</b>.<br></li>
	 * <li>Si no se cumple ninguna de las condiciones anteriores devuelve <b>verdadero</b>.</li>
	 * </ul>
	 * @param padre Posición donde se quiere colocar el naipe.
	 * @param hijo El naipe en sí.*/
	public boolean jugadaValidaTableros(Naipe padre, Naipe hijo) {

		if (padre == null) {
			// Si el valor es 'REY' y no hay padre es válido, si no no lo es
			return hijo.getValor() == Valores.REY;
		}
		if (padre.obtenerColor() == hijo.obtenerColor()) {
			// Si el color es el mismo que el padre no es válido
			return false;
		}
		if (padre.getValor().ordinal() != hijo.getValor().ordinal() +1) {
			// Si el valor (número) del hijo +1 es distinto a el valor del padre no és válido
			return false;
		}
		// Si no cumple ninguna condición la jugada es válida
		return true;
	}

	/** Comprueba si la jugada es válida para los cimientos.<br>
	 * <ul>
	 * <li>Si el valor es 'AS' y no hay padre devuelve <b>verdadero</b>. Por el contrario, devuelve <b>falso</b><br></li>
	 * <li>Si el palo no es el mismo que el padre devuelve <b>falso</b>.<br></li>
	 * <li>Si el valor numérico del hijo -1 es distinto a el valor del padre devuelve <b>falso</b>.<br></li>
	 * <li>Si no se cumple ninguna de las condiciones anteriores devuelve <b>verdadero</b>.</li>
	 * </ul>
	 * @param padre Posición donde se quiere colocar el naipe.
	 * @param hijo El naipe en sí.*/
	public boolean jugadaValidaCimientos(Naipe padre, Naipe hijo) {

		if (padre == null) {

			return hijo.getValor() == Valores.AS;
		}
		if (padre.getPalo() != hijo.getPalo()) {

			return false;
		}
		if (padre.getValor().ordinal() != hijo.getValor().ordinal() -1) {

			return false;
		}	
		return true;
	}

	/** LLena el pack de naipes con todos los 52 naipes (13 naipes x 4 palos) que se usan en solitario */
	private void llenarPackNaipes() {

		mazo.clear();
		for (Palo palo : Palo.values()) {
			for (Valores valor : Valores.values()) {
				mazo.push(new Naipe(palo, valor));
			}
		}
	}

	/** Baraja el orden del pack de naipes. */
	private void barajarPackNaipes() {

		final int numNaipes = 52;

		for (int i = 0; i < numNaipes; i++) {

			intercambiarNaipe(random.nextInt(numNaipes -i), (numNaipes -1) - i);
		}
	}

	/** Intercambia naipes de posición.
	 * @param pos1 Naipe a cambiar
	 * @param pos2 Nueva posición del naipe*/
	private void intercambiarNaipe(int pos1, int pos2) {

		Naipe tmp = mazo.get(pos1);
		mazo.set(pos1, mazo.get(pos2));
		mazo.set(pos2, tmp);
	}

	/** Pone los naipes en los tableros. */
	private void ponerTableros() {

		Stack<Naipe> pila;
		final int numTableros = 7;

		tableros.clear();
		for (int i = 0; i < numTableros; i++) {

			pila = new Stack<Naipe>();
			for (int j = 0; j < i +1; j++) {

				pila.push(mazo.pop());
			}
			tableros.add(pila);
		}
	}

	/** Pone los naipes en los cimientos. */
	private void ponerCimientos() {

		Stack<Naipe> pila;
		final int numCimientos = 4;

		cimientos.clear();
		for (int i = 0; i < numCimientos; i++) {

			pila = new Stack<Naipe>();
			cimientos.add(pila);
		}
	}

	/** Comprueba si la partida ha terminado según el estado de los cimientos. */
	public boolean partidaTerminada() {

		for (Stack<Naipe> pila : cimientos) {

			if (pila.size() != 13) {
				return false;
			}
		}
		return true;
	}

	//	/** Saca un naipe del mazo y la pone en la pila de resto. */
	//	public void voltearMazo() {
	//
	//		resto.push(mazo.pop());
	//	}
	//
	//	/** Saca todos los naipes de la pila de resto y los devuelve al mazo. */
	//	public void reiniciarMazo() {
	//
	//		int tamanyo = resto.size();
	//		for (int i = 0; i < tamanyo; i++) {
	//
	//			mazo.push(resto.pop());
	//		}
	//	}
}
