package com.example.dilki.mynotes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by dilki on 04/02/2018.
 */

public class SendMailTask extends AsyncTask {

    private ProgressDialog statusDialog;
    private Activity sendMailActivity;
    private String mMailSubject;
    private String mMailMassage;
    private String mTo;

    public SendMailTask(Activity activity, String mailSubject, String mailMassage, String TO) {
        sendMailActivity = activity;
        this.mMailSubject = mailSubject;
        this.mMailMassage = mailMassage;
        this.mTo = TO;

    }

    protected void onPreExecute() {
        statusDialog = new ProgressDialog(sendMailActivity);
        statusDialog.setMessage("Getting ready...");
        statusDialog.setIndeterminate(false);
        statusDialog.setCancelable(false);
        statusDialog.show();
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            Log.i("SendMailTask", "About to instantiate GMail...");
            publishProgress("Processing input....");
            GMailSender androidEmail = new GMailSender("mynotesbackupmail@gmail.com", "netballSCG123");
            publishProgress("Preparing mail message....");
            Thread sender = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        GMailSender sender = new GMailSender("mynotesbackupmail@gmail.com", "netballSCG123");
                        sender.sendMail(mMailSubject,
                                mMailMassage,
                                mTo,
                                "mynotesbackupmail@gmail.com");
                    } catch (Exception e) {
                        Log.e("mylog", "Error: " + e.getMessage());
                    }
                }
            });
            sender.start();
            publishProgress("Email Sent.");
            Log.i("SendMailTask", "Mail Sent.");
        } catch (Exception e) {
            publishProgress(e.getMessage());
            Log.e("SendMailTask", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void onProgressUpdate(Object... values) {
        statusDialog.setMessage(values[0].toString());

    }

    @Override
    public void onPostExecute(Object result) {
        statusDialog.dismiss();
    }

}
