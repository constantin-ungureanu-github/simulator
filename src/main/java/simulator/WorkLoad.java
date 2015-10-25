package simulator;

public class WorkLoad {
    private static final WorkLoad instance = new WorkLoad();
    private long workLoad;

    static WorkLoad getInstance() {
        return instance;
    }

    private WorkLoad() {
    }

    boolean isWorkDone() {
        return workLoad == 0;
    }

    void addWork() {
        workLoad++;
    }

    void addWork(long workLoad) {
        this.workLoad += workLoad;
    }

    void removeWork() {
        workLoad--;
    }

    void removeWork(long workLoad) {
        workLoad -= workLoad;
    }
}
