package com.codepath.hungrybird.chef.fragments;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.hungrybird.R;
import com.codepath.hungrybird.consumer.fragments.FilterFragment;
import com.codepath.hungrybird.databinding.ChefDishAddEditFragmentBinding;
import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.User;
import com.codepath.hungrybird.network.ParseClient;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static android.app.Activity.RESULT_OK;

public class DishAddEditFragment extends Fragment {
    public static final String TAG = DishAddEditFragment.class.getSimpleName();

    public final String APP_TAG = "ParseDemoApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    public static final String FRAGMENT_TAG = "FILTER_FRAGMENT_TAG";
    public static final String DISH_ID = "DISH_ID";

    ChefDishAddEditFragmentBinding binding;

    private Dish currentDish = new Dish();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.chef_dish_add_edit_fragment, container, false);
        if (getArguments() != null
                && getArguments().getString(DISH_ID) != null
                && !getArguments().getString(DISH_ID).isEmpty()) {
            // edit flow
            String dishId = getArguments().getString(DISH_ID);
            ParseClient.getInstance().getDishById(dishId, new ParseClient.DishListener() {
                @Override
                public void onSuccess(Dish dish) {
                    currentDish = dish;
                    modelToView(dish);
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "error during saving dish ... " + dishId, Toast.LENGTH_LONG).show();
                }
            });
        }
        setImageUploadButtonOnClickListener();
        setSaveButtonOnClickListener();
        setCancelButtonOnClickListener();
        return binding.getRoot();
    }

    private void setCancelButtonOnClickListener() {
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "cancel button clicked", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setSaveButtonOnClickListener() {
        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "save button clicked", Toast.LENGTH_LONG).show();
                viewToModel();
                ParseClient.getInstance().addDish(currentDish);
            }
        });
    }

    private void setImageUploadButtonOnClickListener() {
        binding.buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "image button clicked", Toast.LENGTH_LONG).show();
                fireCameraIntent();
            }
        });
    }

    private void fireCameraIntent() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir = new File(
                    getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));

//            // wrap File object into a content provider
//            // required for API >= 24
//            // See https://guides.codepath.com/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
//            return FileProvider.getUriForFile(ParseActivity.this, "com.codepath.fileprovider", file);
        }
        return null;
    }

    // Returns true if external storage for photos is available
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri takenPhotoUri = getPhotoFileUri(photoFileName);
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
//                ImageView ivPreview = (ImageView) findViewById(R.id.dishPrimaryImageView);
                binding.imageViewPrimaryImage.setImageBitmap(takenImage);

//                ParseFile dishImageFile = bitmapToParseFile(takenImage);
//                currentDish.setPrimaryImage(dishImageFile);
//                User chef = (User) User.getCurrentUser();
//                Dish dish = mockDish();
//                ParseClient.getInstance().addDish(dish);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @NonNull
    private ParseFile bitmapToParseFile(Bitmap takenImage) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        takenImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] image = stream.toByteArray();
        return new ParseFile(image);
    }

    private Dish mockDish() {
        Dish dish = new Dish();
        dish.setTitle("mock dish title");
        dish.setDescription("mock dish description");
        dish.setPrice(9.99);
        return dish;
    }

    private void modelToView(Dish dish) {
        binding.editTextDishTitle.setText(dish.getTitle());
        binding.editTextDishDescription.setText(dish.getDescription());
        binding.editTextPrice.setText(String.valueOf(dish.getPrice()));
        if (dish.getPrimaryImage() != null && dish.getPrimaryImage().getUrl() != null) {
            Glide.with(getContext()).load(dish.getPrimaryImage().getUrl()).into(binding.imageViewPrimaryImage);
        }
    }

    private void viewToModel() {
        try {
            currentDish.setTitle(binding.editTextDishTitle.getText().toString());
            currentDish.setDescription(binding.editTextDishDescription.getText().toString());
            currentDish.setPrice(Double.parseDouble(binding.editTextPrice.getText().toString()));
            currentDish.setPrimaryImage(bitmapToParseFile(((BitmapDrawable)binding.imageViewPrimaryImage.getDrawable()).getBitmap()));
            currentDish.setChef(new User(ParseUser.getCurrentUser()));
            currentDish.setServingSize(Integer.valueOf((String)binding.spinnerServingSize.getSelectedItem()));
            currentDish.setVeg(checkVeg((String)binding.spinnerDishType.getSelectedItem()));
        } catch (Exception e) {
            Toast.makeText(getContext(), "error adapting view to model " , Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkVeg(String dishType) {
        if (dishType.toLowerCase().trim().equals("veg")) {
            return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_filter:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FilterFragment filterFragment = new FilterFragment();
                filterFragment.show(fm, FRAGMENT_TAG);

                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
