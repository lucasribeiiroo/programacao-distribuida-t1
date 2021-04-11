import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JogoInterface extends Remote {
    int registra() throws RemoteException;

    int joga(int id) throws RemoteException;

    int finaliza(int id) throws RemoteException;
    
    int desiste(int id) throws RemoteException;
}
