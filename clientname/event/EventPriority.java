package clientname.event;

public class EventPriority
{
    public static final byte FIRST = 0;
    public static final byte SECOND = 1;
    public static final byte THIRD = 2;
    public static final byte FOURTH = 3;
    public static final byte FIRTH = 4;
    public static final byte[] VALUE_ARRAY;
    
    static {
        VALUE_ARRAY = new byte[] { 0, 1, 2, 3, 4 };
    }
}
