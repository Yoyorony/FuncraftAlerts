package yoyorony.me.funcraftv2alerts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
    public static ArrayList<String> Title;
    public static ArrayList<String> Subtitle;
    public static ArrayList<String> Dates;
    public static ArrayList<String> Links;
    public static ArrayList<String> SubtitleOption;
    public static int selection = -1;
    public static boolean loaded = false;
    public static boolean loadedOption = false;
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

        listviexRSS = (ListView) findViewById(R.id.listViewRSS);
        listviexRSSOptions = (ListView) findViewById(R.id.listViewRSSOptions);

        ArrayList<StringsForAdapterItems> forumList = getStringsAdapter();
        ArrayList<StringsForAdapterSubmenu2> forumList2 = getStringsAdapter2();

        listviexRSS.setAdapter(new CustomBaseAdapterItems(this, forumList));
        listviexRSSOptions.setAdapter(new CustomBaseAdapterSubmenu2(this, forumList2));

        listviexRSS.setOnItemClickListener(ListViewListener);
        listviexRSSOptions.setOnItemClickListener(ListViewListenerOptions);
    }

    private ArrayList<StringsForAdapterItems> getStringsAdapter() {
        ArrayList<StringsForAdapterItems> Strings = new ArrayList<>();

        StringsForAdapterItems s;
        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        ArrayList<String> list3 = new ArrayList<>();

        boolean go = false;
        switch (ForumActivity.selection) {
            case 0:
                switch (ForumActivitySubmenu.selection) {
                    case 0:
                        go = true;
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/annonces-regles.2/index.rss", 0);
                        break;
                    case 1:
                        go = true;
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/recrutement-staff.5/index.rss", 0);
                        break;
                }
                break;
            case 1:
                switch (ForumActivitySubmenu.selection) {
                    case 0:
                        go = true;
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/discussions.7/index.rss", 0);
                        break;
                    case 1:
                        go = true;
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/teams.41/index.rss", 0);
                        break;
                    case 2:
                        go = true;
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/suggestions-idees.13/index.rss", 0);
                        break;
                    case 3:
                        go = true;
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/astuces-entraide.21/index.rss", 0);
                        break;
                    case 4:
                        go = true;
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/signaler-un-bug.20/index.rss", 0);
                        break;
                }
                break;
            case 2:
                switch (ForumActivitySubmenu.selection) {
                    case 0:
                        go = true;
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/hors-sujet.9/index.rss", 0);
                        break;
                    case 1:
                        go = true;
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/presentez-vous.8/index.rss", 0);
                        break;
                    case 2:
                        go = true;
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/vos-talents-creations.29/index.rss", 0);
                        break;
                }
                break;
            case 3:
                switch (ForumActivitySubmenu.selection) {
                    case 0:
                        go = true;
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/discussions-minecraft.36/index.rss", 0);
                        break;
                    case 1:
                        go = true;
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/resource-packs.33/index.rss", 0);
                        break;
                    case 2:
                        go = true;
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/maps-constructions-redstone.34/index.rss", 0);
                        break;
                    case 3:
                        go = true;
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/mods-plugins-outils.35/index.rss", 0);
                        break;
                }
                break;
        }

        if(go){
            while (!loaded) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                    break;
                }
            }
            list1.addAll(Title);
            list2.addAll(Subtitle);
            list3.addAll(Dates);
        }

        for (int i = 0; i < list1.size(); i++) {
            s = new StringsForAdapterItems();
            s.setTitle(list1.get(i));
            s.setSubtitle(list2.get(i));
            s.setDates(list3.get(i));
            Strings.add(s);
        }

        return Strings;
    }

    private ArrayList<StringsForAdapterSubmenu2> getStringsAdapter2() {
        ArrayList<StringsForAdapterSubmenu2> Strings = new ArrayList<>();

        StringsForAdapterSubmenu2 s;
        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();

        boolean go = false;
        if(ForumActivity.selection == 1){
            if(ForumActivitySubmenu.selection == 1){
                go = true;
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/presentation-de-teams.42/index.rss",
                        "https://community.funcraft.net/forums/recrutement-teams.44/index.rss"}, 1);
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums11Title)));
            }else if(ForumActivitySubmenu.selection == 2){
                go = true;
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/ameliorations.15/index.rss",
                        "https://community.funcraft.net/forums/propositions-de-mini-jeux.14/index.rss"}, 1);
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums12Title)));
            }else if(ForumActivitySubmenu.selection == 3){
                go = true;
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/demande-daide-questions.22/index.rss"}, 1);
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums13Title)));
            }else if(ForumActivitySubmenu.selection == 4){
                go = true;
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/bugs-traites.23/index.rss"}, 1);
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums14Title)));
            }
        }else if(ForumActivitySubmenu.selection == 2){
            if(ForumActivitySubmenu.selection == 0){
                go = true;
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/les-jeux-forum.10/index.rss"}, 1);
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums20Title)));
            }else if(ForumActivitySubmenu.selection == 1){
                go = true;
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/presentation-du-staff.11/index.rss"}, 1);
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums21Title)));
            }else if(ForumActivitySubmenu.selection == 2){
                go = true;
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/demandes-de-creations.31/index.rss"}, 1);
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums22Title)));
            }
        }

        if(go){
            while (!loaded) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                    break;
                }
            }
            list2.addAll(SubtitleOption);
        }

        for (int i = 0; i < list1.size(); i++) {
            s = new StringsForAdapterSubmenu2();
            s.setTitle(list1.get(i));
            s.setSubtitle(list2.get(i));
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