package general;


public class Protocol {
	/**
	 * @author Rosalyn.Sleurink
	 * @version 6
	 */
	
	/**
	 * Aanpassing versie 1 -> 2:
	 * Bij START worden de namen van de spelers niet meegegeven, dit is wel handig om te doen.
	 * 
	 * Aanpassing versie 2 -> 3:
	 * - Version verdeeld in VERSION (String) en VERSIONNO (int)
	 * - Constantes BLACK en WHITE toegevoegd
	 * 
	 * Aanpassing versie 3 -> 4:
	 * - Beide delimiters een String gemaakt en $ is //$ geworden.
	 * 
	 * Aanpassing versie 4 -> 5:
	 * - Delimiter weer terugveranderd naar $.
	 * - Aan TURN zijn String FIRST en PASS toegevoegd
	 * - Tweede voorbeeld bij START is aangepast naar het format.
	 * 
	 * Aanpassing versie 5 -> 6:
	 * - EXIT commando toegevoegd
	 * - Afspraak gemaakt dat bord grootte kan hebben van 5 t/m 19.
	 */
	
	/**
	 * OVERAL WAAR SPATIES STAAN KOMT DUS DELIMITER1 (in de voorbeelden en formats).
	 * OOK MOETEN ALLE COMMANDO'S EINDIGEN MET COMMAND_END.
	 */
	public static final int VERSION_NO = 6;
	
	public static class Client {
		/**
		 * Het eerste commando wat de client naar de server stuurt. Gaat om versie
		 * van het protocol. De volgorde van de extensions is als volgt: 
		 * chat challenge leaderboard security 2+ simultaneous multiplemoves.<br>
		 * Format: NAME clientnaam VERSION versienummer EXTENSIONS boolean boolean boolean etc<br>
		 * Voorbeeld: NAME piet VERSION 2 EXTENSIONS 0 0 1 1 0 0 0
		 */
		public static final String NAME = "NAME";
		public static final String VERSION = "VERSION";
		public static final int VERSIONNO = VERSION_NO;
		public static final String EXTENSIONS = "EXTENSIONS";
		
		/**
		 * Om een move te communiceren. Bord begint linksboven met 0,0.<br>
		 * Format: MOVE rij_kolom of MOVE PASS<br>
		 * Voorbeeld: MOVE 1_3
		 */
		public static final String MOVE = "MOVE";
		public static final String PASS = "PASS";
		
		/**
		 * Als de server een START met aantal spelers heeft gestuurd mag je je voorkeur doorgeven 
		 * voor kleur en grootte van het bord. Dit wordt gevraagd aan de speler die er als eerst 
		 * was. Grootte van het bord mag van 5 t/m 19 zijn.<br>
		 * Format: SETTINGS kleur bordgrootte<br>
		 * Voorbeeld: SETTINGS BLACK 19
		 */
		public static final String SETTINGS = "SETTINGS";
		
		/**
		 * Als je midden in een spel zit en wil stoppen. Je krijgt dan 0 punten.
		 * Wordt niet gestuurd als client abrupt. 
		 * afgesloten wordt. Als je dit stuurt nadat je een REQUESTGAME hebt gedaan gebeurt er
		 * niks.<br>
		 * Format: QUIT<br>
		 * Voorbeeld: QUIT
		 */
		public static final String QUIT = "QUIT";
		
		/**
		 * Kan gestuurd worden als je in een spel zit of in de lobby, betekent dat de Client 
		 * helemaal weg gaat.<br>
		 * Format: EXIT<br>
		 * Voorbeeld: QUIT
		 */
		public static final String EXIT = "EXIT";
		
		/**
		 * Sturen als je een spel wilt spelen. De eerste keer en als een spel afgelopen is opnieuw.
		 * Als je de Challenge extensie niet ondersteunt, stuur dan RANDOM in plaats van een naam.
		 * <br>
		 * Format: REQUESTGAME aantalspelers naamtegenspeler (RANDOM als je geen challenge doet)<br>
		 * Voorbeeld: REQUESTGAME 2 RANDOM of REQUESTGAME 2 piet
		 */
		public static final String REQUESTGAME = "REQUESTGAME";
		public static final String RANDOM = "RANDOM";
		
		
		// -------------- EXTENSIES ------------ //
		
		/**
		 * Als je de uitdaging wil accepteren.<br>
		 * Format: ACCEPTGAME naamuitdager<br>
		 * Voorbeeld: ACCEPTGAME piet
		 */
		public static final String ACCEPTGAME = "ACCEPTGAME";
		
		/**
		 * Als je de uitdaging niet accepteert.<br>
		 * Format: DECLINEGAME naamuitdager<br>
		 * Voorbeeld: DECLINEGAME piet
		 */
		public static final String DECLINEGAME = "DECLINEGAME";
		
		/**
		  * Om op te vragen wie je allemaal kan uitdagen.<br>
		 * Format: LOBBY<br>
		 * Voorbeeld: LOBBY
		 */
		public static final String LOBBY = "LOBBY";
		
		/**
		 * Om een chatbericht te sturen. Als je in een spel zit mogen alleen de spelers het zien. 
		 * Als je in de lobby zit mag iedereen in de lobby het zien.<br>
		 * Format: CHAT bericht<br>
		 * Voorbeeld: CHAT hoi ik ben piet
		 */
		public static final String CHAT = "CHAT";
		
		/**
		 * Om de leaderboard op te vragen. Overige queries moet je afspreken met anderen die ook 
		 * leaderboard willen implementeren.<br>
		 * Format: LEADERBOARD<br>
		 * Voorbeeld: LEADERBOARD
		 */
		public static final String LEADERBOARD = "LEADERBOARD";
	}

