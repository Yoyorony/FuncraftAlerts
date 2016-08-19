package yoyorony.me.funcraftv2alerts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ForumActivityItemsThird extends AppCompatActivity {
    public static ListView listviexRSS = null;
    public static ArrayList<String> Title;
    public static ArrayList<String> Subtitle;
    public static ArrayList<String> Dates;
    public static ArrayList<String> Links;
    public static boolean loaded = false;
    private AdapterView.OnItemClickListener ListViewListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent browserintent = new Intent(Intent.ACTION_VIEW, Uri.parse(Links.get(position)));
            browserintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(browserintent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_apercuforum);
        super.onCreate(savedInstanceState);

        listviexRSS = (ListView) findViewById(R.id.listViewRSS);

        ArrayList<StringsForAdapterItems> forumList = getStringsAdapter();

        listviexRSS.setAdapter(new CustomBaseAdapterItems(this, forumList));

        listviexRSS.setOnItemClickListener(ListViewListener);
    }

    private ArrayList<StringsForAdapterItems> getStringsAdapter() {
        ArrayList<StringsForAdapterItems> Strings = new ArrayList<>();

        StringsForAdapterItems s;
        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        ArrayList<String> list3 = new ArrayList<>();
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
}