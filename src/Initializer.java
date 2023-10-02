import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;

public class Initializer {
    private String problemFile;
    private Problem problem;

    public Initializer() {
        this.problemFile = null;
    }

    public Initializer(String fileName) {
        this.problemFile = fileName;
    }

    public Problem getProblem() {
        return problem;
    }

    void readProblem() throws Exception, IOException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(this.problemFile));
        this.problem = gson.fromJson(reader, Problem.class);
    }

    boolean error(Problem problem) {
        double dt = problem.getSolver().getDt();
        double T = problem.getSolver().getT();
        String type = problem.getSolver().getType();
        double m1 = problem.getSegments()[0].getMass();
        double m2 = problem.getSegments()[1].getMass();
        double l1 = problem.getSegments()[0].getLength();
        double l2 = problem.getSegments()[1].getLength();

        return T <= 0 || dt > T || dt <= 0 || m1 < 0 || m2 < 0 || l1 <= 0 || l2 <= 0;
    }
}
