import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Jogo extends UnicastRemoteObject implements JogoInterface {
    private static String clientHost = "localhost";

    private final Random random = new Random();

    private static Timer pokeTimer = new Timer();
    private static Timer mainTimer = new Timer();

    private static Map<Integer, String> players = new HashMap<>();
    private static Map<Integer, String> playerLocation = new HashMap<>();


    private static Boolean started = false;

    private static int numberOfPlayers = 3;

    public Jogo() throws RemoteException {
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Jogo <ip servidor> <quantidade de jogadores>");
            System.exit(1);
        }

        try {
            numberOfPlayers = Integer.parseInt(args[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.setProperty("java.rmi.server.hostname", args[0]);
            LocateRegistry.createRegistry(1099);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            System.out.println("java RMI registry already exists.");
        }

        try {
            Naming.rebind("Jogo", new Jogo());
            System.out.println("Server is ready.");
        } catch (Exception e) {
            System.out.println("Server failed: " + e);
        }
        verifyPlayers();
    }

    private static void verifyPlayers() {
        mainTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (players.size() == 0) {
                    started = false;
                }
                if (players.size() >= numberOfPlayers) {
                    if (!started) {
                        System.out.printf("There are %d players online%n", numberOfPlayers);
                        System.out.println("Starting game....");
                        players.forEach((key, value) -> {
                            try {
                                String connectLocation = "//" + value + "/Jogador/" + key;
                                JogadorInterface jogador = (JogadorInterface) Naming.lookup(connectLocation);
                                playerLocation.put(key, connectLocation);
                                Thread thread = new Thread(() -> {
                                    try {
                                        jogador.inicia();
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                    finaliza(key);
                                });
                                thread.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        started = true;
                    }
                } else {
                    System.out.println("Waiting for players");
                }
            }
        }, 0, 700);
    }

    public int registra() {
        try {
            clientHost = getClientHost();
            System.out.println(clientHost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int clientId = random.nextInt(100);
        System.out.printf("Registro do cliente %d!%n", clientId);
        players.put(clientId, clientHost);
        return clientId;
    }

    public int joga(int id) throws RemoteException {
        pokeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!started && players.size() == 0) {
                    try {
                        String connectLocation = playerLocation.get(id);
                        JogadorInterface jogador = (JogadorInterface) Naming.lookup(connectLocation);
                        double bonificacao = Math.random() * 100;
                        System.out.println("Probabilidade de bonificacao gerada:" + bonificacao);
                        if (bonificacao <= 3) {
                            jogador.bonifica();
                        }
                        System.out.println("Jogador id: " + id + " jogou");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new RuntimeException("Stopping task");
                }
            }
        }, 0, 700);
        return id;
    }

    private static int finaliza(int id) {
        String connectLocation = playerLocation.get(id);
        try {
            System.out.printf("Jogador %d foi encerrado!%n", id);
            JogadorInterface jogador = (JogadorInterface) Naming.lookup(connectLocation);
            jogador.finaliza();
        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        players.remove(id);
        return id;
    }
}
