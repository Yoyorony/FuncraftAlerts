package yoyorony.me.funcraftv2alerts;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class ForumActivityItems extends AppCompatActivity {
    public static ListView listviexRSS = null;
    public static ListView listviexRSSOptions = null;
    public static ArrayList<String> Title = new ArrayList<>();
    public static ArrayList<String> Subtitle = new ArrayList<>();
    public static ArrayList<String> Dates = new ArrayList<>();
    public static ArrayList<String> Links = new ArrayList<>();
    public static ArrayList<String> SubtitleOption = new ArrayList<>();
    public static int selection = -1;
    public static CustomBaseAdapterItems adapter;
    public static CustomBaseAdapterSubmenu2 adapter2;
    public static AlertDialog waitDialog;
    public static AlertDialog timeoutDialog;
    public static AlertDialog errorDialog;
    public static AlertDialog connexionerrorDialog;
    public static boolean itemsLoaded;
    public static boolean submenuLoaded;
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
    private AdapterView.OnItemClickListener ListViewListenerOptions = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selection = position;
            startActivity(new Intent(getBaseContext(), ForumActivityItemsSecond.class));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_apercuforum);
        super.onCreate(savedInstanceState);

        this.setTitle(getActTitle());

        itemsLoaded = false; submenuLoaded = false;

        buildDialogs();
        waitDialog.show();

        listviexRSS = (ListView) findViewById(R.id.listViewRSS);
        listviexRSSOptions = (ListView) findViewById(R.id.listViewRSSOptions);

        adapter = new CustomBaseAdapterItems(this, new ArrayList<StringsForAdapterItems>());
        listviexRSS.setAdapter(adapter);
        adapter2 = new CustomBaseAdapterSubmenu2(this, new ArrayList<StringsForAdapterSubmenu2>());
        listviexRSSOptions.setAdapter(adapter2);

        listviexRSS.setOnItemClickListener(ListViewListener);
        listviexRSSOptions.setOnItemClickListener(ListViewListenerOptions);

        new DownloadItems().execute();
        new DownloadSubmenu2().execute();
    }

    private class DownloadItems extends AsyncTask<Void, Void, ArrayList<StringsForAdapterItems>> {
        @Override
        protected ArrayList<StringsForAdapterItems> doInBackground(Void... v) {
            return getStringsAdapter();
        }

        protected void onPreExecute(){timeout = false; error = false;}

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
                itemsLoaded = true;
                if(submenuLoaded){
                    waitDialog.dismiss();
                }
            }
        }
    }

    private class DownloadSubmenu2 extends AsyncTask<Void, Void, ArrayList<StringsForAdapterSubmenu2>> {
        @Override
        protected ArrayList<StringsForAdapterSubmenu2> doInBackground(Void... v) {
            return getStringsAdapter2();
        }

        protected void onPreExecute(){timeout = false; error = false;}

        protected void onPostExecute(ArrayList<StringsForAdapterSubmenu2> forumList){
            if(!timeout && !error){
                adapter2.getStringArray().clear();
                adapter2.getStringArray().addAll(forumList);
                adapter2.notifyDataSetChanged();
                submenuLoaded = true;
                if(itemsLoaded){
                    waitDialog.dismiss();
                }
            }
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
        switch (ForumActivity.selection) {
            case 0:
                return "Forum - " + getResources().getStringArray(R.array.Forums0Title)[ForumActivitySubmenu.selection];
            case 1:
                return "Forum - " + getResources().getStringArray(R.array.Forums1Title)[ForumActivitySubmenu.selection];
            case 2:
                return "Forum - " + getResources().getStringArray(R.array.Forums2Title)[ForumActivitySubmenu.selection];
            case 3:
                return "Forum - " + getResources().getStringArray(R.array.Forums3Title)[ForumActivitySubmenu.selection];
        }
        return "Forum";
    }

    private ArrayList<StringsForAdapterItems> getStringsAdapter() {
        ArrayList<StringsForAdapterItems> Strings = new ArrayList<>();

        StringsForAdapterItems s;
        switch (ForumActivity.selection) {
            case 0:
                switch (ForumActivitySubmenu.selection) {
                    case 0:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/annonces-regles.2/index.rss", 0);
                        break;
                    case 1:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/recrutement-staff.5/index.rss", 0);
                        break;
                }
                break;
            case 1:
                switch (ForumActivitySubmenu.selection) {
                    case 0:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/discussions.7/index.rss", 0);
                        break;
                    case 1:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/teams.41/index.rss", 0);
                        break;
                    case 2:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/suggestions-idees.13/index.rss", 0);
                        break;
                    case 3:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/astuces-entraide.21/index.rss", 0);
                        break;
                    case 4:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/signaler-un-bug.20/index.rss", 0);
                        break;
                }
                break;
            case 2:
                switch (ForumActivitySubmenu.selection) {
                    case 0:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/hors-sujet.9/index.rss", 0);
                        break;
                    case 1:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/presentez-vous.8/index.rss", 0);
                        break;
                    case 2:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/vos-talents-creations.29/index.rss", 0);
                        break;
                }
                break;
            case 3:
                switch (ForumActivitySubmenu.selection) {
                    case 0:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/discussions-minecraft.36/index.rss", 0);
                        break;
                    case 1:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/resource-packs.33/index.rss", 0);
                        break;
                    case 2:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/maps-constructions-redstone.34/index.rss", 0);
                        break;
                    case 3:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/mods-plugins-outils.35/index.rss", 0);
                        break;
                }
                break;
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

    private ArrayList<StringsForAdapterSubmenu2> getStringsAdapter2() {
        ArrayList<StringsForAdapterSubmenu2> Strings = new ArrayList<>();

        StringsForAdapterSubmenu2 s;
        ArrayList<String> list1 = new ArrayList<>();

        if(ForumActivity.selection == 1){
            if(ForumActivitySubmenu.selection == 1){
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/presentation-de-teams.42/index.rss",
                        "https://community.funcraft.net/forums/recrutement-teams.44/index.rss"}, 1);
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums11Title)));
            }else if(ForumActivitySubmenu.selection == 2){
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/ameliorations.15/index.rss",
                        "https://community.funcraft.net/forums/propositions-de-mini-jeux.14/index.rss"}, 1);
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums12Title)));
            }else if(ForumActivitySubmenu.selection == 3){
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/demande-daide-questions.22/index.rss"}, 1);
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums13Title)));
            }else if(ForumActivitySubmenu.selection == 4){
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/bugs-traites.23/index.rss"}, 1);
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums14Title)));
            }
        }else if(ForumActivitySubmenu.selection == 2){
            if(ForumActivitySubmenu.selection == 0){
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/les-jeux-forum.10/index.rss"}, 1);
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums20Title)));
            }else if(ForumActivitySubmenu.selection == 1){
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/presentation-du-staff.11/index.rss"}, 1);
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums21Title)));
            }else if(ForumActivitySubmenu.selection == 2){
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/demandes-de-creations.31/index.rss"}, 1);
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums22Title)));
            }
        }

        for (int i = 0; i < SubtitleOption.size(); i++) {
            s = new StringsForAdapterSubmenu2();
            s.setTitle(list1.get(i));
            s.setSubtitle(SubtitleOption.get(i));
            Strings.add(s);
        }

        return Strings;
    }
}

