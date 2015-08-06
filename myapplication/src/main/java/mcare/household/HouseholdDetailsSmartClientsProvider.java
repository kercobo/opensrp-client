package mcare.household;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.raihan.myapplication.R;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by user on 2/12/15.
 */
public class HouseholdDetailsSmartClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;

    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    public HouseholdDetailsSmartClientsProvider(Context context,
                                                View.OnClickListener onClickListener,
                                                CommonPersonObjectController controller) {
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(R.color.text_black);
    }

    @Override
    public View getView(SmartRegisterClient smartRegisterClient, View convertView, ViewGroup viewGroup) {
        ViewGroup itemView = viewGroup;

        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        if(pc.getDetails().get("FWELIGIBLE").equalsIgnoreCase("1")) {

            itemView = (ViewGroup) inflater().inflate(R.layout.household_inhabitants_register_clients, null);
            TextView name = (TextView) itemView.findViewById(R.id.name);
            TextView age = (TextView) itemView.findViewById(R.id.age);
            TextView registerlink = (TextView) itemView.findViewById(R.id.registerlink);
            registerlink.setPaintFlags(registerlink.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);



            Button edit_form = (Button) itemView.findViewById(R.id.edit_forms);
            ImageView profilepic = (ImageView)itemView.findViewById(R.id.profilepic);
            if(pc.getDetails().get("profilepic")!=null){
                HouseHoldDetailActivity.setImagetoHolder((Activity)context,pc.getDetails().get("profilepic"),profilepic, R.drawable.woman_placeholder);
            }
            registerlink.setOnClickListener(onClickListener);
            registerlink.setTag(smartRegisterClient);


            edit_form.setOnClickListener(onClickListener);
            edit_form.setTag(smartRegisterClient);

            profilepic.setOnClickListener(onClickListener);
            profilepic.setTag(smartRegisterClient);

            name.setText(pc.getDetails().get("FWWOMFNAME") != null ? pc.getDetails().get("FWWOMFNAME") : "");
            age.setText(pc.getDetails().get("FWWOMAGE") != null ? pc.getDetails().get("FWWOMAGE") : "");
        }else{

            itemView = (ViewGroup) inflater().inflate(R.layout.household_inhabitants_nonregister_clients, null);
            TextView name = (TextView) itemView.findViewById(R.id.name);
            TextView age = (TextView) itemView.findViewById(R.id.age);
            ImageView profilepic = (ImageView)itemView.findViewById(R.id.profilepic);

            if(pc.getDetails().get("profilepic")!=null){
                HouseHoldDetailActivity.setImagetoHolder((Activity)context,pc.getDetails().get("profilepic"),profilepic, R.drawable.woman_placeholder);
            }

            profilepic.setOnClickListener(onClickListener);
            profilepic.setTag(smartRegisterClient);


            Button editform = (Button) itemView.findViewById(R.id.edit_forms);
            editform.setOnClickListener(onClickListener);
            editform.setTag(smartRegisterClient);


            name.setText(pc.getDetails().get("FWWOMFNAME") != null ? pc.getDetails().get("FWWOMFNAME") : "");
            age.setText(pc.getDetails().get("FWWOMAGE") != null ? pc.getDetails().get("FWWOMAGE") : "");
        }
        itemView.setLayoutParams(clientViewLayoutParams);
        return itemView;
    }

    @Override
    public SmartRegisterClients getClients() {
        return controller.getClients();
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                                              FilterOption searchFilter, SortOption sortOption) {
        return getClients().applyFilter(villageFilter, serviceModeOption, searchFilter, sortOption);
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {
        // do nothing.
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    public LayoutInflater inflater() {
        return inflater;
    }
}
