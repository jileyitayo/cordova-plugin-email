package com.jse52.email;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.idevelop.jil.screenshot.Model.Partner;
import com.idevelop.jil.screenshot.Model.User;
import com.idevelop.jil.screenshot.Services.service.ScreenshotService;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class EmailClient {

    Context cxt;
    User loggedInUser;
    final String mfile = "Flust_compressed_screenshots.zip";

    public EmailClient(Context context, User user)
    {
        cxt = context;
        loggedInUser = user;
    }

    //TODO: Test compression First
    // TODO : Send compressed File to Email - to all the partners added
    public void sendEmail(List<Partner> partners) throws IOException {

        // Compress directory first

        File compressed_directory = new File(cxt.getExternalFilesDir(null), "attached");
        File screenshot_directory = new File(cxt.getExternalFilesDir(null), "images");
        if (compressed_directory.exists() && compressed_directory.isDirectory()) {
            File image_dir = new File(cxt.getExternalFilesDir(null), "images");
            File[] files = image_dir.listFiles();
            Log.d("Files", "Size: "+ files.length);
            for (int i = 0; i < files.length; i++)
            {
                String pathname = files[i].getPath();
                Log.d("Files", "FileName:" + files[i].getPath());
                RedactImageFile.redact(pathname); // redact the screenshots
            }
        }
        else
        {
            compressed_directory.mkdir();
        }

        final String outputFileName = compressed_directory.getPath() + "/" + mfile;

        FileUtils.cleanDirectory(compressed_directory);
        // this compresses the screenshot images
        boolean result = ZipManager.zipFolder(screenshot_directory.getPath(), outputFileName);
        if(result)
        {
            // delete all the pictures in the directory
            FileUtils.cleanDirectory(screenshot_directory);
            Log.i(ScreenshotService.class.getSimpleName(), "Cleaned Successfully" );
        }
        else
        {
            // cannot
            Log.i(ScreenshotService.class.getSimpleName(), "Cannot Compress Files" );
        }

        // loop though all hte partners and send email to them
//        String[] partner_emails = new String[partners.size()];
//        for(int i = 0; i < partners.size(); i++)
//        {
//            partner_emails[i] = partners.get(i).getEmail();
//        }

        StringBuilder partner_emails_flatten = new StringBuilder();
        for(int i = 0; i < partners.size(); i++) {
            partner_emails_flatten.append(partners.get(i).getEmail()).append(",");
        }
        partner_emails_flatten.deleteCharAt(partner_emails_flatten.length() - 1);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        final String partner_emails = partner_emails_flatten.toString();
        final String user_firstname = loggedInUser.getFirstname();
        final String user_lastname = loggedInUser.getLastname();
        final String user_email = loggedInUser.getEmail();
        Log.i(ScreenshotService.class.getSimpleName(), "Preparing to send email...");
        // send email
        new Thread(new Runnable() {
            public void run() {
                try {
                    EmailSender sender = new EmailSender(cxt);
                    String body = "Hello, \n\n" +
                            "You are getting this email because you are an accountable partner to " +
                            user_firstname + " " + user_lastname + ".\n\n" +
                            "Attached to this email is a zip file containing the recent activities.\n\n\nBest Regards,\n\nFlust";
//                    Log.i(ScreenshotService.class.getSimpleName(), body);
                    sender.addAttachment(outputFileName,mfile);
//                    Log.i(ScreenshotService.class.getSimpleName(), "Sending Message to " + user_email + "Partners are " + partner_emails);
                    sender.sendMail("Flust: Update on Accountability Partner", body,
                            user_email,
                            partner_emails);
                    Log.i(ScreenshotService.class.getSimpleName(), "Message Sent...");
                } catch (Exception e) {

                    Toast.makeText(cxt,"Error", Toast.LENGTH_LONG).show();

                }
            }

        }).start();


    }
}
