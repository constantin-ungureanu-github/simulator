package simulator.actors;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Cell extends UntypedActor {
    private Set<ActorRef> subscribers = new HashSet<>();

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ConnectSubscriber) {
            subscribers.add(getSender());
            getSender().tell(Subscriber.AckConnectToCell.getInstance(), getSelf());
        } else if (message instanceof DisconnectSubscriber) {
            subscribers.remove(getSender());
            getSender().tell(Subscriber.AckDisconnectFromCell.getInstance(), getSelf());
        } else {
            unhandled(message);
        }
    }

    public static final class ConnectSubscriber implements Serializable {
        private static final long serialVersionUID = 5394110819042126132L;
        private static ConnectSubscriber instance = new ConnectSubscriber();

        private ConnectSubscriber() {}

        public static ConnectSubscriber getInstance() {
            return instance;
        }
    }

    public static final class DisconnectSubscriber implements Serializable {
        private static final long serialVersionUID = -7122625140056531246L;
        private static DisconnectSubscriber instance = new DisconnectSubscriber();

        private DisconnectSubscriber() {
        }

        public static DisconnectSubscriber getInstance() {
            return instance;
        }
    }
}
