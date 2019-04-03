package com.example.materialtest;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawaerlayout;
    private List<City> cityList =new ArrayList<>();
    private FruitAdapter adapter;

    //判断子线程是否执行完成
    private static Attractions attractions=new Attractions();
    private static boolean flag=false;
    /**
     * 通过设置静态变量，等待子线程完成在执行下一步
     */
    public static void back(Attractions attractions,String url){
        Log.v("sss","子线程完成");
        attractions.Search(url);
        flag=true;
    }
    /**
     * 初始化函数
     */

    public void initFruits(){
        cityList.clear();
        for (int i=0;i<attractions.size();i++){
            City city =new City(attractions.name().get(i),attractions.img().get(i),attractions.detail().get(i),attractions.http().get(i));
            cityList.add(city);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //加载80条数据
        for (int i=1;i<=2;i++){
            MyThread myThread=new MyThread(attractions, String.format("https://you.ctrip.com/sight/chongqing158/s0-p%d.html#sightname",i));
            myThread.start();
            //等待子线程完成
            while (!flag);
            initFruits();
            flag=false;
            Log.v("sss", String.valueOf(cityList.size()));
        }
        initFruits();

        android.support.v7.widget.Toolbar toolbar=findViewById(R.id.toobar);
        setSupportActionBar(toolbar);
        mDrawaerlayout=findViewById(R.id.drawable_layout);
        NavigationView navigationView=findViewById(R.id.nav_view);
        FloatingActionButton fat=findViewById(R.id.fab);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        //navigationView.setCheckedItem(R.id.nav_call);//默认选项
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                mDrawaerlayout.closeDrawers();
                return true;
            }
        });
        fat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"Data delete",Snackbar.LENGTH_SHORT)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(v.getContext(),"Data delete",Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });
        RecyclerView recyclerView =findViewById(R.id.recycler_view);
        final GridLayoutManager layoutManager =new GridLayoutManager(MainActivity.this,2);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new FruitAdapter(cityList);
        recyclerView.setAdapter(adapter);
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
               mDrawaerlayout.openDrawer(GravityCompat.START);
        }
        return true;
    }
}