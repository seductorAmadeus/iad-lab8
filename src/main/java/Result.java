public class Result {
    private float x;
    private float y;
    private float r;
    private boolean inside;

    public Result() {
    }

    public Result(float x, float y, float r, boolean inside) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.inside = inside;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getR() {
        return this.r;
    }

    public void setR(float r) {
        this.r = r;
    }

    public boolean isInside() {
        return this.inside;
    }

    public void setInside(boolean inside) {
        this.inside = inside;
    }

    public String toString() {
        return this.x + ", " + this.y + ", " + this.r + ": " + this.inside;
    }
}
