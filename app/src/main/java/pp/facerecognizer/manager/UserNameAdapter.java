package pp.facerecognizer.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pp.facerecognizer.R;

public class UserNameAdapter extends ArrayAdapter {
    private final int resourceId;

    public UserNameAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String userName = (String) getItem(position); // 获取当前项实例

        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象

        TextView userNameTV = (TextView) view.findViewById(R.id.user_name);//获取该布局内的文本视图

        userNameTV.setText(userName);//为文本视图设置文本内容

        return view;
    }

}
