public class Problem {
    private String title;
    private Segment[] segments;
    private Solver solver;

    public String getTitle() {
        return title;
    }

    public Segment[] getSegments() {
        return segments;
    }

    public Solver getSolver() {
        return solver;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSegments(Segment[] segments) {
        this.segments = segments;
    }

    public void setSolver(Solver solver) {
        this.solver = solver;
    }
}
