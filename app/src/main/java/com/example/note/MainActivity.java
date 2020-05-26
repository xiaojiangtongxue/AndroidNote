package com.example.note;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    final String TAG = "jiangtest";
    FloatingActionButton btn;
    private ListView lv;

    //从数据库中加载所有note
    private NoteDatabase dbHelper;
    private NoteAdapter adapter;
    private List<Note> noteList = new ArrayList<>();
    private Context context = this;

    //对toolbar细化：
    private Toolbar mytoolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //添加按钮
        btn = (FloatingActionButton) findViewById(R.id.floatingActionButton3);
        //为添加按钮添加点击事件
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EditActivity.class);
                intent.putExtra("mode",4);//新建笔记增加mode=4，在之后修改时候可以区分开
                startActivityForResult(intent,0);
            }
        });

//        deleteall();
        lv = findViewById(R.id.lv);
        adapter = new NoteAdapter(getApplicationContext(), noteList);
        refreshListView();
        lv.setAdapter(adapter);

        mytoolbar = findViewById(R.id.myToolbar);

        setSupportActionBar(mytoolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //设置toolbar取代actionbar
        lv.setOnItemClickListener(this);
    }
    //接受startActyvityForResult的结果
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        String content = data.getStringExtra("content");
        String time = data.getStringExtra("time");
        Note note = new Note(content,time,1);//先设置tag默认为1
        CRUD op = new CRUD(context);
        op.open();
        op.addNote(note);
        op.close();
        refreshListView();
    }
    //
    public void refreshListView(){
        CRUD op = new CRUD(context);
        op.open();
        // set adapter
        if (noteList.size() > 0) noteList.clear();
        noteList.addAll(op.getAllNotes());
        op.close();
        adapter.notifyDataSetChanged();
    }
    public void deleteall(){
        CRUD op = new CRUD(context);
        op.open();
        op.removeAll();
        op.close();
    }


    @Override
    //几个参数：parent:父元素 view：整个页面（不怎么用的到） position:父元素中当前定位到子元素的下标
    //id：每个子元素对应唯一id，由于位置改变，子元素的下标可能会改变，所以可以用id唯一确定子元素
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv:
                Note curNote = (Note) parent.getItemAtPosition(position); //定位到当前笔记元素
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("content", curNote.getContent());//获取当前元素笔记的内容
                intent.putExtra("id", curNote.getId());//获取当前...id
                intent.putExtra("time", curNote.getTime());//...事件
                intent.putExtra("mode", 3);     // 点击修改的mode = 3
                intent.putExtra("tag", curNote.getTag());//...tag
                startActivityForResult(intent, 1);      //collect data from edit
                Log.d(TAG, "onItemClick: " + position);
                break;
        }

    }
}
