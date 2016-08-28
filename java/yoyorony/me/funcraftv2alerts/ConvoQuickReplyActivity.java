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
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import yoyorony.me.PostsManager.ConvoPost;

public class ConvoQuickReplyActivity extends AppCompatActivity {
    private static ListView listview = null;
    public static String convoCode = "-1";
    public static String lastMessageLink = "";
    public static String convoName = "";
    public static String xfToken = "";
    public static ArrayList<ConvoPost> convoPosts;
    public static AlertDialog waitDialog;
    public static AlertDialog timeoutDialog;
    public static AlertDialog errorDialog;
    public static AlertDialog connexionerrorDialog;
    public static AlertDialog sendingDialog;
    private static CustomBaseAdapterConvoQuickyReply adapter;
    public static boolean timeout;
    public static boolean error;
    public static boolean connexionerror;
    private static TextView replyText;
    private static LinearLayout replyLayout;
    private AdapterView.OnItemClickListener ListViewListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConvoQuickReplyActivity.this)
                    .setCancelable(true)
                    .setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {}
                    });
            final AlertDialog alertDialog = alertDialogBuilder.create();

            LayoutInflater inflaterforgot = ConvoQuickReplyActivity.this.getLayoutInflater();
            View viewclickconvo = inflaterforgot.inflate(R.layout.clickconvodialog, null);
            setTheme(R.style.AppTheme);

            Button viewbutton = new Button(alertDialog.getContext());
            viewbutton.setText(getString(R.string.clickconvoactionview));
            viewbutton.setTextColor(getResources().getColor(R.color.colortext));
            viewbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserintent = new Intent(Intent.ACTION_VIEW, Uri.parse(convoPosts.get(position).getViewLink()));
                    browserintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(browserintent);
                    alertDialog.dismiss();
                }
            });

            final Button replybutton = new Button(alertDialog.getContext());
            replybutton.setText(getString(R.string.clickconvoactionreply));
            replybutton.setTextColor(getResources().getColor(R.color.colortext));
            replybutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replyText.setText(replyText.getText() + "[QUOTE=\"" + convoPosts.get(position).getQuoteParam() + "\"]" + Jsoup.parse(convoPosts.get(position).getMessage()).text() + "[/QUOTE]\n");
                    alertDialog.dismiss();

                    replyText.requestFocus();
                    Selection.setSelection((Editable) replyText.getText(), replyText.length());

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //TODO not working
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                    listview.setPadding(0, 0, 0, replyLayout.getHeight());
                }
            });

            Button reportbutton = new Button(alertDialog.getContext());
            reportbutton.setText(getString(R.string.clickconvoactionreport));
            reportbutton.setTextColor(getResources().getColor(R.color.colortext));
            reportbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    //TODO report
                }
            });

            LinearLayout ll = (LinearLayout) viewclickconvo.findViewById(R.id.clickconvobuttonslayout);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ll.addView(viewbutton, lp);
            ll.addView(replybutton, lp);
            ll.addView(reportbutton, lp);

            if(convoPosts.get(position).getCanEdited()){
                Button editbutton = new Button(alertDialog.getContext());
                editbutton.setText(getString(R.string.clickconvoactionedit));
                editbutton.setTextColor(getResources().getColor(R.color.colortext));
                //TODO action edit
                ll.addView(editbutton, lp);
            }

            alertDialog.setView(viewclickconvo);
            alertDialog.show();
        }
    };
    private View.OnClickListener ButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.outilbutton:
                    //TODO outils BBCode bouton
                    break;
                case R.id.sendbutton:
                    new SendConvoPost().execute(String.valueOf(replyText.getText()));
                    break;
            }
        }
    };
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            listview.setPadding(0, 0, 0, replyLayout.getHeight());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_convopost);
        super.onCreate(savedInstanceState);

        this.setTitle("Conversation : " + convoName);

        buildDialogs();
        waitDialog.show();

        listview = (ListView) findViewById(R.id.listViewConvopost);
        Button outilsButton = (Button) findViewById(R.id.outilbutton);
        Button sendButton = (Button) findViewById(R.id.sendbutton);
        replyText = (TextView) findViewById(R.id.replytextarea);
        replyLayout = (LinearLayout) findViewById(R.id.replylayout);

        adapter = new CustomBaseAdapterConvoQuickyReply(ConvoQuickReplyActivity.this, new ArrayList<StringsForAdapterConvoQuickReply>());
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(ListViewListener);
        outilsButton.setOnClickListener(ButtonListener);
        sendButton.setOnClickListener(ButtonListener);
        replyText.addTextChangedListener(textWatcher);

        new DownloadConvoPosts().execute();
    }

    private class SendConvoPost extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strs) {
            return Connexion.sendConvoPost("<p>" + strs[0].replace("\n", "</p><p>") + "</p>", "https://community.funcraft.net/conversations/" + convoCode, xfToken);
        }

        protected void onPreExecute(){
            timeout = false; error = false; connexionerror = false;
            sendingDialog.show();
        }

        protected void onPostExecute(Void v){
            if(timeout){
                sendingDialog.dismiss();
                timeoutDialog.show();
            }else if(error){
                sendingDialog.dismiss();
                errorDialog.show();
            }else if(connexionerror){
                sendingDialog.dismiss();
                connexionerrorDialog.show();
            }else{
                sendingDialog.dismiss();
                Toast.makeText(ConvoQuickReplyActivity.this, getString(R.string.sendedtoast), Toast.LENGTH_LONG).show();
                replyText.setText("");

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                listview.setPadding(0, 0, 0, replyLayout.getHeight());
            }
        }
    }

    private class DownloadConvoPosts extends AsyncTask<Void, Void, ArrayList<StringsForAdapterConvoQuickReply>> {
        @Override
        protected ArrayList<StringsForAdapterConvoQuickReply> doInBackground(Void... v) {
            return getStringsAdapter();
        }

        protected void onPreExecute(){
            timeout = false; error = false; connexionerror = false;
            convoPosts = new ArrayList<>();
        }

        protected void onPostExecute(ArrayList<StringsForAdapterConvoQuickReply> forumList){
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
            listview.setPadding(0, 0, 0, replyLayout.getHeight());
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

        final ProgressDialog.Builder sendingDialogBuilder = new ProgressDialog.Builder(this);
        view = this.getLayoutInflater().inflate(R.layout.waitdialogue, null);
        ((TextView) view.findViewById(R.id.waitDialogTitle)).setText(R.string.sendingtitle);
        ((TextView) view.findViewById(R.id.waitDialogMessage)).setText(R.string.sendingmessage);
        sendingDialogBuilder.setCancelable(false);
        sendingDialogBuilder.setView(view);
        sendingDialog = sendingDialogBuilder.create();
    }

    private ArrayList<StringsForAdapterConvoQuickReply> getStringsAdapter() {
        ArrayList<StringsForAdapterConvoQuickReply> Strings = new ArrayList<>();

        Connexion.getConvoPosts(convoCode, lastMessageLink);

        for (int i = 0; i < convoPosts.size(); i++) {
            StringsForAdapterConvoQuickReply s = new StringsForAdapterConvoQuickReply();
            s.setConvo(convoPosts.get(i));
            Strings.add(s);
        }

        return Strings;
    }
}

