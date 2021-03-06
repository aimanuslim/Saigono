package com.theunheard.habittracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by aimanmduslim on 2/2/17.
 */

public class DBHandler extends SQLiteOpenHelper {
    private static DBHandler sInstance;
    private static Context myContext;
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "habits.db";
    public static final String TABLE_HABITS="habits";
    public static final String TABLE_PIT="persons"; // person interacted

    public static final String COL_ID = "_id";
    public static final String COL_OWNID = "_ownerid";
//    public static final String COL_HASH = "hash";
    public static final String COL_NAME = "habitname";
    public static final String COL_DATELP = "dateLastPerformed";
    public static final String COL_CATEGORY = "category";
    public static final String COL_FREQUENCY = "frequency";
    public static final String COL_PERIOD = "period";
    public static final String COL_MULTIPLIER = "multiplier";
    public static final String COL_ALARMID = "alarmID";


    public static final String COL_PITID = "_pitid";
    public static final String COL_PITNAME = "personInteracted";
    public static final String COL_HABITID= "habit";

    private static ArrayList<Integer> requestIDList;

//    public static synchronized DBHandler getInstance(Context context) {
//
//        // Use the application context, which will ensure that you
//        // don't accidentally leak an Activity's context.
//        // See this article for more information: http://bit.ly/6LRzfx
//        if (sInstance == null) {
//            sInstance = new DBHandler(context.getApplicationContext());
//        }
//        return sInstance;
//    }

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = context;
        requestIDList = new ArrayList<Integer>();
    }

    public void addRequestId(int rID){
        requestIDList.add((Integer) new Integer(rID));
    }

    public boolean requestIdExists(int rID) {
        return requestIDList.contains((Integer) new Integer(rID));
    }

    public void removeRequestId(int rID) {
        requestIDList.remove((Integer) new Integer(rID));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryHabits = "CREATE TABLE " + TABLE_HABITS+ "(" +

                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_OWNID + " INTEGER, " +
                COL_NAME + " TEXT, " +
                COL_DATELP + " INTEGER, " + // date has to be stored as an integer
                COL_CATEGORY + " TEXT, " +
                COL_FREQUENCY + " INTEGER, " +
                COL_PERIOD + " INTEGER, " +
                COL_MULTIPLIER + " INTEGER, " +
                COL_ALARMID + " INTEGER " +


                ");";

        String queryPersonsInteracted = "CREATE TABLE " + TABLE_PIT + "(" +

                COL_PITID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_HABITID + " INTEGER," +
                COL_PITNAME + " TEXT " +
                ");";

        db.execSQL(queryHabits);
        db.execSQL(queryPersonsInteracted);





    }

    public void refreshDB () {
        deleteTables();
        createTable();

    }


    public void createTable() {
        SQLiteDatabase db = getWritableDatabase();
        String queryHabits = "CREATE TABLE " + TABLE_HABITS+ "(" +

                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_OWNID + " INTEGER, " +
                COL_NAME + " TEXT, " +
                COL_DATELP + " INTEGER, " + // date has to be stored as an integer
                COL_CATEGORY + " TEXT, " +
                COL_FREQUENCY + " INTEGER, " +
                COL_PERIOD + " TEXT, " +
                COL_MULTIPLIER + " INTEGER, " +
                COL_ALARMID + " INTEGER " +


                ");";

        String queryPersonsInteracted = "CREATE TABLE " + TABLE_PIT + "(" +

                COL_PITID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_HABITID + " INTEGER," +
                COL_PITNAME + " TEXT " +
                ");";

        db.execSQL(queryHabits);
        db.execSQL(queryPersonsInteracted);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS"+TABLE_HABITS);
        db.execSQL("DROP TABLE IF EXISTS"+TABLE_PIT);
        onCreate(db);
    }

    public ArrayList<String> getNames(String colName) {
        ArrayList<String> names = new ArrayList<String>();
        SQLiteDatabase db= getReadableDatabase();
        String query = "SELECT " + colName + " FROM " + TABLE_HABITS;
        Cursor c = db.rawQuery(query ,null);
        c.moveToFirst();
        while(!c.isAfterLast()) {
            if(c.getString(c.getColumnIndex(colName))!=null) {
                String name = c.getString(c.getColumnIndex(colName));
                names.add(name);
            }
            c.moveToNext();

        }
        db.close();
        return names;
    }

    public ArrayList<Habit> getAllHabits() {
        ArrayList<Habit> habitList = new ArrayList<Habit>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_HABITS;
        Cursor c = db.rawQuery(query ,null);
        c.moveToFirst();
        while(!c.isAfterLast()) {
            Habit habit = new Habit();

            habit.setId(c.getString(c.getColumnIndex(COL_ID)));

            if(c.getString(c.getColumnIndex(COL_NAME)) != null) {
                habit.setName(c.getString(c.getColumnIndex(COL_NAME)));
            }



            if(c.getString(c.getColumnIndex(COL_CATEGORY)) != null) {
                habit.setCategory(c.getString(c.getColumnIndex(COL_CATEGORY)));
            }

            try {
                habit.setDateLastPerformed(new Date(c.getLong(c.getColumnIndex(COL_DATELP))));
                if(c.getInt(c.getColumnIndex(COL_MULTIPLIER)) != 0) {
                    habit.setReminderPeriodProperties(c.getInt(c.getColumnIndex(COL_PERIOD)), c.getInt(c.getColumnIndex(COL_MULTIPLIER)));
                }
                habit.setFrequencyPerformed(c.getInt(c.getColumnIndex(COL_FREQUENCY)));
            } catch (Exception e) {
//                Log.d("Habit King", "Date doesn't exist for this row?");
            }

            try{
                habit.setAlarmId(c.getInt(c.getColumnIndex(COL_ALARMID)));
            } catch (Exception e) {
//                Log.d("Database", "column value doesnt exist for this data");
            }
            habitList.add(habit);








            c.moveToNext();

        }
        db.close();
        return habitList;

    }


    public ArrayList<String> getAllHabitNames() {
        ArrayList<String> habitNameList = new ArrayList<String>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_HABITS;
        Cursor c = db.rawQuery(query ,null);
        c.moveToFirst();
        while(!c.isAfterLast()) {
            habitNameList.add(c.getString(c.getColumnIndex(COL_NAME)));

            c.moveToNext();

        }
        db.close();
        return habitNameList;

    }

    public void increaseHabitFreq(String habitName, String category) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("UPDATE " + TABLE_HABITS + " SET " + COL_FREQUENCY + " = " + COL_FREQUENCY + " + 1 WHERE " + COL_NAME + " = '" + habitName +
        "' AND " + COL_CATEGORY + " = '" + category + "';");
        db.close();

    }

    public void updateTimeLastPerformed(Habit habit){
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        String filter = COL_ID +" = " +habit.getId();

        values.put(COL_DATELP, habit.getDateLastPerformed().getTime());
        db.update(TABLE_HABITS, values, filter, null);
        db.close();
    }

    public Habit getHabitByName(String name ) {
        SQLiteDatabase db = getReadableDatabase();
        Habit habit = new Habit();
        String query = "SELECT * FROM " + TABLE_HABITS;
        Cursor c = db.rawQuery(query ,null);
        c.moveToFirst();
        while(!c.isAfterLast()) {


            if(c.getString(c.getColumnIndex(COL_NAME)).equals(name)) {
                habit.setName(c.getString(c.getColumnIndex(COL_NAME)));
                habit.setId(c.getString(c.getColumnIndex(COL_ID)));
                if(c.getString(c.getColumnIndex(COL_CATEGORY)) != null) {
                    habit.setCategory(c.getString(c.getColumnIndex(COL_CATEGORY)));
                }

                try {
                    habit.setDateLastPerformed(new Date(c.getLong(c.getColumnIndex(COL_DATELP))));
                    if(c.getInt(c.getColumnIndex(COL_MULTIPLIER)) != 0) {
                        habit.setReminderPeriodProperties(c.getInt(c.getColumnIndex(COL_PERIOD)), c.getInt(c.getColumnIndex(COL_MULTIPLIER)));
                    }
                    habit.setFrequencyPerformed(c.getInt(c.getColumnIndex(COL_FREQUENCY)));
                    habit.setAlarmId(c.getInt(c.getColumnIndex(COL_ALARMID)));

                } catch (Exception e) {
//                    Log.d("Habit King", "Date doesn't exist for this row?");
                }

                db.close();
                return habit;
            }

            c.moveToNext();

        }

        db.close();
        return null;



    }


    public ArrayList<String> getAllPersonNames() {
        ArrayList<String> personNameList = new ArrayList<String>();




        SQLiteDatabase db = getReadableDatabase();


        String query = "SELECT * FROM " + TABLE_PIT;
        Cursor c = db.rawQuery(query ,null);
        Cursor habitCursor;
        c.moveToFirst();
        while(!c.isAfterLast()) {
            personNameList.add(c.getString(c.getColumnIndex(COL_PITNAME)));
            c.moveToNext();
        }
        db.close();
        return personNameList;
    }

    public ArrayList<Person> getAllPerson() {
        ArrayList<Person> personsList = new ArrayList<Person>();




        SQLiteDatabase db = getReadableDatabase();


        String query = "SELECT * FROM " + TABLE_PIT;
        Cursor c = db.rawQuery(query ,null);
        Cursor habitCursor;
        c.moveToFirst();
        while(!c.isAfterLast()) {
            Person person = new Person();

            person.setId(c.getString(c.getColumnIndex(COL_PITID)));
            person.setName(c.getString(c.getColumnIndex(COL_PITNAME)));
            habitCursor = db.rawQuery("SELECT * FROM " + TABLE_HABITS + " WHERE " + COL_ID + " = " + c.getLong(c.getColumnIndex(COL_HABITID)), null);
            habitCursor.moveToFirst();
            try {
                person.setHabitName(habitCursor.getString(habitCursor.getColumnIndex(COL_NAME)));
                person.setLastDateInteractedWith(new Date(habitCursor.getLong(habitCursor.getColumnIndex(COL_DATELP))));
            } catch (Exception e) {
                person.setHabitName("None");
                person.setLastDateInteractedWith(new Date());
            }



            personsList.add(person);
            c.moveToNext();

        }
        db.close();
        return personsList;
    }

    public boolean habitExist(String habitName, String categoryName) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_HABITS;
        Cursor c = db.rawQuery(query ,null);
        c.moveToFirst();
        while(!c.isAfterLast()) {


            if(c.getString(c.getColumnIndex(COL_NAME)).trim().equals(habitName) && c.getString(c.getColumnIndex(COL_CATEGORY)).trim().equals(categoryName)) {
                db.close();
                return true;
            }

            c.moveToNext();

        }

        db.close();
        return false;

    }


    public void addHabit(Habit habit) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        values.put(COL_NAME, habit.getName());
        values.put(COL_OWNID, habit.getOwnerUid());
        values.put(COL_CATEGORY, habit.getCategory());
        values.put(COL_FREQUENCY, 1);
        values.put(COL_DATELP, habit.getDateLastPerformed().getTime());
        values.put(COL_PERIOD, habit.getReminderPerPeriodLengthMode());
        values.put(COL_MULTIPLIER, habit.getReminderPeriodMultiplier());
        values.put(COL_ALARMID, habit.getAlarmId());

