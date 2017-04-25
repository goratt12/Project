package communication;

public class Protocol {
    public static final int INT_SIZE = 4;

    public static final char DRIVER = '0';
    public static final char CLIENT = '1';
    public static final char CONTROL = '2';

    public static final int REGISTER = 1;
    public static final int LOGIN = 2;

    public static final int ASK_DELIVERY = 3;
    public static final int CANCEL_DELIVERY = 4;
}
