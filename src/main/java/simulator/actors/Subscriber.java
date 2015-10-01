package simulator.actors;

import static simulator.actors.Subscriber.State.Available;
import static simulator.actors.Subscriber.State.Idle;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.AbstractFSM;
import akka.actor.ActorRef;
import simulator.actors.Cell.ConnectSubscriber;
import simulator.actors.Cell.DisconnectSubscriber;
import simulator.actors.Cell.Send;
import simulator.actors.Subscriber.Data;
import simulator.actors.Subscriber.State;

public class Subscriber extends AbstractFSM<State, Data> {
    private static Logger log = LoggerFactory.getLogger(Subscriber.class);

    {
        startWith(Idle, new Data());

        when(Idle, matchEventEquals(ConnectToCell.getInstance(), (state, data) -> {
            return stay().replying((ConnectSubscriber.getInstance()));
        }));

        when(Idle, matchEventEquals(AckConnectToCell.getInstance(), (state, data) -> {
            data.setCell(sender());
            Master.getMaster().tell(Master.Ping.getInstance(), self());
            return goTo(Available);
        }));

        when(Idle, matchEventEquals(NAckConnectToCell.getInstance(), (state, data) -> {
            Master.getMaster().tell(Master.Ping.getInstance(), self());
            return stay();
        }));

        when(Available, matchEventEquals(NAckConnectToCell.getInstance(), (state, data) -> {
            data.setCell(sender());
            Master.getMaster().tell(Master.Ping.getInstance(), self());
            return goTo(Idle);
        }));

        when(Available, matchEventEquals(DisconnectFromCell.getInstance(), (state, data) -> {
            data.getCell().tell(DisconnectSubscriber.getInstance(), self());
            return stay();
        }));

        when(Available, matchEventEquals(AckDisconnectFromCell.getInstance(), (state, data) -> {
            data.setCell(null);
            Master.getMaster().tell(Master.Ping.getInstance(), self());
            return goTo(Idle);
        }));

        when(Available, matchEventEquals(SendSMS.getInstance(), (state, data) -> {
            return stay().replying(ReceiveSMS.getInstance());
        }));

        when(Available, matchEventEquals(AckSendSMS.getInstance(), (state, data) -> {
            Master.getMaster().tell(Master.Ping.getInstance(), self());
            return stay();
        }));

        when(Available, matchEventEquals(NAckSendSMS.getInstance(), (state, data) -> {
            Master.getMaster().tell(Master.Ping.getInstance(), self());
            return stay();
        }));

        when(Available, matchEventEquals(ReceiveSMS.getInstance(), (state, data) -> {
            return stay().using(new Data()).replying(AckSendSMS.getInstance());
        }));

        when(Available, matchEventEquals(MakeVoiceCall.getInstance(), (state, data) -> {
            data.getCell().tell(new Send(self()), self());
            return stay();
        }));

        when(Available, matchEventEquals(AckMakeVoiceCall.getInstance(), (state, data) -> {
            log.info("{} made voice call using cell {}", self(), sender());
            Master.getMaster().tell(Master.Ping.getInstance(), self());
            return stay();
        }));

        when(Available, matchEventEquals(NAckMakeVoiceCall.getInstance(), (state, data) -> {
            Master.getMaster().tell(Master.Ping.getInstance(), self());
            return stay();
        }));

        when(Available, matchEventEquals(ReceiveVoiceCall.getInstance(), (state, data) -> {
            return stay().replying(AckMakeVoiceCall.getInstance());
        }));

        initialize();
    }

    public static enum State {
        Idle, Available
    }

    public static final class Data {
        private ActorRef cell;

        public ActorRef getCell() {
            return cell;
        }

        public void setCell(ActorRef cell) {
            this.cell = cell;
        }
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
