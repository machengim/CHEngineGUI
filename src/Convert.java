import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class Convert
{
    private static int flag_quote = 0;
    private static int flag_code = 0;

    public Convert(){}

    public static String[] readMeta(LinkedList<StringBuilder> mdLines)
    {
        String[] metas = new String[8];
        metas[7] = "n";
        int flag = 0;
        Iterator it = mdLines.iterator();
        while (flag < 2 && it.hasNext())
        {
            StringBuilder line = (StringBuilder) it.next();
            String s = "";
            if (line.indexOf("---") >= 0)
                flag++;
            if (line.indexOf(":") >= 0)
                s = line.substring(line.indexOf(":") + 1).trim();
            if (line.indexOf("id") >= 0)
                metas[0] = s;
            else if (line.indexOf("title") >= 0)
                metas[1] = s;
            else if (line.indexOf("cat") >= 0)
                metas[2] = s;
            else if (line.indexOf("tags") >= 0)  //need to be split to a list.
                metas[3] = s;
            else if (line.indexOf("date") >= 0)
                metas[4] = s;
            else if (line.indexOf("url") >= 0)
                metas[5] = s;
            else if (line.indexOf("author") >= 0)
                metas[6] = s;
            else if (line.indexOf("page") >= 0)
                metas[7] = "y";
        }
        return metas;
    }

    public static void dumpMeta(LinkedList<StringBuilder> mdLines)
    {
        int flag = 0;
        while (flag < 2)
        {
            StringBuilder line = mdLines.removeFirst();
            if (line.indexOf("---") >= 0)
                flag++;
        }
    }

    public static String toHtml(LinkedList<StringBuilder> mdLines)
    {
        StringBuilder result = new StringBuilder();
        for (StringBuilder sb: mdLines)
        {
            convertLine(sb);
            result.append(sb);
        }
        return result.toString();
    }

    private static void convertLine(StringBuilder sb)
    {
        if (isEmptyLine(sb))
        {
            sb.delete(0, sb.length());
            return;
        }
        handleHead(sb);
        handleBody(sb);
        handleTail(sb);
    }

    private static void handleHead(StringBuilder sb)
    {
        if (sb.charAt(0) == ' ')    //blank space should be handled at the very first time.
            getPre(sb);
        switch (sb.charAt(0))
        {
            case '#':
                getTitle(sb);   break;
            case '>':
                getQuote(sb);   break;
            case '+':
                getList(sb);    break;
            case '-':
            case '*':
                getHr(sb);      break;
            default:
                break;
        }
    }

    private static void handleBody(StringBuilder sb)
    {
        if (Pattern.matches(".*!\\[.*\\].*\\(.*\\).*\n+", sb))
            getImg(sb);
        if (Pattern.matches(".*\\[.*\\].*\\(.*\\).*\n+", sb))
            getUrl(sb);
        if (Pattern.matches(".*\\*\\*.*\\*\\*.*\n+", sb))
            getStrong(sb);
        if (Pattern.matches(".*\\*.*\\*.*\n+", sb))
            getEmCode(sb, "*");
        if (Pattern.matches(".*`.*`.*\n+", sb))
            getEmCode(sb, "`");
    }

    private static void handleTail(StringBuilder sb)
    {
        int count = timesAtTail(sb, '\n');
        if (sb.indexOf("<li>") >= 0)
            count--;
        sb.delete(sb.length() - count, sb.length());
        if (count == 1)
            sb.append("</br>");
        else if (count >= 2)
        {
            sb.insert(0, "<p>");
            sb.insert(sb.length(), "</p>");
        }
    }

    private static void getEmCode(StringBuilder sb, String s)
    {
        String head, end;
        if (s.equals("*"))
        {
            head = "<em>";  end = "</em>";
        }
        else
        {
            head = "<code>";    end = "</code>";
        }
        int p = sb.indexOf(s);
        sb.replace(p, p + 1, head);
        p = sb.indexOf(s);
        sb.replace(p, p + 1, end);
    }

    private static void getHr(StringBuilder sb)
    {
        int count = timesAtHead(sb, '-');
        if (count == 0)
            count = timesAtHead(sb, '*');
        if (count < 3)
            return;
        sb.delete(0, count);
        sb.insert(0, "<hr>");
    }

    private static void getImg(StringBuilder sb)
    {
        int p = sb.indexOf("(");
        sb.replace(0, p + 1, "<img src=\"");
        p = sb.indexOf(")");
        sb.replace(p, p + 1, "\">");
    }

    private static void getList(StringBuilder sb)
    {
        int count = timesAtTail(sb, '\n');
        sb.deleteCharAt(0);
        sb.insert(0, "<li>");
        sb.insert(sb.length() - count, "</li>");
    }

    private static void getPre(StringBuilder sb)
    {
        int count = timesAtHead(sb, ' ');
        if (count < 3)
        {
            sb.delete(0, count);
            return;
        }
        count = timesAtTail(sb, '\n');
        if (flag_code == 0)
        {
            flag_code = 1;
            sb.insert(0, "<pre>");
        }
        if (flag_code == 1 && timesAtTail(sb, '\n') >= 2)
        {
            sb.insert(sb.length() - count, "</pre>");
            flag_code = 0;
        }
        /*
        while (count-- > 1)
            sb.deleteCharAt(sb.length() - 1); */
    }

    private static void getQuote(StringBuilder sb)
    {
        sb.deleteCharAt(0);
        if (flag_quote == 0)
        {
            flag_quote = 1;
            sb.insert(0, "<blockquote>");
        }
        if (flag_quote == 1 && timesAtTail(sb, '\n') >= 2)
        {
            sb.insert(sb.length() - 2, "</blockquote>");
            flag_quote = 0;
        }
        sb.deleteCharAt(sb.length() - 1);
    }

    private static void getStrong(StringBuilder sb)
    {
        int p = sb.indexOf("**");
        sb.replace(p, p + 2, "<strong>");
        p = sb.indexOf("**");
        sb.replace(p, p + 2, "</strong>");
    }

    private static void getTitle(StringBuilder sb)
    {
        int count = 0;
        while (sb.length() > 0 && sb.charAt(0) == '#')
        {
            count++;
            sb.deleteCharAt(0);
        }
        String s1 = "<h" + count + ">";
        String s2 = "</h" + count + ">";
        sb.insert(0, s1);
        sb.insert(sb.length() - 2, s2);
    }

    private static void getUrl(StringBuilder sb)
    {
        int p1 = sb.indexOf("[");
        int p2 = sb.indexOf("]");
        String text = sb.substring(p1 + 1, p2);
        sb.delete(p1, p2 + 1);
        p1 = sb.indexOf("(");
        p2 = sb.indexOf(")");
        String url = sb.substring(p1 + 1, p2);
        if (text.trim().length() == 0)
            text = url;
        int extern = 0;
        if (url.contains("http"))
            extern = 1;
        String newstr;
        if (extern == 0)
            newstr = "<a href=\"" + url + "\">" + text + "</a>";
        else
            newstr = "<a href=\"" + url + "\" target=\"_blank\">" + text + "</a>";
        sb.replace(0, sb.length() -1, newstr);
    }

    public static boolean isEmptyLine(StringBuilder sb)
    {
        for (int i = 0; i < sb.length(); i++)
        {
            if (sb.charAt(i) != ' ' && sb.charAt(i) != '\n')
                return false;
        }
        return true;
    }

    //need modification, only one display limit applies.
    public static String getBrief(String text, int limit)
    {
        limit = (text.length() > limit)? limit: text.length();
        String brief = text.substring(0, limit);
        int i, j;
        if ((i = brief.indexOf("<!")) >= 0)
            return brief.substring(0, i);
        else
        {
            i = brief.lastIndexOf("<");
            j = brief.lastIndexOf(">");
            if (i != -1 && i > j)
                brief = brief.substring(0, i);
            return brief + "...";
        }
    }

    private static int timesAtHead(StringBuilder sb, char c)
    {
        int count = 0;
        while (sb.charAt(count) == c)
            count++;
        return count;
    }

    private static int timesAtTail(StringBuilder sb, char c)
    {
        int count = sb.length() - 1;
        while (count >= 0 && sb.charAt(count) == c)
            count--;
        return sb.length() - 1 - count;
    }
}