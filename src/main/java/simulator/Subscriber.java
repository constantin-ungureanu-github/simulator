package simulator;

import static simulator.Subscriber.Messages.AckConnectToCell;
import static simulator.Subscriber.Messages.AckDisconnectFromCell;
import static simulator.Subscriber.Messages.AckMakeVoiceCall;
import static simulator.Subscriber.Messages.AckSendSMS;
import static simulator.Subscriber.Messages.ConnectToCell;
import static simulator.Subscriber.Messages.DisconnectFromCell;
import static simulator.Subscriber.Messages.MakeVoiceCall;
import static simulator.Subscriber.Messages.NAckConnectToCell;
import static simulator.Subscriber.Messages.NAckMakeVoiceCall;
import static simulator.Subscriber.Messages.NAckSendSMS;
import static simulator.Subscriber.Messages.ReceiveSMS;
import static simulator.Subscriber.Messages.ReceiveVoiceCall;
import static simulator.Subscriber.Messages.SendSMS;
import static simulator.Subscriber.State.Available;
import static simulator.Subscriber.State.Unavailable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Subscriber extends UntypedActor {
    private static Logger log = LoggerFactory.getLogger(Subscriber.class);

    public static enum State {
        Available,
        Unavailable
    }

    public enum Messages {
        ConnectToCell,
        AckConnectToCell,
        NAckConnectToCell,
        DisconnectFromCell,
        AckDisconnectFromCell,
        SendSMS,
        AckSendSMS,
        NAckSendSMS,
        ReceiveSMS,
        MakeVoiceCall,
        AckMakeVoiceCall,
        NAckMakeVoiceCall,
        ReceiveVoiceCall
    }

    private State state = Unavailable;
    private ActorRef cell;

    public ActorRef getCell() {
        return cell;
    }

    public void setCell(ActorRef cell) {
        this.cell = cell;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (state == Unavailable) {
            if (message == ConnectToCell) {
                getSender().tell(Cell.Messages.ConnectSubscriber, getSelf());
            } else if (message == AckConnectToCell) {
                setCell(sender());
                state = Available;
                Master.getMaster().tell(Master.Messages.Ping, getSelf());
            } else if (message == NAckConnectToCell) {
                Master.getMaster().tell(Master.Messages.Ping, getSelf());
            } else {
                unhandled(message);
            }
        } else if (state == Available) {
            if (message == NAckConnectToCell) {
                setCell(sender());
                state = Unavailable;
                Master.getMaster().tell(Master.Messages.Ping, getSelf());
            } else if (message == DisconnectFromCell) {
                getCell().tell(Cell.Messages.DisconnectSubscriber, getSelf());
            } else if (message == AckDisconnectFromCell) {
                setCell(null);
                state = Unavailable;
                Master.getMaster().tell(Master.Messages.Ping, getSelf());
            } else if (message == SendSMS) {
                getSender().tell(ReceiveSMS, getSelf());
            } else if (message == AckSendSMS) {
                Master.getMaster().tell(Master.Messages.Ping, getSelf());
            } else if (message == NAckSendSMS) {
                Master.getMaster().tell(Master.Messages.Ping, getSelf());
            } else if (message == ReceiveSMS) {
                getSender().tell(AckSendSMS, getSelf());
            } else if (message == MakeVoiceCall) {
                getCell().tell(new Cell.Send(getSelf()), getSelf());
            } else if (message == AckMakeVoiceCall) {
                log.info("{} made voice call using cell {}", getSelf(), sender());
                Master.getMaster().tell(Master.Messages.Ping, getSelf());
            } else if (message == NAckMakeVoiceCall) {
                Master.getMaster().tell(Master.Messages.Ping, getSelf());
            } else if (message == ReceiveVoiceCall) {
                getSender().tell(AckMakeVoiceCall, getSelf());
            } else {
                unhandled(message);
            }
        }
    }
}
