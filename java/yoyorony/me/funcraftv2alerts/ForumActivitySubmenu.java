package yoyorony.me.funcraftv2alerts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
    public static ArrayList<String> Subtitle = new ArrayList<>();
    public static boolean loaded = false;
    public static int selection = -1;
    private OnItemClickListener ListViewListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selection = position;
            startActivity(new Intent(getBaseContext(), ForumActivityItems.class));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_apercuforum);
        super.onCreate(savedInstanceState);

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

        ArrayList<StringsForAdapterSubmenu> forumList = getStringsAdapter();

        listviexRSS.setAdapter(new CustomBaseAdapterSubmenu(this, forumList));

        listviexRSS.setOnItemClickListener(ListViewListener);
    }

    private ArrayList<StringsForAdapterSubmenu> getStringsAdapter() {
        ArrayList<StringsForAdapterSubmenu> Strings = new ArrayList<>();

        StringsForAdapterSubmenu s;
        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        switch (ForumActivity.selection) {
            case 0:
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/annonces-regles.2/index.rss",
                        "https://community.funcraft.net/forums/recrutement-staff.5/index.rss"}, 0);
                while (!loaded) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                        break;
                    }
                }
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums0Title)));
                list2.addAll(Subtitle);
                break;
            case 1:
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/discussions.7/index.rss",
                        "https://community.funcraft.net/forums/teams.41/index.rss",
                        "https://community.funcraft.net/forums/suggestions-idees.13/index.rss",
                        "https://community.funcraft.net/forums/astuces-entraide.21/index.rss",
                        "https://community.funcraft.net/forums/signaler-un-bug.20/index.rss"}, 0);
                while (!loaded) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                        break;
                    }
                }
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums1Title)));
                list2.addAll(Subtitle);
                break;
            case 2:
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/hors-sujet.9/index.rss",
                        "https://community.funcraft.net/forums/presentez-vous.8/index.rss",
                        "https://community.funcraft.net/forums/vos-talents-creations.29/index.rss"}, 0);
                while (!loaded) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                        break;
                    }
                }
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums2Title)));
                list2.addAll(Subtitle);
                break;
            case 3:
                Connexion.getSubmenuSubtitles(new String[]{"https://community.funcraft.net/forums/discussions-minecraft.36/index.rss",
                        "https://community.funcraft.net/forums/resource-packs.33/index.rss",
                        "https://community.funcraft.net/forums/maps-constructions-redstone.34/index.rss",
                        "https://community.funcraft.net/forums/mods-plugins-outils.35/index.rss"}, 0);
                while (!loaded) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                        break;
                    }
                }
                list1.addAll(Arrays.asList(getResources().getStringArray(R.array.Forums3Title)));
                list2.addAll(Subtitle);
                break;
        }

        for (int i = 0; i < list1.size(); i++) {
            s = new StringsForAdapterSubmenu();
            s.setTitle(list1.get(i));
            s.setSubtitle(list2.get(i));
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