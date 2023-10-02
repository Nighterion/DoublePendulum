public class Solver {
    private String type;
    private double dt;
    private double T;

    public String getType() {
        return type;
    }

    public double getDt() {
        return dt;
    }

    public double getT() {
        return T;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDt(double dt) {
        this.dt = dt;
    }

    public void setT(double t) {
        T = t;
    }
}
