package simulator.actors;

import static simulator.actors.Network.State.Available;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import akka.actor.AbstractFSM;
import akka.actor.ActorRef;
import simulator.actors.Cell.ConnectCellAck;
import simulator.actors.Cell.DisconnectFromNetwork;
import simulator.actors.Cell.Receive;
import simulator.actors.Network.Data;
import simulator.actors.Network.State;

public class Network extends AbstractFSM<State, Data> {
    {
        startWith(Available, new Data());

        when(Available, matchEventEquals(ConnectCell.getInstance(), (state, data) -> {
            data.addCell(sender());
            return stay().replying(ConnectCellAck.getInstance());
        }));

        when(Available, matchEventEquals(DisconnectCell.getInstance(), (state, data) -> {
            data.removeCell(sender());
            return stay().replying(DisconnectFromNetwork.getInstance());
        }));

        when(Available, matchEvent(Process.class, (state, data) -> {
            state.cell.tell(new Receive(state.subscriber), self());
            return stay();
        }));

        initialize();
    }

    public static enum State {
        Available
    }

    public static final class Data {
        private Set<ActorRef> cells = new HashSet<>();

        public void addCell(ActorRef sender) {
            cells.add(sender);
        }

        public void removeCell(ActorRef sender) {
            cells.remove(sender);
        }
    }

    public static final class ConnectCell implements Serializable {
        private static final long serialVersionUID = -816773315486292193L;
        private static ConnectCell instance = new ConnectCell();

        private ConnectCell() {
        }

        public static ConnectCell getInstance() {
            return instance;
        }
    }

    public static final class DisconnectCell implements Serializable {
        private static final long serialVersionUID = -3746081286585846021L;
        private static DisconnectCell instance = new DisconnectCell();

        private DisconnectCell() {
        }

        public static DisconnectCell getInstance() {
            return instance;
        }
    }

    public static final class Process implements Serializable {
        private static final long serialVersionUID = -5920798171534291280L;

        public final ActorRef subscriber;
        public final ActorRef cell;

        public Process(ActorRef subscriber, ActorRef cell) {
            this.subscriber = subscriber;
            this.cell = cell;
        }
    }
}
