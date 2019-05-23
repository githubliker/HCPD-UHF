package com.airhockey.android.listener;

import java.util.ArrayList;
import java.util.List;

public class EventManager implements Observable {
    private List<Observer> observers = new ArrayList<>();
    private float[] message;
    private float[] message2;

    private static class EventManagerHolder{
        private static final EventManager instance=new EventManager();
    }

    private EventManager(){}
    public static EventManager getInstance(){
        return EventManagerHolder.instance;
    }

    public void publishMessage(float[] message,float[] message2) {
        this.message = message;
        this.message2 = message2;
        notifyObservers();
    }
    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        int i = observers.indexOf(observer);
        if (i > 0) {
            observers.remove(i);
        }
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update(message,message2);
        }
    }
}
