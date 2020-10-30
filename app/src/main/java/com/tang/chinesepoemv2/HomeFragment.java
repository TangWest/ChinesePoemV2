package com.tang.chinesepoemv2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import android.view.inputmethod.InputMethodManager;
import android.widget.SimpleAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class HomeFragment extends Fragment implements Runnable {
    private String TAG = "HomeFragment";

    private View view;
    private TextView tv;
    private ListView lv;
    private Button btn;

    private SimpleAdapter listItemAdapter;
    private ArrayList<HashMap<String, String>> listItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        tv = (TextView) getActivity().findViewById(R.id.search_poems_edittext);
        btn = (Button) getActivity().findViewById(R.id.search_poems_button);
        lv = (ListView) getActivity().findViewById(R.id.home_list);

        Thread thread = new Thread(this);
        thread.start();

        //btn绑定事件
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(tv.getText())) {
                    Toast t = Toast.makeText(getActivity(), "请先输入要搜索的诗词", Toast.LENGTH_SHORT);
                    t.show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String searchword = tv.getText().toString();
                            searchPoem(searchword);
                        }
                    }).start();
                }
            }
        });

        //lv绑定事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object itemAtPosition = lv.getItemAtPosition(position);
                HashMap<String, String> map = (HashMap<String, String>) itemAtPosition;
                String poemid = map.get("poem_id");

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("poemid", poemid);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                //布局显示
                String[] data = (String[]) msg.obj;
                listItems = new ArrayList<HashMap<String, String>>();
                for(int i = 0;i<data.length;i++){
                    String[] splitdata = data[i].split("#");

                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("poem_id",splitdata[0]);
                    map.put("poem_title",splitdata[1]);
                    map.put("poem_content",splitdata[2]);
                    listItems.add(map);
//                    Log.i(TAG, "handleMessage: add the "+i+" item");
                }
                listItemAdapter = new SimpleAdapter(getActivity(),
                        listItems,
                        R.layout.poems_list_item,
                        new String[]{"poem_id","poem_title","poem_content"},
                        new int[]{R.id.poem_id, R.id.poem_title,R.id.poem_content}
                );
                lv.setAdapter(listItemAdapter);

                TextView emptv = (TextView) getActivity().findViewById(R.id.home_noresult);
                TextView seatv = (TextView) getActivity().findViewById(R.id.search_poems_edittext);
                String sw = seatv.getText().toString();
                emptv.setText("抱歉，搜索"+sw+"暂无结果。");
                lv.setEmptyView(emptv);

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
            }
        }
    };

    public void run(){//重写run()

        getActivity().runOnUiThread(new Runnable() {//更新ui 在主线程中
            @Override
            public void run() {

                new Thread(new Runnable(){
                    @Override
                    public void run() {//HTTP请求 不能在主线程中
                        showList();
                    }

                }).start();
            }
        });
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

    private void useJsoup(String html,int mark){//mark==1 首页useJsoup，mark==2 搜索结果页useJsoup
        Document doc = Jsoup.parse(html);
        Elements cards = doc.select("div.card").select(".shici_card");
        Log.i(TAG,"card "+cards.size());

        List<String> data = new ArrayList<String>();
        for (int i=0;i<cards.size();i++){
            String href = null;
            Elements title = cards.get(i).getElementsByClass("shici_list_main").select("h3");
            Elements content = cards.get(i).getElementsByClass("shici_content");

            //content中去除"收起""展开全文"
            String contentstr = content.text();
            contentstr = contentstr.replace("收起","");
            contentstr = contentstr.replace("展开全文","");

            //首页和搜索界面href的获取略有不同
            if(mark==1){
                href = title.select("a").first().attr("href");
            }else if(mark==2){
                href = title.select("a").attr("href");
            }
            //href改为id
            String idstr = href.replaceAll("[^\\d]","");

            String item = null;
            if(idstr.length()>0&title.text().length()>0&contentstr.length()>0){//判空
                item = idstr+"#"+title.text()+"#"+contentstr;
                data.add(item);
            }
        }
        String[] datastr = data.toArray(new String[]{});

        Message msg = handler.obtainMessage(0);
        msg.obj = datastr;
        handler.sendMessage(msg);
    }

    private  void showList(){
        URL url = null;
        InputStream in = null;
        try{
            String poemurl = "https://www.shicimingju.com";
            url = new URL(poemurl);
            Log.i(TAG,poemurl);

            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            in = http.getInputStream();
            String html = inputStream2String(in);
            useJsoup(html,1);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void searchPoem(String searchword){
        URL url = null;
        InputStream in = null;
        try{
            String poemurl = "https://www.shicimingju.com/chaxun/all/"+searchword;
            Log.i(TAG,poemurl);

            url = new URL(poemurl);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            in = http.getInputStream();
            String html = inputStream2String(in);
            useJsoup(html,2);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}