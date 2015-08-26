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

    LoggingAdapter log = Logging.getLogger(getContext().system(), Cell.class.getName());

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ConnectSubscriber) {
            ActorRef subscriber = ((ConnectSubscriber) message).getSubscriber();
            subscribers.add(subscriber);
            subscriber.tell(new Subscriber.AckConnectToCell(getSelf()), getSender());
        } else if (message instanceof DisconnectSubscriber) {
            ActorRef subscriber = ((ConnectSubscriber) message).getSubscriber();
            subscribers.remove(subscriber);
            subscriber.tell(new Subscriber.AckDisconnectFromCell(getSelf()), getSelf());
        } else {
            unhandled(message);
        }
    }

    public static final class ConnectSubscriber implements Serializable {
        private static final long serialVersionUID = 5394110819042126132L;
        private ActorRef subscriber;

        public ConnectSubscriber(ActorRef subscriber) {
            setSubscriber(subscriber);
        }

        public ActorRef getSubscriber() {
            return subscriber;
        }

        public void setSubscriber(ActorRef subscriber) {
            this.subscriber = subscriber;
        }
    }

    public static final class DisconnectSubscriber implements Serializable {
        private static final long serialVersionUID = -7122625140056531246L;
        private ActorRef subscriber;

        public DisconnectSubscriber(ActorRef subscriber) {
            setSubscriber(subscriber);
        }

        public ActorRef getSubscriber() {
            return subscriber;
        }

        public void setSubscriber(ActorRef subscriber) {
            this.subscriber = subscriber;
        }
    }
}
