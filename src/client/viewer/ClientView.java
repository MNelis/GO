package client.viewer;

import java.util.Observer;

public interface ClientView extends Observer {
	public void start();
	public void print(String msg);
	public void error(String msg);
	public String readString(String tekst);
}
