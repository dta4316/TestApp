package dta4316.testapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.amazonaws.models.nosql.StoreInfoDO;
import java.util.List;

public class StoreServicesViewAdapter extends ArrayAdapter<StoreInfoDO.Service> {
    private final List<StoreInfoDO.Service> list;
    private final Activity context;

    public StoreServicesViewAdapter(Activity context, List<StoreInfoDO.Service> list) {
        super(context, R.layout.view_store_services, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView txtServiceName;
        protected CheckBox chkIsSelected;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.view_store_services, null);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.txtServiceName = (TextView) view.findViewById(R.id.txtServiceName);
            viewHolder.chkIsSelected = (CheckBox) view.findViewById(R.id.chkIsSelected);
            viewHolder.chkIsSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
                    StoreInfoDO.Service service = (StoreInfoDO.Service) viewHolder.chkIsSelected.getTag();
                    service.SetIsSelected(checkBox.isChecked());
                }
            });
            view.setTag(viewHolder);
            viewHolder.chkIsSelected.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).chkIsSelected.setTag(list.get(position));
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.txtServiceName.setText(list.get(position).GetName());
        holder.chkIsSelected.setChecked(list.get(position).GetIsSelected());

        return view;
    }
}