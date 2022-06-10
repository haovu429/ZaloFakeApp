package hcmute.edu.vn.zalo_04.util;

// Java program to find the
// difference between two dates

import android.icu.util.Calendar;

import java.sql.Time;
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
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
            /*if (difference_In_Time < 0){
                return -difference_In_Days;
            }*/
            return difference_In_Days;

        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public void findDifference(String start_date,
                               String end_date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

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

    public boolean is_OutOfDate(String time, String timeline){
        if ( findDifferenceDay(time, timeline) > 0){
            return true;
        }
        return false;
    }

    public String up_downTime(String time, int date_num, int month_num, int year_num) {
        // Định dạng thời gian
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");


        Calendar c1 = Calendar.getInstance();
        //Calendar c2 = Calendar.getInstance();

        // Định nghĩa mốc thời gian ban đầu là ngày 31-07-2011

        Date date = new Date();
        try {
            date = dateFormat.parse(time);
        } catch (Exception e){
            e.printStackTrace();
        }


        c1.setTime(date);
        //c2.setTime(date);



        System.out.println("Ngày ban đầu : " + dateFormat.format(c1.getTime()));

        // Tăng ngày thêm 8 ngày -- Sử dụng phương thức roll()
        //c1.roll(Calendar.DATE, 8);

        //Các trường hợp khác
        /*c1.roll(Calendar.DATE, date_num);
        c1.roll(Calendar.MONTH, month_num);
        c1.roll(Calendar.YEAR, year_num) ;*/

        int day_now = Integer.parseInt(dayFormat.format(c1.getTime()));
        int month_now = Integer.parseInt(monthFormat.format(c1.getTime()));
        int year_now = Integer.parseInt(yearFormat.format(c1.getTime()));

        if (year_num >= year_now){
            return null;
        } else {
            c1.roll(Calendar.YEAR, -year_num);
            year_now = Integer.parseInt(yearFormat.format(c1.getTime()));
        }

        if (month_num > 12){
            return null;
        } else {
            if (month_num >= month_now){
                c1.roll(Calendar.YEAR, -1);
                year_now = Integer.parseInt(yearFormat.format(c1.getTime()));
                c1.roll(Calendar.MONTH, -month_num);
            } else {
                c1.roll(Calendar.MONTH, -month_num);
            }
            month_now = Integer.parseInt(monthFormat.format(c1.getTime()));
        }

        if (date_num > 31){
            return null;
        } else {
            if (date_num >= day_now){
                c1.roll(Calendar.MONTH, -1);
                month_now = Integer.parseInt(monthFormat.format(c1.getTime()));
                c1.roll(Calendar.DATE, -date_num);
            } else {
                c1.roll(Calendar.DATE, -date_num);
            }
            day_now = Integer.parseInt(dayFormat.format(c1.getTime()));
        }






        System.out.println("Ngày được tăng thêm day (Sử dụng Roll) : "+ dayFormat.format(c1.getTime()));
        System.out.println("Ngày được tăng thêm month (Sử dụng Roll) : "+ monthFormat.format(c1.getTime()));
        System.out.println("Ngày được tăng thêm year (Sử dụng Roll) : "+ yearFormat.format(c1.getTime()));
        System.out.println("Ngày được tăng thêm (Sử dụng Roll) : "+ dateFormat.format(c1.getTime()));

        // c1.roll(Calendar.DATE, -8); // Giảm ngày 8 ngày ==> 23-07-2011
        //System.out.println("Ngày được tăng thêm 8 ngày (Sử dụng Roll) : "+ dateFormat.format(c1.getTime()));


        /* Các trường hợp khác
        c1.roll(Calendar.DATE, true); //Tăng 1 ngày -- Nếu muốn giảm một ngày truyền vào false
        c1.roll(Calendar.MONTH, 2);   //Tăng lên 2 tháng
        c1.roll(Calendar.YEAR, 2) ;      //Tăng lên 2 năm
        */

        // Tăng ngày thêm 8 ngày -- Sử dụng phương thức add()
        //c2.add(Calendar.DATE, 8);
        //c2.add(Calendar.DATE, -8); // Giảm ngày 8 ngày ==> 23-07-2011
        //System.out.println("Ngày được tăng thêm 8 ngày (Sử dụng add)  : " + dateFormat.format(c2.getTime()));

        /* Các trường hợp khác :
        c2.add(Calendar.MONTH, 2);   //Tăng lên 2 tháng
        c2.add(Calendar.YEAR, 2) ;      //Tăng lên 2 năm
        */
        return dateFormat.format(c1.getTime());
    }

    private int dayOfMonth(int month, int year){
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                //System.out.println("Có 31 ngày");
                return 31;
                //break;
            case 4:
            case 6:
            case 9:
            case 11:
                //System.out.println("Có 30 ngày");
                return 30;
                //break;
            case 2:
                if((year % 4==0 && year %100 !=0)||(year %400==0)) {
                    //System.out.println("Có 29 ngày");
                    return 29;
                }else {
                    //System.out.println("Có 28 ngày");
                    return 28;
                }
                //break;
            default:
                //System.out.println("Nhập dữ liệu sai!");
                return -1;
                //break;
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
        //TimeUtil timeUtil = new TimeUtil();
        //timeUtil.findDifference(start_date, end_date);
    }
}
