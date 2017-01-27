package pl.rosiakit.px500.utils;

import java.util.NoSuchElementException;

public enum PhotoCategory {
    UNCATEGORIZIED(0, "Uncategorized"),
    CELEBRITIES(1, "Celebrities"),
    FILM(2, "Film"),
    JOURNALISM(3, "Journalism"),
    NUDE(4, "Nude"),
    BLACK_AND_WHITE(5, "Black and white"),
    STILL_LIFE(6, "Still Life"),
    PEOPLE(7, "People"),
    LANDSCAPES(8,"Landscapes"),
    CITY_AND_ARCHITECTURE(9,"City and Architecture"),
    ABSTRACT(10,"Abstract"),
    ANIMALS(11,"Animals"),
    MACRO(12,"Macro"),
    TRAVEL(13,"Travel"),
    FASHION(14,"Fashion"),
    COMMERCIAL(15,"Commercial"),
    CONCERT(16,"Concert"),
    SPORT(17,"Sport"),
    NATURE(18,"Nature"),
    PERFORMING_ARTS(19,"Performing Arts"),
    FAMILY(20,"Family"),
    STREET(21,"Street"),
    UNDERWATER(22,"Underwater"),
    FOOD(23,"Food"),
    FINE_ART(24,"Fine Art"),
    WEDDING(25,"Wedding"),
    TRANSPORTATION(26,"Transportation"),
    URBAN_EXPLORATION(27,"Urban Exploration");

    private final int id;
    private final String value;

    PhotoCategory(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public static PhotoCategory getCategoryById(int id){
        for(PhotoCategory cat : PhotoCategory.values()){
            if(cat.getId() == id){
                return cat;
            }
        }

        throw new NoSuchElementException();
    }

    public static PhotoCategory getCategoryByName(String name){
        for(PhotoCategory cat : PhotoCategory.values()){
            if(name.equalsIgnoreCase(cat.getName())){
                return cat;
            }
        }

        throw new NoSuchElementException();
    }

    public int getId(){
        return id;
    }

    public String getName()
    {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}
