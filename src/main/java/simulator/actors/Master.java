package simulator.actors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Master extends UntypedActor {
    private long step, startTime, duration, cellsNumber, subscribersNumber;
    private List<ActorRef> subscribers = new ArrayList<ActorRef>();
    private List<ActorRef> cells = new ArrayList<ActorRef>();

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Start) {
            startTime = System.currentTimeMillis();
            duration = ((Start) message).getDuration();
            cellsNumber = ((Start) message).getCellsNumber();
            subscribersNumber = ((Start) message).getSubscribersNumber();

            addCells();
            addSubscribers();
            initializeSubscribers();
        } else if (message instanceof Stop) {
            long stopTime = System.currentTimeMillis();
            System.out.println("Finished after " + (stopTime - startTime) + " milliseconds.");
            System.exit(0);
        } else if (message instanceof Ping) {
            WorkStatus.getInstance().removeWork();
            if (WorkStatus.getInstance().isWorkDone()) {
                if (step < duration) {
                    step++;
                    getSender().tell(Tick.getInstance(), getSelf());
                    getSender().tell(Pong.getInstance(), getSelf());
                } else {
                    getSender().tell(Stop.getInstance(), getSelf());
                }
            }
        } else if (message instanceof Pong) {
            WorkStatus.getInstance().addWork();
            getSender().tell(Ping.getInstance(), getSelf());
        } else if (message instanceof Tick) {
            Random random = new Random(subscribersNumber);
            WorkStatus.getInstance().addWork(subscribersNumber);
            for (ActorRef subscriber : subscribers)
                subscriber.tell(new Subscriber.SendSMS(subscribers.get(random.nextInt((int) subscribersNumber))), getSelf());
        } else {
            unhandled(message);
        }
    }

    private void addCells() {
        for (long i = 0L; i < cellsNumber; i++)
            cells.add(context().system().actorOf(Props.create(Cell.class), "cell_" + i));
    }

    private void addSubscribers() {
        for (long i = 0L; i < subscribersNumber; i++)
            subscribers.add(context().system().actorOf(Props.create(Subscriber.class), "subscriber_" + i));
    }

    private void initializeSubscribers() {
        Random random = new Random(cellsNumber);
        WorkStatus.getInstance().addWork(subscribersNumber);
        for (ActorRef subscriber : subscribers)
            subscriber.tell(new Subscriber.ConnectToCell(cells.get(random.nextInt((int) cellsNumber))), getSelf());
    }

    private static final class WorkStatus {
        private static final WorkStatus instance = new WorkStatus();
        private long workLoad;

        private WorkStatus() {
        }

        static WorkStatus getInstance() {
            return instance;
        }

        boolean isWorkDone() {
            return workLoad == 0;
        }

        void addWork(long workLoad) {
            this.workLoad += workLoad;
        }

        void addWork() {
            workLoad++;
        }

        void removeWork() {
            workLoad--;
        }

        @SuppressWarnings("unused")
        void removeWork(long workLoad) {
            workLoad -= workLoad;
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

    public static final class Stop implements Serializable {
        private static final long serialVersionUID = 5860804743274500349L;
        private static final Stop instance = new Stop();

        private Stop() {
        }

        public static Stop getInstance() {
            return instance;
        }
    }

    public static final class Ping implements Serializable {
        private static final long serialVersionUID = 5592624326581846277L;
        private static final Ping instance = new Ping();

        private Ping() {
        }

        public static Ping getInstance() {
            return instance;
        }
    }

    public static final class Pong implements Serializable {
        private static final long serialVersionUID = -3249837482565376870L;
        private static final Pong instance = new Pong();

        private Pong() {
        }

        public static Pong getInstance() {
            return instance;
        }
    }

    public static final class Tick implements Serializable {
        private static final long serialVersionUID = 3408513431293936766L;
        private static final Tick instance = new Tick();

        private Tick() {
        }

        public static Tick getInstance() {
            return instance;
        }
    }
}
