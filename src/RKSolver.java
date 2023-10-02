import java.util.ArrayList;

public class RKSolver {
    private ArrayList<Double> theta1;
    private ArrayList<Double> theta2;
    private ArrayList<Double> omega1;
    private ArrayList<Double> omega2;
    private ArrayList<Double> energy;
    private DoublePendulum dPendulum;
    private double T;
    private double dt;

    public RKSolver() {
        this.theta1 = new ArrayList<>();
        this.theta2 = new ArrayList<>();
        this.omega1 = new ArrayList<>();
        this.omega2 = new ArrayList<>();
        this.energy = new ArrayList<>();
        this.dPendulum = null;
        this.T = 0;
        this.dt = 0;
    }

    public void setdPendulum(DoublePendulum dPendulum) {
        this.dPendulum = dPendulum;
        this.theta1.clear();
        this.theta2.clear();
        this.omega1.clear();
        this.omega2.clear();
        this.energy.clear();
        this.theta1.add(dPendulum.getFirstSeg().getTheta());
        this.theta2.add(dPendulum.getSecondSeg().getTheta());
        this.omega1.add(dPendulum.getFirstSeg().getOmega());
        this.omega2.add(dPendulum.getSecondSeg().getOmega());
        this.energy.add(calcEnergy(
                dPendulum.getFirstSeg().getTheta(),
                dPendulum.getSecondSeg().getTheta(),
                dPendulum.getFirstSeg().getOmega(),
                dPendulum.getSecondSeg().getOmega()
        ));
    }

    public void setT(double t) {
        T = t;
    }

    public void setDt(double dt) {
        this.dt = dt;
    }

    public ArrayList<Double> getTheta1() {
        return theta1;
    }

    public ArrayList<Double> getTheta2() {
        return theta2;
    }

    public ArrayList<Double> getOmega1() {
        return omega1;
    }

    public ArrayList<Double> getOmega2() {
        return omega2;
    }

    public ArrayList<Double> getEnergy() {
        return energy;
    }

    void solve() {
        int steps = (int)(this.T / this.dt);

        for (int i = 1; i <= steps; i++) {
            double theta1C = theta1.get(i-1);
            double theta2C = theta2.get(i-1);
            double omega1C = omega1.get(i-1);
            double omega2C = omega2.get(i-1);

            double theta1K1 = theta1Derivative(omega1C);
            double theta2K1 = theta2Derivative(omega2C);
            double omega1K1 = omega1Derivative(theta1C, theta2C,omega1C, omega2C);
            double omega2K1 = omega2Derivative(theta1C, theta2C,omega1C, omega2C);

            double theta1K2 = theta1Derivative(omega1C+this.dt*omega1K1/2);
            double theta2K2 = theta1Derivative(omega2C+this.dt*omega2K1/2);
            double omega1K2 = omega1Derivative(theta1C+this.dt*theta1K1/2,theta2C+this.dt*theta2K1/2, omega1C+this.dt*omega1K1/2,omega2C+this.dt*omega2K1/2);
            double omega2K2 = omega2Derivative(theta1C+this.dt*theta1K1/2,theta2C+this.dt*theta2K1/2, omega1C+this.dt*omega1K1/2,omega2C+this.dt*omega2K1/2);

            double theta1K3 = theta1Derivative(omega1C+this.dt*omega1K2/2);
            double theta2K3 = theta2Derivative(omega2C+this.dt*omega2K2/2);
            double omega1K3 = omega1Derivative(theta1C+this.dt*theta1K2/2,theta2C+this.dt*theta2K2/2, omega1C+this.dt*omega1K2/2,omega2C+this.dt*omega2K2/2);
            double omega2K3 = omega2Derivative(theta1C+this.dt*theta1K2/2,theta2C+this.dt*theta2K2/2, omega1C+this.dt*omega1K2/2,omega2C+this.dt*omega2K2/2);

            double theta1K4 = theta1Derivative(omega1C+this.dt*omega1K3);
            double theta2K4 = theta2Derivative(omega2C+this.dt*omega2K3);
            double omega1K4 = omega1Derivative(theta1C+this.dt*theta1K3,theta2C+this.dt*theta2K3, omega1C+this.dt*omega1K3,omega2C+this.dt*omega2K3);
            double omega2K4 = omega2Derivative(theta1C+this.dt*theta1K3,theta2C+this.dt*theta2K3, omega1C+this.dt*omega1K3,omega2C+this.dt*omega2K3);

            double theta1 = theta1C + this.dt/6 * (theta1K1 + 2 * theta1K2 + 2 * theta1K3 + theta1K4);
            double theta2 = theta2C + this.dt/6 * (theta2K1 + 2 * theta2K2 + 2 * theta2K3 + theta2K4);
            double omega1 = omega1C + this.dt/6 * (omega1K1 + 2 * omega1K2 + 2 * omega1K3 + omega1K4);
            double omega2 = omega2C + this.dt/6 * (omega2K1 + 2 * omega2K2 + 2 * omega2K3 + omega2K4);

            this.theta1.add(theta1);
            this.theta2.add(theta2);
            this.omega1.add(omega1);
            this.omega2.add(omega2);

            this.energy.add(calcEnergy(theta1,theta2,omega1,omega2));
        }

    }

