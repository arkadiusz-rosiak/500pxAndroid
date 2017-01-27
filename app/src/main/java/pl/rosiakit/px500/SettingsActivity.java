package pl.rosiakit.px500;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import pl.rosiakit.px500.utils.PhotoCategory;
import pl.rosiakit.px500.utils.SelectedCategories;

import static pl.rosiakit.px500.config.Constants.DEFAULT_RPP;
import static pl.rosiakit.px500.config.Constants.SETTINGS_RPP_KEY;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private boolean changesHasBeenMade = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences("api_settings", MODE_PRIVATE);

        fetchPhotoCategories();
        prepareActionBar();
        provideCurrentSettings();
        addListenerToRPPField();
    }

    private void addListenerToRPPField() {
        EditText settingsRpp = (EditText) findViewById(R.id.settings_rpp);
        settingsRpp.addTextChangedListener(new TextWatcher() {

            SharedPreferences.Editor editor;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                editor = preferences.edit();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editor.putInt(SETTINGS_RPP_KEY, getRppSettingAsInt(s));
            }

            @Override
            public void afterTextChanged(Editable s) {
                editor.commit();
            }
        });
    }

    private void fetchPhotoCategories() {
        ViewGroup wrapper = (ViewGroup) findViewById(R.id.categories);

        final SelectedCategories selectedCategories = new SelectedCategories(this);

        for (PhotoCategory cat : PhotoCategory.values()) {

            LinearLayout linear = new LinearLayout(this);
            linear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linear.setOrientation(LinearLayout.VERTICAL);

            CheckBox checkBox = new CheckBox(this);
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            checkBox.setText(cat.toString());

            if(selectedCategories.isSelected(cat)){
                checkBox.setChecked(true);
            }

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String name = buttonView.getText().toString();
                    PhotoCategory category = PhotoCategory.getCategoryByName(name);

                    if(isChecked) {
                        selectedCategories.selectCategory(category);
                    }
                    else
                    {
                        selectedCategories.removeCategory(category);
                    }

                    changesHasBeenMade = true;
                }
            });

            wrapper.addView(checkBox);

        }
    }

    private void provideCurrentSettings() {
        int rpp = preferences.getInt(SETTINGS_RPP_KEY, DEFAULT_RPP);
        EditText settingsRpp = (EditText) findViewById(R.id.settings_rpp);
        settingsRpp.setText(String.valueOf(rpp));
    }

    private int getRppSettingAsInt(CharSequence s) {
        try {
            return Integer.parseInt(s.toString());
        } catch (NumberFormatException e) {
            return DEFAULT_RPP;
        }
    }

    private void prepareActionBar() {
        ActionBar bar = getSupportActionBar();
        if(bar != null) {
            bar.setTitle("Settings");
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            if(changesHasBeenMade) {
                setResult(RESULT_OK, getIntent());
            }

            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
