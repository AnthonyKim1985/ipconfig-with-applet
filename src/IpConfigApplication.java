import java.applet.Applet;
import java.applet.AppletContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpConfigApplication extends Applet {
    private AppletContext appletContext;
    private static Process process;

    @Override
    public void init() {
        appletContext = getAppletContext();
        showStatus("Javascript from java");

        try {
            process = Runtime.getRuntime().exec("C:\\Windows\\System32\\ipconfig /all");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "MS949"))) {
            final StringBuilder sb = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null)
                sb.append(String.format("%s", new String(line.getBytes(), "UTF-8")));

            final Pattern pattern = Pattern.compile("[0-9A-Fa-f][0-9A-Fa-f]-[0-9A-Fa-f][0-9A-Fa-f]-[0-9A-Fa-f][0-9A-Fa-f]-[0-9A-Fa-f][0-9A-Fa-f]-[0-9A-Fa-f][0-9A-Fa-f]-[0-9A-Fa-f][0-9A-Fa-f]");
            final Matcher matcher = pattern.matcher(sb.toString());
            final StringBuilder macAddressBuilder = new StringBuilder();

            final Set<String> macAddressSet = new HashSet<>();
            while (matcher.find())
                macAddressSet.add(matcher.group(0));

            final List<String> macAddressList = new ArrayList<>(macAddressSet);
            Collections.sort(macAddressList);

            for (String macAddress : macAddressList)
                macAddressBuilder.append(String.format("%s,", macAddress));
            macAddressBuilder.deleteCharAt(macAddressBuilder.toString().length() - 1);

//            System.out.println(macAddressBuilder);
            appletContext.showDocument(new URL("javascript:deployIpConfigApplication('" + macAddressBuilder + "');"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}