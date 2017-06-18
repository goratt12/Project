package communication;

public class Protocol {
    public static final int INT_SIZE = 4;

    //to server

    public static final char DRIVER = '0';
    public static final char CLIENT = '1';
    public static final char CONTROL = '2';

    public static final char REGISTER = '1';
    public static final char LOGIN = '2';


    public static final char ASK_DELIVERY = '3';
    public static final char CANCEL_DELIVERY = '4';



    //from server

    public static final int CONFIRM = 1;
    public static final int REGISTER_CONFIRM = 1;
    public static final int DELIVERY_ASK_CONFIRM = 2;
}
