package yoyorony.me.funcraftv2alerts;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class AlertReaderActivity extends AppCompatActivity {
    public static ListView listviexRSS = null;
    public static SwipeRefreshLayout swiper = null;
    public static ArrayList<String> Who;
    public static ArrayList<String> Where;
    public static ArrayList<Integer> Type;
    public static ArrayList<String> Link;
    public static ArrayList<Boolean> New;
    public static ArrayList<String> Date; //TODO date
    public static CustomBaseAdapterAlerts adapter;
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
            Intent browserintent = new Intent(Intent.ACTION_VIEW, Uri.parse(Link.get(position)));
            browserintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(browserintent);
        }
    };
    private SwipeRefreshLayout.OnRefreshListener SwiperListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            new DownloadAlerts().execute();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_apercualerts);
        super.onCreate(savedInstanceState);

        this.setTitle("Aperçu des alertes");

        buildDialogs();
        waitDialog.show();

        listviexRSS = (ListView) findViewById(R.id.listViewAlerts);
        swiper = (SwipeRefreshLayout) findViewById(R.id.swipeAlerts);

        swiper.setOnRefreshListener(SwiperListener);

        adapter = new CustomBaseAdapterAlerts(this, new ArrayList<StringsForAdapterAlerts>());
        listviexRSS.setAdapter(adapter);

        listviexRSS.setOnItemClickListener(ListViewListener);

        new DownloadAlerts().execute();
    }

    private class DownloadAlerts extends AsyncTask<Void, Void, ArrayList<StringsForAdapterAlerts>> {
        @Override
        protected ArrayList<StringsForAdapterAlerts> doInBackground(Void... v) {
            return getStringsAdapter();
        }

        protected void onPreExecute(){
            timeout = false; error = false; connexionerror = false;
            Who = new ArrayList<>();
            Where = new ArrayList<>();
            Type = new ArrayList<>();
            Link = new ArrayList<>();
            New = new ArrayList<>();
        }

        protected void onPostExecute(ArrayList<StringsForAdapterAlerts> forumList){
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

    private ArrayList<StringsForAdapterAlerts> getStringsAdapter() {
        ArrayList<StringsForAdapterAlerts> Strings = new ArrayList<>();

        Connexion.getAlerts();

        for (int i = 0; i < Who.size(); i++) {
            StringsForAdapterAlerts s = new StringsForAdapterAlerts();

            s.setWho(Who.get(i));
            s.setWhere(Where.get(i));
            s.setImageResID(Type.get(i));
            s.setNew(New.get(i));
            Strings.add(s);
        }

        return Strings;
    }
}

class StringsForAdapterAlerts {
    private String who = "";
    private String where = "";
    private boolean neww = false;
    private int imageResID;

    public String getWho() {
        return who;
    }

    public void setWho(String s) {
        this.who = s;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String s) {
        this.where = s;
    }

    public int getImageResID(){
        return imageResID;
    }

    public void setImageResID(int type){
        switch (type){
            case 0: case 6: case 7: case 8:
                this.imageResID = R.drawable.ic_reply_black_24dp;
                break;
            case 1:
                this.imageResID = R.drawable.ic_attach_file_black_24dp;
                break;
            case 2:
                this.imageResID = R.drawable.ic_format_quote_black_24dp;
                break;
            case 3: case 9:
                this.imageResID = R.drawable.ic_at_sign;
                break;
            case 4: case 10: case 11:
                this.imageResID = R.drawable.ic_favorite_black_24dp;
                break;
            case 5: case 14:
                this.imageResID = R.drawable.ic_create_black_24dp;
                break;
            case 12:
                this.imageResID = R.drawable.ic_person_add_black_24dp;
                break;
            case 13:
                this.imageResID = R.drawable.ic_cup;
        }
    }

    public boolean getNew() {
        return neww;
    }

    public void setNew(boolean b){
        neww = b;
    }
}

class CustomBaseAdapterAlerts extends BaseAdapter {
    private static ArrayList<StringsForAdapterAlerts> StringArray;
    private LayoutInflater Inflater;

    public CustomBaseAdapterAlerts(Context context, ArrayList<StringsForAdapterAlerts> Strings) {
        StringArray = Strings;
        Inflater = LayoutInflater.from(context);
    }

    public ArrayList<StringsForAdapterAlerts> getStringArray() {return StringArray;}

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
            convertView = Inflater.inflate(R.layout.listviewcustom_alerts, null);
            holder = new ViewHolder();
            holder.txtWho = (TextView) convertView.findViewById(R.id.who);
            holder.txtWhere = (TextView) convertView.findViewById(R.id.where);
            holder.imgType = (ImageView) convertView.findViewById(R.id.type);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtWhere.setText(StringArray.get(position).getWhere());
        holder.imgType.setImageResource(StringArray.get(position).getImageResID());

        if(StringArray.get(position).getNew()){
            holder.txtWho.setText(StringArray.get(position).getWho() + " " + '\u2731');
            holder.txtWhere.setTypeface(null, Typeface.BOLD);
        }else{
            holder.txtWho.setText(StringArray.get(position).getWho());
            holder.txtWhere.setTypeface(null, Typeface.NORMAL);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView txtWho;
        TextView txtWhere;
        ImageView imgType;
    }
}