package simulator.actors;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Subscriber extends UntypedActor {
    private ActorRef cell;

    private static Logger log = LoggerFactory.getLogger(Subscriber.class);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ConnectToCell) {
            getSender().tell(Cell.ConnectSubscriber.getInstance(), getSelf());
        } else if (message instanceof AckConnectToCell) {
        	setCell(getSender());
        	Master.getMaster().tell(Master.Ping.getInstance(), getSelf());
        } else if (message instanceof NAckConnectToCell) {
            setCell(null);
            Master.getMaster().tell(Master.Ping.getInstance(), getSelf());
        } else if (message instanceof DisconnectFromCell) {
            getCell().tell(Cell.DisconnectSubscriber.getInstance(), getSelf());
        } else if (message instanceof AckDisconnectFromCell) {
            setCell(null);
            Master.getMaster().tell(Master.Ping.getInstance(), getSelf());
        } else if (message instanceof SendSMS) {
            getSender().tell(ReceiveSMS.getInstance(), getSelf());
        } else if (message instanceof AckSendSMS) {
            Master.getMaster().tell(Master.Ping.getInstance(), getSelf());
        } else if (message instanceof NAckSendSMS) {
            Master.getMaster().tell(Master.Ping.getInstance(), getSelf());
        } else if (message instanceof ReceiveSMS) {
            getSender().tell(AckSendSMS.getInstance(), getSelf());
        } else if (message instanceof MakeVoiceCall) {
            getSender().tell(ReceiveVoiceCall.getInstance(), getSelf());
        } else if (message instanceof AckMakeVoiceCall) {
            log.info("{}", message);
            Master.getMaster().tell(Master.Ping.getInstance(), getSelf());
        } else if (message instanceof NAckMakeVoiceCall) {
            Master.getMaster().tell(Master.Ping.getInstance(), getSelf());
        } else if (message instanceof ReceiveVoiceCall) {
            getSender().tell(AckMakeVoiceCall.getInstance(), getSelf());
        } else {
            unhandled(message);
        }
    }

    public ActorRef getCell() {
        return cell;
    }

    public void setCell(ActorRef cell) {
        this.cell = cell;
    }

	public static final class ConnectToCell implements Serializable {
        private static final long serialVersionUID = -1690119546167038359L;
        private static ConnectToCell instance = new ConnectToCell();

        private ConnectToCell() {
        }

        public static ConnectToCell getInstance() {
            return instance;
        }
    }

    public static final class AckConnectToCell implements Serializable {
        private static final long serialVersionUID = 5279648322574152683L;
        private static AckConnectToCell instance = new AckConnectToCell();

        private AckConnectToCell() {
        }

        public static AckConnectToCell getInstance() {
            return instance;
        }
    }

    public static final class NAckConnectToCell implements Serializable {
		private static final long serialVersionUID = -5756205205881084882L;
		private static NAckConnectToCell instance = new NAckConnectToCell();

		private NAckConnectToCell() {
        }

        public static NAckConnectToCell getInstance() {
            return instance;
        }
    }

    public static final class DisconnectFromCell implements Serializable {
        private static final long serialVersionUID = 5346038999058973129L;
        private static DisconnectFromCell instance = new DisconnectFromCell();

        private DisconnectFromCell() {
        }

        public static DisconnectFromCell getInstance() {
            return instance;
        }
    }

    public static final class AckDisconnectFromCell implements Serializable {
        private static final long serialVersionUID = -6306573968601136056L;
        private static AckDisconnectFromCell instance = new AckDisconnectFromCell();

        private AckDisconnectFromCell() {
        }

        public static AckDisconnectFromCell getInstance() {
            return instance;
        }
    }

    public static final class SendSMS implements Serializable {
        private static final long serialVersionUID = 2789066518138474943L;
        private static SendSMS instance = new SendSMS();

        private SendSMS() {
        }

        public static SendSMS getInstance() {
            return instance;
        }
    }

    public static final class AckSendSMS implements Serializable {
		private static final long serialVersionUID = -6073036744404507432L;
		private static AckSendSMS instance = new AckSendSMS();

		private AckSendSMS() {
        }

        public static AckSendSMS getInstance() {
            return instance;
        }
    }

    public static final class NAckSendSMS implements Serializable {
		private static final long serialVersionUID = -5162774852791267920L;
		private static NAckSendSMS instance = new NAckSendSMS();

		private NAckSendSMS() {
        }

        public static NAckSendSMS getInstance() {
            return instance;
        }
    }

    public static final class ReceiveSMS implements Serializable {
        private static final long serialVersionUID = -6793909914988975328L;
        private static ReceiveSMS instance = new ReceiveSMS();

        private ReceiveSMS() {
        }

        public static ReceiveSMS getInstance() {
            return instance;
        }
    }

    public static final class MakeVoiceCall implements Serializable {
        private static final long serialVersionUID = -8377483922930960660L;
        private static MakeVoiceCall instance = new MakeVoiceCall();

        private MakeVoiceCall() {
        }

        public static MakeVoiceCall getInstance() {
            return instance;
        }
    }

    public static final class AckMakeVoiceCall implements Serializable {
		private static final long serialVersionUID = -1398871624346829811L;
        private static AckMakeVoiceCall instance = new AckMakeVoiceCall();

        private AckMakeVoiceCall() {
        }

        public static AckMakeVoiceCall getInstance() {
            return instance;
        }
    }

    public static final class NAckMakeVoiceCall implements Serializable {
		private static final long serialVersionUID = 4341146802258145251L;
		private static NAckMakeVoiceCall instance = new NAckMakeVoiceCall();

		private NAckMakeVoiceCall() {
        }

        public static NAckMakeVoiceCall getInstance() {
            return instance;
        }
    }

    public static final class ReceiveVoiceCall implements Serializable {
		private static final long serialVersionUID = -6986997443922771902L;
		private static ReceiveVoiceCall instance = new ReceiveVoiceCall();

        private ReceiveVoiceCall() {
        }

        public static ReceiveVoiceCall getInstance() {
            return instance;
        }
    }
}
