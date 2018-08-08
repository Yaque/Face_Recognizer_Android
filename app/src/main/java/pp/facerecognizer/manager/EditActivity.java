package pp.facerecognizer.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pp.facerecognizer.MainActivity;
import pp.facerecognizer.R;
import pp.facerecognizer.UserManager;
import pp.facerecognizer.wrapper.LibSVM;

public class EditActivity extends AppCompatActivity {
    private Button deleteUserButton;
    private Button backManagerButton;
    private ListView userListViewEdit;
    private ProgressDialog progressDialog;
    private UserManager userManager;
    private DeleteAdapter deleteAdapter;
    public static ArrayList<DeleteUser>  data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        EditActivity editActivity = this;
        userManager = new UserManager();


        userListViewEdit = findViewById(R.id.user_listview_edit);

        deleteUserButton = findViewById(R.id.delete_user_button);
        backManagerButton = findViewById(R.id.back_manager_button);

        progressDialog = new ProgressDialog(EditActivity.this);
        progressDialog.setTitle("提示信息");
        progressDialog.setMessage("正在删除中，请稍后......");
        //    设置setCancelable(false); 表示我们不能取消这个弹出框，等下载完成之后再让弹出框消失
        progressDialog.setCancelable(false);
        //    设置ProgressDialog样式为圆圈的形式
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        ArrayList<String> nameData = (ArrayList<String>) userManager.getClassNames();

        boolean[] thisCheckBoxs = new boolean[nameData.size()];
        for (int i = 0; i < thisCheckBoxs.length; i++) {
            thisCheckBoxs[i] = false;
            DeleteUser deleteUser = new DeleteUser();
            deleteUser.setCheckBox(thisCheckBoxs[i]);
            deleteUser.setName(nameData.get(i));
            data.add(deleteUser);
        }

        deleteAdapter = new DeleteAdapter(this, R.layout.item_user_edit, data);
        userListViewEdit.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        userListViewEdit.setAdapter(deleteAdapter);


        //弹出dialog对话框提示用户是否删除
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edittext, null);
        EditText editText = dialogView.findViewById(R.id.edit_text);
        AlertDialog editDialog = new AlertDialog.Builder(EditActivity.this)
                .setTitle("确定删除？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteTask().execute();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stu
                        deleteAdapter.notifyDataSetChanged();  //刷新本页
                    }
                })
                .create();

        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDialog.show();
            }
        });

        backManagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditActivity.this, ManagerMain.class);
                editActivity.finish();
                startActivity(intent);
            }
        });


    }

    class DeleteAdapter extends ArrayAdapter {
        private final int resourceId;


        public DeleteAdapter(Context context, int textViewResourceId, ArrayList<DeleteUser> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DeleteUser deleteUser = (DeleteUser) getItem(position); // 获取当前项实例

            View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象

            CheckBox checkBoxUserName = view.findViewById(R.id.delete_checkBox);//获取该布局内的文本视图

            checkBoxUserName.setChecked(deleteUser.isCheckBox());
            checkBoxUserName.setText(deleteUser.getName());//为文本视图设置文本内容

            checkBoxUserName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    EditActivity.data.get(position).setCheckBox(b);
                    userListViewEdit.setItemChecked(position, checkBoxUserName.isChecked());
                }
            });

            return view;
        }
    }

    class DeleteTask extends AsyncTask<Integer, Integer, String> {
        // AsyncTask<Params, Progress, Result>
        //后面尖括号内分别是参数（例子里是线程休息时间），进度(publishProgress用到)，返回值类型
        @Override
        protected void onPreExecute() {
            progressDialog.show();
            //第一个执行方法
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Integer... params) {
            //第二个执行方法,onPreExecute()执行完后执行
//            for(int i=0;i<=100;i++){
//                publishProgress(i);
//                try {
//                    Thread.sleep(params[0]);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
            for (int f = 0; f < data.size(); f++) {
                if (data.get(f).isCheckBox()) {
                    userManager.deletePerson(data.get(f).getName());
                    data.remove(f);
                    f--;
                }
            }
            userManager.train();

            return "执行完毕";
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            //这个函数在doInBackground调用publishProgress时触发，虽然调用时只有一个参数
            //但是这里取到的是一个数组,所以要用progesss[0]来取值
            //第n个参数就用progress[n]来取值
//            tv.setText(progress[0]+"%");

            super.onProgressUpdate(progress);
        }
        @Override
        protected void onPostExecute(String result) {
            //doInBackground返回时触发，换句话说，就是doInBackground执行完后触发
            //这里的result就是上面doInBackground执行后的返回值，所以这里是"执行完毕"
//            setTitle(result);
            deleteAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
            super.onPostExecute(result);
        }
    }
}
