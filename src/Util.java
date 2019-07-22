import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    public Util(){}

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

    public static String getNow()
    {
        Date dt = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(dt);
    }

    public static String getToday()
    {
        Date dt = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(dt);
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
