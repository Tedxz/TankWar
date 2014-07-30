package xz.tankwar.module.tankgenerator;

import java.io.Serializable;
import java.util.*;

import xz.tankwar.component.tank.*;

public abstract class AbstractWaveTankGenerator implements Serializable {
    
    protected List<ComTank> tankList = new ArrayList<ComTank>(); 
    protected int pointer = 0;
    
    protected AbstractWaveTankGenerator() {}
    public AbstractWaveTankGenerator(int difficulty) {}
    
    public boolean hasNext() {
        return pointer < tankList.size();
    }
    
    public ComTank next() {
        return tankList.get(pointer++);
    }
    
}
