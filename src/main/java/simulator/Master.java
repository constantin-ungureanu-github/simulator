package simulator;

import static simulator.Master.Messages.Ping;
import static simulator.Master.Messages.Pong;
import static simulator.Master.Messages.Stop;
import static simulator.Master.Messages.Tick;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.core.async.AsyncLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Master extends UntypedActor {
    private static Logger log = LoggerFactory.getLogger(Master.class);
    private static ActorRef master;

    public enum Messages {
        Ping,
        Pong,
        Tick,
        Stop
    }

    private long step, startTime, duration, cellsNumber, subscribersNumber;
    private List<ActorRef> subscribers = new ArrayList<ActorRef>();
    private List<ActorRef> cells = new ArrayList<ActorRef>();
    private ActorRef network;

    public static ActorRef getMaster() {
        return master;
    }

    private void addNetwork() {
        network = context().system().actorOf(Props.create(Network.class), "network");
    }

    private void addCells() {
        for (long i = 0L; i < cellsNumber; i++)
            cells.add(context().system().actorOf(Props.create(Cell.class), "cell_" + i));
    }

    private void addSubscribers() {
        for (long i = 0L; i < subscribersNumber; i++)
            subscribers.add(context().system().actorOf(Props.create(Subscriber.class), "subscriber_" + i));
    }

    private void initializeCells() {
        WorkLoad.getInstance().addWork(cellsNumber);
        cells.stream().forEach(cell -> cell.tell(Cell.Messages.ConnectToNetwork, network));
    }

    private void initializeSubscribers() {
        WorkLoad.getInstance().addWork(subscribersNumber);
        subscribers.stream().forEach(subscriber -> subscriber.tell(Subscriber.Messages.ConnectToCell, cells.get(ThreadLocalRandom.current().nextInt((int) cellsNumber))));
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Start) {
            log.info("Simulation started.");
            startTime = System.currentTimeMillis();

            master = getSelf();
            duration = ((Start) message).getDuration();
            cellsNumber = ((Start) message).getCellsNumber();
            subscribersNumber = ((Start) message).getSubscribersNumber();

            addNetwork();
            addCells();
            addSubscribers();

            initializeCells();
            initializeSubscribers();
        } else if (message == Stop) {
            long stopTime = System.currentTimeMillis();
            log.info("Simulation completed after {} milliseconds.", stopTime - startTime);
            AsyncLogger.stop();
            getContext().system().terminate();
        } else if (message == Ping) {
            WorkLoad.getInstance().removeWork();
            if (WorkLoad.getInstance().isWorkDone()) {
                if (step < duration) {
                    step++;
                    getSelf().tell(Tick, getSelf());
                    getSelf().tell(Pong, getSelf());
                } else {
                    getSelf().tell(Stop, getSelf());
                }
            }
        } else if (message == Pong) {
            WorkLoad.getInstance().addWork();
            getSender().tell(Ping, getSelf());
        } else if (message == Tick) {
            log.info("{}", step);
            WorkLoad.getInstance().addWork(subscribersNumber);
            subscribers.stream().forEach(subscriber -> subscriber.tell(Subscriber.Messages.MakeVoiceCall, subscribers.get(ThreadLocalRandom.current().nextInt((int) subscribersNumber))));
        } else {
            unhandled(message);
        }
    }

    public static final class Start implements Serializable {
        private static final long serialVersionUID = -5750159585853846166L;
        private long duration, cellsNumber, subscribersNumber;

        public Start(long duration, long cellsNumber, long subscribersNumber) {
            setDuration(duration);
            setCellsNumber(cellsNumber);
            setSubscribersNumber(subscribersNumber);
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public long getCellsNumber() {
            return cellsNumber;
        }

        public void setCellsNumber(long cellsNumber) {
            this.cellsNumber = cellsNumber;
        }

        public long getSubscribersNumber() {
            return subscribersNumber;
        }

        public void setSubscribersNumber(long subscribersNumber) {
            this.subscribersNumber = subscribersNumber;
        }
    }
}
