import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

class Simulator {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage run <ticks> <cells> <subscribers>");
            return;
        }

        ActorSystem system = ActorSystem.create("system");
        ActorRef clock = system.actorOf(Props.create(Master.class), "clock");

        clock.tell(new Master.Start(Long.parseLong(args[0]), Long.parseLong(args[1]), Long.parseLong(args[2])), clock);
    }
}