    double calcEnergy (double theta1, double theta2, double omega1, double omega2) {
        double m1 = this.dPendulum.getFirstSeg().getMass();
        double m2 = this.dPendulum.getSecondSeg().getMass();
        double l1 = this.dPendulum.getFirstSeg().getLength();
        double l2 = this.dPendulum.getSecondSeg().getLength();
        return (
               m1 * l1 * l1 * omega1 * omega1 / 6
                       + m2 * l1 * l1 * omega1 * omega1 / 2
                        + m2 * l2 * l2 * omega2 * omega2 / 6
                        + m2 * l1 * l2 * omega1 * omega2 * Math.cos(theta1 - theta2) / 2
        ) + (
                - m1 * App.G * l1 * Math.cos(theta1) / 2
                        - m2 * App.G * l1 * Math.cos(theta1)
                        - m2 * App.G * l2 * Math.cos(theta2) / 2
        );
    }

    double omega1Derivative(double theta1, double theta2, double omega1, double omega2) {
        double m1 = this.dPendulum.getFirstSeg().getMass();
        double m2 = this.dPendulum.getSecondSeg().getMass();
        double l1 = this.dPendulum.getFirstSeg().getLength();
        double l2 = this.dPendulum.getSecondSeg().getLength();
        return (
                9 * omega1 * omega1 * l1 * m2 * Math.sin(2 * theta1 - 2 * theta2)
                        + 12 * m2 * l2 * omega2 * omega2 * Math.sin(theta1 - theta2)
                        + 12 * (3 * Math.sin(- 2 * theta2 + theta1) * m2 / 4 + Math.sin(theta1) * (m1 + 5 * m2 / 4))
                        * App.G

        )
                / (
                l1 * (9 * m2 * Math.cos(2 * theta1 - 2 * theta2) - 8 * m1 - 15 * m2)
        );
    }

    double omega2Derivative(double theta1, double theta2, double omega1, double omega2) {
        double m1 = this.dPendulum.getFirstSeg().getMass();
        double m2 = this.dPendulum.getSecondSeg().getMass();
        double l1 = this.dPendulum.getFirstSeg().getLength();
        double l2 = this.dPendulum.getSecondSeg().getLength();
        return (
                - 9 * App.G * Math.sin(2 * theta1 - theta2) * (m1 + 2 * m2)
                        - 9 * m2 * l2 * omega2 * omega2 * Math.sin(2 * theta1 - 2 * theta2)
                        - 12 * l1 * omega1 * omega1 * (m1 + 3 * m2) * Math.sin(theta1 - theta2)
                        + 3 * App.G * Math.sin(theta2) * (m1 + 6 * m2)
        )
                / (
                l2 * (9 * m2 * Math.cos(2 * theta1 - 2 * theta2) - 8 * m1 - 15 * m2)
        );
    }

    double theta1Derivative(double omega1) {
        return omega1;
    }

    double theta2Derivative(double omega2) {
        return omega2;
    }
}
