package simulator.actors;

import java.io.Serializable;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Subscriber extends UntypedActor {
    private ActorRef cell;

    LoggingAdapter log = Logging.getLogger(getContext().system(), Subscriber.class.getName());

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ConnectToCell) {
            ((ConnectToCell) message).getCell().tell(new Cell.ConnectSubscriber(getSelf()), getSender());
        } else if (message instanceof AckConnectToCell) {
        	setCell(((AckConnectToCell) message).getCell());
        	getSender().tell(Master.Ping.getInstance(), getSender());
        } else if (message instanceof NAckConnectToCell) {
        	getSender().tell(Master.Ping.getInstance(), getSender());
        } else if (message instanceof DisconnectFromCell) {
        	setCell(null);
        	getCell().tell(new Cell.DisconnectSubscriber(getSelf()), getSender());
        } else if (message instanceof AckDisconnectFromCell) {
        	getSender().tell(Master.Ping.getInstance(), getSender());
        } else if (message instanceof SendSMS) {
            ((SendSMS) message).getSubscriber().tell(new ReceiveSMS(getSelf()), getSender());
        } else if (message instanceof AckSendSMS) {
        	getSender().tell(Master.Ping.getInstance(), getSender());
        } else if (message instanceof NAckSendSMS) {
        	getSender().tell(Master.Ping.getInstance(), getSender());
        } else if (message instanceof ReceiveSMS) {
        	((ReceiveSMS) message).getSubscriber().tell(new AckSendSMS(getSelf()), getSender());
//        	((ReceiveSMS) message).getSubscriber().tell(new NAckSendSMS(getSelf()), getSender());
        } else if (message instanceof MakeVoiceCall) {
            ((MakeVoiceCall) message).getSubscriber().tell(new ReceiveVoiceCall(getSelf()), getSender());
        } else if (message instanceof AckMakeVoiceCall) {
//            log.info("Message Received {}", message);
        	getSender().tell(Master.Ping.getInstance(), getSender());
        } else if (message instanceof NAckMakeVoiceCall) {
        	getSender().tell(Master.Ping.getInstance(), getSender());
        } else if (message instanceof ReceiveVoiceCall) {
        	((ReceiveVoiceCall) message).getSubscriber().tell(new AckMakeVoiceCall(getSelf()), getSender());
//        	((ReceiveVoiceCall) message).getSubscriber().tell(new NAckMakeVoiceCall(getSelf()), getSender());
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

    public static final class NAckConnectToCell implements Serializable {
		private static final long serialVersionUID = -5756205205881084882L;
		private ActorRef cell;

        public NAckConnectToCell(ActorRef cell) {
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
        private ActorRef cell;

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
        private ActorRef cell;

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

    public static final class AckSendSMS implements Serializable {
		private static final long serialVersionUID = -6073036744404507432L;
		private ActorRef subscriber;

        public AckSendSMS(ActorRef subscriber) {
            setSubscriber(subscriber);
        }

        public ActorRef getSubscriber() {
            return subscriber;
        }

        public void setSubscriber(ActorRef subscriber) {
            this.subscriber = subscriber;
        }
    }

    public static final class NAckSendSMS implements Serializable {
		private static final long serialVersionUID = -5162774852791267920L;
		private ActorRef subscriber;

        public NAckSendSMS(ActorRef subscriber) {
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
        private ActorRef subscriber;

        public MakeVoiceCall(ActorRef subscriber) {
        	setSubscriber(subscriber);
        }

		public ActorRef getSubscriber() {
			return subscriber;
		}

		public void setSubscriber(ActorRef subscriber) {
			this.subscriber = subscriber;
		}
    }

    public static final class AckMakeVoiceCall implements Serializable {
		private static final long serialVersionUID = -1398871624346829811L;
        private ActorRef subscriber;

        public AckMakeVoiceCall(ActorRef subscriber) {
        	setSubscriber(subscriber);
        }

		public ActorRef getSubscriber() {
			return subscriber;
		}

		public void setSubscriber(ActorRef subscriber) {
			this.subscriber = subscriber;
		}
    }

    public static final class NAckMakeVoiceCall implements Serializable {
		private static final long serialVersionUID = 4341146802258145251L;
		private ActorRef subscriber;

        public NAckMakeVoiceCall(ActorRef subscriber) {
        	setSubscriber(subscriber);
        }

		public ActorRef getSubscriber() {
			return subscriber;
		}

		public void setSubscriber(ActorRef subscriber) {
			this.subscriber = subscriber;
		}
    }

    public static final class ReceiveVoiceCall implements Serializable {
		private static final long serialVersionUID = -6986997443922771902L;
		private ActorRef subscriber;

        public ReceiveVoiceCall(ActorRef subscriber) {
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
