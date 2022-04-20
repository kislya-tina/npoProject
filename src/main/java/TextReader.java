import java.io.*;
import java.util.ArrayList;


public class TextReader {
    public static void main( String[] args ) throws IOException {
        File file = new File("ServerSocket1.txt");
        receiveBytes(file);
        sendBytes(file);
        findFirstSuccessTrade(file);//1:07:57.700
        findLastSuccessTrade(file);
        findDowntime(file);//1:0:54.81 or  0:59:43.81 ???
    }

    /*todo
1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22
0 3 . 0 7 . 2 0 1 6  "  9  :  4  4  :  1  2  .  1  4  1

 int hour = Integer.parseInt(time.substring(0, 1));
 int  minute = Integer.parseInt(time.substring(2, 4));
 int sec = Integer.parseInt(time.substring(5, 7));
 int millsec = Integer.parseInt(time.substring(8, 11));

    ClientSocket (RemoteAddress=213.87.127.179, RemotePort=51265):
    State=ssWaitAnswer
    */


    public static void findDowntime(File file){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            ArrayList<String> list = new ArrayList<>();
            String line = reader.readLine();
            String time = "tr";
            int hourStart = 0, minuteStart = 0, secStart = 0, milsecStart = 0;
            int hourEnd = 0, minuteEnd = 0, secEnd = 0, milsecEnd = 0;
            int hourSum = 0, minuteSum = 0, secSum = 0, milsecSum = 0;
            int i = 0;
            int counter = 0;
            boolean cancelCounter = false;
            while (line != null) {
                list.add(i, line);
                if(line.contains("State=ssWaitAnswer")){
                    if(list.get(i-2).charAt(12) == ':') {//до 10 часов утра
                        time = list.get(i - 2).substring(11).trim();
                        hourStart = Integer.parseInt(time.substring(0, 1));
                        minuteStart = Integer.parseInt(time.substring(2, 4));
                        secStart = Integer.parseInt(time.substring(5, 7));
                        milsecStart = Integer.parseInt(time.substring(8, 11));
//                        System.out.println("Start time: "+hourStart+":"+minuteStart+":"+secStart+"."+milsecStart);
                        counter++;
                    }else{// после 10
                        time = list.get(i - 2).substring(11).trim();
                        hourStart = Integer.parseInt(time.substring(0, 2));
                        minuteStart = Integer.parseInt(time.substring(3, 5));
                        secStart = Integer.parseInt(time.substring(6, 8));
                        milsecStart = Integer.parseInt(time.substring(9, 12));
//                        System.out.println("Start time: "+hourStart+":"+minuteStart+":"+secStart+"."+milsecStart);
                        counter++;
                    }
                }
                if(line.contains("State=ssIdle") && !cancelCounter){
                    cancelCounter = true;
                    line = reader.readLine();
                    i++;
                    continue;
                }
                if(line.contains("State=ssIdle") && cancelCounter){
                    if(list.get(i-2).charAt(12) == ':') {//до 10 часов утра
                        time = list.get(i - 2).substring(11).trim();
                        hourEnd = Integer.parseInt(time.substring(0, 1));
                        minuteEnd = Integer.parseInt(time.substring(2, 4));
                        secEnd = Integer.parseInt(time.substring(5, 7));
                        milsecEnd = Integer.parseInt(time.substring(8, 11));
//                        System.out.println("End time: "+hourEnd+":"+minuteEnd+":"+secEnd+"."+milsecEnd);
                        counter++;
                    }else{// после 10
                        time = list.get(i - 2).substring(11).trim();
                        hourEnd = Integer.parseInt(time.substring(0, 2));
                        minuteEnd = Integer.parseInt(time.substring(3, 5));
                        secEnd = Integer.parseInt(time.substring(6, 8));
                        milsecEnd = Integer.parseInt(time.substring(9, 12));
//                        System.out.println("End time: "+hourEnd+":"+minuteEnd+":"+secEnd+"."+milsecEnd);
                        counter++;
                    }
                }
                if(counter % 2 == 0 && line.contains("State=ssIdle")){
//                    if(milsecEnd - milsecStart < 0){
//                        milsecEnd = milsecEnd - milsecStart + 1000;
//                    }else {
//                        milsecEnd -= milsecStart;
//                    }
                    if( secEnd > secStart && milsecEnd > milsecStart){
                        secEnd -= secStart;
                        milsecEnd -= milsecStart;
                    }else{
                        if(secEnd > secStart && milsecEnd < milsecStart) {
                            secEnd -= secStart + 1;
                            milsecEnd = milsecEnd - milsecStart + 1000;
                        }else{
                            secEnd = 0;
                            milsecEnd -= milsecStart;
                        }
                    }

                    hourEnd -= hourStart;

                    if(minuteEnd - minuteStart < 0){
                        minuteEnd -=minuteStart - 60;
                    }else {
                        minuteEnd -= minuteStart;
                    }
//                    if(secEnd - secStart < 0){
//                        secEnd = secEnd - secStart + 60;
//                    }else {
//                        secEnd -= secStart ;
//                    }

                    //System.out.println("Downtime: "+hourEnd+":"+minuteEnd+":"+secEnd+"."+milsecEnd+"\n");
                    milsecSum += milsecEnd;
                }
                line = reader.readLine();
                i++;
            }
            hourSum = milsecSum / 3600000;
            minuteSum = (milsecSum / 60000) % 60;
            secSum = (milsecSum / 1000) % 60;
            milsecSum = milsecSum % 1000;
            System.out.println("4::Суммарный интервал простоя: "+hourSum+":"+minuteSum+":"+secSum+"."+milsecSum);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void findFirstSuccessTrade(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        ArrayList<String> list = new ArrayList<>();
        String line = reader.readLine();
        int i = 0;
        while (line != null) {
            list.add(i, line);
            if(line.contains("Socket->ReceiveBuf() ")){
                String time = list.get(i-2).substring(11).trim();
                System.out.print("5::Время первого успешного обмена: " + time + "\n");
                break;
            }
            line = reader.readLine();
            i++;
        }
        reader.close();
    }

    public static void findLastSuccessTrade(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        ArrayList<String> list = new ArrayList<>();
        String line = reader.readLine();
        int i = 0;
        String time = "tr";
        while (line != null) {
            list.add(i, line);
            if(line.contains("Socket->ReceiveBuf() ")){
                time = list.get(i-2).substring(11).trim();
            }
            line = reader.readLine();
            i++;
        }
        System.out.println("6::Время последнего успешного обмена: "+time);
        reader.close();
    }

    public static void receiveBytes(File file){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            int count = 0;
            int countSuccess = 0;
            while (line != null) {
                Filters filt = new Filters(line, "Socket->ReceiveBuf() ");
                if(filt.isContainsTarget()) {
                    count += Integer.parseInt(line.substring(31, 34).trim());
                    countSuccess ++;
                }
                line = reader.readLine();
            }
            System.out.print("1::Количество циклов успешных обменов: "+countSuccess+ "\n"+
                    "3::Колечиство байт на прием: "+count+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendBytes(File file){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            int count = 0;
            while (line != null) {
                Filters filt = new Filters(line, "Передача ");
                if(filt.isContainsTarget()) {
                    Filters filt1 = new Filters(line.substring(9, 12));
                    count += Integer.parseInt(line.substring(9, 12).trim());
                }
                line = reader.readLine();
            }
            System.out.print("2::Колечиство байт на передачу: "+count+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}