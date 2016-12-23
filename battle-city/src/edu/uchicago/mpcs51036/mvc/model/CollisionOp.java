package edu.uchicago.mpcs51036.mvc.model;

public class CollisionOp {

    public enum Operation {
        ADD, REMOVE
    }

    private Movable mMovable;
    private Operation mOperation;

    public CollisionOp(Movable movable, Operation operation) {
        mMovable = movable;
        mOperation = operation;
    }

    public Movable getMovable() {
        return mMovable;
    }

    public Operation getOperation() {
        return mOperation;
    }

}


