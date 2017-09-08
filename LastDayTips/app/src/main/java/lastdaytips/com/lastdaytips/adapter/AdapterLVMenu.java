package lastdaytips.com.lastdaytips.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import lastdaytips.com.lastdaytips.R;
import lastdaytips.com.lastdaytips.model.ItemMenu;

/**
 * Created by MyPC on 07/09/2017.
 */
public class AdapterLVMenu extends BaseAdapter {

    private Context context;
    private ArrayList<ItemMenu> arr;

    public AdapterLVMenu(Context context, ArrayList<ItemMenu> arr) {
        this.context = context;
        this.arr = arr;
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int position) {
        return arr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.line_lvmenu,null);

        ItemMenu im = arr.get(position);

        TextView tv_menuitem = (TextView) convertView.findViewById(R.id.tv_menuitem);

        tv_menuitem.setText(im.getName());
        return convertView;
    }
}
