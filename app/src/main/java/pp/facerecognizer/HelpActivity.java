package pp.facerecognizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class HelpActivity extends AppCompatActivity{
    private Button backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        backButton = findViewById(R.id.back_main_from_help_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HelpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
