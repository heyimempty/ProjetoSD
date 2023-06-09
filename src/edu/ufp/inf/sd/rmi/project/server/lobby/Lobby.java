package edu.ufp.inf.sd.rmi.project.server.lobby;

import edu.ufp.inf.sd.rmi.project.client.ObserverRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class Lobby extends UnicastRemoteObject implements LobbyRI {


    private State state;
    private State gameState;
    private final UUID id;
    private List<ObserverRI> observers = Collections.synchronizedList(new ArrayList<>());
    private final LobbyMapEnum map;
    private LobbyStatusEnum lobbyStatus;
    private String owner;


    public Lobby(LobbyMapEnum map, String owner) throws RemoteException {
        super();
        this.id = UUID.randomUUID();
        this.lobbyStatus = LobbyStatusEnum.PAUSED;
        this.map = map;
        this.owner = owner;
    }

    public LobbyMapEnum getMap() {
        return map;
    }

    public LobbyStatusEnum getLobbyStatus() {
        return lobbyStatus;
    }

    public void setLobbyStatus(LobbyStatusEnum lobbyStatus) throws RemoteException {
        this.lobbyStatus = lobbyStatus;
        notifyAllObservers();
    }

    public State getState() throws RemoteException {
        return state;
    }

    public void setState(State state, int id) throws RemoteException {
        if (this.state == null) {
            this.state = state;
        } else {
            if (this.state.getUsername() != null) {
                this.state.getId().set(id, state.getId().get(id));
                this.state.getCharacter()[id] = state.character[id];
                this.state.getUsername().set(id, state.getUsername().get(id));
            } else {
                this.state.getId().add(id, state.getId().get(id));
                this.state.getCharacter()[id] = state.character[id];
                this.state.getUsername().add(id, state.getUsername().get(id));
            }
        }
        notifyAllObservers();
    }

    public State getGameState() {
        return gameState;
    }

    public void setGameState(State state) throws RemoteException {
        this.gameState = state;
        notifyAllObserversGame();

    }

    public void notifyAllObservers() throws RemoteException {
        for (ObserverRI observer : observers) {
            observer.update();
        }
    }

    public void notifyAllObserversGame() throws RemoteException {
        for (ObserverRI observer : observers) {
            observer.updateGame();
        }
    }

    public int getIndexObserver(String username) throws RemoteException {
        for (int i = 0; i < observers.size(); i++) {
            if (observers.get(i).getUsername().equals(username)) {
                return i;
            }
        }
        return 0;
    }

    public UUID getId() {
        return id;
    }

    public List<ObserverRI> getObservers() {
        return observers;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setObservers(List<ObserverRI> observers) {
        this.observers = observers;
    }

    public LobbyStatusEnum getLobbyState() throws RemoteException {
        return lobbyStatus;
    }

    public void setLobbyState(LobbyStatusEnum lobbyState) {
        this.lobbyStatus = lobbyState;
    }

    @Override
    public List<ObserverRI> players() throws RemoteException {
        return observers;
    }

    @Override
    public UUID getID() throws RemoteException {
        return id;
    }

    @Override
    public LobbyMapEnum getMapName() throws RemoteException {
        return map;
    }

    @Override
    public void attach(ObserverRI observer) throws RemoteException {
        this.observers.add(observer);
    }


    @Override
    public void detach(ObserverRI observer) throws RemoteException {
        this.observers.remove(observer);
    }

    public int getCurrentPlayers() throws RemoteException {
        return observers.size();
    }

    public int getMaxPlayers() throws RemoteException {
        if (map.equals(LobbyMapEnum.FourCorners)) {
            return 4;
        }
        return 2;
    }


}
