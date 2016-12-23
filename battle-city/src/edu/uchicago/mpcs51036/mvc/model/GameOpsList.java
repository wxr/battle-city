package edu.uchicago.mpcs51036.mvc.model;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public class GameOpsList extends LinkedList<CollisionOp> {

    private final ReentrantLock lock;

    public GameOpsList() {
        this.lock = new ReentrantLock();
    }

    public void enqueue(Movable movable, CollisionOp.Operation operation) {
        lock.lock();
        try {
            addLast(new CollisionOp(movable, operation));
        } finally {
            lock.unlock();
        }
    }

    public CollisionOp dequeue() {
        lock.lock();
        try {
            return removeFirst();
        } finally {
            lock.unlock();
        }
    }

}


