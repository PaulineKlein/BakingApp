package com.pklein.bakingapp.tools;

import android.util.Log;

import com.pklein.bakingapp.data.ingredient;
import com.pklein.bakingapp.data.recipe;
import com.pklein.bakingapp.data.step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class JsonUtils {

    private static final String TAG= "JsonUtils";

    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_SERVINGS = "servings";
    private static final String JSON_IMAGE = "image";

    private static final String JSON_INGREDIENTS = "ingredients";
    private static final String JSON_QUANTITY = "quantity";
    private static final String JSON_MEASURE = "measure";
    private static final String JSON_INGREDIENT = "ingredient";

    private static final String JSON_STEP = "steps";
    private static final String JSON_SHORTDESC = "shortDescription";
    private static final String JSON_DESC = "description";
    private static final String JSON_VIDEO_URL = "videoURL";
    private static final String JSON_THUMBNAIL_URL = "thumbnailURL";


    /**
     * This method returns a list of Recipes described inside a JSON file
     *
     * @param json  The Json with the list of the recipes to parse
     * @return List<Recipe> : a list of recipe objects
     */
    public static List<recipe> parseBakingJson(String json) throws JSONException {

        Log.i(TAG, "Start parseBakingJson");

        JSONArray BakingJsonArray = new JSONArray(json);
        List<recipe> ListRecipes = new ArrayList<>();

        for(int i=0; i<BakingJsonArray.length(); i++){
            JSONObject BakingJsonobj = BakingJsonArray.getJSONObject(i);
            recipe rec = new recipe();
            List<ingredient> ingList = new ArrayList<>();
            List<step> stepList = new ArrayList<>();

            Log.i(TAG, "JSON_NAME "+BakingJsonobj.optString(JSON_NAME));

            rec.setmPos(i);
            if(BakingJsonobj.has(JSON_ID)){ rec.setmId(BakingJsonobj.optInt(JSON_ID));}
            if(BakingJsonobj.has(JSON_NAME)){ rec.setmName(BakingJsonobj.optString(JSON_NAME));}
            if(BakingJsonobj.has(JSON_SERVINGS)){ rec.setmServings(BakingJsonobj.optInt(JSON_SERVINGS));}
            if(BakingJsonobj.has(JSON_IMAGE)){ rec.setmImage(BakingJsonobj.optString(JSON_IMAGE));}

            //ingredients :
            if(BakingJsonobj.has(JSON_INGREDIENTS)){
                JSONArray ListIngredientsJson = BakingJsonobj.getJSONArray(JSON_INGREDIENTS);

                for (int j = 0; j < ListIngredientsJson.length(); j++) {
                    JSONObject Ingredientsobj = ListIngredientsJson.getJSONObject(j);
                    ingredient ing = new ingredient();
                    if(Ingredientsobj.has(JSON_QUANTITY)){ ing.setmQuantity(Ingredientsobj.optString(JSON_QUANTITY));}
                    if(Ingredientsobj.has(JSON_MEASURE)){ ing.setmMeasure(Ingredientsobj.optString(JSON_MEASURE));}
                    if(Ingredientsobj.has(JSON_INGREDIENT)){ ing.setmIngredient(Ingredientsobj.optString(JSON_INGREDIENT));}

                    //add the ingredient to the list array :
                    ingList.add(ing);
                }
            }
            rec.setmIngredients(ingList); // add all the ingredients to the recipe object

            //steps :
            if(BakingJsonobj.has(JSON_STEP)){
                JSONArray ListstepsJson = BakingJsonobj.getJSONArray(JSON_STEP);
                for (int k = 0; k < ListstepsJson.length(); k++) {
                    JSONObject Stepsobj = ListstepsJson.getJSONObject(k);
                    step step = new step();
                    step.setmPos(k);
                    if(Stepsobj.has(JSON_ID)){ step.setmId(Stepsobj.optInt(JSON_ID));}
                    if(Stepsobj.has(JSON_SHORTDESC)){ step.setmShortDescription(Stepsobj.optString(JSON_SHORTDESC));}
                    if(Stepsobj.has(JSON_DESC)){ step.setmDescription(Stepsobj.optString(JSON_DESC));}
                    if(Stepsobj.has(JSON_VIDEO_URL)){ step.setmVideoURL(Stepsobj.optString(JSON_VIDEO_URL));}
                    if(Stepsobj.has(JSON_THUMBNAIL_URL)){ step.setmThumbnailURL(Stepsobj.optString(JSON_THUMBNAIL_URL));}

                    //add the step to the list array :
                    stepList.add(step);
                }
            }
            rec.setmStep(stepList); // add all the steps to the recipe object

            //add the recipe to the list array :
            ListRecipes.add(rec);
        }

        Log.i(TAG, "End parseBakingJson");
        return ListRecipes;
    }

}
