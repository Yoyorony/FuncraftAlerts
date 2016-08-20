package yoyorony.me.funcraftv2alerts;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class ForumActivitySubmenu extends AppCompatActivity {
    public static ListView listviexRSS = null;
    public static SwipeRefreshLayout swiper = null;
    public static ArrayList<String> Subtitle = new ArrayList<>();
    public static int selection = -1;
    public static CustomBaseAdapterSubmenu adapter;
    public static AlertDialog waitDialog;
    public static AlertDialog timeoutDialog;
    public static AlertDialog errorDialog;
    public static AlertDialog connexionerrorDialog;
    public static boolean timeout;
    public static boolean error;
    public static boolean connexionerror;
    private OnItemClickListener ListViewListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selection = position;
            startActivity(new Intent(getBaseContext(), ForumActivityItems.class));
        }
    };
    private SwipeRefreshLayout.OnRefreshListener SwiperListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            new DownloadSubmenu().execute();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_apercuforum);
        super.onCreate(savedInstanceState);

        buildDialogs();
        waitDialog.show();

        switch (ForumActivity.selection) {
            case 0:
                this.setTitle("Forum - Funcraft");
                break;
            case 1:
                this.setTitle("Forum - Serveur et mini-jeu");
                break;
            case 2:
                this.setTitle("Forum - Hors Mincraft");
                break;
            case 3:
                this.setTitle("forum - Minecraft");
                break;
        }

        listviexRSS = (ListView) findViewById(R.id.listViewRSS);
        swiper = (SwipeRefreshLayout) findViewById(R.id.swipeRSS);

        swiper.setOnRefreshListener(SwiperListener);

        adapter = new CustomBaseAdapterSubmenu(this, new ArrayList<StringsForAdapterSubmenu>());
        listviexRSS.setAdapter(adapter);

        listviexRSS.setOnItemClickListener(ListViewListener);

        new DownloadSubmenu().execute();
    }

    private class DownloadSubmenu extends AsyncTask<Void, Void, ArrayList<StringsForAdapterSubmenu>> {
        @Override
        protected ArrayList<StringsForAdapterSubmenu> doInBackground(Void... v) {
            return getStringsAdapter();
        }

        protected void onPreExecute(){timeout = false; error = false; connexionerror = false;}

        protected void onPostExecute(ArrayList<StringsForAdapterSubmenu> forumList){
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
        ((TextView) view.findViewById(R.id.alertDialogMessage)).setText(R.string.noconnexionmessage);
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

    private ArrayList<StringsForAdapterSubmenu> getStringsAdapter() {
        ArrayList<StringsForAdapterSubmenu> Strings = new ArrayList<>();

        StringsForAdapterSubmenu s;
        ArrayList<String> list1 = new ArrayList<>();
        switch (ForumActivity.selection) {
            case 0:
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/annonces-regles.2/index.rss",
                        "https://community.funcraft.net/forums/recrutement-staff.5/index.rss"}, 0);
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums0Title)));
                break;
            case 1:
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/discussions.7/index.rss",
                        "https://community.funcraft.net/forums/teams.41/index.rss",
                        "https://community.funcraft.net/forums/suggestions-idees.13/index.rss",
                        "https://community.funcraft.net/forums/astuces-entraide.21/index.rss",
                        "https://community.funcraft.net/forums/signaler-un-bug.20/index.rss"}, 0);

                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums1Title)));
                break;
            case 2:
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/hors-sujet.9/index.rss",
                        "https://community.funcraft.net/forums/presentez-vous.8/index.rss",
                        "https://community.funcraft.net/forums/vos-talents-creations.29/index.rss"}, 0);

                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums2Title)));
                break;
            case 3:
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/discussions-minecraft.36/index.rss",
                        "https://community.funcraft.net/forums/resource-packs.33/index.rss",
                        "https://community.funcraft.net/forums/maps-constructions-redstone.34/index.rss",
                        "https://community.funcraft.net/forums/mods-plugins-outils.35/index.rss"}, 0);

                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums3Title)));
                break;
        }

        for (int i = 0; i < Subtitle.size(); i++) {
            s = new StringsForAdapterSubmenu();
            s.setTitle(list1.get(i));
            s.setSubtitle(Subtitle.get(i));
            Strings.add(s);
        }

        return Strings;
    }
}

class StringsForAdapterSubmenu {
    private String title = "";
    private String subtitle = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String s) {
        this.title = s;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String s) {
        this.subtitle = s;
    }
}

class CustomBaseAdapterSubmenu extends BaseAdapter {
    private static ArrayList<StringsForAdapterSubmenu> StringArray;
    private LayoutInflater Inflater;

    public CustomBaseAdapterSubmenu(Context context, ArrayList<StringsForAdapterSubmenu> Strings) {
        StringArray = Strings;
        Inflater = LayoutInflater.from(context);
    }

    public ArrayList<StringsForAdapterSubmenu> getStringArray(){return StringArray;}

    public int getCount() {
        return StringArray.size();
    }

    public Object getItem(int position) {
        return StringArray.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = Inflater.inflate(R.layout.listviewcustom_submenu, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.txtSubtitle = (TextView) convertView.findViewById(R.id.subtitle);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtTitle.setText(StringArray.get(position).getTitle());
        holder.txtSubtitle.setText(StringArray.get(position).getSubtitle());

        return convertView;
    }

    static class ViewHolder {
        TextView txtTitle;
        TextView txtSubtitle;
    }
}