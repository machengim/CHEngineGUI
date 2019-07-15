import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    public Util(){}

    public static int getCat(String s)
    {
        if (s.length() == 0)
            return 1;
        int read = 1;
        try
        {
            read = Integer.parseInt(s);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        int cat = (read == 2)? 2: 1;
        return cat;
    }

    public static String getToday()
    {
        Date dt = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(dt);
    }

    public static String getNow()
    {
        Date dt = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(dt);
    }

    public static String formatDate(String s)
    {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
                            "Oct", "Nov", "Dec"};
        int mm = Integer.parseInt(s.substring(4, 6));
        int dd = Integer.parseInt(s.substring(6));
        String month = months[mm - 1];
        String date = month + " " + dd + ", " + s.substring(0, 4);
        return date;
    }

    public static String simpleDate(String s)
    {
        return s.substring(0,4) + "-" + s.substring(4, 6) + "-" + s.substring(6);
    }

    public static int StrToInt(String s)
    {
        int i = -1;
        try{
            i = Integer.parseInt(s);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        return i;
    }

}
