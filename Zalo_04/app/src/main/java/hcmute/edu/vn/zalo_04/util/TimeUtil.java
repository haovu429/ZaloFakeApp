package hcmute.edu.vn.zalo_04.util;

// Java program to find the
// difference between two dates

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.Date;

public class TimeUtil {

    // Function to print difference in
    // time start_date and end_date

    // SimpleDateFormat converts the
    // string format to date object

    public static String getTimeNow(){
        SimpleDateFormat sdf
                = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");
        //khai báo đối tượng current thuộc class LocalDateTime
        LocalDateTime current = LocalDateTime.now();
        //sử dụng class DateTimeFormatter để định dạng ngày giờ theo kiểu pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        //sử dụng phương thức format() để định dạng ngày giờ hiện tại rồi gán cho chuỗi formatted
        String formatted = current.format(formatter);
        //hiển thị chuỗi formatted ra màn hình
        System.out.println("\n\nNgày giờ hiện tại: " + formatted);
        return formatted;
    }


    public static long findDifferenceDay(String start_date, String end_date){
        SimpleDateFormat sdf
                = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            // Calucalte time difference
            // in milliseconds
            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            // Calucalte time difference in seconds,
            // minutes, hours, years, and days

            long difference_In_Days
                    = TimeUnit
                    .MILLISECONDS
                    .toDays(difference_In_Time)
                    % 365;
            return difference_In_Days;

        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static void findDifference(String start_date,
                               String end_date)
    {
        SimpleDateFormat sdf
                = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");

        // Try Class
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            // Calucalte time difference
            // in milliseconds
            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            // Calucalte time difference in seconds,
            // minutes, hours, years, and days
            long difference_In_Seconds
                    = TimeUnit.MILLISECONDS
                    .toSeconds(difference_In_Time)
                    % 60;

            long difference_In_Minutes
                    = TimeUnit
                    .MILLISECONDS
                    .toMinutes(difference_In_Time)
                    % 60;

            long difference_In_Hours
                    = TimeUnit
                    .MILLISECONDS
                    .toHours(difference_In_Time)
                    % 24;

            long difference_In_Days
                    = TimeUnit
                    .MILLISECONDS
                    .toDays(difference_In_Time)
                    % 365;

            long difference_In_Years
                    = TimeUnit
                    .MILLISECONDS
                    .toDays(difference_In_Time)
                    / 365l;

            // Print the date difference in
            // years, in days, in hours, in
            // minutes, and in seconds
            System.out.print(
                    "Difference"
                            + " between two dates is: ");

            // Print result
            System.out.println(
                    difference_In_Years
                            + " years, "
                            + difference_In_Days
                            + " days, "
                            + difference_In_Hours
                            + " hours, "
                            + difference_In_Minutes
                            + " minutes, "
                            + difference_In_Seconds
                            + " seconds");
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // Driver Code
    public static void main(String[] args)
    {
        // Given start_date
        String start_date
                = "10-01-2018 01:10:20";

        // Given end_date
        String end_date
                = "10-06-2020 06:30:50";

        // Function Call
        findDifference(start_date,
                end_date);
    }
}
