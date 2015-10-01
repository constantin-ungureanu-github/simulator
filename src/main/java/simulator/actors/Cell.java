package simulator.actors;

import static simulator.actors.Cell.State.Available;
import static simulator.actors.Cell.State.Idle;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import akka.actor.AbstractFSM;
import akka.actor.ActorRef;
import simulator.actors.Cell.Data;
import simulator.actors.Cell.State;
import simulator.actors.Network.ConnectCell;
import simulator.actors.Subscriber.AckConnectToCell;
import simulator.actors.Subscriber.AckDisconnectFromCell;
import simulator.actors.Subscriber.AckMakeVoiceCall;

public class Cell extends AbstractFSM<State, Data> {
    {
        startWith(Idle, new Data());

        when(Idle, matchEventEquals(ConnectToNetwork.getInstance(), (state, data) -> {
            return stay().replying((ConnectCell.getInstance()));
        }));

        when(Idle, matchEventEquals(ConnectCellAck.getInstance(), (state, data) -> {
            data.setNetwork(sender());
            Master.getMaster().tell(Master.Ping.getInstance(), self());
            return goTo(Available);
        }));

        when(Idle, matchEventEquals(ConnectSubscriber.getInstance(), (state, data) -> {
            data.addSubscriber(sender());
            return stay().replying((AckConnectToCell.getInstance()));
        }));

        when(Available, matchEventEquals(ConnectSubscriber.getInstance(), (state, data) -> {
            data.addSubscriber(sender());
            return stay().replying((AckConnectToCell.getInstance()));
        }));

        when(Available, matchEventEquals(DisconnectSubscriber.getInstance(), (state, data) -> {
            data.removeSubscriber(sender());
            return stay().replying((AckDisconnectFromCell.getInstance()));
        }));

        when(Available, matchEvent(Send.class, (state, data) -> {
            data.getNetwork().tell(new Network.Process(state.subscriber, self()), self());
            return stay();
        }));

        when(Available, matchEvent(Receive.class, (state, data) -> {
            state.subscriber.tell(AckMakeVoiceCall.getInstance(), self());
            return stay();
        }));

        initialize();
    }

    public static enum State {
        Idle, Available, Down
    }

    public static final class Data {
        private Set<ActorRef> subscribers = new HashSet<>();
        private ActorRef network;

        public void addSubscriber(ActorRef sender) {
            subscribers.add(sender);
        }

        public void removeSubscriber(ActorRef sender) {
            subscribers.remove(sender);
        }

        public ActorRef getNetwork() {
            return network;
        }

        public void setNetwork(ActorRef network) {
            this.network = network;
        }
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
