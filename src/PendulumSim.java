import com.opencsv.CSVWriter;

import java.awt.*;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;


public class PendulumSim {
    private Initializer initializer;
    private EuSolver euSolver;
    private RKSolver rkSolver;
    private DoublePendulum dPendulum;
    private int solverType;
    private int tick;
    private int animLength;
    private int animStep;
    private boolean error;

    private TimeGraph theta1Graph;
    private TimeGraph theta2Graph;
    private TimeGraph omega1Graph;
    private TimeGraph omega2Graph;

    private TimeGraph energyGraph;

    private final ArrayList<String[]> dataLines;


    public PendulumSim (String problemFile) {
        this.tick = 0;
        this.error = false;

        this.initializer = new Initializer(problemFile);

        try {
            this.initializer.readProblem();
        } catch (Exception exception) {
            System.out.println(exception.toString());
            this.error = true;
        }

        this.dataLines = new ArrayList<>();



        if(!error) {
            Problem problem = this.initializer.getProblem();
            Segment[] segments = problem.getSegments();

            Pendulum p1 = new Pendulum(new Dot(500, 500),
                    segments[0].getMass(),
                    segments[0].getLength(),
                    segments[0].getTheta(),
                    segments[0].getOmega());

            Pendulum p2 = new Pendulum(segments[1].getMass(),
                    segments[1].getLength(),
                    segments[1].getTheta(),
                    segments[1].getOmega());

            this.dPendulum = new DoublePendulum(p1, p2);

            switch (problem.getSolver().getType()) {
                case "Euler" -> {
                    this.solverType = 0;
                    this.euSolver = new EuSolver();
                    this.euSolver.setdPendulum(this.dPendulum);
                    this.euSolver.setT(problem.getSolver().getT());
                    this.euSolver.setDt(problem.getSolver().getDt());
                }
                case "Runge-Kutta" -> {
                    this.solverType = 1;
                    this.rkSolver = new RKSolver();
                    this.rkSolver.setdPendulum(this.dPendulum);
                    this.rkSolver.setT(problem.getSolver().getT());
                    this.rkSolver.setDt(problem.getSolver().getDt());
                }
            }

            int graphCap = 250;

            this.animLength =(int) (problem.getSolver().getT() / problem.getSolver().getDt());

            this.animStep = (int) Math.ceil((double)this.animLength /  graphCap);

            ScaleSynchronizer thetaScaleSynchronizer = new ScaleSynchronizer();

            this.theta1Graph = new TimeGraph(1000, 300, graphCap);
            this.theta1Graph.setTitle("Theta");
            this.theta1Graph.setOrigin(1000, 0);
            this.theta1Graph.setPlainCl(new Color(0, 0, 0));
            this.theta1Graph.setBorderCl(new Color(100, 100, 100));
            this.theta1Graph.setDotCl(Color.GREEN);
            this.theta1Graph.setLineCl(Color.GREEN);
            this.theta1Graph.setLevelLineCl(Color.GREEN);
            this.theta1Graph.setValueTextCl(Color.WHITE);
            this.theta1Graph.setTitleTextCl(Color.WHITE);
            this.theta1Graph.setScaleTextCl(Color.WHITE);
            this.theta1Graph.setTextSize(12);
            this.theta1Graph.setInteger(false);
            this.theta1Graph.setScaleSynchronizer(thetaScaleSynchronizer);

            this.theta2Graph = new TimeGraph(1000, 300, graphCap);
            this.theta2Graph.setTitle("");
            this.theta2Graph.setOrigin(1000, 0);
            this.theta2Graph.setPlainCl(new Color(0, 0, 0, 0));
            this.theta2Graph.setBorderCl(new Color(100, 100, 100));
            this.theta2Graph.setDotCl(Color.MAGENTA);
            this.theta2Graph.setLineCl(Color.MAGENTA);
            this.theta2Graph.setLevelLineCl(Color.MAGENTA);
            this.theta2Graph.setValueTextCl(Color.WHITE);
            this.theta2Graph.setTitleTextCl(Color.WHITE);
            this.theta2Graph.setScaleTextCl(Color.WHITE);
            this.theta2Graph.setTextSize(12);
            this.theta2Graph.setInteger(false);
            this.theta2Graph.setScaleSynchronizer(thetaScaleSynchronizer);

            thetaScaleSynchronizer.addGraph(theta1Graph);
            thetaScaleSynchronizer.addGraph(theta2Graph);

            ScaleSynchronizer omegaScaleSynchronizer = new ScaleSynchronizer();

            this.omega1Graph = new TimeGraph(1000, 300, graphCap);
            this.omega1Graph.setTitle("Omega");
            this.omega1Graph.setOrigin(1000, 300);
            this.omega1Graph.setPlainCl(new Color(0, 0, 0));
            this.omega1Graph.setBorderCl(new Color(100, 100, 100));
            this.omega1Graph.setDotCl(Color.GREEN);
            this.omega1Graph.setLineCl(Color.GREEN);
            this.omega1Graph.setLevelLineCl(Color.GREEN);
            this.omega1Graph.setValueTextCl(Color.WHITE);
            this.omega1Graph.setTitleTextCl(Color.WHITE);
            this.omega1Graph.setScaleTextCl(Color.WHITE);
            this.omega1Graph.setTextSize(12);
            this.omega1Graph.setInteger(false);
            this.omega1Graph.setScaleSynchronizer(omegaScaleSynchronizer);

            this.omega2Graph = new TimeGraph(1000, 300, graphCap);
            this.omega2Graph.setTitle("");
            this.omega2Graph.setOrigin(1000, 300);
            this.omega2Graph.setPlainCl(new Color(0, 0, 0, 0));
            this.omega2Graph.setBorderCl(new Color(100, 100, 100));
            this.omega2Graph.setDotCl(Color.MAGENTA);
            this.omega2Graph.setLineCl(Color.MAGENTA);
            this.omega2Graph.setLevelLineCl(Color.MAGENTA);
            this.omega2Graph.setValueTextCl(Color.WHITE);
            this.omega2Graph.setTitleTextCl(Color.WHITE);
            this.omega2Graph.setScaleTextCl(Color.WHITE);
            this.omega2Graph.setTextSize(12);
            this.omega2Graph.setInteger(false);
            this.omega2Graph.setScaleSynchronizer(omegaScaleSynchronizer);

            omegaScaleSynchronizer.addGraph(omega1Graph);
            omegaScaleSynchronizer.addGraph(omega2Graph);

            this.energyGraph = new TimeGraph(1000, 300, graphCap);
            this.energyGraph.setTitle("Energy");
            this.energyGraph.setOrigin(1000, 600);
            this.energyGraph.setPlainCl(new Color(0, 0, 0));
            this.energyGraph.setBorderCl(new Color(100, 100, 100));
            this.energyGraph.setDotCl(Color.CYAN);
            this.energyGraph.setLineCl(Color.CYAN);
            this.energyGraph.setLevelLineCl(Color.CYAN);
            this.energyGraph.setValueTextCl(Color.WHITE);
            this.energyGraph.setTitleTextCl(Color.WHITE);
            this.energyGraph.setScaleTextCl(Color.WHITE);
            this.energyGraph.setTextSize(12);
            this.energyGraph.setInteger(false);
        }
    }