//        db.execSQL("ALTER TABLE " + TABLE_HABITS + " ADD COLUMN " + COL_HASH + " TEXT" );
//        db.execSQL("ALTER TABLE " + TABLE_HABITS + " ADD COLUMN " + COL_PERIOD + " INTEGER" );
        // TODO: setting the habit's id here may not be the correct way of doing things, but have to figure out how.
        habit.setId(Long.toString(db.insert(TABLE_HABITS, null, values)));
        db.close();

//        addPersonInteracted(habit);
    }

    private boolean personExist(String personName) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PIT;
        Cursor c = db.rawQuery(query ,null);
        c.moveToFirst();
        while(!c.isAfterLast()) {
            if(c.getString(c.getColumnIndex(COL_PITNAME)).trim().equals(personName)) {
                db.close();
                return true;
            }
            c.moveToNext();
        }
        db.close();
        return false;
    }

    public void addPersonInteracted(ArrayList<String> personList, String habitId) {

        SQLiteDatabase db = getWritableDatabase();

        for(String pn: personList ) {
            ContentValues values = new ContentValues();
            values.put(COL_PITNAME, pn);
            if(personExist(pn)){
                String ID = getHabitIdFromPersonInteracted(pn);
                if(habitIsNewer(habitId, ID)){
                    values.put(COL_HABITID, Long.parseLong(habitId));
                }
                // reopen closed database
                db = getWritableDatabase();
                db.update(TABLE_PIT, values, COL_PITNAME+" = ?", new String[] {pn});
            } else {
                values.put(COL_HABITID, Long.parseLong(habitId));
                db = getWritableDatabase();
                db.insert(TABLE_PIT, null, values);
            }
        }
        db.close();
    }

    private String getHabitIdFromPersonInteracted(String pn) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PIT;
        Cursor c = db.rawQuery(query ,null);
        c.moveToFirst();
        while(!c.isAfterLast()) {
            if(c.getString(c.getColumnIndex(COL_PITNAME)).trim().equals(pn)) {
                db.close();
                return String.valueOf(c.getLong(c.getColumnIndex(COL_HABITID)));
            }
            c.moveToNext();
        }
        db.close();
        return null;
    }

    private boolean habitIsNewer(String inputHabitId, String checkHabitId) {
        SQLiteDatabase db = getReadableDatabase();
        Integer inputHabitIntegerDate = null;
        Integer checkHabitIntegerDate = null;
        String query = "SELECT * FROM " + TABLE_HABITS;
        Cursor c = db.rawQuery(query ,null);
        c.moveToFirst();
        while(!c.isAfterLast()) {


            if(c.getInt(c.getColumnIndex(COL_ID)) == Integer.parseInt(inputHabitId)) {
                inputHabitIntegerDate = c.getInt(c.getColumnIndex(COL_DATELP));
            }

            if(c.getInt(c.getColumnIndex(COL_ID)) == Integer.parseInt(checkHabitId)) {
                checkHabitIntegerDate = c.getInt(c.getColumnIndex(COL_DATELP));
            }

            c.moveToNext();

        }
        db.close();

        if(inputHabitIntegerDate != null && checkHabitIntegerDate != null){
            return inputHabitIntegerDate > checkHabitIntegerDate;
        }
        return false;
    }

    public void modifyHabit (Habit habit) {
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, habit.getName());
        cv.put(COL_CATEGORY, habit.getCategory());
        cv.put(COL_FREQUENCY, habit.getFrequencyPerformed());
        cv.put(COL_DATELP, habit.getDateLastPerformed().getTime());
        cv.put(COL_PERIOD, habit.getReminderPerPeriodLengthMode());
        cv.put(COL_MULTIPLIER, habit.getReminderPeriodMultiplier());
        cv.put(COL_ALARMID, habit.getAlarmId());



        String ID = habit.getId();
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_HABITS, cv, COL_ID+"="+ID, null);
        db.close();




    }

    public Integer getIdByHabitName(String habitName) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_HABITS;
        Cursor c = db.rawQuery(query ,null);
        c.moveToFirst();
        while(!c.isAfterLast()) {


            if(c.getString(c.getColumnIndex(COL_NAME)).trim().equals(habitName)) {
                return c.getInt(c.getColumnIndex(COL_ID));
            }

            c.moveToNext();

        }
        db.close();
        return null;
    }

    public void modifyPerson(Person person) {
        ContentValues cv = new ContentValues();
        cv.put(COL_PITNAME, person.getName());
        cv.put(COL_HABITID, getIdByHabitName(person.getHabitName()));

        String ID = person.getId();
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_PIT, cv, COL_PITID+"="+ID, null);
        db.close();

    }



    public boolean deleteHabitByID(Integer id) {
        SQLiteDatabase db = getWritableDatabase();
        boolean res = db.delete(TABLE_HABITS, COL_ID + " = " + id.toString(), null) > 0;
        db.close();
        return res;
    }

    public boolean deletePersonByHabitID(Integer habitID) {
        SQLiteDatabase db = getWritableDatabase();
        boolean res = db.delete(TABLE_PIT, COL_HABITID + " = " + habitID.toString(), null) > 0;
        db.close();
        return res;
    }

    public boolean deletePersonByID(Integer id) {
        SQLiteDatabase db = getWritableDatabase();
        boolean res = db.delete(TABLE_PIT, COL_PITID + " = " + id.toString(), null) > 0;
        db.close();
        return res;
    }

    public void deleteHabitByName(String habitname){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_HABITS + " WHERE " + COL_NAME + "=\"" + habitname + "\"" +
//                " AND " + COL_OWNID + "=\"" + ownerId + "\"" +
                ";");
        db.close();

    }

    public void deleteAllHabitsAndPerson() {
        // cancel alarms
        ArrayList<Habit> habits = getAllHabits();
        for(Habit habit: habits){
            cancelAlarm(habit.getAlarmId());
        }

        // delete habit from database.
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_HABITS + " WHERE 1;");
        db.execSQL("DELETE FROM " + TABLE_PIT + " WHERE 1;");
        close();
    }

    public void deleteAllPersons() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PIT + " WHERE 1;");
        close();
    }


    public String databasetostring(){

        String dbString="";

        SQLiteDatabase db= getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_HABITS;

        Cursor c =db.rawQuery(query,null);

        c.moveToFirst();

        while (!c.isAfterLast())

        {

            if(c.getString(c.getColumnIndex(COL_NAME))!=null)

            {

                dbString+= c.getString(c.getColumnIndex(COL_NAME));
                dbString+= " Date: " + c.getString(c.getColumnIndex(COL_DATELP));

                dbString+="\n";

            }

            c.moveToNext();

        }

        db.close();

        return dbString;

    }

    public void deleteTables() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_HABITS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PIT);
        db.close();
    }

    public int getNextAvailableRequestID() {
        int i = 0;
        boolean found = false;
        if(requestIDList == null) {
            return i;
        }
        while(!found) {
            if(requestIDList.contains(new Integer(i))){
                i++;
            } else {
                found = true;
            }
        }
        return i;
    }

    public void cancelAlarm(int alarmId) {
        AlarmManager am = (AlarmManager)
                myContext.getSystemService(Context.ALARM_SERVICE);
        Intent alertIntent = new Intent(myContext, NotificationPublisher.class);
        am.cancel(PendingIntent.getBroadcast(myContext, alarmId, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        removeRequestId(alarmId);
//        Log.d("Alarm", "Alarm cancel -> ID: " + alarmId);
    }

    public void updateAlarm(int alarmId, long startTime, Long repeatingPeriodInMillis, Habit habit) {
        cancelAlarm(alarmId);
        setAlarm(startTime, repeatingPeriodInMillis, habit);
    }

    public int setAlarm(Long startTime, Long repeatingInterval, Habit habit) {
        Intent alertIntent = new Intent(myContext, NotificationPublisher.class);
        alertIntent.putExtra("Habit Name", habit.getName().toString());
        alertIntent.putExtra("Last Performed", habit.getDateTimeLastPerformedAsString().toString());
        int requestID = getNextAvailableRequestID();
        addRequestId(requestID);

        AlarmManager alarmManager = (AlarmManager)
                myContext.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(getActivity(), 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, repeatingInterval, PendingIntent.getBroadcast(myContext, requestID, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
//        Log.d("Alarm", "Alarm set -> ID: " + requestID);
        return requestID;
    }

}
