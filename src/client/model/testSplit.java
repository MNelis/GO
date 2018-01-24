package client.model;

//import general.Protocol.Client;
import general.Protocol.General;

public class testSplit {

	public static void main(String[] args) {
		// String initialMessage =
		// Client.NAME + General.DELIMITER1 + "name" + General.DELIMITER1
		// + "VERSION" + General.DELIMITER1 + Client.VERSION + General.DELIMITER1 +
		// Client.EXTENSIONS + General.DELIMITER1 + "0$0$0$0$0$0$0" +
		// General.COMMAND_END;
		// System.out.println(initialMessage);
		// String[] split = initialMessage.split("\\" +
		// String.valueOf(General.DELIMITER1));
		// System.out.println(String.valueOf(General.DELIMITER1));
		// System.out.println("'hkjhd'");
		
		String msg = "abc dfs er fd ";
		String msg2 = "asj$dskjhdsf$sajsd";
		System.out.println(General.DELIMITER1);
		System.out.println(msg.replace(" ", General.DELIMITER1));
		System.out.println(msg2.split("\\" + General.DELIMITER1)[0]);
		
		
		

	}

}