class StringsForAdapterConvoQuickReply {
    private ConvoPost ConvoPost;

    public void setConvo(ConvoPost convo){ConvoPost = convo;}

    public String getWho(){return ConvoPost.getWho();}
    public String getMessage(){return ConvoPost.getMessage();}
    public String getPubDate(){return ConvoPost.getPubDate();}
}

class CustomBaseAdapterConvoQuickyReply extends BaseAdapter {
    private static ArrayList<StringsForAdapterConvoQuickReply> StringArray;
    private LayoutInflater Inflater;

    public CustomBaseAdapterConvoQuickyReply(Context context, ArrayList<StringsForAdapterConvoQuickReply> Strings) {
        StringArray = Strings;
        Inflater = LayoutInflater.from(context);
    }

    public ArrayList<StringsForAdapterConvoQuickReply> getStringArray() {
        return StringArray;
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
            convertView = Inflater.inflate(R.layout.listviewcustom_convoposts, null);
            holder = new ViewHolder();
            holder.txtWho = (TextView) convertView.findViewById(R.id.who);
            holder.txtMessage = (TextView) convertView.findViewById(R.id.message);
            holder.txtPubDate = (TextView) convertView.findViewById(R.id.pubdate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtWho.setText(StringArray.get(position).getWho());
        holder.txtMessage.setText(Html.fromHtml(StringArray.get(position).getMessage()));
        holder.txtPubDate.setText(StringArray.get(position).getPubDate());

        return convertView;
    }

    static class ViewHolder {
        TextView txtWho;
        TextView txtMessage;
        TextView txtPubDate;
    }
}
