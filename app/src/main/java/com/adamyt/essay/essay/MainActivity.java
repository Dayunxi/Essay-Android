package com.adamyt.essay.essay;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adamyt.essay.utils.EssayBean;
import com.adamyt.essay.utils.EssayUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ArrayList<EssayBean> essayList;

    public static final String IS_NEW = "com.adamyt.essay.IS_NEW";
    public static final String ESSAY_URL = "com.adamyt.essay.ESSAY_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra(IS_NEW, true);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadEssay();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.nav_authorize:
                System.out.println("nav_authorize");
                break;
            case R.id.nav_drafts:
                System.out.println("nav_drafts");
                break;
            case R.id.nav_register:
                System.out.println("nav_register");
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
//            case R.id.nav_import:
//                System.out.println("nav_import");
//                break;
//            case R.id.nav_export:
//                System.out.println("nav_export");
//                break;
            case R.id.nav_setting:
                System.out.println("nav_setting");
                break;
//            case R.id.nav_about:
//                System.out.println("nav_about");
//                break;
            case R.id.nav_logout:
                System.out.println("nav_logout");
                break;
        }

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadEssay() {
        essayList = EssayUtils.getAllPublicEssay(this);
        ListView lv = findViewById(R.id.essay_list);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                System.out.println(position);
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(essayList.get(position).essayUrl));
//                startActivity(intent);
            }
        });
        lv.setAdapter(new EssayAdapter());
    }

    private class EssayAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return essayList.size();
        }

        @Override
        public EssayBean getItem(int position) {
            return essayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(getApplicationContext(), R.layout.essay_list_item, null);
                holder.lv_title = convertView.findViewById(R.id.lv_title);
                holder.lv_date = convertView.findViewById(R.id.lv_date);
                holder.lv_icon = convertView.findViewById(R.id.lv_icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            EssayBean item = getItem(position);
            holder.lv_title.setText(item.title);
//            System.out.println(Locale.getDefault());
            holder.lv_date.setText((new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())).format(item.date));
            holder.lv_icon.setImageDrawable(item.icon);
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView lv_title;
        TextView lv_date;
        ImageView lv_icon;
    }
}
