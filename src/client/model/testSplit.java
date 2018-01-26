package client.model;

//import general.Protocol.Client;
import general.Protocol.General;

public class testSplit {

	public static void main(String[] args) {
		
		String msg = "abc dfs er fd ";
		String msg2 = "asj$dskjhdsf$sajsd";
		System.out.print(msg);
		System.out.print(msg.replace(" ", General.DELIMITER1));
		System.out.println(msg2.split("\\" + General.DELIMITER1)[0]);
				
		
	}

}
