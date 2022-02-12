package connection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class graphicInterface {

	static Connection conn = null;

	public static void main(String argv[]) {

		try {
			String DB_NAME = "SongKick";
            


			
			
		
			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + DB_NAME ,  Utils.USER ,  Utils.PASS);
			System.out.println("Connessione al database postgresql...");
			Statement stmt = null;
			stmt = conn.createStatement();
			Scanner input1 = new Scanner(System.in);
			String sceltaOpzione1 = input1.nextLine().trim();
			System.out.println(sceltaOpzione1);
			String delete_query = "DELETE FROM Utente WHERE(cognome ='"+ sceltaOpzione1 + "')";
			stmt.execute(delete_query);
			 conn.close();

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

	public static void runQuery(int whichQuery) {
		Statement stmt = null;
		ResultSet rs = null;
		String select_query = "SELECT * FROM Utente";
        
		String insert_query = "INSERT INTO Utente ( codiceFiscale, cognome,nome,registrazione )\r\n" + 
				                           "VALUES('aader32','Rossi',	'Mario','11-6-19 14:25:13');";

		String delete_query = "DELETE FROM Utente WHERE(cognome ='Rossi' )";

		String update_query = "UPDATE UTENTE SET cognome ='RUOSSI' WHERE (nome = 'NOMEATTORE_11')";
		
		String query = "";
		
		

		switch (whichQuery) {
		case Utils.SELECT_QUERY:
			query = select_query;
			break;
		case Utils.DELETE_QUERY:
			query = delete_query;
			break;
		case Utils.INSERT_QUERY:
			query = insert_query;
			break;
		case Utils.UPDATE_QUERY:
			query = update_query;
			break;
		default:
			query = select_query;
			break;
		}
		try {
			stmt = conn.createStatement();
			if (stmt.execute(query)) {

				if (query.equalsIgnoreCase(select_query)) {

					rs = stmt.getResultSet();

					while (rs.next()) {
						String CD = rs.getString("codiceFiscale"); 
						String CG =  rs.getString("cognome");
						String NO = rs.getString("nome");
						Date ID = rs.getDate("registrazione"); 
						
						
						//Boolean DN = rs.getBoolean("gratis");
						

						System.out.println("codice fiscale :" + CD +  ", cognome: " + CG + ",nome: " + NO  +",registrazione :"+ ID );//+ ",DataN: " + DN );
					}

				}
			}

		} catch (SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
			System.err.println("SQLState: " + ex.getSQLState());
			System.err.println("VendorError: " + ex.getErrorCode());
		} 

		}

		
	

}

