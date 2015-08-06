package mcare.household;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.raihan.myapplication.R;

import org.ei.opensrp.Context;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mcare.elco.ElcoSmartRegisterActivity;
import util.ImageCache;
import util.ImageFetcher;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static java.text.MessageFormat.format;
import static org.ei.opensrp.util.StringUtil.humanize;

/**
 * Created by raihan on 5/11/15.
 */
public class HouseHoldDetailActivity extends Activity {

    //image retrieving
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private static int mImageThumbSize;
    private static int mImageThumbSpacing;

    private static ImageFetcher mImageFetcher;




    //image retrieving

    public static CommonPersonObjectClient householdclient;
    public static CommonPersonObjectController householdcontroller;
    private SmartRegisterPaginatedAdapter clientsAdapter;
    private final PaginationViewHandler paginationViewHandler = new PaginationViewHandler();
    ListView Clientsview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.household_detail_activity);

        TextView householdhead_name = (TextView)findViewById(R.id.name_household_head);
        TextView mauza = (TextView)findViewById(R.id.mauza);
        TextView household_hhid = (TextView)findViewById(R.id.house_detail_hhid);
        TextView household_hhid_jivita = (TextView)findViewById(R.id.hh_detail_jivita);

        ImageButton back = (ImageButton)findViewById(R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(HouseHoldDetailActivity.this, HouseHoldSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
            }
        });


        householdhead_name.setText(householdclient.getDetails().get("FWHOHFNAME"));
        mauza.setText(humanize((householdclient.getDetails().get("location_name") != null ? householdclient.getDetails().get("location_name") : "").replace("+", "_")));
        household_hhid.setText(getResources().getString(R.string.hhid_gob)+ householdclient.getDetails().get("FWGOBHHID"));
        household_hhid_jivita.setText(getResources().getString(R.string.hhid_jivita) + householdclient.getDetails().get("FWJIVHHID"));
        final ImageView householdview = (ImageView)findViewById(R.id.householdprofileview);

        if(householdclient.getDetails().get("profilepic")!= null){
            setImagetoHolder(HouseHoldDetailActivity.this,householdclient.getDetails().get("profilepic"),householdview, R.mipmap.household_profile_thumb);
        }
        householdview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindobject = "household";
                entityid = householdclient.entityId();
                dispatchTakePictureIntent(householdview);

            }
        });


        Clientsview = (ListView)findViewById(R.id.list);
        paginationViewHandler.addPagination(Clientsview);

        householdcontroller = new CommonPersonObjectController(Context.getInstance().allCommonsRepositoryobjects("elco"), Context.getInstance().allBeneficiaries(),context.listCache(),
                context.personObjectClientsCache(),"FWWOMFNAME","elco","relationalid",householdclient.entityId(), CommonPersonObjectController.ByColumnAndByDetails.byrelationalid,"FWWOMFNAME", CommonPersonObjectController.ByColumnAndByDetails.byDetails);
                clientsAdapter = adapter();
        clientsAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                paginationViewHandler.refresh();
            }
        });
        Clientsview.setAdapter(clientsAdapter);
        Log.v("view size", "" + householdcontroller.getClients().size());
        if(householdcontroller.getClients().size()<1){

            Clientsview.setVisibility(INVISIBLE);
        }
        if(!(clientsAdapter.getCount()>1)){
            paginationViewHandler.footerView.setVisibility(INVISIBLE);
        }

//        clientsProgressView.setVisibility(View.GONE);