	public static class Server {
		/**
		 * Het eerste commando wat de server naar de client stuurt. Gaat om versie
		 * van het protocol. De volgorde van de extensions is als volgt: 
		 * chat challenge leaderboard security 2+ simultaneous multiplemoves.<br>
		 * Format: NAME clientnaam VERSION versienummer EXTENSIONS boolean boolean boolean etc<br>
		 * Voorbeeld: NAME serverpiet VERSION 2 EXTENSIONS 0 0 1 1 0 0 0
		 */
		public static final String NAME = "NAME";
		public static final String VERSION = "VERSION";
		public static final int VERSIONNO = VERSION_NO;
		public static final String EXTENSIONS = "EXTENSIONS";
		
		/**
		 * Een spel starten. Dit stuur je naar de eerste speler. <br>
		 * Format: START aantalspelers (naar speler 1)<br>
		 * Format: START aantalspelers kleur bordgrootte speler1 speler2 (3, etc..) 
		 * (naar alle spelers) Bordgrootte kan waarde hebben van 5 t/m 19.<br>
		 * Voorbeeld: START 2 of START 2 BLACK 19 jan piet
		 */
		public static final String START = "START";
		
		/**
		 * Vertelt aan de spelers welke beurt er gedaan is. Speler1 is de speler die de beurt heeft
		 * gedaan, speler 2 de speler die nu aan de beurt is om een MOVE door te geven. Als dit de
		 * eerste beurt is zijn speler1 en speler2 allebei de speler die nu aan de beurt is, en dan
		 * stuur je FIRST i.p.v. de integers. Als de speler past geeft je PASS door ip.v. de 
		 * integers.<br>
		 * Format: TURN speler1 rij_kolom speler2<br>
		 * Voorbeeld: TURN piet 1_3 jan of TURN piet FIRST piet
		 */
		public static final String TURN = "TURN";
		public static final String FIRST = "FIRST";
		public static final String PASS = "PASS";
		
		/**
		 * Als het spel klaar is om welke reden dan ook. Reden kan zijn FINISHED (normaal einde), 
		 * ABORTED (abrupt einde) of TIMEOUT (geen respons binnen redelijke tijd)<br>
		 * Format: ENDGAME reden winspeler score verliesspeler score<br>
		 * Voorbeeld: ENDGAME FINISHED piet 12 jan 10
		 */
		public static final String ENDGAME = "ENDGAME";
		public static final String FINISHED = "FINISHED";
		public static final String ABORTED = "ABORTED";
		public static final String TIMEOUT = "TIMEOUT";
		
		/**
		 * Errortypes die we gedefinieerd hebben: UNKNOWNCOMMAND, INVALIDMOVE, NAMETAKEN, 
		 * INCOMPATIBLEPROTOCOL, OTHER.<br>
		 * Format: ERROR type bericht<br>
		 * Voorbeeld: ERROR NAMETAKEN de naam piet is al bezet
		 */
		public static final String ERROR = "ERROR";
		public static final String UNKNOWN = "UNKNOWNCOMMAND";
		public static final String INVALID = "INVALIDMOVE";
		public static final String NAMETAKEN = "NAMETAKEN";
		public static final String INCOMPATIBLEPROTOCOL = "INCOMPATIBLEPROTOCOL";
		public static final String OTHER = "OTHER";
		
		// -------------- EXTENSIES ------------ //

		/**
		 * Stuurt aan één client wie hem heeft uitgedaagd.<br>
		 * Format: REQUESTGAME uitdager<br>
		 * Voorbeeld: REQUESTGAME piet
		 */
		public static final String REQUESTGAME = "REQUESTGAME";
		
		/**
		 * Stuurt aan de uitdager dat de uitdaging is geweigerd en door wie.<br>
		 * Format: DECLINED uitgedaagde<br>
		 * Voorbeeld: DECLINED piet
		 */
		public static final String DECLINED = "DECLINED";
		
		/**
		 * Reactie op LOBBY van de client. Stuurt alle spelers die uitgedaagd kunnen worden 
		 * (in de lobby zitten).<br>
		 * Format: LOBBY naam1_naam2_naam3<br>
		 * Voorbeeld: LOBBY piet jan koos
		 */
		public static final String LOBBY = "LOBBY";

		/**
		 * Stuurt chatbericht naar relevante clients (in spel of in lobby).<br>
		 * Format: CHAT naam bericht<br>
		 * Voorbeeld: CHAT piet hallo ik ben piet (Met correcte delimiter ziet dat er dus uit als:
		 * CHAT$piet$hallo ik ben piet)
		 */
		public static final String CHAT = "CHAT";
				
		/**
		 * Reactie op LEADERBOARD van client. Stuurt de beste 10 scores naar één client.
		 * Overige queries moet je afspreken met anderen die ook 
		 * leaderboard willen implementeren.<br>
		 * Format: LEADERBOARD naam1 score1 naam2 score2 naam3 score3 enz<br>
		 * Voorbeeld: LEADERBOARD piet 1834897 jan 2 koos 1
		 */
		public static final String LEADERBOARD = "LEADERBOARD";
	}

	public static class General {
		
		/**
		 * ENCODING kun je ergens bij je printstream/bufferedreader/writer instellen (zie API).
		 */
		public static final String ENCODING = "UTF-8";
		public static final int TIMEOUTSECONDS = 90;
		public static final short DEFAULT_PORT = 5647;
		public static final String DELIMITER1 = "$";
		public static final String DELIMITER2 = "_";
		public static final String COMMAND_END = "\n";
		public static final String BLACK = "BLACK";
		public static final String WHITE = "WHITE";
	}

}
