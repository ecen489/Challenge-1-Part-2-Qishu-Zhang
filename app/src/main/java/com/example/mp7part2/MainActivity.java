package com.example.mp7part2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity {

    private final int REQ_CODE = 20;

    private SQLiteOpenHelper db_help;
    private SQLiteDatabase db;

    private Button btnFindPic;
    private Button btnTakePic;
    private ImageView imgV;
    private EditText enter_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db_help = new Pictures(this);

        btnTakePic = findViewById(R.id.capture_pic_btn);
        btnFindPic = findViewById(R.id.find_img_btn);
        imgV = findViewById(R.id.show_img_imgView);
        enter_id = findViewById(R.id.enter_ID_editTxt);

        btnFindPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load_DB_img();
            }
        });

        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent, REQ_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(this.REQ_CODE == requestCode && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            imgV.setImageBitmap(bitmap);

            byte[] bytesOfImages = getBytes(bitmap);
            db = db_help.getWritableDatabase();
            long id_val = ((Pictures) db_help).insertPictureToDB(db, bytesOfImages);
            db.close();
            //show that it was a success!!
            Toast toast = Toast.makeText(this, "Picture saved. ID# " + String.valueOf(id_val), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // bitmap to bytes conversion
    private byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // byte array to bitmap conversion
    private Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    private void load_DB_img() {
        long idValue = 0;
        try {
            idValue = Long.parseLong(enter_id.getText().toString());
        } catch(NumberFormatException e) {
            Toast toast = Toast.makeText(this, "ERROR: invalid ID.", Toast.LENGTH_SHORT);
            toast.show();
        }
        if(idValue != 0) {
            db = db_help.getWritableDatabase();
            Cursor curs = db.rawQuery("SELECT IMAGE FROM PICTURES WHERE ID = " + idValue, null);
            if(curs.moveToFirst()) {
                byte[] blob = curs.getBlob(curs.getColumnIndex("IMAGE"));
                //close preperly
                curs.close();
                Bitmap bitmap = getImage(blob);
                imgV.setImageBitmap(bitmap);
            }
            curs.close();
            db.close();
        }
    }

}
