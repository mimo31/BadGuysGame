package game.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IOBase {
	public static final Version version = new Version();
	public final static String rootDirectory = System.getProperty("user.dir") + "\\BadGuysGame";
	public final static String resourcesDirectory = rootDirectory + "\\Resources";
	public final static String serverRootDirectory = "http://178.248.252.60/~xfukv01/BGG";
	private final static byte[] serverIP = new byte[] { (byte) 178, (byte) 248, (byte) 252, 60 };

	public static boolean isServerReachable() throws UnknownHostException, IOException {
		return InetAddress.getByAddress(serverIP).isReachable(5000);
	}
}
