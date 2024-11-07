package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import excepciones.UsuarioContrasenyaException;

public class BaseDeDatos {

	final String jdbcUrl;
	final String username;
	final String password;

	public Connection conexion;
	private Statement statement;
	private ResultSet resultSet;

	public BaseDeDatos() {
		/* Conectarse a db en navegador: {ip_servidor}/phpPgAdmin/
		 * En el fichero hosts de Windows (c:\Windows\System32\drivers\etc\hosts) 'postgresql' es el alias de la IP de la base de datos
		 * */
		// MI BASE DE DATOS OPENSUSE
		// this.jdbcUrl = "jdbc:postgresql://postgresql:5432/solitaire_db";
		// this.username = "vicent";
		// this.password = "vicent";
		
		// BASE DE DATOS HOSTING GRATUITO
		// PANEL DE CONTROL: https://console.clever-cloud.com/users/me/addons/addon_841d93e6-eaaa-49ba-9652-71b853080c34
		this.jdbcUrl = "jdbc:postgresql://bauiegf0ledz1xarmvlt-postgresql.services.clever-cloud.com:50013/bauiegf0ledz1xarmvlt";
		this.username = "uqd6hedakyriuf3mtbc4";
		this.password = "dPTioHLKwYWLhvO9NhRmYLs22nR0Y7";
	}

	// Consultas
	
	public boolean usuarioExiste(String usuario) {
		
		boolean resultado = false;
		
		try {

			Class.forName("org.postgresql.Driver");

			// Establecer la conexión
			this.conexion = DriverManager.getConnection(this.jdbcUrl, this.username, this.password);

			// Ejecutar una consulta
			this.statement = conexion.createStatement();

			this.resultSet = this.statement.executeQuery(
					  "SELECT COUNT(*) FROM (\r\n"
					+ "  SELECT user_id FROM solitaire.luser\r\n"
					+ "  WHERE username = '"+usuario+"'\r\n"
					+ ") AS users");

			// Procesar los resultados
			while (resultSet.next()) {

				int columnValue = resultSet.getInt("count");
				System.out.println(columnValue);

				if (columnValue > 0) resultado = true;
			}
			// Cerrar la conexión
			this.conexion.close();

		} catch (SQLException e) {
			Util.alertaBD();
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
		}
		
		return resultado;
	}

	public boolean consultarUsuarioContraseña(String usuario, String contrasenya) throws UsuarioContrasenyaException {
		
		boolean exito = true;
		try {
			Class.forName("org.postgresql.Driver");

			// Establecer la conexión
			this.conexion = DriverManager.getConnection(this.jdbcUrl, this.username, this.password);

			// Ejecutar una consulta
			this.statement = conexion.createStatement();

			this.resultSet = this.statement.executeQuery(
					  "SELECT CASE\r\n"
					+ "    WHEN (SELECT COUNT(*) FROM solitaire.LUSER WHERE username = '"+usuario+"') = 0 THEN 'Usuario no encontrado'\r\n"
					+ "    ELSE CASE\r\n"
					+ "        WHEN u_password = '"+contrasenya+"' THEN 'Acceso concedido'\r\n"
					+ "        ELSE 'Contraseña incorrecta'\r\n"
					+ "    END\r\n"
					+ "END AS resultado\r\n"
					+ "FROM (\r\n"
					+ "    SELECT username, u_password FROM solitaire.luser\r\n"
					+ "    WHERE username = '"+usuario+"') AS luser");

			if (!resultSet.next()) {
				throw new UsuarioContrasenyaException();
			}
			
			// Procesar los resultados
			while (resultSet.next()) {

				String columnValue = resultSet.getString("resultado");
				System.out.println(columnValue);

				if (columnValue.equals("Usuario no encontrado") || columnValue.equals("Contraseña incorrecta")) {
					
					throw new UsuarioContrasenyaException();
				}
			}
			// Cerrar la conexión
			this.conexion.close();

		} catch (SQLException e) {
			Util.alertaBD();
			exito = false;
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
		}
		return exito;
	}

	public boolean crearUsuario(String usuario, String contrasenya, String nombreCompleo) {

		boolean exito = true;
		try {
			Class.forName("org.postgresql.Driver");

			// Establecer la conexión
			this.conexion = DriverManager.getConnection(this.jdbcUrl, this.username, this.password);

			// Ejecutar una consulta
			this.statement = conexion.createStatement();
			
			statement.execute(
					"INSERT INTO solitaire.luser (username, u_password, full_name, email, global_score, last_login_date, registration_date)\r\n"
							+ "VALUES ('"+usuario+"', '"+contrasenya+"', '"+nombreCompleo+"', NULL, 0, NULL, CURRENT_DATE);");
			
			// Cerrar la conexión
			this.conexion.close();
			return false;

		} catch (SQLException e) {
			Util.alertaBD();
			exito = false;
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
		}
		return exito;
	}
}
