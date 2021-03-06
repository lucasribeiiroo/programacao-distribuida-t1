import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class Jogador extends UnicastRemoteObject implements JogadorInterface {
    private static boolean quit = false;

    private static int playerId;

    private static JogoInterface jogo = null;

    private static boolean changed = false;

    public Jogador() throws RemoteException {
    }

    public static void main(String[] args) {
        playerId = 0;

        if (args.length != 2) {
            System.out.println("Usage: java Jogador <ip local> <ip servidor>");
            System.exit(1);
        }

        try {
            System.setProperty("java.rmi.server.hostname", args[0]);
            LocateRegistry.createRegistry(1099);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            System.out.println("java RMI registry already exists.");
        }

        try {
            Naming.rebind("Jogador", new Jogador());
            System.out.println("Jogador Server is ready.");
        } catch (Exception e) {
            System.out.println("Jogador Serverfailed: " + e);
        }

        String remoteHostName = args[1];
        String connectLocation = "//" + remoteHostName + "/Jogo";

        try {
            System.out.println("Connecting to server at : " + connectLocation);
            jogo = (JogoInterface) Naming.lookup(connectLocation);
        } catch (Exception e) {
            System.out.println("Jogador failed: ");
            e.printStackTrace();
        }

        // registra
        try {
            playerId = jogo.registra();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Naming.rebind("Jogador/" + playerId, new Jogador());
            System.out.println("Jogador address was rebinded to Jogador/" + playerId);
        } catch (Exception e) {
            System.out.println("Jogador Serverfailed: " + e);
        }
    }

    @Override
    public void inicia() {
        try {
            for (int i = 0; i < 50; i++) {
                if (quit) {
                    break;
                }
                double desistencia = Math.random() * 100;
                if (desistencia <= 1) {
                    jogo.desiste(playerId);
                } else {
                    System.out.printf("Jogada numero %d%n", i + 1);
                    jogo.joga(playerId);
                }
                Random random = new Random();
                int time = random.nextInt(701) + 250;
                Thread.sleep(time);
            }
            finaliza();
        } catch (InterruptedException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public void bonifica() throws RemoteException {
        System.out.println(new StringBuilder()
                .append("Jogador ")
                .append(playerId)
                .append(" Bonificado"));
    }

    public void finaliza() throws RemoteException {
        System.out.println("Jogador " + playerId + " finalizou suas jogadas");
        quit = true;
        jogo.finaliza(playerId);
    }

    public void verifica() throws RemoteException {
        if (quit)
            System.out.println("Jogador esta inativo");
        else
            System.out.println("Jogador nao esta inativo");
    }
}
