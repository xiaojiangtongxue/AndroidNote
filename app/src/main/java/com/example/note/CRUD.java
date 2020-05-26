package com.example.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CRUD {
    SQLiteOpenHelper dbHandler;
    SQLiteDatabase db;

    private static final String[] columns = {
      NoteDatabase.ID,
      NoteDatabase.CONTENT,
      NoteDatabase.TIME,
      NoteDatabase.MODE
    };

    public CRUD(Context context){
        dbHandler = new NoteDatabase(context);
    }
    //打开数据库
    public void open(){
        db = dbHandler.getWritableDatabase();
    }
    //关闭数据库
    public void close(){
        dbHandler.close();
    }
    //添加笔记的方法，对数据库进行操作
    public Note addNote(Note note){
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDatabase.CONTENT,note.getContent());
        contentValues.put(NoteDatabase.TIME,note.getTime());
        contentValues.put(NoteDatabase.MODE,note.getTag());
        long insertId = db.insert(NoteDatabase.TABLE_NAME,null,contentValues);
        note.setId(insertId);
        return note;
    }
    //获取单条笔记的方法：
    public Note getNode(long id){
        //using cursor:
        Cursor cursor = db.query(NoteDatabase.TABLE_NAME,columns,NoteDatabase.ID+"=?",
                new String[]{String.valueOf(id)},null,null,null,null);
        if(cursor != null) cursor.moveToFirst();
        Note e = new Note(cursor.getString(1),
                cursor.getString(2),cursor.getInt(3));
        return e;
    }
    //获取所有Note的方法：
    public List<Note> getAllNotes(){
        Cursor cursor = db.query(NoteDatabase.TABLE_NAME,columns,
                null,null,null,null,null);

        List<Note> notes = new ArrayList<>();
        if(cursor.getCount() > 0){
            while (cursor.moveToNext()){
                Note note = new Note();
                note.setId(cursor.getLong(cursor.getColumnIndex(NoteDatabase.ID)));
                note.setContent(cursor.getString(cursor.getColumnIndex(NoteDatabase.CONTENT)));
                note.setTime(cursor.getString(cursor.getColumnIndex(NoteDatabase.TIME)));
                note.setTag(cursor.getInt(cursor.getColumnIndex(NoteDatabase.MODE)));
                notes.add(note);
            }
        }
        return notes;
    }
    //修改note：
    public int updateNote(Note note){
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDatabase.CONTENT,note.getContent());
        contentValues.put(NoteDatabase.TIME,note.getTime());
        contentValues.put(NoteDatabase.MODE,note.getTag());
        return db.update(NoteDatabase.TABLE_NAME,contentValues,
                NoteDatabase.ID + "?=", new String[]{String.valueOf(note.getId())});
    }
    //删除note
    public void removeNote(Note note){
        db.delete(NoteDatabase.TABLE_NAME,
                NoteDatabase.ID + "="+ note.getId(),null);
    }
    //删除所有
    public void removeAll(){
        db.delete(NoteDatabase.TABLE_NAME,1 + "=" + 1,null);
    }
}
