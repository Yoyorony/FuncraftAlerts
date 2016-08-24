package yoyorony.me.funcraftv2alerts;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

@SuppressLint("InflateParams")
public class MainActivity extends AppCompatActivity {
    /**
     * TODO todo :
     * - optimisation/simplification du code (surtout notifs)
     * - consult flux de nouvelle (pour 1.2.0)
     * - connect direct consult (doc html pour POST)
     * - activity_profilepost
     *
     * TODO totest :
     * - debug consult alerts + convos
     */

    public static AlertDialog.Builder alertDialog;
    public static LayoutInflater inflaterDialog;
    public static Context mContext;

    public static Handler ConnectHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            String[] s = ((String) message.obj).split(":777:");
            MainActivity.alert(s[0], s[1]);
        }
    };
    public static Handler MAJHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            String[] s = ((String) message.obj).split(":777:");
            MainActivity.alertMAJ(s[0], s[1]);
        }
    };

    public static TextView header1 = null;
    public static EditText name = null;
    public static EditText mdp = null;
    public static Button connect = null;
    public static Button forgot = null;
    public static ProgressBar progressBarConnect = null;
    public static TextView header2 = null;
    public static Switch activenotif = null;
    public static TextView alertstext = null;
    public static CheckBox alerts = null;
    public static TextView convostext = null;
    public static CheckBox convos = null;
    public static TextView freqtext = null;
    public static SeekBar freqseek = null;
    public static TextView sonnerietext = null;
    public static CheckBox sonnerie = null;
    public static TextView vibreurtext = null;
    public static CheckBox vibreur = null;
    public static ImageButton vibreurparam = null;
    public static TextView ledtext = null;
    public static CheckBox led = null;
    public static ImageButton ledparam = null;
    public static TextView connexiontext = null;
    public static ImageButton connexion = null;
    public static TextView header5 = null;
    public static Button forums = null;
    public static Button consultalert = null;
    public static Button consultconvo = null;
    public static TextView header4 = null;
    public static Button about = null;
    public static Button aide = null;
    public static Button checkversion = null;
    public static ProgressBar progressBarCheckversion = null;
    public static Button changelog = null;
    public static int[] currentnotifcontent = new int[]{0, 0};
    private OnClickListener ButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.connectbutton:
                    progressBarConnect.setIndeterminate(true);
                    Connexion.testConnexion(new String[][]{{getString(R.string.wififailtitle), getString(R.string.wififailmessage)},
                            {getString(R.string.networkfailtitle), getString(R.string.networkfailmessage)},
                            {getString(R.string.connexionfailtitle), getString(R.string.connexionfailmessage)},
                            {getString(R.string.idsfailtitle), getString(R.string.idsfailmessage)},
                            {getString(R.string.unknownerrortitle), getString(R.string.unknownerrormessage)},
                            {getString(R.string.invalididstitle), getString(R.string.invalididsmessage)},
                            {getString(R.string.connectsuccestitle), getString(R.string.connectsuccesmessage)},
                            {getString(R.string.timeouttitle), getString(R.string.timeoutmessage)},
                            {getString(R.string.deuxgfailtitle), getString(R.string.deuxgfailmessage)}});
                    break;
                case R.id.forgetbutton:
                    LayoutInflater inflaterforgot = MainActivity.this.getLayoutInflater();
                    View viewforgot = inflaterforgot.inflate(R.layout.forgotdialog, null);
                    AlertDialog.Builder alertDialogBuilderforgot = new AlertDialog.Builder(MainActivity.this)
                            .setView(viewforgot)
                            .setCancelable(true)
                            .setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    FunApp.preferenceseditor.putString("name", "");
                                    FunApp.preferenceseditor.putString("mdp", "");
                                    FunApp.preferenceseditor.commit();
                                    name.setText("");
                                    mdp.setText("");
                                    try {
                                        Notifs.notifmanager.cancel(2101);
                                    } catch (NullPointerException ignored) {}
                                    FunApp.alerts[0] = 0;
                                    FunApp.alerts[1] = 0;
                                    currentnotifcontent[0] = 0;
                                    currentnotifcontent[1] = 0;
                                    FunApp.preferenceseditor.putBoolean("notified", false);
                                }
                            })
                            .setNegativeButton("NON", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    alertDialogBuilderforgot.create().show();
                    break;
                case R.id.aproposbutton:
                    MainActivity.alert(getString(R.string.apropostitle), getString(R.string.aproposmessage));
                    break;
                case R.id.helpbutton:
                    MainActivity.alertscroll(getString(R.string.helptitle), getString(R.string.helpmessage));
                    break;
                case R.id.checkversionbutton:
                    progressBarCheckversion.setIndeterminate(true);
                    Connexion.checkVersion((new String[][]{{getString(R.string.wififailtitle), getString(R.string.wififailmessage)},
                            {getString(R.string.networkfailtitle), getString(R.string.networkfailmessage)},
                            {getString(R.string.connexionfailtitle), getString(R.string.connexionfailmessage)},
                            {getString(R.string.newversiontitle), getString(R.string.newversionmessage)},
                            {getString(R.string.nonewversiontitle), getString(R.string.nonewversionmessage)},
                            {getString(R.string.timeouttitle), getString(R.string.timeoutmessage)},
                            {getString(R.string.deuxgfailtitle), getString(R.string.deuxgfailmessage)}}));
                    break;
                case R.id.changelogbutton:
                    MainActivity.alertscroll(getString(R.string.changelogtitle), getString(R.string.changelogmessage));
                    break;
                case R.id.vibreurcheckBoxparam:
                    LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                    View view = inflater.inflate(R.layout.vibreurparamdialog, null);
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.PaternsArray, R.layout.spinnercustom);
                    adapter.setDropDownViewResource(R.layout.spinnercustomin);
                    final Spinner spinner1 = (Spinner) view.findViewById(R.id.spinnervibreurparam1);
                    final Spinner spinner2 = (Spinner) view.findViewById(R.id.spinnervibreurparam2);
                    final Spinner spinner3 = (Spinner) view.findViewById(R.id.spinnervibreurparam3);
                    Button test = (Button) view.findViewById(R.id.vibreurtest);
                    spinner1.setAdapter(adapter);
                    spinner2.setAdapter(adapter);
                    spinner3.setAdapter(adapter);
                    final TextView textView = (TextView) view.findViewById(R.id.vibreurparamtext);
                    final Switch switchv = (Switch) view.findViewById(R.id.vibreurparamSwitch);
                    switchv.getThumbDrawable().setColorFilter(Color.parseColor(getString(R.string.codecolorcheckred)), android.graphics.PorterDuff.Mode.SRC_IN);
                    boolean custom = FunApp.preferences.getBoolean("customvibreur", false);
                    switchv.setChecked(custom);
                    if (custom) {
                        textView.setTextColor(Color.BLACK);
                    } else {
                        textView.setTextColor(Color.GRAY);
                    }
                    spinner1.setEnabled(custom);
                    spinner1.setSelection(FunApp.preferences.getInt("customvibreur1", 0));
                    spinner1.setAlpha(custom ? 1.0f : 0.4f);
                    spinner2.setEnabled(custom);
                    spinner2.setSelection(FunApp.preferences.getInt("customvibreur2", 0));
                    spinner2.setAlpha(custom ? 1.0f : 0.4f);
                    spinner3.setEnabled(custom);
                    spinner3.setSelection(FunApp.preferences.getInt("customvibreur3", 0));
                    spinner3.setAlpha(custom ? 1.0f : 0.4f);
                    switchv.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                textView.setTextColor(Color.BLACK);
                            } else {
                                textView.setTextColor(Color.GRAY);
                            }
                            spinner1.setEnabled(isChecked);
                            spinner1.setAlpha(isChecked ? 1.0f : 0.4f);
                            spinner2.setEnabled(isChecked);
                            spinner2.setAlpha(isChecked ? 1.0f : 0.4f);
                            spinner3.setEnabled(isChecked);
                            spinner3.setAlpha(isChecked ? 1.0f : 0.4f);
                        }
                    });
                    test.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            long[] patern;
                            if (switchv.isChecked()) {
                                patern = new long[6];
                                patern[0] = 0;
                                patern[1] = (spinner1.getSelectedItemPosition() == 0 ? 0 : (spinner1.getSelectedItemPosition() == 1 ? 500 : (spinner1.getSelectedItemPosition() == 2 ? 200 : 100)));
                                patern[2] = patern[1];
                                patern[3] = (spinner2.getSelectedItemPosition() == 0 ? 0 : (spinner2.getSelectedItemPosition() == 1 ? 500 : (spinner2.getSelectedItemPosition() == 2 ? 200 : 100)));
                                patern[4] = patern[3];
                                patern[5] = (spinner3.getSelectedItemPosition() == 0 ? 0 : (spinner3.getSelectedItemPosition() == 1 ? 500 : (spinner3.getSelectedItemPosition() == 2 ? 200 : 100)));
                            } else {
                                patern = new long[]{0, 200, 200, 500};
                            }
                            ((Vibrator) getBaseContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(patern, -1);
                        }
                    });
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this)
                            .setView(view)
                            .setCancelable(true)
                            .setPositiveButton("APPLIQUER", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    FunApp.preferenceseditor.putBoolean("customvibreur", switchv.isChecked());
                                    FunApp.preferenceseditor.putInt("customvibreur1", spinner1.getSelectedItemPosition());
                                    FunApp.preferenceseditor.putInt("customvibreur2", spinner2.getSelectedItemPosition());
                                    FunApp.preferenceseditor.putInt("customvibreur3", spinner3.getSelectedItemPosition());
                                    FunApp.preferenceseditor.commit();
                                }
                            })
                            .setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    alertDialogBuilder.create().show();
                    break;
                case R.id.ledcheckBoxparam:
                    LayoutInflater inflater1 = MainActivity.this.getLayoutInflater();
                    View view1 = inflater1.inflate(R.layout.ledparamdialog, null);
                    ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(MainActivity.this, R.array.LedColorArray, R.layout.spinnercustom);
                    adapter1.setDropDownViewResource(R.layout.spinnercustomin);
                    final Spinner spinner11 = (Spinner) view1.findViewById(R.id.spinnerledparam);
                    spinner11.setSelection(FunApp.preferences.getInt("customcolorled", 0));
                    spinner11.setAdapter(adapter1);
                    AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(MainActivity.this)
                            .setView(view1)
                            .setCancelable(true)
                            .setPositiveButton("APPLIQUER", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    FunApp.preferenceseditor.putInt("customcolorled", spinner11.getSelectedItemPosition());
                                    FunApp.preferenceseditor.commit();
                                }
                            })
                            .setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    alertDialogBuilder1.create().show();
                    break;
                case R.id.connexionparam:
                    LayoutInflater inflater2 = MainActivity.this.getLayoutInflater();
                    View view2 = inflater2.inflate(R.layout.connexiondialog, null);

                    final CheckBox deuxG = (CheckBox) view2.findViewById(R.id.deuxG);
                    deuxG.setChecked(FunApp.preferences.getBoolean("deuxG", false));
                    deuxG.setEnabled(!FunApp.preferences.getBoolean("wifionly", true));
                    final TextView deuxGtext = (TextView) view2.findViewById(R.id.deuxGtext);
                    deuxGtext.setTextColor(FunApp.preferences.getBoolean("wifionly", true) ? Color.GRAY : Color.BLACK);
                    final CheckBox wifionly = (CheckBox) view2.findViewById(R.id.wifionly);
                    wifionly.setChecked(FunApp.preferences.getBoolean("wifionly", true));
                    final CheckBox automaj = (CheckBox) view2.findViewById(R.id.automaj);
                    automaj.setChecked(FunApp.preferences.getBoolean("automaj", true));
                    wifionly.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            deuxG.setEnabled(!isChecked);
                            deuxGtext.setTextColor(isChecked ? Color.GRAY : Color.BLACK);
                        }
                    });

                    AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(MainActivity.this)
                            .setView(view2)
                            .setCancelable(true)
                            .setPositiveButton("APPLIQUER", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    FunApp.preferenceseditor.putBoolean("automaj", automaj.isChecked());
                                    FunApp.preferenceseditor.putBoolean("wifionly", wifionly.isChecked());
                                    FunApp.preferenceseditor.putBoolean("deuxG", deuxG.isChecked());
                                    FunApp.preferenceseditor.commit();
                                    if (!isMyServiceRunning(AutoMAJ.class) && FunApp.preferences.getBoolean("automaj", true)) {
                                        AutoMAJ.setAlarm(getBaseContext());
                                    } else if (!FunApp.preferences.getBoolean("automaj", true)) {
                                        stopService(new Intent(getBaseContext(), AutoMAJ.class));
                                    }
                                }
                            })
                            .setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    alertDialogBuilder2.create().show();
                    break;
                case R.id.forumbutton:
                    startActivity(new Intent(getBaseContext(), ForumActivity.class));
                    break;
                case R.id.alertbutton:
                    startActivity(new Intent(getBaseContext(), AlertReaderActivity.class));
                    break;
                case R.id.convobutton:
                    startActivity(new Intent(getBaseContext(), ConvoReaderActivity.class));
                    break;
            }
        }
    };
    private OnCheckedChangeListener ActivListener = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!isChecked) {
                alertstext.setTextColor(Color.GRAY);
                alerts.setEnabled(false);
                convostext.setTextColor(Color.GRAY);
                convos.setEnabled(false);
                freqtext.setTextColor(Color.GRAY);
                freqseek.setEnabled(false);
                sonnerietext.setTextColor(Color.GRAY);
                sonnerie.setEnabled(false);
                vibreurtext.setTextColor(Color.GRAY);
                vibreur.setEnabled(false);
                vibreurparam.getDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                vibreurparam.setEnabled(false);
                ledtext.setTextColor(Color.GRAY);
                led.setEnabled(false);
                ledparam.getDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                ledparam.setEnabled(false);
                connexiontext.setTextColor(Color.GRAY);
                connexion.getDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                connexion.setEnabled(false);
                FunApp.preferenceseditor.putBoolean("activnotif", false);
                FunApp.preferenceseditor.commit();

                if (isMyServiceRunning(Notifs.class)) {
                    stopService(new Intent(getBaseContext(), Notifs.class));
                }
                try {
                    Notifs.notifmanager.cancel(2101);
                } catch (NullPointerException ignored) {
                }
                currentnotifcontent[0] = 0;
                currentnotifcontent[1] = 0;
                FunApp.preferenceseditor.putBoolean("notified", false);
                FunApp.preferenceseditor.commit();
            } else {
                alertstext.setTextColor(Color.BLACK);
                alerts.setEnabled(true);
                convostext.setTextColor(Color.BLACK);
                convos.setEnabled(true);
                freqtext.setTextColor(Color.BLACK);
                freqseek.setEnabled(true);
                sonnerietext.setTextColor(Color.BLACK);
                sonnerie.setEnabled(true);
                vibreurtext.setTextColor(Color.BLACK);
                vibreur.setEnabled(true);
                vibreurparam.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                vibreurparam.setEnabled(true);
                ledtext.setTextColor(Color.BLACK);
                led.setEnabled(true);
                ledparam.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                ledparam.setEnabled(true);
                connexiontext.setTextColor(Color.BLACK);
                connexion.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                connexion.setEnabled(true);
                FunApp.preferenceseditor.putBoolean("activnotif", true);
                FunApp.preferenceseditor.commit();

                if (!isMyServiceRunning(Notifs.class)) {
                    Notifs.setAlarm(getBaseContext());
                }
            }
        }
    };
    private OnCheckedChangeListener OptionsListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.alertscheckBox:
                    FunApp.preferenceseditor.putBoolean("alerts", isChecked);
                    FunApp.preferenceseditor.commit();
                    Notifs.needRefresh = true;
                    break;
                case R.id.convocheckBox:
                    FunApp.preferenceseditor.putBoolean("convos", isChecked);
                    FunApp.preferenceseditor.commit();
                    Notifs.needRefresh = true;
                    break;
                case R.id.sonneriecheckBox:
                    FunApp.preferenceseditor.putBoolean("sonnerie", isChecked);
                    FunApp.preferenceseditor.commit();
                    break;
                case R.id.vibreurcheckBox:
                    FunApp.preferenceseditor.putBoolean("vibreur", isChecked);
                    FunApp.preferenceseditor.commit();
                    break;
                case R.id.ledcheckBox:
                    FunApp.preferenceseditor.putBoolean("led", isChecked);
                    FunApp.preferenceseditor.commit();
                    break;
            }
        }
    };
    private OnSeekBarChangeListener FreqListener = new OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (!isMyServiceRunning(Notifs.class)) {
                Notifs.setAlarm(getBaseContext());
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (isMyServiceRunning(Notifs.class)) {
                stopService(new Intent(getBaseContext(), Notifs.class));
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            FunApp.preferenceseditor.putInt("freq", progress);
            FunApp.preferenceseditor.commit();
            Styles.changeFreqText(progress);
        }
    };

    public static void alert(String title, String message) {
        View view = inflaterDialog.inflate(R.layout.alertdialogue, null);
        ((TextView) view.findViewById(R.id.alertDialogTitle)).setText(title);
        ((TextView) view.findViewById(R.id.alertDialogMessage)).setText(message);
        alertDialog.setCancelable(true);
        alertDialog.setView(view);
        alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("", null);
        alertDialog.setPositiveButton("", null);
        alertDialog.show();
    }

    public static void alertscroll(String title, String message) {
        View view = inflaterDialog.inflate(R.layout.alertdialoguescroll, null);
        ((TextView) view.findViewById(R.id.alertDialogTitle)).setText(title);
        ((TextView) view.findViewById(R.id.alertDialogMessage)).setText(message);
        alertDialog.setCancelable(true);
        alertDialog.setView(view);
        alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("", null);
        alertDialog.setPositiveButton("", null);
        alertDialog.show();
    }

    public static void alertMAJ(String title, String message) {
        View view = inflaterDialog.inflate(R.layout.alertdialogue, null);
        ((TextView) view.findViewById(R.id.alertDialogTitle)).setText(title);
        ((TextView) view.findViewById(R.id.alertDialogMessage)).setText(message);
        alertDialog.setCancelable(true);
        alertDialog.setView(view);
        alertDialog.setPositiveButton("TELECHARGER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent browserintent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://serveur24sur24.free.fr/FuncraftAlerts/FuncraftAlerts.apk"));
                browserintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(browserintent);
            }
        });
        alertDialog.setNegativeButton("NE RIEN FAIRE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setNeutralButton("", null);
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        header1 = (TextView) findViewById(R.id.header1);
        name = (EditText) findViewById(R.id.nameinput);
        mdp = (EditText) findViewById(R.id.mdpinput);
        connect = (Button) findViewById(R.id.connectbutton);
        connect.setOnClickListener(ButtonListener);
        forgot = (Button) findViewById(R.id.forgetbutton);
        forgot.setOnClickListener(ButtonListener);
        progressBarConnect = (ProgressBar) findViewById(R.id.progressBarConnect);
        header2 = (TextView) findViewById(R.id.header2);
        activenotif = (Switch) findViewById(R.id.activnotifswitch);
        activenotif.setOnCheckedChangeListener(ActivListener);
        alertstext = (TextView) findViewById(R.id.alertscheckBoxtext);
        alerts = (CheckBox) findViewById(R.id.alertscheckBox);
        alerts.setOnCheckedChangeListener(OptionsListener);
        convostext = (TextView) findViewById(R.id.convocheckBoxtext);
        convos = (CheckBox) findViewById(R.id.convocheckBox);
        convos.setOnCheckedChangeListener(OptionsListener);
        freqtext = (TextView) findViewById(R.id.freqtext);
        freqseek = (SeekBar) findViewById(R.id.freqSeek);
        freqseek.setOnSeekBarChangeListener(FreqListener);
        sonnerietext = (TextView) findViewById(R.id.sonneriecheckBoxtext);
        sonnerie = (CheckBox) findViewById(R.id.sonneriecheckBox);
        sonnerie.setOnCheckedChangeListener(OptionsListener);
        vibreurtext = (TextView) findViewById(R.id.vibreurcheckBoxtext);
        vibreurparam = (ImageButton) findViewById(R.id.vibreurcheckBoxparam);
        vibreurparam.setOnClickListener(ButtonListener);
        vibreur = (CheckBox) findViewById(R.id.vibreurcheckBox);
        vibreur.setOnCheckedChangeListener(OptionsListener);
        ledtext = (TextView) findViewById(R.id.ledcheckBoxtext);
        ledparam = (ImageButton) findViewById(R.id.ledcheckBoxparam);
        ledparam.setOnClickListener(ButtonListener);
        led = (CheckBox) findViewById(R.id.ledcheckBox);
        led.setOnCheckedChangeListener(OptionsListener);
        connexiontext = (TextView) findViewById(R.id.connexiontext);
        connexion = (ImageButton) findViewById(R.id.connexionparam);
        connexion.setOnClickListener(ButtonListener);
        header5 = (TextView) findViewById(R.id.header5);

        forums = (Button) findViewById(R.id.forumbutton);
        forums.setOnClickListener(ButtonListener);
        consultalert = (Button) findViewById(R.id.convobutton);
        consultalert.setOnClickListener(ButtonListener);
        consultconvo = (Button) findViewById(R.id.alertbutton);
        consultconvo.setOnClickListener(ButtonListener);

        header4 = (TextView) findViewById(R.id.header4);
        about = (Button) findViewById(R.id.aproposbutton);
        about.setOnClickListener(ButtonListener);
        aide = (Button) findViewById(R.id.helpbutton);
        aide.setOnClickListener(ButtonListener);
        checkversion = (Button) findViewById(R.id.checkversionbutton);
        checkversion.setOnClickListener(ButtonListener);
        progressBarCheckversion = (ProgressBar) findViewById(R.id.progressBarCheckVersion);
        changelog = (Button) findViewById(R.id.changelogbutton);
        changelog.setOnClickListener(ButtonListener);

        activenotif.setChecked(FunApp.preferences.getBoolean("activnotif", true));
        prefActivNotif(FunApp.preferences.getBoolean("activnotif", true));
        vibreur.setChecked(FunApp.preferences.getBoolean("vibreur", true));
        led.setChecked(FunApp.preferences.getBoolean("led", false));
        sonnerie.setChecked(FunApp.preferences.getBoolean("sonnerie", false));
        freqseek.setProgress(FunApp.preferences.getInt("freq", 2));
        Styles.changeFreqText(FunApp.preferences.getInt("freq", 2));
        convos.setChecked(FunApp.preferences.getBoolean("convos", true));
        alerts.setChecked(FunApp.preferences.getBoolean("alerts", true));
        mdp.setText(FunApp.preferences.getString("mdp", ""));
        name.setText(FunApp.preferences.getString("name", ""));
        progressBarConnect.setProgress(FunApp.preferences.getInt("connectedProgress", 0));

        progressBarCheckversion.getIndeterminateDrawable().setColorFilter(Color.parseColor(getString(R.string.codecolorcheckred)), android.graphics.PorterDuff.Mode.SRC_IN);
        progressBarCheckversion.getProgressDrawable().setColorFilter(Color.parseColor(getString(R.string.codecolorcheckred)), android.graphics.PorterDuff.Mode.SRC_IN);
        progressBarConnect.getIndeterminateDrawable().setColorFilter(Color.parseColor(getString(R.string.codecolorcheckred)), android.graphics.PorterDuff.Mode.SRC_IN);
        progressBarConnect.getProgressDrawable().setColorFilter(Color.parseColor(getString(R.string.codecolorcheckred)), android.graphics.PorterDuff.Mode.SRC_IN);
        activenotif.getThumbDrawable().setColorFilter(Color.parseColor(getString(R.string.codecolorcheckred)), android.graphics.PorterDuff.Mode.SRC_IN);

        inflaterDialog = MainActivity.this.getLayoutInflater();
        alertDialog = new AlertDialog.Builder(MainActivity.this);
        if (!isMyServiceRunning(AutoMAJ.class)) {
            AutoMAJ.setAlarm(getBaseContext());
        } else if (FunApp.majdispo) {
            Toast.makeText(MainActivity.this, "Rappel : mise à jour disponible", Toast.LENGTH_LONG).show();
        }
    }

    private void prefActivNotif(boolean b) {
        if (!b) {
            alertstext.setTextColor(Color.GRAY);
            alerts.setEnabled(false);
            convostext.setTextColor(Color.GRAY);
            convos.setEnabled(false);
            freqtext.setTextColor(Color.GRAY);
            freqseek.setEnabled(false);
            sonnerietext.setTextColor(Color.GRAY);
            sonnerie.setEnabled(false);
            vibreurtext.setTextColor(Color.GRAY);
            vibreur.setEnabled(false);
            vibreurparam.getDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            vibreurparam.setEnabled(false);
            ledtext.setTextColor(Color.GRAY);
            led.setEnabled(false);
            ledparam.getDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            ledparam.setEnabled(false);
            connexiontext.setTextColor(Color.GRAY);
            connexion.setEnabled(false);
            connexion.getDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        } else {
            alertstext.setTextColor(Color.BLACK);
            alerts.setEnabled(true);
            convostext.setTextColor(Color.BLACK);
            convos.setEnabled(true);
            freqtext.setTextColor(Color.BLACK);
            freqseek.setEnabled(true);
            sonnerietext.setTextColor(Color.BLACK);
            sonnerie.setEnabled(true);
            vibreurtext.setTextColor(Color.BLACK);
            vibreur.setEnabled(true);
            vibreurparam.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            vibreurparam.setEnabled(true);
            ledtext.setTextColor(Color.BLACK);
            led.setEnabled(true);
            ledparam.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            ledparam.setEnabled(true);
            connexiontext.setTextColor(Color.BLACK);
            connexion.setEnabled(true);
            connexion.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}