package pl.tomaszjader.codescanner;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {

    private String helpHtml = "<b>Pomoc</b><br/><i>Jest <s>dobrze</s> świetnie</i>";
    private Button backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        TextView helpTextView = findViewById(R.id.helpText);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            helpTextView.setText(Html.fromHtml(this.helpHtml, Html.FROM_HTML_MODE_COMPACT));
        } else {
            helpTextView.setText(Html.fromHtml(this.helpHtml));
        }
        this.backButton = findViewById(R.id.backToMenuButton);
        this.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuActivity();
            }
        });
    }

    private void openMenuActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}