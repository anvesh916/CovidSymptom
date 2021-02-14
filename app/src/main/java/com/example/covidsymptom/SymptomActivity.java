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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SymptomActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, RatingBar.OnRatingBarChangeListener {
    public static final String NAUSEA = "Nausea";
    public static final String HEADACHE = "Headache";
    public static final String DIARRHEA = "Diarrhea";
    public static final String SOAR_THROAT = "Soar Throat";
    public static final String FEVER = "Fever";
    public static final String MUSCLE_ACHE = "Muscle Ache";
    public static final String LOSS_OF_SMELL_OR_TASTE = "Loss of Smell or Taste";
    public static final String COUGH = "Cough";
    public static final String SHORTNESS_OF_BREATH = "Shortness of Breath";
    public static final String FEELING_TIRED = "Feeling tired";
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
        } else if (symptoms.get(item) != null) {
            symptoms.put(item, 0);
        }
        deleteButton.setVisibility(rating > 0 ? View.VISIBLE : View.INVISIBLE);
        this.updateList();
    }

    public void removeRating(View view) {
        simpleRatingBar.setRating(0);
    }

    public void uploadSymptoms(View view) {
        SymptomModel symptomModel = new SymptomModel();
        symptomModel.setNAUSEA(symptoms.get(NAUSEA));
        symptomModel.setHEAD_ACHE(symptoms.get(HEADACHE));
        symptomModel.setDIARRHEA(symptoms.get(DIARRHEA));
        symptomModel.setSOAR_THROAT(symptoms.get(SOAR_THROAT));
        symptomModel.setFEVER(symptoms.get(FEVER));
        symptomModel.setMUSCLE_ACHE(symptoms.get(MUSCLE_ACHE));
        symptomModel.setNO_SMELL_TASTE(symptoms.get(LOSS_OF_SMELL_OR_TASTE));
        symptomModel.setCOUGH(symptoms.get(COUGH));
        symptomModel.setSHORT_BREATH(symptoms.get(SHORTNESS_OF_BREATH));
        symptomModel.setFEEL_TIRED(symptoms.get(FEELING_TIRED));
        dataBaseHelper.addOne(symptomModel);

        this.updateList();
    }

    private void updateList() {
        // Show the list
        List<String> items = new ArrayList<String>();
        SymptomModel allSymptoms = dataBaseHelper.getByID(1);
        items.add("Heart Rate " + allSymptoms.getHEART_RATE());
        items.add("Respiratory Rate " + allSymptoms.getRESP_RATE());
        items.add("Nausea " + allSymptoms.getNAUSEA());
        items.add("Headache " + allSymptoms.getHEAD_ACHE());
        items.add("Diarrhea " + allSymptoms.getDIARRHEA());
        items.add("Soar Throat " + allSymptoms.getSOAR_THROAT());
        items.add("Fever " + allSymptoms.getFEVER());
        items.add("Muscle Ache " + allSymptoms.getMUSCLE_ACHE());
        items.add("Loss of Smell or Taste " + allSymptoms.getNO_SMELL_TASTE());
        items.add("Cough " + allSymptoms.getCOUGH());
        items.add("Shortness of Breath " + allSymptoms.getSHORT_BREATH());
        items.add("Feeling tired " + allSymptoms.getFEEL_TIRED());

        symptoms.put(NAUSEA, allSymptoms.getNAUSEA());
        symptoms.put(HEADACHE, allSymptoms.getHEAD_ACHE());
        symptoms.put(DIARRHEA, allSymptoms.getDIARRHEA());
        symptoms.put(SOAR_THROAT, allSymptoms.getSOAR_THROAT());
        symptoms.put(FEVER, allSymptoms.getFEVER());
        symptoms.put(MUSCLE_ACHE, allSymptoms.getMUSCLE_ACHE());
        symptoms.put(LOSS_OF_SMELL_OR_TASTE, allSymptoms.getNO_SMELL_TASTE());
        symptoms.put(COUGH, allSymptoms.getCOUGH());
        symptoms.put(SHORTNESS_OF_BREATH, allSymptoms.getSHORT_BREATH());
        symptoms.put(FEELING_TIRED, allSymptoms.getFEEL_TIRED());

        ArrayAdapter symptomsList = new ArrayAdapter<String>(SymptomActivity.this, android.R.layout.simple_list_item_1, items);
        signList.setAdapter(symptomsList);
    }
}