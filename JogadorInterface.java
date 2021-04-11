import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JogadorInterface extends Remote {
    void inicia() throws RemoteException;
    void bonifica() throws RemoteException;

}
