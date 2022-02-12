
package connection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Test {

	static Connection conn = null;

	public static void main(String argv[]) {

		try {
			String DB_NAME = "SongKick";
            


			
			
			Scanner input = new Scanner(System.in);
			System.out.println("\t\t\t\t\tBenvenuto nel applicazione SongKick");

			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + DB_NAME ,  Utils.USER ,  Utils.PASS);
			
			System.out.println("SCRIVI 1 PER CANCELLARE UN UTENTE \n"
					+ "SCRIVI 2 PER AGGIORNARE I DATI DI  UN UTENTE \n" 
					+ "SCRIVI 3 PER INSERIRE DATI DI UN UTENTE \n"
			        + "SCRIVI 4 PER RICERCARE CONCERTI PER CITTA  \n");
			
			
			
			String sceltaOpzione = input.nextLine().trim();

			if (sceltaOpzione.equals("1")) {
				deleteUtente();

			} else if (sceltaOpzione.equals("2")) {
	            updateUtente();
				
			} else if (sceltaOpzione.equals("3")) {

				insertUtente();

			} else if (sceltaOpzione.equals("4")) {
				getConcert();
			}
			
			 conn.close();

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

	private static void updateUtente() throws SQLException {
		Statement stmt = null;
		stmt = conn.createStatement();
		System.out.println("\n istanze di Utenti presenti nella base di dati :");
		runQuery(0);
		System.out.println("\n inserisci il cognome che vuoi modificare");
		Scanner input11 = new Scanner(System.in);
		String sceltaOpzione11 = input11.nextLine().trim();
		System.out.println("\n inserisci il nuovo cognome ");
		Scanner input1 = new Scanner(System.in);
		String sceltaOpzione1 = input1.nextLine().trim();
		String update_query = "UPDATE UTENTE SET cognome ='"+ sceltaOpzione1+"' WHERE (nome = 'Mario')";

		stmt.execute(update_query);
		
        System.out.println("\n Nuove Istanze nella Tabella Utente");
        runQuery(0);
		
	}

	private static void getConcert() throws SQLException {
		// TODO Auto-generated method stub
		Statement stmt = null;
		stmt = conn.createStatement();
		ResultSet rs = null;
		System.out.println("inserisci la città  ");
		
		Scanner input1 = new Scanner(System.in);
		String sceltaOpzione1 = input1.nextLine().trim();
		System.out.println("inserisci i giorni   ");
		Scanner input2 = new Scanner(System.in);
		String sceltaOpzione2 = input2.nextLine().trim();
		String researchQuery = "select  l.id , l.nome , i.inizio\n"
				 + "from concertipercitta i,Locale l \n"
				 + "where i.citta= '" + sceltaOpzione1 +"'"
				+  "and i.locale = l.id and cast(inizio as date) > '01-01-2022'\r\n" + 
				"	 and cast(inizio as date) < '01-02-2022' ";
		stmt.execute(researchQuery);
		rs = stmt.getResultSet();

		while (rs.next()) {
			Integer L = rs.getInt("id");
			String N = rs.getString("nome");
			Date ID = rs.getDate("inizio");
			
			
			//Boolean DN = rs.getBoolean("gratis");
			

			System.out.println( "ID locale :" + L + "  Nome locale : "+ N +"  inizio Concerto: " + ID );
			}
		
	}

	private static void insertUtente() throws SQLException {
		Statement stmt = null;
		stmt = conn.createStatement();
		System.out.println("\n istanze di Utenti presenti nella base di dati :");
		runQuery(0);
		System.out.println("\n"+"inserisci il Codice Fiscale, nome ,cognome ");
		Scanner input1 = new Scanner(System.in);
		String sceltaOpzione1 = input1.nextLine().trim();
		String insert_query = "INSERT INTO Utente ( codiceFiscale, cognome,nome,registrazione )\r\n" + 
                                  "VALUES("+sceltaOpzione1+",CURRENT_TIMESTAMP);";
		stmt.execute(insert_query);
		
        System.out.println("\n Nuove Istanze nella Tabella Utente");
        runQuery(0);
		
	}

	public static void runQuery(int whichQuery) {
		Statement stmt = null;
		ResultSet rs = null;
		
		String select_query = "SELECT * FROM Utente";
        
		String insert_query = "INSERT INTO Utente ( codiceFiscale, cognome,nome,registrazione )\r\n" + 
				                           "VALUES('aader32','Rossi',	'Mario','11-6-19 14:25:13');";

		String delete_query = "DELETE FROM Utente WHERE(cognome ='Rossi' )";

		String update_query = "UPDATE UTENTE SET cognome ='RUOSSI' WHERE (nome = 'Mario')";
		
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

		
	
	public static void deleteUtente() throws SQLException {
		Statement stmt = null;
		stmt = conn.createStatement();
		System.out.println("\n istanze di Utenti presenti nella base di dati :");
		runQuery(0);
		System.out.println("\n inserisci il cognome che vuoi cancellare");
		Scanner input1 = new Scanner(System.in);
		String sceltaOpzione1 = input1.nextLine().trim();
		String delete_query = "DELETE FROM Utente WHERE(cognome ='"+ sceltaOpzione1 + "')";
		stmt.execute(delete_query);
		
        System.out.println("\n Nuove Istanze nella Tabella Utente");
        runQuery(0);
		

		
	   }
}
	