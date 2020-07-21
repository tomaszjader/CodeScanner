package pl.tomaszjader.codescanner;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultActivity extends AppCompatActivity {

    private String text;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        TextView textView = findViewById(R.id.helpText);

        try {
            JSONObject json = new JSONObject(getIntent().getExtras().getString("response"));
            this.status = json.getString("status");
            this.text = json.getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int backgroundColor;
        switch (this.status) {
            case "primary":
                backgroundColor = 0xff0275d8;
                break;
            case "secondary":
                backgroundColor = 0xff6c757d;
                break;
            case "success":
                backgroundColor = 0xff5cb85c;
                break;
            case "info":
                backgroundColor = 0xff5bc0de;
                break;
            case "warning":
                backgroundColor = 0xfff0ad4e;
                break;
            case "danger":
                backgroundColor = 0xffd9534f;
                break;
            case "dark":
                backgroundColor = 0xff292b2c;
                break;
            case "light":
                backgroundColor = 0xfff7f7f7;
                break;
            default:
                backgroundColor = 0xffffffff;
        }
        textView.setBackgroundColor(backgroundColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(this.text, Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml(this.text));
        }
    }
}