import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JogadorInterface extends Remote {
	public int Result(int val) throws RemoteException;
}
