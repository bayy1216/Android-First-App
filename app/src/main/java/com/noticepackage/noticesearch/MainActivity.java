package com.noticepackage.noticesearch;


import android.os.Bundle;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.noticesearch.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity {
    // FrameLayout에 각 메뉴의 Fragment를 바꿔 줌
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction fragmentTransaction;
    // 4개의 메뉴에 들어갈 Fragment들
    private Menu1Fragment menu1Fragment;
    private Menu2Fragment menu2Fragment;
    private Menu3Fragment menu3Fragment;
    private Menu4Fragment menu4Fragment;
    private Setting1Fragment setting1Fragment;
    public Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_view);




        //initFrag();
        changeFrag(3);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_menu1:
                        changeFrag(1);
                        return true;
                    case R.id.navigation_menu2:
                        changeFrag(2);
                        return true;
                    case R.id.navigation_menu3:
                        changeFrag(3);
                        return true;
                    case R.id.navigation_menu4:
                        changeFrag(4);
                        return true;
                }
                return false;
            }
        });


    }


    private void initFrag(){

        setting1Fragment=new Setting1Fragment();

        //fragmentTransaction.add(R.id.container,menu2Fragment);

        fragmentTransaction.add(R.id.container,setting1Fragment);
        fragmentTransaction.commit();
    }

    public void changeFrag(int index){
        Fragment selected = null;
        fragmentTransaction = fragmentManager.beginTransaction();
        switch(index){
            case 1:
                if(menu1Fragment==null){
                    menu1Fragment = new Menu1Fragment();
                    fragmentTransaction.add(R.id.container,menu1Fragment);
                }
                selected=menu1Fragment;
                break;
            case 2:
                if(menu2Fragment==null){
                    menu2Fragment = new Menu2Fragment();
                    fragmentTransaction.add(R.id.container,menu2Fragment);
                }
                selected=menu2Fragment;
                break;
            case 3:
                if(menu3Fragment==null){
                    menu3Fragment = new Menu3Fragment();
                    fragmentTransaction.add(R.id.container,menu3Fragment);
                }
                selected=menu3Fragment;
                break;
            case 4:
                if(menu4Fragment==null){
                    menu4Fragment = new Menu4Fragment();
                    fragmentTransaction.add(R.id.container,menu4Fragment);
                }
                selected=menu4Fragment;
                break;
            case 5:
                if(setting1Fragment==null){
                    setting1Fragment = new Setting1Fragment();
                    fragmentTransaction.add(R.id.container,setting1Fragment);
                }
                selected=setting1Fragment;
                break;
        }


        if(menu1Fragment!=null) fragmentTransaction.hide(menu1Fragment);
        if(menu2Fragment!=null) fragmentTransaction.hide(menu2Fragment);
        if(menu3Fragment!=null) fragmentTransaction.hide(menu3Fragment);
        if(menu4Fragment!=null) fragmentTransaction.hide(menu4Fragment);
        if(setting1Fragment!=null) fragmentTransaction.hide(setting1Fragment);

        fragmentTransaction.show(selected);
        fragmentTransaction.commit();
    }
    public void fragBtnClick(Bundle bundle) {
        this.mBundle = bundle;
    } //fragBtnClcick()




}
