public class DoublePendulum {
    private Pendulum firstSeg;
    private Pendulum secondSeg;

    public DoublePendulum() {
        this.firstSeg = new Pendulum();
        this.secondSeg = connectSeg(new Pendulum());
    }

    public DoublePendulum(Pendulum p1, Pendulum p2) {
        this.firstSeg = p1;
        this.secondSeg = connectSeg(p2);
    }

    public Pendulum getFirstSeg() {
        return firstSeg;
    }

    public Pendulum getSecondSeg() {
        return secondSeg;
    }

    public Pendulum connectSeg (Pendulum pendulum) {
        if(this.firstSeg != null) {
            pendulum.setFixPoint(this.firstSeg.getEndPoint());
        }
        return pendulum;
    }

    public void initiate(double theta1, double theta2) {
        this.firstSeg.setTheta(theta1);
        this.secondSeg.setTheta(theta2);
    }

    void render() {
        this.firstSeg.render();
        this.secondSeg.render();
    }
}
