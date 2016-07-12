package yoyorony.me.funcraftv2alerts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class ForumActivityItemsSecond extends AppCompatActivity {
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
            startActivity(new Intent(getBaseContext(), ForumActivityItemsThird.class));
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
        switch (ForumActivity.selection) {
            case 1:
                switch (ForumActivitySubmenu.selection) {
                    case 1:
                        switch (ForumActivityItems.selection){
                            case 0:
                                Connexion.getItemsFeed("https://community.funcraft.net/forums/presentation-de-teams.42/index.rss", 1);
                                break;
                            case 1:
                                Connexion.getItemsFeed("https://community.funcraft.net/forums/recrutement-teams.44/index.rss", 1);
                                break;
                        }
                        break;
                    case 2:
                        switch (ForumActivityItems.selection){
                            case 0:
                                Connexion.getItemsFeed("https://community.funcraft.net/forums/ameliorations.15/index.rss", 1);
                                break;
                            case 1:
                                Connexion.getItemsFeed("https://community.funcraft.net/forums/propositions-de-mini-jeux.14/index.rss", 1);
                                break;
                        }
                        break;
                    case 3:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/demande-daide-questions.22/index.rss", 1);
                        break;
                    case 4:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/bugs-traites.23/index.rss", 1);
                        break;
                }
                break;
            case 2:
                switch (ForumActivitySubmenu.selection) {
                    case 0:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/les-jeux-forum.10/index.rss", 1);
                        break;
                    case 1:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/presentation-du-staff.11/index.rss", 1);
                        break;
                    case 2:
                        Connexion.getItemsFeed("https://community.funcraft.net/forums/demandes-de-creations.31/index.rss", 1);
                        break;
                }
                break;
        }

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
            if(ForumActivitySubmenu.selection == 2){
                if(ForumActivityItems.selection == 0){
                    go = true;
                    Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/ameliorations-acceptees.16/index.rss",
                            "https://community.funcraft.net/forums/ameliorations-refusees.17/index.rss"}, 2);
                }else if(ForumActivityItems.selection == 1) {
                    go = true;
                    Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/propositions-acceptees.18/index.rss",
                            "https://community.funcraft.net/forums/propositions-refusees.19/index.rss"}, 2);
                }
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
            list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums110Title)));
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