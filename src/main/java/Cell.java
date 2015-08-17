import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Cell extends UntypedActor {
    private List<ActorRef> subscribers = new ArrayList<ActorRef>();

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ConnectSubscriber) {
            subscribers.add(((ConnectSubscriber) message).getSubscriber());
            ((ConnectSubscriber) message).getSubscriber().tell(new Subscriber.AckConnectToCell(getSelf()), getSender());
        } else if (message instanceof DisconnectSubscriber) {
            subscribers.remove(((DisconnectSubscriber) message).getSubscriber());
            ((ConnectSubscriber) message).getSubscriber().tell(new Subscriber.AckDisconnectFromCell(getSelf()), getSelf());
        } else {
            unhandled(message);
        }
    }

    public static final class ConnectSubscriber implements Serializable {
        private static final long serialVersionUID = 5394110819042126132L;
        ActorRef subscriber;

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
        ActorRef subscriber;

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
