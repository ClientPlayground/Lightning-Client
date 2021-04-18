package clientname.event.impl;

import clientname.event.*;

public class KeyEvent extends EventCancelable
{
    private final int key;
    
    public KeyEvent(final int key) {
        this.key = key;
    }
    
    public int getKey() {
        return this.key;
    }
}
