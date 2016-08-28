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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ConvoReaderActivity extends AppCompatActivity {
    private static ListView listviex = null;
    private static SwipeRefreshLayout swiper = null;
    public static ArrayList<String> Title;
    public static ArrayList<String> PubDateMessage;
    public static ArrayList<String> LastGuy;
    public static ArrayList<Boolean> Lue;
    public static ArrayList<String> Link;
    private static CustomBaseAdapterConvos adapter;
    private static AlertDialog waitDialog;
    private static AlertDialog timeoutDialog;
    private static AlertDialog errorDialog;
    private static AlertDialog connexionerrorDialog;
    public static boolean timeout;
    public static boolean error;
    public static boolean connexionerror;
    private OnItemClickListener ListViewListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String temp = Link.get(position);
            temp = temp.substring(temp.indexOf("conversations/"));
            temp = temp.substring(temp.indexOf(".") +1);
            ConvoQuickReplyActivity.convoCode = temp.substring(0, temp.indexOf("/"));

            ConvoQuickReplyActivity.lastMessageLink = Link.get(position);

            ConvoQuickReplyActivity.convoName = Title.get(position);

            startActivity(new Intent(getBaseContext(), ConvoQuickReplyActivity.class));
        }
    };
    private SwipeRefreshLayout.OnRefreshListener SwiperListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            new DownloadConvos().execute();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_apercuconvos);
        super.onCreate(savedInstanceState);

        this.setTitle("Aperçu des conversations");

        buildDialogs();
        waitDialog.show();

        listviex = (ListView) findViewById(R.id.listViewConvos);
        swiper = (SwipeRefreshLayout) findViewById(R.id.swipeConvos);

        swiper.setOnRefreshListener(SwiperListener);

        adapter = new CustomBaseAdapterConvos(ConvoReaderActivity.this, new ArrayList<StringsForAdapterConvos>());
        listviex.setAdapter(adapter);

        listviex.setOnItemClickListener(ListViewListener);

        new DownloadConvos().execute();
    }

    private class DownloadConvos extends AsyncTask<Void, Void, ArrayList<StringsForAdapterConvos>>{
        @Override
        protected ArrayList<StringsForAdapterConvos> doInBackground(Void... v) {
            return getStringsAdapter();
        }

        protected void onPreExecute(){
            timeout = false; error = false; connexionerror = false;
            Link = new ArrayList<>();
            Lue = new ArrayList<>();
            LastGuy = new ArrayList<>();
            PubDateMessage = new ArrayList<>();
            Title = new ArrayList<>();
        }

        protected void onPostExecute(ArrayList<StringsForAdapterConvos> forumList){
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

    private ArrayList<StringsForAdapterConvos> getStringsAdapter() {
        ArrayList<StringsForAdapterConvos> Strings = new ArrayList<>();

        Connexion.getConvos();

        for (int i = 0; i < Title.size(); i++) {
            StringsForAdapterConvos s = new StringsForAdapterConvos();

            s.setTitle(Title.get(i));
            s.setLastGuy(LastGuy.get(i));
            s.setPubDateMessage(PubDateMessage.get(i));
            s.setLue(Lue.get(i));
            Strings.add(s);
        }

        return Strings;
    }
}

class StringsForAdapterConvos {
    private String Title = "";
    private String PubDateMessage = "";
    private String LastGuy;
    private int Lue;
    private boolean LueBool;

    public void setTitle(String title) {
        Title = title;
    }

    public String getTitle() {
        return Title;
    }

    public void setPubDateMessage(String pubDateMessage) {
        PubDateMessage = pubDateMessage;
    }

    public String getPubDateMessage() {
        return PubDateMessage;
    }

    public void setLastGuy(String lastGuy) {
        LastGuy = lastGuy;
    }

    public String getLastGuy() {
        return LastGuy;
    }

    public void setLue(boolean lue) {
        if(lue){Lue = R.drawable.ic_drafts_black_24dp;}
        else{Lue = R.drawable.ic_mail_black_24dp;}
        LueBool = lue;
    }

    public int getLue() {
        return Lue;
    }

    public boolean getLueBool() {
        return LueBool;
    }
}

class CustomBaseAdapterConvos extends BaseAdapter {
    private static ArrayList<StringsForAdapterConvos> StringArray;
    private LayoutInflater Inflater;

    public CustomBaseAdapterConvos(Context context, ArrayList<StringsForAdapterConvos> Strings) {
        StringArray = Strings;
        Inflater = LayoutInflater.from(context);
    }

    public ArrayList<StringsForAdapterConvos> getStringArray(){return StringArray;}

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
            convertView = Inflater.inflate(R.layout.listviewcustom_convos, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.txtLastGuy = (TextView) convertView.findViewById(R.id.lastguy);
            holder.txtPubDate = (TextView) convertView.findViewById(R.id.pubdate);
            holder.imgLue = (ImageView) convertView.findViewById(R.id.lue);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtTitle.setText(StringArray.get(position).getTitle());
        holder.txtLastGuy.setText(StringArray.get(position).getLastGuy());
        holder.txtPubDate.setText(StringArray.get(position).getPubDateMessage());
        holder.imgLue.setImageResource(StringArray.get(position).getLue());

        if(!StringArray.get(position).getLueBool()){
            holder.txtLastGuy.setTypeface(null, Typeface.BOLD);
            holder.txtPubDate.setTypeface(null, Typeface.BOLD_ITALIC);
        }else{
            holder.txtLastGuy.setTypeface(null, Typeface.NORMAL);
            holder.txtPubDate.setTypeface(null, Typeface.ITALIC);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView txtTitle;
        TextView txtLastGuy;
        TextView txtPubDate;
        ImageView imgLue;
    }
}