package simulator;

import static simulator.Network.Messages.ConnectCell;
import static simulator.Network.Messages.DisconnectCell;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import simulator.Cell.Receive;

public class Network extends UntypedActor {
    private static Logger log = LoggerFactory.getLogger(Network.class);

    public enum Messages {
        ConnectCell,
        DisconnectCell
    }

    private Set<ActorRef> cells = new HashSet<>();

    public void addCell(ActorRef sender) {
        cells.add(sender);
    }

    public void removeCell(ActorRef sender) {
        cells.remove(sender);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message == ConnectCell) {
            addCell(sender());
            getSender().tell(Cell.Messages.ConnectCellAck, getSelf());
        } else if (message == DisconnectCell) {
            removeCell(sender());
            getSender().tell(Cell.Messages.DisconnectFromNetwork, getSelf());
        } else if (message instanceof Process) {
            ((Process) message).cell.tell(new Receive(((Process) message).subscriber), getSelf());
        } else {
            log.error("{}", message);
            unhandled(message);
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