//        paginationViewHandler.refresh();
    }
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(new HouseholdDetailsSmartClientsProvider(this,paginationViewHandler ,householdcontroller));
    }
    private class PaginationViewHandler implements View.OnClickListener {
        private Button nextPageView;
        private Button previousPageView;
        private TextView pageInfoView;
        public ViewGroup footerView;

        private void addPagination(ListView clientsView) {
            footerView = getPaginationView();
            nextPageView = (Button) footerView.findViewById(org.ei.opensrp.R.id.btn_next_page);
            previousPageView = (Button) footerView.findViewById(org.ei.opensrp.R.id.btn_previous_page);
            pageInfoView = (TextView) footerView.findViewById(org.ei.opensrp.R.id.txt_page_info);

            nextPageView.setOnClickListener(this);
            previousPageView.setOnClickListener(this);

            footerView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    (int) getResources().getDimension(org.ei.opensrp.R.dimen.pagination_bar_height)));

        }

        public ViewGroup getPaginationView() {
            return (ViewGroup) getLayoutInflater().inflate(org.ei.opensrp.R.layout.smart_register_pagination, null);
        }

        private int getCurrentPageCount() {
            return clientsAdapter.currentPage() + 1 > clientsAdapter.pageCount() ? clientsAdapter.pageCount() : clientsAdapter.currentPage() + 1;
        }

        public void refresh() {
            pageInfoView.setText(
                    format(getResources().getString(org.ei.opensrp.R.string.str_page_info),
                            (getCurrentPageCount()),
                            (clientsAdapter.pageCount())));
            nextPageView.setVisibility(clientsAdapter.hasNextPage() ? VISIBLE : INVISIBLE);
            previousPageView.setVisibility(clientsAdapter.hasPreviousPage() ? VISIBLE : INVISIBLE);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case org.ei.opensrp.R.id.btn_next_page:
                    gotoNextPage();
                    break;
                case org.ei.opensrp.R.id.btn_previous_page:
                    goBackToPreviousPage();
                    break;
                case R.id.profilepic:
                    entityid = ((CommonPersonObjectClient)view.getTag()).entityId();
                    bindobject = "elco";
                    mImageView = (ImageView)view;
                    dispatchTakePictureIntent((ImageView)view);
                    break;
                case R.id.registerlink:
                    startActivity(new Intent(HouseHoldDetailActivity.this, ElcoSmartRegisterActivity.class));
                    break;

            }
        }

        private void gotoNextPage() {
            clientsAdapter.nextPage();
            clientsAdapter.notifyDataSetChanged();
        }

        private void goBackToPreviousPage() {
            clientsAdapter.previousPage();
            clientsAdapter.notifyDataSetChanged();
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    static final int REQUEST_TAKE_PHOTO = 1;
   static ImageView mImageView;
    static File currentfile;
    static String bindobject;
    static String entityid;
    private void dispatchTakePictureIntent(ImageView imageView) {
        mImageView = imageView;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                currentfile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            String imageBitmap = (String) extras.get(MediaStore.EXTRA_OUTPUT);
//            Toast.makeText(this,imageBitmap,Toast.LENGTH_LONG).show();
            HashMap <String,String> details = new HashMap<String,String>();
            details.put("profilepic",currentfile.getAbsolutePath());
            saveimagereference(bindobject,entityid,details);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(currentfile.getPath(), options);
            mImageView.setImageBitmap(bitmap);
        }
    }
    public void saveimagereference(String bindobject,String entityid,Map<String,String> details){
        Context.getInstance().allCommonsRepositoryobjects(bindobject).mergeDetails(entityid,details);
//                householdclient.entityId();
//        Toast.makeText(this,entityid,Toast.LENGTH_LONG).show();
    }
    public static void setImagetoHolder(Activity activity,String file, ImageView view, int placeholder){
        mImageThumbSize = 300;
        mImageThumbSpacing = Context.getInstance().applicationContext().getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);


        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(activity, IMAGE_CACHE_DIR);
             cacheParams.setMemCacheSizePercent(0.50f); // Set memory cache to 25% of app memory
        mImageFetcher = new ImageFetcher(activity, mImageThumbSize);
        mImageFetcher.setLoadingImage(placeholder);
        mImageFetcher.addImageCache(activity.getFragmentManager(), cacheParams);
//        Toast.makeText(activity,file,Toast.LENGTH_LONG).show();
        mImageFetcher.loadImage("file:///"+file,view);

//        Uri.parse(new File("/sdcard/cats.jpg")






//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(file, options);
//        view.setImageBitmap(bitmap);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, HouseHoldSmartRegisterActivity.class));
        overridePendingTransition(0, 0);


    }
}
