package yoyorony.me.funcraftv2alerts;

public class Styles {

    public static void changeFreqText(int freq) {
        switch (freq) {
            case 0:
                MainActivity.freqtext.setText("Fréquence de synchronisation : 1min");
                break;
            case 1:
                MainActivity.freqtext.setText("Fréquence de synchronisation : 2min");
                break;
            case 2:
                MainActivity.freqtext.setText("Fréquence de synchronisation : 5min");
                break;
            case 3:
                MainActivity.freqtext.setText("Fréquence de synchronisation : 10min");
                break;
            case 4:
                MainActivity.freqtext.setText("Fréquence de synchronisation : 30min");
                break;
            case 5:
                MainActivity.freqtext.setText("Fréquence de synchronisation : 1h");
                break;
            case 6:
                MainActivity.freqtext.setText("Fréquence de synchronisation : 2h");
                break;
            default:
                MainActivity.freqtext.setText("Fréquence de synchronisation : Err");
                break;
        }
    }
}
