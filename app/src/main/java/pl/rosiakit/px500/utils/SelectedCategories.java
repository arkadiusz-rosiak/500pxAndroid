package pl.rosiakit.px500.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class SelectedCategories {

    private static final String SETTINGS_KEY = "setting_categories";
    private static final String DEFAULT_CATEGORIES = prepareDefaultCategories();

    @NonNull
    private static String prepareDefaultCategories(){
        StringBuilder sb = new StringBuilder();

        for(PhotoCategory category : PhotoCategory.values()){
            if(!category.equals(PhotoCategory.NUDE) && !category.equals(PhotoCategory.PEOPLE)){
                sb.append(category.getId()).append(";");
            }
        }

        return sb.toString();
    }

    private Set<PhotoCategory> categories = new HashSet<>();
    private SharedPreferences preferences;

    public SelectedCategories(Context context) {
        preferences = context.getSharedPreferences("api_settings", MODE_PRIVATE);
        String serialized = preferences.getString(SETTINGS_KEY, DEFAULT_CATEGORIES);
        categories = unserialize(serialized);
    }

    private Set<PhotoCategory> unserialize(String serialized) {

        String[] split = serialized.split(";");

        Set<PhotoCategory> categories = new HashSet<>();
        for(String x : split){

            try{
                int id = Integer.parseInt(x);
                categories.add(PhotoCategory.getCategoryById(id));
            }
            catch (NumberFormatException e){
                Log.e("CAT","Cannot parse " + x + " as int.");
            }
            catch (NoSuchElementException e){
                Log.e("CAT", "Cannot find category with id "+ x);
            }
        }

        return categories;
    }

    public boolean isSelected(PhotoCategory category){
        return categories.contains(category);

    }

    public void selectCategory(PhotoCategory category){
        categories.add(category);
        save();
    }

    public void removeCategory(PhotoCategory category){
        categories.remove(category);
        save();
    }

    private void save() {
        String serialized = serialize();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SETTINGS_KEY, serialized);
        editor.commit();
    }

    @NonNull
    private String serialize() {

        StringBuilder sb = new StringBuilder();

        for(PhotoCategory category : categories){
            sb.append(category.getId()).append(";");
        }

        return sb.toString();
    }

    public Set<PhotoCategory> getSelectedCategories(){
        return categories;
    }

}
