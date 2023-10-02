import java.awt.*;
public class Pendulum {
    private Dot fixPoint;
    private double mass;
    private double length;
    private double theta;
    private double omega;

    private Color color;

    //Constructors
    public Pendulum() {
        this.fixPoint = new Dot();
        this.mass = 1;
        this.length = 1;
        this.theta = 0;
        this.omega = 0;
        this.color = new Color (210, 10, 10);
    }

    public Pendulum (double mass, double length, double theta, double omega) {
        this.mass = mass;
        this.length = length;
        this.theta = theta;
        this.omega = omega;
        this.color = new Color(210, 10, 10);
    }

    public Pendulum (Dot fixPoint, double mass, double length, double theta, double omega) {
        this(mass, length, theta, omega);
        this.fixPoint = fixPoint;
    }

    //Setters


    public void setFixPoint(Dot fixPoint) {
        this.fixPoint = fixPoint;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public void setOmega(double omega) {
        this.omega = omega;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    //Getters


    public Dot getFixPoint() {
        return fixPoint;
    }

    public double getMass() {
        return mass;
    }

    public double getLength() {
        return length;
    }

    public double getTheta() {
        return theta;
    }

    public double getOmega() {
        return omega;
    }

    public Dot getEndPoint() {
        return new Dot(this.fixPoint.getX() + this.length*Math.sin(this.theta), this.fixPoint.getY() + this.length*Math.cos(this.theta));
    }

    public void render() {
        Dot endPoint = getEndPoint();

        App.processingRef.stroke(this.color.getRGB());
        App.processingRef.strokeWeight(3);
        App.processingRef.line((float)this.fixPoint.getX(), (float)this.fixPoint.getY(), (float)endPoint.getX(), (float)endPoint.getY());
        App.processingRef.stroke(Color.CYAN.getRGB());
        App.processingRef.circle((float)this.fixPoint.getX(), (float)this.fixPoint.getY(), 5);
        App.processingRef.stroke(Color.ORANGE.getRGB());
        App.processingRef.circle((float)endPoint.getX(), (float)endPoint.getY(), 5);
    }


}
