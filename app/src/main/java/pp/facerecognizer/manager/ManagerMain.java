package pp.facerecognizer.manager;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

import pp.facerecognizer.Classifier;
import pp.facerecognizer.MainActivity;
import pp.facerecognizer.R;
import pp.facerecognizer.UserManager;

import static pp.facerecognizer.R.id.user_name;


public class ManagerMain extends AppCompatActivity {
    private static final int FACE_SIZE = 160;
    private Button addUserButton;
    private Button deleteUserButton;
    private Button backMainButton;
    private ListView userListView;
    private EditText editText;
    private boolean training = false;

    private UserManager userManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ManagerMain managerMain = this;

        userManager = new UserManager();

        userListView = findViewById(R.id.user_listview);

        deleteUserButton = findViewById(R.id.delete_user_button);
        addUserButton = findViewById(R.id.add_user_button);
        backMainButton = findViewById(R.id.back_main_button);

        ArrayList<String> data = (ArrayList<String>) userManager.getClassNames();

        UserNameAdapter adapter = new UserNameAdapter(this, R.layout.item_menu, data);

        userListView.setAdapter(adapter);


        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edittext, null);

        editText = dialogView.findViewById(R.id.edit_text);

        AlertDialog editDialog = new AlertDialog.Builder(ManagerMain.this)
                .setTitle(R.string.enter_user_name)
                .setView(dialogView)
                .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                    int idx = userManager.addPerson(editText.getText().toString());
                    performFileSearch(idx - 1);

                    adapter.notifyDataSetChanged();  //刷新本页
                })
                .create();

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog.show();
            }
        });



        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ManagerMain.this,EditActivity.class);
//                managerMain.finish();
                startActivity(intent);
            }
        });

        backMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManagerMain.this, MainActivity.class);
                managerMain.finish();
                MainActivity.mainActivity.finish();
                startActivity(intent);
            }
        });
    }

    public void performFileSearch(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");

        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            training = true;

            ClipData clipData = data.getClipData();
            ArrayList<Uri> uris = new ArrayList<>();

            if (clipData == null) {
                uris.add(data.getData());
            } else {
                for (int i = 0; i < clipData.getItemCount(); i++)
                    uris.add(clipData.getItemAt(i).getUri());
            }

            new Thread(() -> {
                try {
                    Classifier classifier = Classifier.getInstance(getAssets(), FACE_SIZE, FACE_SIZE);
                    classifier.updateData(requestCode, getContentResolver(), uris);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    training = false;
                }
            }).start();

        }
    }
}
