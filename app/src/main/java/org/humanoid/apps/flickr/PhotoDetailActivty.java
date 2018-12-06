package org.humanoid.apps.flickr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoDetailActivty extends BaseActivity {
    private static final String TAG = "PhotoDetailActivty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        activateToolbar(true);

        Intent intent = getIntent();
        final Photo photo = (Photo) intent.getSerializableExtra(PHOTO_TRANSFER);
        if(photo != null){
            Resources resources = getResources();
            TextView photoTitle = (TextView) findViewById(R.id.photo_title);
            photoTitle.setText(resources.getString(R.string.photo_text_title,photo.getTitle()));

            TextView tags = (TextView) findViewById(R.id.photo_tags);
            tags.setText(resources.getString(R.string.photo_text_tags,photo.getTags()));

            TextView author = (TextView) findViewById(R.id.photo_author);
            author.setText(photo.getAuthor());

            ImageView photoImage = (ImageView) findViewById(R.id.photo_image);
            Picasso.get().load(photo.getImage().replaceFirst("_m.","_b."))
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(photoImage);
        }

        FloatingActionButton saveImageButton = findViewById(R.id.saveImageFAB);
        saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PhotoDetailActivty.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(PhotoDetailActivty.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
                } else {
                    DownloadPhoto download = new DownloadPhoto(photo.getTitle() + ".png");
                    Picasso.get()
                            .load(photo.getImage().replaceFirst("_m.","_b."))
                            .into(download);
                    if(download.isSuccess())
                        Snackbar.make(v, "Image Downloaded!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

}