    public boolean isError (){
        return error;
    }

    void solve() {
        switch (this.solverType) {
            case 0 -> {
                this.euSolver.solve();
            }
            case 1 -> {
                this.rkSolver.solve();
            }
        }
    }

    void resetAnimationTick() {
        this.tick = 0;
        this.theta1Graph.clear();
        this.theta2Graph.clear();
        this.omega1Graph.clear();
        this.omega2Graph.clear();
        this.energyGraph.clear();
    }

    void animate() throws IOException {
        if (this.tick > this.animLength) {
            this.exportData();
            return;
        }


        App.processingRef.background(0);
        double theta1 = 0;
        double theta2 = 0;
        double omega1 = 0;
        double omega2 = 0;
        double energy = 0;
        switch (this.solverType) {
            case 0 -> {
                theta1 = this.euSolver.getTheta1().get(this.tick);
                theta2 = this.euSolver.getTheta2().get(this.tick);
                omega1 = this.euSolver.getOmega1().get(this.tick);
                omega2 = this.euSolver.getOmega2().get(this.tick);
                energy = this.euSolver.getEnergy().get(this.tick);
            }
            case 1 -> {
                theta1 = this.rkSolver.getTheta1().get(this.tick);
                theta2 = this.rkSolver.getTheta2().get(this.tick);
                omega1 = this.rkSolver.getOmega1().get(this.tick);
                omega2 = this.rkSolver.getOmega2().get(this.tick);
                energy = this.rkSolver.getEnergy().get(this.tick);
            }
        }

        this.dPendulum.getFirstSeg().setTheta(theta1);
        this.dPendulum.getSecondSeg().setTheta(theta2);
        this.dPendulum.getFirstSeg().setOmega(omega1);
        this.dPendulum.getSecondSeg().setOmega(omega2);

        this.dPendulum.getSecondSeg().setFixPoint(this.dPendulum.getFirstSeg().getEndPoint());

        if(this.tick % this.animStep == 0) {
            this.theta1Graph.addValue(theta1);
            this.theta2Graph.addValue(theta2);
            this.omega1Graph.addValue(omega1);
            this.omega2Graph.addValue(omega2);
            this.energyGraph.addValue(energy);
            dataLines.add(new String[]
                    {
                            String.valueOf(this.tick * this.initializer.getProblem().getSolver().getDt()),  String.valueOf(theta1), String.valueOf(theta2), String.valueOf(omega1), String.valueOf(omega2)
                    });
        }



        this.dPendulum.render();
        this.theta1Graph.render();
        this.theta2Graph.render();
        this.omega1Graph.render();
        this.omega2Graph.render();
        this.energyGraph.render();

        this.tick += 1;
    }

    void exportData () throws IOException {
        try (
                Writer writer = Files.newBufferedWriter(Paths.get("results.csv"));
                CSVWriter csvWriter = new CSVWriter(writer);
                ) {
                csvWriter.writeNext(new String[] { "time", "theta1", "theta2", "omega1", "omega2"});
                csvWriter.writeAll(dataLines);
        }
    }


}
