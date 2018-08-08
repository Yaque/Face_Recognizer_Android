package pp.facerecognizer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Welcome extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        Handler handler = new Handler();
        //当计时结束,跳转至主界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Welcome.this, MainActivity.class);
                startActivity(intent);
                Welcome.this.finish();

            }
        }, 3000);

    }
}
