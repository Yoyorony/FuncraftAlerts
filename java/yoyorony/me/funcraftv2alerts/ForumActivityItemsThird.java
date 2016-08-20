package yoyorony.me.funcraftv2alerts;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ForumActivityItemsThird extends AppCompatActivity {
    public static ListView listviexRSS = null;
    public static SwipeRefreshLayout swiper = null;
    public static ArrayList<String> Title = new ArrayList<>();
    public static ArrayList<String> Subtitle = new ArrayList<>();
    public static ArrayList<String> Dates = new ArrayList<>();
    public static ArrayList<String> Links = new ArrayList<>();
    public static CustomBaseAdapterItems adapter;
    public static AlertDialog waitDialog;
    public static AlertDialog timeoutDialog;
    public static AlertDialog errorDialog;
    public static AlertDialog connexionerrorDialog;
    public static boolean timeout;
    public static boolean error;
    public static boolean connexionerror;
    private AdapterView.OnItemClickListener ListViewListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent browserintent = new Intent(Intent.ACTION_VIEW, Uri.parse(Links.get(position)));
            browserintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(browserintent);
        }
    };
    private SwipeRefreshLayout.OnRefreshListener SwiperListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            new DownloadItems().execute();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_apercuforum);
        super.onCreate(savedInstanceState);

        this.setTitle(getActTitle());

        buildDialogs();
        waitDialog.show();

        listviexRSS = (ListView) findViewById(R.id.listViewRSS);
        swiper = (SwipeRefreshLayout) findViewById(R.id.swipeRSS);

        swiper.setOnRefreshListener(SwiperListener);

        adapter = new CustomBaseAdapterItems(this, new ArrayList<StringsForAdapterItems>());
        listviexRSS.setAdapter(adapter);

        listviexRSS.setOnItemClickListener(ListViewListener);

        new DownloadItems().execute();
    }

    private class DownloadItems extends AsyncTask<Void, Void, ArrayList<StringsForAdapterItems>> {
        @Override
        protected ArrayList<StringsForAdapterItems> doInBackground(Void... v) {
            return getStringsAdapter();
        }

        protected void onPreExecute(){
            timeout = false; error = false; connexionerror = false;
            Title = new ArrayList<>();
            Subtitle = new ArrayList<>();
            Dates = new ArrayList<>();
            Links = new ArrayList<>();
        }

        protected void onPostExecute(ArrayList<StringsForAdapterItems> forumList){
            if(timeout){
                waitDialog.dismiss();
                timeoutDialog.show();
            }else if(error){
                waitDialog.dismiss();
                errorDialog.show();
            }else if(connexionerror){
                waitDialog.dismiss();
                connexionerrorDialog.show();
            }else{
                adapter.getStringArray().clear();
                adapter.getStringArray().addAll(forumList);
                adapter.notifyDataSetChanged();
                waitDialog.dismiss();
            }
            swiper.setRefreshing(false);
        }
    }

    private void buildDialogs(){
        final ProgressDialog.Builder waitDialogBuilder = new ProgressDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.waitdialogue, null);
        ((TextView) view.findViewById(R.id.waitDialogTitle)).setText(R.string.waitTitle);
        ((TextView) view.findViewById(R.id.waitDialogMessage)).setText(R.string.waitMessage);
        waitDialogBuilder.setCancelable(false);
        waitDialogBuilder.setView(view);
        waitDialog = waitDialogBuilder.create();

        final ProgressDialog.Builder timeoutDialogBuilder = new ProgressDialog.Builder(this);
        view = this.getLayoutInflater().inflate(R.layout.alertdialogue, null);
        ((TextView) view.findViewById(R.id.alertDialogTitle)).setText(R.string.timeouttitle);
        ((TextView) view.findViewById(R.id.alertDialogMessage)).setText(R.string.timeoutmessage);
        timeoutDialogBuilder.setCancelable(true);
        timeoutDialogBuilder.setView(view);
        timeoutDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        timeoutDialog = timeoutDialogBuilder.create();

        final ProgressDialog.Builder errorDialogBuilder = new ProgressDialog.Builder(this);
        view = this.getLayoutInflater().inflate(R.layout.alertdialogue, null);
        ((TextView) view.findViewById(R.id.alertDialogTitle)).setText(R.string.connexionfailtitle);
        ((TextView) view.findViewById(R.id.alertDialogMessage)).setText(R.string.connexionfailmessage);
        errorDialogBuilder.setCancelable(true);
        errorDialogBuilder.setView(view);
        errorDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        errorDialog = errorDialogBuilder.create();

        final ProgressDialog.Builder connexionerrorDialogBuilder = new ProgressDialog.Builder(this);
        view = this.getLayoutInflater().inflate(R.layout.alertdialogue, null);
        ((TextView) view.findViewById(R.id.alertDialogTitle)).setText(R.string.noconnexiontitle);
        ((TextView) view.findViewById(R.id.alertDialogMessage)).setText(R.string.nonewversionmessage);
        connexionerrorDialogBuilder.setCancelable(true);
        connexionerrorDialogBuilder.setView(view);
        connexionerrorDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        connexionerrorDialog = connexionerrorDialogBuilder.create();
    }

    private String getActTitle(){
        return "Forum - " + getResources().getStringArray(R.array.Forums110Title)[ForumActivityItemsSecond.selection];
    }

    private ArrayList<StringsForAdapterItems> getStringsAdapter() {
        ArrayList<StringsForAdapterItems> Strings = new ArrayList<>();

        StringsForAdapterItems s;
        if(ForumActivity.selection == 1 && ForumActivitySubmenu.selection == 2){
            switch (ForumActivityItems.selection){
                case 0:
                    switch (ForumActivityItemsSecond.selection){
                        case 0:
                            Connexion.getItemsFeed("https://community.funcraft.net/forums/ameliorations-acceptees.16/index.rss", 2);
                            break;
                        case 1:
                            Connexion.getItemsFeed("https://community.funcraft.net/forums/ameliorations-refusees.17/index.rss", 2);
                            break;
                    }
                    break;
                case 1:
                    switch (ForumActivityItemsSecond.selection){
                        case 0:
                            Connexion.getItemsFeed("https://community.funcraft.net/forums/propositions-acceptees.18/index.rss", 2);
                            break;
                        case 1:
                            Connexion.getItemsFeed("https://community.funcraft.net/forums/propositions-refusees.19/index.rss", 2);
                            break;
                    }
                    break;
            }
        }

        for (int i = 0; i < Title.size(); i++) {
            s = new StringsForAdapterItems();
            s.setTitle(Title.get(i));
            s.setSubtitle(Subtitle.get(i));
            s.setDates(Dates.get(i));
            Strings.add(s);
        }

        return Strings;
    }
}