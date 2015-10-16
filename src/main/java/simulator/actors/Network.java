package simulator.actors;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import simulator.actors.Cell.ConnectCellAck;
import simulator.actors.Cell.DisconnectFromNetwork;
import simulator.actors.Cell.Receive;

public class Network extends UntypedActor {
    private static Logger log = LoggerFactory.getLogger(Network.class);
    private Set<ActorRef> cells = new HashSet<>();

    @Override
    public void onReceive(Object message) throws Exception {
	if (message instanceof ConnectCell) {
	    addCell(sender());
	    getSender().tell(ConnectCellAck.getInstance(), getSelf());
	} else if (message instanceof DisconnectCell) {
	    removeCell(sender());
	    getSender().tell(DisconnectFromNetwork.getInstance(), getSelf());
	} else if (message instanceof Process) {
	    ((Process) message).cell.tell(new Receive(((Process) message).subscriber), getSelf());
	} else {
	    log.error("{}", message);
	    unhandled(message);
	}
    }

    public void addCell(ActorRef sender) {
	cells.add(sender);
    }

    public void removeCell(ActorRef sender) {
	cells.remove(sender);
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
