import java.io.Serializable;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Subscriber extends UntypedActor {
    private ActorRef cell;

    public ActorRef getCell() {
        return cell;
    }

    public void setCell(ActorRef cell) {
        this.cell = cell;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ConnectToCell) {
            cell = ((ConnectToCell) message).getCell();
            cell.tell(new Cell.ConnectSubscriber(getSelf()), getSender());
        } else if (message instanceof AckConnectToCell) {
//            System.out.println("Connected " + this.toString() + " to " + ((AckConnectToCell) message).getCell());
            getSender().tell(Master.Ping.getInstance(), getSender());
        } else if (message instanceof DisconnectFromCell) {
            cell = null;
        } else if (message instanceof AckDisconnectFromCell) {
            getSender().tell(Master.Ping.getInstance(), getSender());
        } else if (message instanceof SendSMS) {
//            System.out.println(this.toString() + " sent message to " + ((SendSMS) message).getSubscriber() + " via cell " + getCell());
            ((SendSMS) message).getSubscriber().tell(new ReceiveSMS(getSelf()), getSender());
        } else if (message instanceof ReceiveSMS) {
//            System.out.println(this.toString() + " received message from " + ((ReceiveSMS) message).getSubscriber() + " via cell " + getCell());
            getSender().tell(Master.Ping.getInstance(), getSender());
        } else {
            unhandled(message);
        }
    }

    public static final class SendSMS implements Serializable {
        private static final long serialVersionUID = 2789066518138474943L;
        private ActorRef subscriber;

        public SendSMS(ActorRef subscriber) {
            setSubscriber(subscriber);
        }

        public ActorRef getSubscriber() {
            return subscriber;
        }

        public void setSubscriber(ActorRef subscriber) {
            this.subscriber = subscriber;
        }
    }

    public static final class ReceiveSMS implements Serializable {
        private static final long serialVersionUID = -6793909914988975328L;
        private ActorRef subscriber;

        public ReceiveSMS(ActorRef subscriber) {
            setSubscriber(subscriber);
        }

        public ActorRef getSubscriber() {
            return subscriber;
        }

        public void setSubscriber(ActorRef subscriber) {
            this.subscriber = subscriber;
        }
    }

    public static final class MakeVoiceCall implements Serializable {
        private static final long serialVersionUID = -8377483922930960660L;
        private static final MakeVoiceCall instance = new MakeVoiceCall();

        public MakeVoiceCall() {
        }

        public static MakeVoiceCall getInstance() {
            return instance;
        }
    }

    public static final class ConnectToCell implements Serializable {
        private static final long serialVersionUID = -1690119546167038359L;
        private ActorRef cell;

        public ConnectToCell(ActorRef cell) {
            setCell(cell);
        }

        public ActorRef getCell() {
            return cell;
        }

        public void setCell(ActorRef cell) {
            this.cell = cell;
        }
    }

    public static final class AckConnectToCell implements Serializable {
        private static final long serialVersionUID = 5279648322574152683L;
        private ActorRef cell;

        public AckConnectToCell(ActorRef cell) {
            setCell(cell);
        }

        public ActorRef getCell() {
            return cell;
        }

        public void setCell(ActorRef cell) {
            this.cell = cell;
        }
    }

    public static final class DisconnectFromCell implements Serializable {
        private static final long serialVersionUID = 5346038999058973129L;
        ActorRef cell;

        public DisconnectFromCell(ActorRef cell) {
            setCell(cell);
        }

        public ActorRef getCell() {
            return cell;
        }

        public void setCell(ActorRef cell) {
            this.cell = cell;
        }
    }

    public static final class AckDisconnectFromCell implements Serializable {
        private static final long serialVersionUID = -6306573968601136056L;
        ActorRef cell;

        public AckDisconnectFromCell(ActorRef cell) {
            setCell(cell);
        }

        public ActorRef getCell() {
            return cell;
        }

        public void setCell(ActorRef cell) {
            this.cell = cell;
        }
    }
}