class StringsForAdapterItems {
    private String title = "";
    private String subtitle = "";
    private String dates = "";

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

    public String getDates() {
        return dates;
    }

    public void setDates(String s) {
        this.dates = s;
    }
}

class CustomBaseAdapterItems extends BaseAdapter {
    private static ArrayList<StringsForAdapterItems> StringArray;
    private LayoutInflater Inflater;

    public CustomBaseAdapterItems(Context context, ArrayList<StringsForAdapterItems> Strings) {
        StringArray = Strings;
        Inflater = LayoutInflater.from(context);
    }

    public ArrayList<StringsForAdapterItems> getStringArray(){return StringArray;}

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
            convertView = Inflater.inflate(R.layout.listviewcustom_items, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.txtSubtitle = (TextView) convertView.findViewById(R.id.subtitle);
            holder.txtDates = (TextView) convertView.findViewById(R.id.dates);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtTitle.setText(StringArray.get(position).getTitle());
        holder.txtSubtitle.setText(StringArray.get(position).getSubtitle());
        holder.txtDates.setText(StringArray.get(position).getDates());

        return convertView;
    }

    static class ViewHolder {
        TextView txtTitle;
        TextView txtSubtitle;
        TextView txtDates;
    }
}

class StringsForAdapterSubmenu2 {
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

class CustomBaseAdapterSubmenu2 extends BaseAdapter {
    private static ArrayList<StringsForAdapterSubmenu2> StringArray;
    private LayoutInflater Inflater;

    public CustomBaseAdapterSubmenu2(Context context, ArrayList<StringsForAdapterSubmenu2> Strings) {
        StringArray = Strings;
        Inflater = LayoutInflater.from(context);
    }

    public ArrayList<StringsForAdapterSubmenu2> getStringArray(){return StringArray;}

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