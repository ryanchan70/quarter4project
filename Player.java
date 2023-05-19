public class Player {
    private int x;
    private int y;
    private int width;
    private int height;

    public Player() {
        x = 80;
        width = 30;
        height = 30;
        y = 400-height;
    }

    public Player(int width, int height){
        this.width = width;
        this.height = height;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public boolean checkCollision(int otherX, int otherY, int otherWidth, int otherHeight) {
        if (x < otherX+otherWidth && x+width > otherX &&
                y < otherY+otherHeight && y+height > otherY) {
            return true;
        }
        return false;
    }
}
