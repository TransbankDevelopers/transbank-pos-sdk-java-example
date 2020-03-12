package cl.transbank.possdk.example;

public abstract class BusinessRunnable implements Runnable {

    Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public abstract void run();

    public abstract void updateInterface();

}
