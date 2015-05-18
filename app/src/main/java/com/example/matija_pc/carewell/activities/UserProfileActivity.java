package com.example.matija_pc.carewell.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matija_pc.carewell.BitmapScaler;
import com.example.matija_pc.carewell.R;
import com.example.matija_pc.carewell.adapters.CallsAdapter;
import com.example.matija_pc.carewell.adapters.ContactsAdapter;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;
import com.example.matija_pc.carewell.fragments.CallsFragment;
import com.example.matija_pc.carewell.fragments.ContactsFragment;
import com.example.matija_pc.carewell.listeners.CallButtonListener;
import com.example.matija_pc.carewell.listeners.SendMessageButtonListener;


public class UserProfileActivity extends Activity {

    Intent intent;
    private static final int SELECT_PICTURE = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_profile);

        intent=getIntent();
        RelativeLayout videoCallLayout = (RelativeLayout) findViewById(R.id.video_call_layout);
        ImageButton videoCallButton = (ImageButton) findViewById(R.id.video_call_button);
        RelativeLayout audioCallLayout = (RelativeLayout) findViewById(R.id.audio_call_layout);
        ImageButton audioCallButton = (ImageButton) findViewById(R.id.audio_call_button);
        RelativeLayout sendMessageLayout = (RelativeLayout) findViewById(R.id.send_message_layout);
        ImageButton sendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        ImageView userImage = (ImageView) findViewById(R.id.user_picture);
        String currentImagePath = intent.getStringExtra(MainActivity.IMAGE_PATH);

        //load user image using predefined dimension (1024x1024)
        Bitmap bitmap = BitmapScaler.decodeSampledBitmap(currentImagePath, 0, 0);
        //if there is already a picture defined for this contact, display it, else, display generic picture
        if (bitmap!=null) {
            userImage.setImageBitmap(bitmap);
            userImage.setBackgroundColor(0x0);
        } else {
            userImage.setImageResource(R.drawable.generic_picture);
        }

        //define listeners for buttons and set tags
        String userID = intent.getStringExtra(MainActivity.USER_ID);
        CallButtonListener.CallHelper videoCallHelper = new CallButtonListener.CallHelper();
        videoCallHelper.userID = userID;
        videoCallHelper.callType = "video";

        CallButtonListener.CallHelper audioCallHelper = new CallButtonListener.CallHelper();
        audioCallHelper.userID = userID;
        audioCallHelper.callType = "audio";

        videoCallButton.setTag(videoCallHelper);
        videoCallLayout.setTag(videoCallHelper);
        audioCallButton.setTag(audioCallHelper);
        audioCallLayout.setTag(audioCallHelper);
        sendMessageButton.setTag(userID);
        sendMessageLayout.setTag(userID);
        videoCallLayout.setOnClickListener(new CallButtonListener(this));
        videoCallButton.setOnClickListener(new CallButtonListener(this));
        audioCallLayout.setOnClickListener(new CallButtonListener(this));
        audioCallButton.setOnClickListener(new CallButtonListener(this));
        sendMessageLayout.setOnClickListener(new SendMessageButtonListener(this));
        sendMessageButton.setOnClickListener(new SendMessageButtonListener(this));
        userImage.setOnClickListener(changeUserImage);
    }

    //listener for changing user image
    View.OnClickListener changeUserImage = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
            builder.setTitle("Choose action");
            //open image gallery
            builder.setPositiveButton("Choose from Gallery", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent getImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(getImage, SELECT_PICTURE);

                }
            })
                    //remove current image path from database and assign generic picture to this user
                .setNegativeButton("Delete current picture", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DatabaseTables.Contacts.IMAGE_PATH, "");
                        String userID = intent.getStringExtra(MainActivity.USER_ID);
                        DatabaseOperations databaseOperations = new DatabaseOperations(getApplicationContext());
                        databaseOperations.update(DatabaseTables.Contacts.TABLE_NAME, contentValues, DatabaseTables.Contacts.USER_ID, userID);
                        ImageView imageView = (ImageView) v.findViewById(R.id.user_picture);
                        imageView.setImageResource(R.drawable.generic_picture);
                        //clear distinct pictures list
                        CallsAdapter.distinctContacts.clear();
                        CallsFragment.callsAdapter.notifyDataSetChanged();

                        ContactsAdapter.distinctContacts.clear();
                        ContactsFragment.adapter.notifyDataSetChanged();

                    }
                })
                    .show();

        }
    };

    public void onActivityResult (int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode==RESULT_OK) {
            if (requestCode == SELECT_PICTURE && resultData!=null) {
                //get image path and store it in database
                Uri selectedImage = resultData.getData();
                String[] filePath = new String[] {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePath, null, null, null);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                cursor.close();

                //update image path in database
                ContentValues values = new ContentValues();
                String userID = intent.getStringExtra(MainActivity.USER_ID);
                values.put(DatabaseTables.Contacts.IMAGE_PATH, selectedImagePath);
                DatabaseOperations databaseOperations = new DatabaseOperations(getApplicationContext());
                databaseOperations.update(DatabaseTables.Contacts.TABLE_NAME, values, DatabaseTables.Contacts.USER_ID, userID);

                //update user picture
                ImageView userImage = (ImageView) findViewById(R.id.user_picture);
                Bitmap bitmap = BitmapScaler.decodeSampledBitmap(selectedImagePath, 0, 0);
                userImage.setImageBitmap(bitmap);

                //clear distinct pictures list
                CallsAdapter.distinctContacts.clear();
                CallsFragment.callsAdapter.notifyDataSetChanged();

                ContactsAdapter.distinctContacts.clear();
                ContactsFragment.adapter.notifyDataSetChanged();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        //intent = getIntent();
        String firstName = intent.getStringExtra(MainActivity.FIRST_NAME);
        String lastName = intent.getStringExtra(MainActivity.LAST_NAME);
        String title=firstName+" "+lastName;
        getActionBar().setTitle(title);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //if a delete action is chosen, delete this user from database
        if (id==R.id.action_delete){
            new AlertDialog.Builder(this)
                    .setMessage(R.string.confirm_deletion)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick (DialogInterface dialogInterface, int which) {

                            String userID=intent.getStringExtra(MainActivity.USER_ID);
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra(MainActivity.USER_ID, userID);
                            setResult(MainActivity.DELETE, returnIntent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        }


        //if an edit action is chosen, provide a form for editing first name and last name
        if (id==R.id.action_edit) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserProfileActivity.this);
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.edit_user_data, null);

            final EditText firstName = (EditText) view.findViewById(R.id.edit_first_name);
            final EditText lastName = (EditText) view.findViewById(R.id.edit_last_name);

            final String tempFirstName = intent.getStringExtra(MainActivity.FIRST_NAME);
            final String tempLastName = intent.getStringExtra(MainActivity.LAST_NAME);
            final String userID = intent.getStringExtra(MainActivity.USER_ID);

            firstName.setText(tempFirstName, TextView.BufferType.EDITABLE);
            lastName.setText(tempLastName, TextView.BufferType.EDITABLE);
            alertDialogBuilder.setView(view);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if any of the contacts has changed, write the change into the database
                    if (!firstName.getText().toString().equals(tempFirstName) || !lastName.getText().toString().equals(tempLastName)) {
                        //if both fields are empty
                        if (firstName.getText().toString().equals("") && lastName.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "Both fields cannot be left empty", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra(MainActivity.USER_ID, userID);
                            returnIntent.putExtra(MainActivity.FIRST_NAME, firstName.getText().toString());
                            returnIntent.putExtra(MainActivity.LAST_NAME, lastName.getText().toString());
                            setResult(MainActivity.UPDATE, returnIntent);
                            finish();
                        }

                    }
                }
            })
                    .setNegativeButton("Cancel", null)
                    .show();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
