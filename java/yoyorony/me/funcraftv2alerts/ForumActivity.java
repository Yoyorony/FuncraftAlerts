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

public class ForumActivity extends AppCompatActivity {
    public static ListView listviexRSS = null;
    public static int selection = -1;
    private OnItemClickListener ListViewListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selection = position;
            startActivity(new Intent(getBaseContext(), ForumActivitySubmenu.class));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_apercuforum);
        super.onCreate(savedInstanceState);

        this.setTitle("Aperçu des forums");

        listviexRSS = (ListView) findViewById(R.id.listViewRSS);

        ArrayList<StringsForAdapter> forumList = getStringsAdapter();

        listviexRSS.setAdapter(new CustomBaseAdapter(this, forumList));

        listviexRSS.setOnItemClickListener(ListViewListener);
    }

    private ArrayList<StringsForAdapter> getStringsAdapter() {
        ArrayList<StringsForAdapter> Strings = new ArrayList<>();

        StringsForAdapter s;
        ArrayList<String> list1 = new ArrayList<>();
        list1.addAll(Arrays.asList(getResources().getStringArray(R.array.ForumsPrincipauxTitle)));
        ArrayList<String> list2 = new ArrayList<>();
        list2.addAll(Arrays.asList(getResources().getStringArray(R.array.ForumsPrincipauxSubtitle)));

        for (int i = 0; i < list1.size(); i++) {
            s = new StringsForAdapter();
            s.setTitle(list1.get(i));
            s.setSubtitle(list2.get(i));
            Strings.add(s);
        }

        return Strings;
    }
}

class StringsForAdapter {
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

class CustomBaseAdapter extends BaseAdapter {
    private static ArrayList<StringsForAdapter> StringArray;
    private LayoutInflater Inflater;

    public CustomBaseAdapter(Context context, ArrayList<StringsForAdapter> Strings) {
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
            convertView = Inflater.inflate(R.layout.listviewcustom, null);
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
