package com.adamyt.essay.essay;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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
    private EssayAdapter essayAdapter;

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

        View navHeaderMain = navigationView.getHeaderView(0);
        ImageView avatar = navHeaderMain.findViewById(R.id.navAvatar);
        if(avatar!=null) avatar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        refresh();
    }

    @Override
    public void onStart(){
        super.onStart();
        System.out.println("onStart...");
        refresh();
    }

    @Override
    public void onResume(){
        super.onResume();
        System.out.println("onResume...");
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
            // TODO: authorize dialog
            case R.id.nav_authorize:
                System.out.println("nav_authorize");
                showAuthorizeDialog();
                break;
            case R.id.nav_drafts:
                System.out.println("nav_drafts");
                EssayUtils.getAllEssay(this);
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
                Toast.makeText(this, Environment.getExternalStorageDirectory().toString(), Toast.LENGTH_SHORT).show();
                break;
//            case R.id.nav_about:
//                System.out.println("nav_about");
//                break;
            case R.id.nav_logout:
                System.out.println("nav_logout");
                logout();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAuthorizeDialog(){
        View view = View.inflate(this, R.layout.authorize_input, null);
        final EditText editText = view.findViewById(R.id.authorize_password);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Authorize")//设置对话框的标题
                .setView(view)
                .setNegativeButton(R.string.confirm_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = editText.getText().toString();
                        Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    // load essay and user or logout
    private void refresh(){
        SharedPreferences sp = getSharedPreferences("data", 0);
        EssayUtils.CurrentUsername = sp.getString("currentUser", null);
        if(EssayUtils.CurrentUsername!=null){
            EssayUtils.hasLoggedIn = true;
            EssayUtils.isAuthorized = false; // for test
            NavigationView navigationView = findViewById(R.id.nav_view);
            View navHeaderMain = navigationView.getHeaderView(0);
            ImageView avatar = navHeaderMain.findViewById(R.id.navAvatar);
//            avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_black));
            TextView usernameText = navHeaderMain.findViewById(R.id.navUsername);
            usernameText.setText(EssayUtils.CurrentUsername);
            loadEssay();
        }
        else{
            EssayUtils.hasLoggedIn = false;
            EssayUtils.isAuthorized = false;
            NavigationView navigationView = findViewById(R.id.nav_view);
            View navHeaderMain = navigationView.getHeaderView(0);
            ImageView avatar = navHeaderMain.findViewById(R.id.navAvatar);
            TextView usernameText = navHeaderMain.findViewById(R.id.navUsername);
            usernameText.setText(R.string.nav_header_title);
            TextView promptEmpty = findViewById(R.id.list_empty_text);
            promptEmpty.setVisibility(View.VISIBLE);
//            promptEmpty.setText("Please login");
//            ListView lv = findViewById(R.id.essay_list);

            if(essayList!=null){
                essayList.clear();
                essayAdapter.notifyDataSetChanged();
            }
        }
    }

    private void logout(){
        SharedPreferences sp = getSharedPreferences("data", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("currentUser", null);
        editor.apply();
        refresh();
    }

    private void loadEssay() {
        TextView promptEmpty = findViewById(R.id.list_empty_text);
        if(EssayUtils.hasLoggedIn){
            if(EssayUtils.isAuthorized) essayList = EssayUtils.getAllEssay(this);
            else essayList = EssayUtils.getAllPublicEssay(this);

            if(essayList==null) promptEmpty.setVisibility(View.VISIBLE);
            else promptEmpty.setVisibility(View.GONE);

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
            essayAdapter = new EssayAdapter();
            lv.setAdapter(essayAdapter);
        }
        else{
            promptEmpty.setVisibility(View.VISIBLE);
        }

    }

    private class EssayAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return essayList==null? 0 : essayList.size();
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
