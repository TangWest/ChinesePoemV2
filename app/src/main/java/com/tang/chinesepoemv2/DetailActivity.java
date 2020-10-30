package com.tang.chinesepoemv2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements Runnable{

    private String TAG = "DetailActivity";
    private Toolbar mToolbar;
    private TextView tv1;
    private TextView tv2;
    private ListView lv;
    private Button btn;
    private SimpleAdapter listItemAdapter;
    private ArrayList<HashMap<String, String>> listItems;
    private String poemid = "23309";
    String titlestr = null;
    String oricontentsstr = null;

    int c=0; //0没有收藏，1收藏了。myC、mark同理。

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);

        //工具栏
        mToolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar =  getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }
        //诗
        tv1 = findViewById(R.id.detail_text_1);
        //释义
        tv2 = findViewById(R.id.detail_text_2);
        //评论
        lv = (ListView) findViewById(R.id.detail_list);
        btn = (Button) findViewById(R.id.send_notes_button);

        tv1.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv2.setMovementMethod(ScrollingMovementMethod.getInstance());

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        //获得poemid
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        poemid = bundle.getString("poemid");
        Log.i(TAG,"id from bundle "+poemid);

        Thread thread = new Thread(this);
        thread.start();

        //工具栏
        mToolbar.inflateMenu(R.menu.toolbar_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_1:
                        Menu menu = mToolbar.getMenu();
                        MenuItem menuItem = menu.findItem(R.id.menu_item_1);
                        if(c==0){//没有收藏，则需改为收藏。
                            menuItem.setIcon(R.mipmap.collect_yes);
                            //数据库添加
                            addOnePoem();
                            c=1;
                        }else{
                            menuItem.setIcon(R.mipmap.collect_no);
                            //数据库删除
                            delPoem();
                            c=0;
                        }
                        break;
                }
                return false;
            }
        });

        //发送笔记
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOneNote();
            }
        });

        //长按笔记
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long longid) {
                //数据库删除
                Object itemAtPosition = lv.getItemAtPosition(position);
                HashMap<String,String> map = (HashMap<String,String>)itemAtPosition;
                String notetime = map.get("note_time");
                delNote(poemid,notetime);
                //视图删除
                listItems.remove(position);
                listItemAdapter.notifyDataSetChanged();
                return true;//长按之后不执行点击事件
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        c = calcuteC();
        if (c==0) {
            menu.findItem(R.id.menu_item_1).setIcon(R.mipmap.collect_no);
        } else {
            menu.findItem(R.id.menu_item_1).setIcon(R.mipmap.collect_yes);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);//添加菜单
        return true;
    }

    private int calcuteC(){
        //数据库查询
        int myC = findOnePoem(poemid);
        return myC;
    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                String str = (String) msg.obj;
                tv1.setText(Html.fromHtml(str));
            }
            if(msg.what == 1) {
                String str = (String) msg.obj;
                if(str.length()>0){
                    tv2.setText(Html.fromHtml(str));
                }else {
                    tv2.setText("暂无赏析。");
                }
            }
            if(msg.what == 2) {
                //布局显示
                String[] data = (String[]) msg.obj;
                listItems = new ArrayList<HashMap<String, String>>();
                for(int i = 0;i<data.length;i++){
                    String[] splitdata = data[i].split("#");

                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("note_content",splitdata[0]);
                    map.put("note_time",splitdata[1]);
                    listItems.add(map);
                }
                listItemAdapter = new SimpleAdapter(DetailActivity.this,
                        listItems,
                        R.layout.notes_list_item,
                        new String[]{"note_content", "note_time"},
                        new int[]{R.id.note_content, R.id.note_time}
                );
                lv.setAdapter(listItemAdapter);
            }
        }
    };

    public void run(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                showPoemDetail(poemid);
            }
        }).start();

    }

    private  void showPoemDetail(String pi){
        URL url = null;
        InputStream in = null;
        try{
            String poemurl = "https://www.shicimingju.com/chaxun/list/"+pi+".html";
            Log.i(TAG,poemurl);

            url = new URL(poemurl);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            in = http.getInputStream();
            String html = inputStream2String(in);
            showPoem(html);
            showDigest(html);
            showNotes();
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"UTF-8");
        while (true){
            int rsz = in.read(buffer,0,buffer.length);
            if(rsz < 0)
                break;
            out.append(buffer,0,rsz);
        }
        inputStream.close();
        return out.toString();
    }

    private void showPoem(String html) throws UnsupportedEncodingException {
        Document doc = Jsoup.parse(html);
        Elements title = doc.select("h1#zs_title");
        Elements nz = doc.getElementsByClass("niandai_zuozhe");
        Elements contents = doc.getElementsByClass("item_content").select("div");

        titlestr = title.text();
        String nzstr = nz.text();
        //内容逢句号等 换行
        oricontentsstr = contents.text();
        String contentsstr = null;
        String[] s= {"。","？","！","；"};
        for(int i=0;i<s.length;i++){
            contentsstr = oricontentsstr.replace(s[i], s[i]+"<br>");
        }

        String poemstr = "<font size='1' style='font-family:SimSun'><big><b>"+titlestr+"</b></big></font><br>"
                +"<font size='2' style='font-family:SimSun'><b>"+nzstr+"</b></font><br>"
                +"<font size='3' style='font-family:SimSun'><big><b>"+contentsstr+"</b></big></font>";
        Message msg = handler.obtainMessage(0);
        msg.obj = poemstr;
        handler.sendMessage(msg);
    }
    private void showDigest(String html) throws UnsupportedEncodingException {
        Document doc = Jsoup.parse(html);
        Elements digest = doc.getElementsByClass("shangxi_content");

        String digeststr = digest.text();
        Message msg = handler.obtainMessage(1);
        msg.obj = digeststr;
        handler.sendMessage(msg);
    }

    //展示笔记
    private void showNotes(){
        List<String> data = new ArrayList<String>();
        NotesManager notesManager = new NotesManager(DetailActivity.this);
        for(NotesItem notesItem : notesManager.listAll(poemid)){
            data.add(notesItem.getNotecontent() + "#" + notesItem.getNotetime());
        }
        String[] datastr = data.toArray(new String[]{});

        Message msg = handler.obtainMessage(2);
        msg.obj = datastr;
        handler.sendMessage(msg);
    }

    //添加笔记
    private void addOneNote(){
        //获取当前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = date.toString();
        //获取笔记内容
        TextView et = (TextView) findViewById(R.id.send_notes_edittext);
        String content = et.getText().toString();

        NotesManager notesManager = new NotesManager(DetailActivity.this);
        NotesItem notesItem = new NotesItem(poemid,content,time);
        notesManager.add(notesItem);

        et.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        showNotes();
    }

    private void delNote(String poemid, String notetime){
        NotesManager notesManager = new NotesManager(DetailActivity.this);
        notesManager.delete(poemid,notetime);
    }

    private void addOnePoem(){
        //在加载页面时，已经将html解析出的题目及内容放入共有变量titlestr和oricontentsstr中。

        PoemsManager poemsManager = new PoemsManager(DetailActivity.this);
        PoemsItem poemsItem = new PoemsItem(poemid,titlestr,oricontentsstr);
        poemsManager.add(poemsItem);
    }

    private void delPoem(){
        PoemsManager poemsManager = new PoemsManager(DetailActivity.this);
        poemsManager.delete(poemid);
    }

    private int findOnePoem(String poemid){
        PoemsManager poemsManager = new PoemsManager(DetailActivity.this);
        int mark = poemsManager.isInTable(poemid);
        return mark;
    }
}
