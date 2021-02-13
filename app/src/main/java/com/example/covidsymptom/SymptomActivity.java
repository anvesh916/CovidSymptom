package com.example.covidsymptom;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;

public class SymptomActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, RatingBar.OnRatingBarChangeListener {
    private HashMap<String, Integer> symptoms;
    private RatingBar simpleRatingBar;
    private Spinner spinner;
    private int spinnerPosition;
    private ImageButton deleteButton;
    private SymptomModel symptom;
    private DataBaseHelper dataBaseHelper;
    private ListView signList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom);

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.symptoms_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Initialise hashmap with default values
        symptoms = new HashMap<String, Integer>();
        symptoms.put("Nausea", 0);
        symptoms.put("Headache", 0);
        symptoms.put("Diarrhea", 0);
        symptoms.put("Soar Throat", 0);
        symptoms.put("Fever", 0);
        symptoms.put("Muscle Ache", 0);
        symptoms.put("Loss of Smell or Taste", 0);
        symptoms.put("Cough", 0);
        symptoms.put("Shortness of Breath", 0);
        symptoms.put("Feeling tired", 0);

        simpleRatingBar = findViewById(R.id.simpleRatingBar);
        simpleRatingBar.setOnRatingBarChangeListener(this);

        deleteButton = findViewById(R.id.deleteButton);

        dataBaseHelper = new DataBaseHelper(SymptomActivity.this);
        signList = findViewById(R.id.sign_list);
        this.updateList();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerPosition = position;
        String item = (String) parent.getSelectedItem();
        if (symptoms.get(item) != null) {
            simpleRatingBar.setRating(symptoms.get(item));
        } else {
            simpleRatingBar.setRating(0);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        String item = (String) spinner.getItemAtPosition(spinnerPosition);
        if (rating > 0) {
            symptoms.put(item, (int) rating);
            //   symptom = new SymptomModel(item, rating);
//            dataBaseHelper.addOne(symptom);
        } else if (symptoms.get(item) != null) {
            symptoms.put(item, 0);
            //   dataBaseHelper.deleteOne(new SymptomModel(item, 0));
        }
        deleteButton.setVisibility(rating > 0 ? View.VISIBLE : View.INVISIBLE);
        this.updateList();
    }

    public void removeRating(View view) {
        simpleRatingBar.setRating(0);
    }

    public void uploadSymptoms(View view) {
        for (String i : symptoms.keySet()) {
            dataBaseHelper.addOne(symptom);
        }
        this.updateList();
    }

    private void updateList() {
//        // Show the list
//        List<SymptomModel> allSymptoms = dataBaseHelper.getAll();
//        for (int i = 0; i < allSymptoms.size(); i++) {
//            symptoms.put(allSymptoms.get(i).getSign(), allSymptoms.get(i).getValue());
//        }
//        ArrayAdapter symptoms = new ArrayAdapter<SymptomModel>(SymptomActivity.this, android.R.layout.simple_list_item_1, allSymptoms);
//        signList.setAdapter(symptoms);
    }
}