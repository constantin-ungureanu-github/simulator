package simulator;

import static simulator.Cell.Messages.ConnectCellAck;
import static simulator.Cell.Messages.ConnectSubscriber;
import static simulator.Cell.Messages.ConnectToNetwork;
import static simulator.Cell.Messages.DisconnectSubscriber;
import static simulator.Cell.State.Available;
import static simulator.Cell.State.Unavailable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Cell extends UntypedActor {
    public static enum State {
        Available,
        Unavailable
    }

    public enum Messages {
        ConnectSubscriber,
        DisconnectSubscriber,
        ConnectToNetwork,
        ConnectCellAck,
        ConnectCellNack,
        DisconnectFromNetwork
    }

    private State state = Unavailable;
    private Set<ActorRef> subscribers = new HashSet<>();
    private ActorRef network;

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

    @Override
    public void onReceive(Object message) throws Exception {
        if (state == Unavailable) {
            if (message == ConnectToNetwork) {
                getSender().tell(Network.Messages.ConnectCell, getSelf());
            } else if (message == ConnectCellAck) {
                setNetwork(sender());
                state = State.Available;
                Master.getMaster().tell(Master.Messages.Ping, getSelf());
            } else if (message == ConnectSubscriber) {
                addSubscriber(sender());
                getSender().tell(Subscriber.Messages.AckConnectToCell, getSelf());
            } else {
                unhandled(message);
            }
        } else if (state == Available) {
            if (message == ConnectSubscriber) {
                addSubscriber(getSender());
                getSender().tell(Subscriber.Messages.AckConnectToCell, getSelf());
            } else if (message == DisconnectSubscriber) {
                removeSubscriber(getSender());
                getSender().tell(Subscriber.Messages.AckDisconnectFromCell, getSelf());
            } else if (message instanceof Send) {
                getNetwork().tell(new Network.Process(((Send) message).subscriber, getSelf()), getSelf());
            } else if (message instanceof Receive) {
                ((Receive) message).subscriber.tell(Subscriber.Messages.AckMakeVoiceCall, getSelf());
            } else {
                unhandled(message);
            }
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
