import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;

public class Jogo extends UnicastRemoteObject implements JogoInterface {
	private static volatile int result;
	private static volatile boolean changed;
	private static volatile String remoteHostName;

	public Jogo() throws RemoteException {
	}
	
	public static void main(String[] args) throws RemoteException {
		if (args.length != 1) {
			System.out.println("Usage: java Jogo <server ip>");
			System.exit(1);
		}

		try {
			System.setProperty("java.rmi.server.hostname", args[0]);
			LocateRegistry.createRegistry(52369);
			System.out.println("java RMI registry created.");
		} catch (RemoteException e) {
			System.out.println("java RMI registry already exists.");
		}

		try {
			String server = "rmi://" + args[0] + ":52369/Hello";
			Naming.rebind(server, new Jogo());
			System.out.println("Jogo Server is ready.");
		} catch (Exception e) {
			System.out.println("Jogo Serverfailed: " + e);
		}
		
		while (true) {
			if (changed == true) {
				changed = false;

				String connectLocation = "rmi://" + remoteHostName + ":52369/Hello2";

				JogadorInterface hello2 = null;
				try {
					System.out.println("Calling client back at : " + connectLocation);
					hello2 = (JogadorInterface) Naming.lookup(connectLocation);
				} catch (Exception e) {
					System.out.println ("Callback failed: ");
					e.printStackTrace();
				}

				try {
					hello2.Result(result);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {}
		}
	}
	
	public int Add(int a, int b) {
		result = a + b;
		changed = true;
		try {
			remoteHostName = getClientHost();
		} catch (Exception e) {
			System.out.println ("Failed to get client IP");
			e.printStackTrace();
		}
		
		return 1;
	}
}
