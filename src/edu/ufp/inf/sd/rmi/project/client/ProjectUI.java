package edu.ufp.inf.sd.rmi.project.client;

import edu.ufp.inf.sd.rmi.project.server.gamefactory.GameFactoryRI;
import edu.ufp.inf.sd.rmi.project.server.gamesession.GameSessionImpl;
import edu.ufp.inf.sd.rmi.project.server.gamesession.GameSessionRI;
import edu.ufp.inf.sd.rmi.project.server.lobby.LobbyMapEnum;
import edu.ufp.inf.sd.rmi.project.server.lobby.LobbyRI;
import edu.ufp.inf.sd.rmi.project.variables.User;
import engine.Game;

import java.rmi.RemoteException;
import java.util.Observer;
import java.util.Scanner;

public class ProjectUI {

    private final GameFactoryRI stub;
    private GameSessionRI session;
    private ObserverImpl observer;
    private LobbyRI lobbyRI;

    public ProjectUI(GameFactoryRI GameFactoryRI) {
        this.stub = GameFactoryRI;
        this.start();
    }

    private void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter 'login' or 'register': ");
        String action = scanner.nextLine();
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        switch (action) {
            case "login":
                try {
                    this.session = this.stub.login(username, password);
                    System.out.println("Logged in Successfully!");
                    lobbyMenu(this.session, username);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case "register":
                try {
                    this.session = this.stub.register(username, password);
                    System.out.println("Registered Successfully");
                    lobbyMenu(this.session, username);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            default:
                System.out.println("Invalid Action");
                break;
        }
    }

    private void lobbyMenu(GameSessionRI session, String username) {
        Scanner input = new Scanner(System.in);

        System.out.println("Welcome to the Lobby Menu!");
        System.out.println("Please choose an option:");
        System.out.println("1. Created Lobbies");
        System.out.println("2. Create New Lobby");
        int choice = input.nextInt();
        switch (choice) {
            case 1:
                System.out.println("You have chosen Created Lobbies.");
                System.out.println("Please choose a lobby to join by ID:");
                try {
                    System.out.print(this.session.lobbyList());
                    int chosenLobbyId = input.nextInt();
                    System.out.println("Joining lobby " + chosenLobbyId);
                    int joinResult = this.session.joinLobby(chosenLobbyId, session);
                    switch (joinResult) {
                        case 1:
                            System.out.println("Invalid lobby ID. Please try again.");
                            break;
                        case 2:
                            System.out.println("Match ongoing, you can't join.");
                            break;
                        case 0:
                            LobbyRI lobby = this.session.getLobby(chosenLobbyId);
                            this.observer = new ObserverImpl(lobby, username);
                            this.startGame(this.session, this.observer);
                            break;
                    }
                } catch (RemoteException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            case 2:
                System.out.println("You have chosen Create New Lobby.");
                System.out.println("How many players? (Enter 2 or 4)");
                int numPlayers = input.nextInt();
                if (numPlayers == 2 || numPlayers == 4) {
                    switch (numPlayers) {
                        case 4:
                            System.out.println("You have chosen FourCorners map.");
                            try {
                                int index = this.session.createLobby(LobbyMapEnum.FourCorners, this.session);
                                LobbyRI lobby = this.session.getLobby(index);
                                this.observer = new ObserverImpl(lobby, username);
                                this.startGame(this.session, this.observer);

                                break;
                            } catch (RemoteException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        case 2:
                            System.out.println("You have chosen SmallVs map.");
                            try {
                                int index = this.session.createLobby(LobbyMapEnum.SmallVs, this.session);
                                LobbyRI lobby = this.session.getLobby(index);
                                this.observer = new ObserverImpl(lobby, username);
                                this.startGame(this.session, this.observer);
                                break;
                            } catch (RemoteException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        default:
                            System.out.println("Invalid choice. Please choose again.");
                            break;
                    }
                }
                System.out.println("Invalid choice. Please choose again.");
                break;
            default:
                System.out.println("Invalid choice. Please choose again.");
                break;
        }
    }

    private void startGame(GameSessionRI session, ObserverImpl observer) throws RemoteException, InterruptedException {
        new Game(this.stub, session, observer);
    }
}