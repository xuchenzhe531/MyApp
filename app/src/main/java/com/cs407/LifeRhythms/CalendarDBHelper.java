package com.cs407.LifeRhythms;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
public class CalendarDBHelper {
    static SQLiteDatabase sqLiteDatabase;
    public CalendarDBHelper(SQLiteDatabase sqLiteDatabase) {
        CalendarDBHelper.sqLiteDatabase = sqLiteDatabase;
    }
    public void createTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS calendars " +
                "(id INTEGER PRIMARY KEY, username TEXT, date TEXT, title TEXT, desiredDate TEXT,StartTime TEXT,EndTime TEXT,todo TEXT,category TEXT, src TEXT)");
    }
    public ArrayList<ToDo> readCalendars(String username) {
        createTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * from calendars where username like ?", new String[]{"%" + username + "%" });
        int dateIndex = c.getColumnIndex("date");
        int titleIndex = c.getColumnIndex("title");
        int desiredDateIndex = c.getColumnIndex("desiredDate");
        int StartTimeIndex = c.getColumnIndex("StartTime");
        int EndTimeIndex = c.getColumnIndex("EndTime");
        int todoIndex = c.getColumnIndex("todo");
        int categoryIndex = c.getColumnIndex("category");
        c.moveToFirst();

        ArrayList<ToDo> calendarsList = new ArrayList<>();
        while (!c.isAfterLast()) {
            String title = c.getString(titleIndex);
            String date = c.getString(dateIndex);
            String desiredDate = c.getString(desiredDateIndex);
            String StartTime  = c.getString(StartTimeIndex);
            String EndTime = c.getString(EndTimeIndex);
            String todo = c.getString(todoIndex);
            String category = c.getString(categoryIndex);

            ToDo note = new ToDo(date, username, title, desiredDate,StartTime,EndTime,todo,category);
            calendarsList.add(note);
            c.moveToNext();
        }
        c.close();
        sqLiteDatabase.close();

        return calendarsList;
    }
    public void saveCalendars(String username, String date, String title, String desiredDate,String StartTime,String EndTime,String todo,String category) {
        createTable();
        sqLiteDatabase.execSQL("INSERT INTO calendars (username, date, title, desiredDate,StartTime,EndTime,todo,category) VALUES (?, ?, ?, ?,?,?,?,?)",
                new String[]{username, date, title, desiredDate,StartTime,EndTime,todo,category});
    }
    public void updateCalendar(String username, String date, String title, String desiredDate,String StartTime,String EndTime,String todo,String category) {
        createTable();
        sqLiteDatabase.execSQL("UPDATE calendars set desiredDate = ?,StartTime = ?, EndTime=?,todo=?,category=?,date = ? where title = ? and username = ?",
                new String[]{desiredDate,StartTime,EndTime,todo,category, date, title, username});
    }
    public void deleteCalendars( String desiredDate,String StartTime,String EndTime,String todo,String category, String title) {
        createTable();
        String date = "";
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT date FROM calendars WHERE desiredDate = ? AND StartTime = ? AND EndTime=?AND todo=?AND category=?",
                new String[]{desiredDate,StartTime,EndTime,todo,category});

        if (cursor.moveToNext()) {
            date = cursor.getString(0);
        }

        sqLiteDatabase.execSQL("DELETE FROM calendars WHERE desiredDate = ? AND StartTime = ? AND EndTime=?AND todo=?AND category=? AND date = ?",
                new String[]{desiredDate,StartTime,EndTime,todo,category, date});
        cursor.close();
    }
    public List<ScheduleItem> getScheduleForDate(String date, String username){
        List<ScheduleItem> scheduleItems = new ArrayList<>();
        String[] columns = {"desiredDate", "StartTime", "EndTime", "todo"};
        String selection = "desiredDate = ? AND username = ?";
        String[] selectionArgs = { date, username };
        Cursor cursor = sqLiteDatabase.query("calendars", columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String scheduleDate = cursor.getString(cursor.getColumnIndex("desiredDate"));
                @SuppressLint("Range") String startTime = cursor.getString(cursor.getColumnIndex("StartTime"));
                @SuppressLint("Range") String endTime = cursor.getString(cursor.getColumnIndex("EndTime"));
                @SuppressLint("Range") String item = cursor.getString(cursor.getColumnIndex("todo"));

                ScheduleItem scheduleItem = new ScheduleItem(scheduleDate,startTime,endTime,item);

                scheduleItems.add(scheduleItem);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return scheduleItems;


    }
}
