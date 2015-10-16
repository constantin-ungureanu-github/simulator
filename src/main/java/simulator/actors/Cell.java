package simulator.actors;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import simulator.actors.Network.ConnectCell;
import simulator.actors.Subscriber.AckConnectToCell;
import simulator.actors.Subscriber.AckDisconnectFromCell;
import simulator.actors.Subscriber.AckMakeVoiceCall;

public class Cell extends UntypedActor {
    public static enum State {
	Available, Unavailable
    }

    private State state = State.Unavailable;
    private Set<ActorRef> subscribers = new HashSet<>();
    private ActorRef network;

    @Override
    public void onReceive(Object message) throws Exception {
	if (state == State.Unavailable) {
	    if (message instanceof ConnectToNetwork) {
		getSender().tell(ConnectCell.getInstance(), getSelf());
	    } else if (message instanceof ConnectCellAck) {
		setNetwork(sender());
		state = State.Available;
		Master.getMaster().tell(Master.Ping.getInstance(), getSelf());
	    } else if (message instanceof ConnectSubscriber) {
		addSubscriber(sender());
		getSender().tell(AckConnectToCell.getInstance(), getSelf());
	    } else {
		unhandled(message);
	    }
	} else if (state == State.Available) {
	    if (message instanceof ConnectSubscriber) {
		addSubscriber(getSender());
		getSender().tell(AckConnectToCell.getInstance(), getSelf());
	    } else if (message instanceof DisconnectSubscriber) {
		removeSubscriber(getSender());
		getSender().tell(AckDisconnectFromCell.getInstance(), getSelf());
	    } else if (message instanceof Send) {
		getNetwork().tell(new Network.Process(((Send) message).subscriber, getSelf()), getSelf());
	    } else if (message instanceof Receive) {
		((Receive) message).subscriber.tell(AckMakeVoiceCall.getInstance(), getSelf());
	    } else {
		unhandled(message);
	    }
	}
    }

    public ActorRef getNetwork() {
	return network;
    }

    public void setNetwork(ActorRef network) {
	this.network = network;
    }

    public void addSubscriber(ActorRef sender) {
	subscribers.add(sender);
    }

    public void removeSubscriber(ActorRef sender) {
	subscribers.remove(sender);
    }

    public static final class ConnectSubscriber implements Serializable {
	private static final long serialVersionUID = -7079432705142463973L;
	private static ConnectSubscriber instance = new ConnectSubscriber();

	private ConnectSubscriber() {
	}

	public static ConnectSubscriber getInstance() {
	    return instance;
	}
    }

    public static final class DisconnectSubscriber implements Serializable {
	private static final long serialVersionUID = -6095397235802982132L;
	private static DisconnectSubscriber instance = new DisconnectSubscriber();

	private DisconnectSubscriber() {
	}

	public static DisconnectSubscriber getInstance() {
	    return instance;
	}
    }

    public static final class ConnectToNetwork implements Serializable {
	private static final long serialVersionUID = -2648941695696567091L;
	private static ConnectToNetwork instance = new ConnectToNetwork();

	private ConnectToNetwork() {
	}

	public static ConnectToNetwork getInstance() {
	    return instance;
	}
    }

    public static final class ConnectCellAck implements Serializable {
	private static final long serialVersionUID = 8092796598921562962L;
	private static ConnectCellAck instance = new ConnectCellAck();

	private ConnectCellAck() {
	}

	public static ConnectCellAck getInstance() {
	    return instance;
	}
    }

    public static final class ConnectCellNack implements Serializable {
	private static final long serialVersionUID = -6676909275702080021L;
	private static ConnectCellNack instance = new ConnectCellNack();

	private ConnectCellNack() {
	}

	public static ConnectCellNack getInstance() {
	    return instance;
	}
    }

    public static final class DisconnectFromNetwork implements Serializable {
	private static final long serialVersionUID = -5392056759691026691L;
	private static DisconnectFromNetwork instance = new DisconnectFromNetwork();

	private DisconnectFromNetwork() {
	}

	public static DisconnectFromNetwork getInstance() {
	    return instance;
	}
    }

    public static final class Send implements Serializable {
	private static final long serialVersionUID = -3605078275080606455L;
	public final ActorRef subscriber;

	public Send(ActorRef subscriber) {
	    this.subscriber = subscriber;
	}
    }

    public static final class Receive implements Serializable {
	private static final long serialVersionUID = -3811296818671741425L;
	public final ActorRef subscriber;

	public Receive(ActorRef subscriber) {
	    this.subscriber = subscriber;
	}
    }
}
