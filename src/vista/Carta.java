package vista;

import java.util.Stack;

import controlador.PrincipalController;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import modelo.Juego;
import modelo.Naipe;
import util.Util;

public class Carta extends ImageView {

	private Image cara;
	private Image reverso;

	private String id;
	private double x;
	private double y;
	private boolean revelada;
	private final int ANCHURA;
	private final int ALTURA;

	private Naipe naipeTmp;
	private Stack<Naipe> pilaTmp;
	private Stack<Naipe> naipesAMover;
	private Stack<Carta> cartasAMover;
	private Juego solitario;

	public Carta(String id, double x, double y, String imagen, boolean revelada, Juego solitario) {

		this.id = id;
		this.x = x;
		this.y = y;
		this.ANCHURA = 107;
		this.ALTURA = 142;
		this.solitario = solitario;
		this.cara = new Image(getClass().getResourceAsStream(imagen));
		this.reverso = new Image(getClass().getResourceAsStream("/vista/img/reverso_rojo.png"));
		this.revelada = revelada;

		// Asignar imagen
		this.setImage(this.revelada ? cara : reverso);

		this.setCursor(Cursor.OPEN_HAND);

		// Asignación de posicionamiento y tamaño
		this.setX(x);
		this.setY(y);
		this.setFitWidth(ANCHURA);
		this.setFitHeight(ALTURA);

		// Asignar eventos de Drag n' Drop
		this.addEventFilter(MouseEvent.MOUSE_PRESSED, this::iniciarMovimiento);
		this.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::moverCarta);
		this.addEventFilter(MouseEvent.MOUSE_RELEASED, this::acabarMovimiento);

	}

	public void revelarCarta() {

		this.setImage(this.cara);
	}

	public void iniciarMovimiento(MouseEvent event) {

		double x = Util.redondearCoordenada(event.getSceneX(), false);
		double y = Util.redondearCoordenada(event.getSceneY(), true);
		int[] vX = Util.TABLEROS_X;
		int indiceInicial = 0;

		// Determinar la pila de origen
		// Si es del mazo
		if (x == Util.MAZO_X && y == Util.FILA_1_Y) {

			this.pilaTmp = solitario.mazo;
		}
		// Si es del resto
		else if (x == Util.RESTO_X && y == Util.FILA_1_Y) {

			this.pilaTmp = solitario.resto;
		}
		// Si es de cualquier otro
		else {

			for (int i = 0; i < vX.length; i++) {
				// Si es de cimientos
				if (y == Util.FILA_1_Y && x == Util.CIMIENTOS_X[i]) {

					this.pilaTmp = solitario.cimientos.get(i);
					break;
				}
				// Si es de tableros
				else if (y == Util.FILA_2_Y && x == Util.TABLEROS_X[i]) {

					this.pilaTmp = solitario.tableros.get(i);
					break;
				}
			}
		}

		try {

			if (!this.pilaTmp.isEmpty()) {
				// Obtener la carta de la cima de la pila
				this.naipeTmp = this.pilaTmp.peek();
				indiceInicial = this.pilaTmp.indexOf(this.naipeTmp);

				if (esSecuenciaValida(this.pilaTmp, indiceInicial)) {
					// Guardar la secuencia de cartas a mover
					this.naipesAMover = new Stack<Naipe>();
					this.cartasAMover = new Stack<Carta>();

					while (this.pilaTmp.size() > indiceInicial) {

						Carta carta;
						for (int i = 0; i < solitario.listaCartas.size(); i++) {

							carta = solitario.listaCartas.get(i);

							if (carta.id.equals(Util.obtenerIdCarta(this.pilaTmp.peek()))) {

								this.cartasAMover.push(carta);
								break;
							}
						}
						this.naipesAMover.push(this.pilaTmp.pop());
						this.naipeTmp = this.naipesAMover.peek();
					}
				}
				else {
					// Si la secuencia no es válida, cancelar el movimiento
					this.naipeTmp = null;
				}
			}

		} catch (NullPointerException e) {
			System.err.println("Cancelando movimiento...");
		}
	}

	public void moverCarta(MouseEvent event) {

		//Volver a pintar la carta mientras se mueve
		Point2D mousePoint   = new Point2D(event.getX(), event.getY());  
		Point2D mousePoint_p = this.localToParent(mousePoint);

		double compensarX = mousePoint_p.getX() - (ANCHURA/2);
		double compensarY = mousePoint_p.getY() - (ALTURA/2);

		// Traer la carta al frente y moverla
		this.setCursor(Cursor.CLOSED_HAND);
		this.toFront();
		this.relocate(compensarX, compensarY);

		Carta carta;
		for (int i = 0; i < this.cartasAMover.size(); i++) {

			carta = this.cartasAMover.get(i);
			carta.toFront();
			carta.relocate(compensarX, compensarY + (i *24));
		}
	}

	private void moverCarta(double x, double y) {

		//Posicionar carta en el nuevo lugar
		this.relocate(x, y);

		//Marcar la nueva posicion
		this.x = x;
		this.y = y;
	}

	public void acabarMovimiento(MouseEvent event) {

		//Redondear coordenadas para cuadrar imagen en casilla
		double x = Util.redondearCoordenada(event.getSceneX(), false);
		double y = Util.redondearCoordenada(event.getSceneY(), true);
		int[] v = (y == 65) ? Util.CIMIENTOS_X : Util.TABLEROS_X;
		final int INCREMENTO_Y = 24;
		boolean jugadaValida = true;
		int tamanyoPila;
		Stack<Naipe> pila;
		Naipe padre;
		Naipe hijo;

		this.setCursor(Cursor.OPEN_HAND);

		// Si es el mazo o el resto
		if ((x == Util.RESTO_X || x == Util.MAZO_X) && y == Util.FILA_1_Y) {

			pila = (x == Util.MAZO_X) ? solitario.mazo : solitario.resto;

			if (x == Util.MAZO_X && y == Util.FILA_1_Y) {

				this.addEventFilter(MouseEvent.MOUSE_CLICKED, (eventoM) -> clicarMazo());
			}

			if (((this.x == Util.RESTO_X || this.x == Util.MAZO_X) && this.y == Util.FILA_1_Y)) {

				pila.push(this.naipeTmp);

				this.x = x;
				this.y = y;

				this.relocate(x, y);
			}
			else {

				this.relocate(this.x, this.y);
			}
		}
		else {

			for (int i = 0; i < v.length; i++) {			
				// Si no es la tercera posición de la primera fila
				if (!(x == 421 && y == 65)) {

					pila = (y == Util.FILA_1_Y) ? solitario.cimientos.get(i) : solitario.tableros.get(i);

					try {
						tamanyoPila = pila.size();

					} catch (NullPointerException e) {
						tamanyoPila = 0;
					}

					// Si la posición X es la misma
					if (x == v[i]) {

						// Obtener la carta padre
						if (tamanyoPila > 0) {
							padre = pila.get(tamanyoPila -1);
						}
						else {
							padre = null;
						}

						// Obtener la carta hija
						hijo = this.naipesAMover.peek();

						if (!this.cartasAMover.isEmpty()) {

							hijo = this.naipesAMover.size() > 1 ? hijo : this.naipesAMover.get(0);
						}

						if (y == Util.FILA_1_Y) {

							jugadaValida = solitario.jugadaValidaCimientos(padre, hijo);
						}
						else {

							jugadaValida = solitario.jugadaValidaTableros(padre, hijo);
						}

						// Comprobar que la jugada es válida
						if (!jugadaValida) {

							while (!this.naipesAMover.isEmpty()) {

								this.pilaTmp.push(this.naipesAMover.pop());
							}

							//Reposicionar la carta en la posición original
							this.relocate(this.x, this.y);

							System.err.println("Jugada Inválida\n");
						}
						else {

							if (y == Util.FILA_2_Y) y += INCREMENTO_Y * tamanyoPila;

							while (!this.naipesAMover.isEmpty()) {

								pila.push(this.naipesAMover.pop());
							}

							moverCarta(x, y);

							if (!this.pilaTmp.isEmpty()) {

								Carta carta;

								for (int j = 0; j < solitario.listaCartas.size(); j++) {

									carta = solitario.listaCartas.get(j);

									if (carta.id.equals(Util.obtenerIdCarta(this.pilaTmp.peek()))) {

										carta.revelarCarta();
										refrescar();
										break;
									}
								}
							}

							if (solitario.partidaTerminada()) {		

								PrincipalController controller = new PrincipalController();

								controller.partidaGanada();
							}

							System.out.println("Jugada válida\n");
						}
					}
				}
				else {
					//Reposicionar la carta en la posición original
					while (!this.naipesAMover.isEmpty()) {

						this.pilaTmp.push(this.naipesAMover.pop());
					}

					this.relocate(this.x, this.y);
				}
			}
		}
	}

	private void refrescar() {

		for (Carta carta : solitario.listaCartas) {

			carta.setVisible(false);
			carta.setVisible(true);

			carta.relocate(carta.x, carta.y);
		}
	}

	private boolean esSecuenciaValida(Stack<Naipe> pila, int indiceInicial) {

		for (int i = indiceInicial; i < pila.size() -1; i++) {

			if (pila.get(i).getValor().ordinal() +1 != pila.get(i +1).getValor().ordinal() ||
					pila.get(i).obtenerColor() == pila.get(i +1).obtenerColor()) {

				return false;
			}
		}
		return true;
	}

	private void clicarMazo() {

		Naipe naipe;

		if (solitario.mazo.isEmpty()) {

			while (!solitario.resto.isEmpty()) {

				naipe = solitario.resto.pop();
				solitario.mazo.push(naipe);
				moverCartaEntreMazoResto(naipe, Util.MAZO_X);
			}
		}
		else {

			naipe = solitario.mazo.pop();
			solitario.resto.push(naipe);
			moverCartaEntreMazoResto(naipe, Util.RESTO_X);
		}

		refrescar();
	}

	private void moverCartaEntreMazoResto(Naipe naipe, double x) {

		for (Carta carta : solitario.listaCartas) {

			if (carta.id.equals(Util.obtenerIdCarta(naipe))) {

				carta.moverCarta(x, Util.FILA_1_Y);
				carta.toFront();
				break;
			}
		}
	}
}
