import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JogoInterface extends Remote {
	public int registra() throws RemoteException;
}
